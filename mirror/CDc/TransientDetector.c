/*
 * TransientDetector.c
 *
 *  Created on: Jul 26, 2009
 *      Author: ghaitho
 */

#include "TransientDetector.h"
#include "Reducer.h"
#include "RawFrame.h"
#include "StateTable.h"
#include "Motion.h"
#include "Aircraft.h"
#include "Constants.h"
#include "helper.h"
#include "HashMap.h"
#include "VoxelMap.h"
#include "Detector.h"

#include "utils.h"
#include "rapita.h"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

// Externals of the detector statistics and current pointer
extern struct DetectorStatEntry* detectorStats;
extern int statsCurrentFrame;

// FIXME: remove unused comments

float voxelSize;
RawFrame * currentFrame;
int motionctr = 0;

int frameno = -1;
char linetoprint[200];

Motion * createdMotions;

void TRANSIENTDETECTOR_init() {
    STATETABLE_init();
    voxelSize = GOOD_VOXEL_SIZE;
    VOXELMAP_init();
}
void TRANSIENTDETECTOR_destroy() {
    VOXELMAP_destroy();
    STATETABLE_destroy();
}
// -------------------------------------------------------------------------------------------------
/** \brief Detector executor.
 *
 * Does some benchmarking for detector.rin.
 */
void TRANSIENTDETECTOR_run() {
    list_motions * l = 0;
    collisions * c = 0;
    collisions * ptr = 0;
    list_motions * ptrl = 0;

    collisions * ptrcolltofree = 0;
    Vector3d * ptrvectortofree = 0;

    motions * ptrmotionstofree = 0;
    motions * ptrmotiontofree = 0;
    list_motions * ptrlistmotionstofree = 0;

    int motioncount;

    c = (collisions *) checkedMalloc(sizeof (collisions)); // freed
    c->one = 0;
    c->two = 0;
    c->location = 0;
    c->next = 0;

    l = (list_motions *) checkedMalloc(sizeof (list_motions)); // freed
    l->next = 0;
    l->val = 0;

    if (/*SYNCHRONOUS_DETECTOR ||*/ DEBUG_DETECTOR) {
        TRANSIENTDETECTOR_dumpFrame("CD-PROCESSING-FRAME (indexed as received):");
    }
    benchmarkRapita(rapita_set(RAPITA_REDUCER_INIT));
    REDUCER_init(voxelSize);
    benchmarkRapita(rapita_set(RAPITA_DONE+RAPITA_REDUCER_INIT));
    createdMotions = (Motion *) checkedMalloc(sizeof (Motion) * currentFrame->planeCnt); // freed
    int numberOfCollisions = TRANSIENTDETECTOR_lookForCollisions(TRANSIENTDETECTOR_createMotions(), l, c);
    benchmarkRtems(\
        detectorStats[statsCurrentFrame].detected=numberOfCollisions;
    )
    // printing all the detected collisions
    if (/*SYNCHRONOUS_DETECTOR ||*/ DEBUG_DETECTOR) {
        printf("CD detected %d collisions.\n", numberOfCollisions);
        int colIndex = 0;
        ptr = c;
        char buf1[20];
        while (ptr->next) {
            printf("CD collision %d occured at location %s with 2 involved aircraft.\n", colIndex, VECTOR3D_toString(buf1, ptr->location));
            printf("CD collision %d includes aircraft %s\n", colIndex, ptr->one->callsign);
            printf("CD collision %d includes aircraft %s\n", colIndex, ptr->two->callsign);
            colIndex++;
            ptr = ptr->next;
        }
    }
    debug(" ");

    benchmarkRapita(rapita_set(RAPITA_DETECTOR_CLEANUP));
    // free memory

    // free collisions
    ptr = c;
    while (ptr->next) {
        ptrcolltofree = ptr;
        ptrvectortofree = ptr->location;
        ptr = ptr->next;
        free(ptrcolltofree);
        free(ptrvectortofree);
    }
    free(ptr);
    // free list motions with collisions
    ptrl = l;
    while (ptrl->next) {
        ptrlistmotionstofree = ptrl;
        ptrmotionstofree = ptrl->val;
        ptrl = ptrl->next;
        while (ptrmotionstofree->next) {
            ptrmotiontofree = ptrmotionstofree;
            ptrmotionstofree = ptrmotionstofree->next;
            free(ptrmotiontofree);
        }
        free(ptrmotionstofree);
        free(ptrlistmotionstofree);
    }
    free(ptrl);
    // free motions in current frame
    for (motioncount = 0; motioncount < motionctr; motioncount++) {
        free(createdMotions[motioncount].pos_one);
        free(createdMotions[motioncount].pos_two);
        free(createdMotions[motioncount].aircraft->callsign);
        free(createdMotions[motioncount].aircraft);
    }
    free(createdMotions);
    benchmarkRapita(rapita_set(RAPITA_DONE+RAPITA_DETECTOR_CLEANUP));
}

// -------------------------------------------------------------------------------------------------
/** \brief Analyzes the frame.
 *
 * Also outputs the suspected collisions benchmarking.
 */
int TRANSIENTDETECTOR_lookForCollisions(Motion * m, list_motions * l, collisions * c) {
    TRANSIENTDETECTOR_reduceCollisionSet(m, l);
    benchmarkRtems(
            detectorStats[statsCurrentFrame].time_afterReducer=clock_now();
    )
    //storing the number of suspected collisions
    int suspectedSize = 0;
    list_motions * mlist = l;
    while (mlist->next != 0) {
        suspectedSize++;
        mlist = mlist->next;
    }
    benchmarkRtems(
        detectorStats[statsCurrentFrame].suspected=suspectedSize;
    )
    if ((/*SYNCHRONOUS_DETECTOR ||*/ DEBUG_DETECTOR) && suspectedSize > 0) {
        printf("CD found %d potential collisions\n", suspectedSize);
        int i = 0;

        mlist = l;
        while (mlist->next != 0) {
            int cnt = 0;
            motions * mm = mlist->val;
            while (mm->val) {
                cnt++;
                mm = mm->next;
            }
            //printf("%d\n",cnt);
            mm = mlist->val;
            while (mm->val) {
                printf("CD: potential collision %d (of %d aircraft) includes motion %s\n", i, cnt, MOTION_toString(linetoprint, mm->val));
                mm = mm->next;
            }
            i++;
            mlist = mlist->next;
        }

    }

    // Now we will determine if the suspected collisions are the real collisions
    mlist = l;
    int truesize = 0;
    while (mlist->next) {
        truesize += TRANSIENTDETECTOR_determineCollisions(mlist->val, c);
        mlist = mlist ->next;
    }
    return truesize;

}

// -------------------------------------------------------------------------------------------------
/** \brief Collects motios
 *
 * At first, this was quadratic complexity. But obviously this is only a small performance
 * gain. The big fish was the iterator which is now completely rewritten.
 */
void collectMotions(list_motions * ll) {
    list_motions * ptr = 0;
    int j = 0, k = 0;
    int listcount = 0;
   // VOXELMAP_initIter();
    ptr=ll;
    HashMapIterator iter=VOXELMAP_getIterator();
    while (!HashMapIterator_done(&iter)) {
        j=HashMapIterator_next(&iter);
        listcount = VOXELMAP_getMotionCountUsingLocation(j); // constant
        if (listcount > 1) {
            // ptr = ll; quadratic
            while (ptr->next != 0) ptr = ptr->next;
            ptr->next = (list_motions *) checkedMalloc(sizeof (list_motions)); // free in TRANSIENTDETECTOR_run
            ptr->val = (motions *) checkedMalloc(sizeof (motions)); // free in TRANSIENTDETECTOR_run
            motions * mm = ptr->val;
            ptr->next->val = 0;
            ptr->next->next = 0;
            for (k = 0; k < listcount; k++) {
                mm->next = (motions *) checkedMalloc(sizeof (motions)); // free in TRANSIENTDETECTOR_run
                mm->next->next = 0;
                mm->next->val = 0;
                VOXELMAP_getUsingLocation(j, k, &(mm->val)); // constant
                mm = mm->next;
            } 
        }
    }
}

// -------------------------------------------------------------------------------------------------
/** \Brief Reduces the list of collisions by using 2D checking.
 *
 * Takes a List of Motions and returns an List of Lists of Motions, where the inner lists implement RandomAccess.
 * Each Vector of Motions that is returned represents a set of Motions that might have collisions.
 */
list_motions * TRANSIENTDETECTOR_reduceCollisionSet(Motion * m, list_motions * l) {
    benchmarkRapita(rapita_set(RAPITA_REDUCER));
    HashMap* graph_colors;
    graph_colors = HashMap_create();
    int motionIter = 0;
    Motion * motionptr = 0;
    VOXELMAP_reset();
    for (motionIter = 0; motionIter < motionctr; motionIter++) {
        motionptr = (Motion*) (m + motionIter * sizeof (Motion));
        REDUCER_performVoxelHashing(&m[motionIter], graph_colors);
    }
    HashMap_free(graph_colors);
    collectMotions(l);
    benchmarkRapita(rapita_set(RAPITA_DONE+RAPITA_REDUCER));
    return l;
}

Motion * TRANSIENTDETECTOR_createMotions() {
    benchmarkRapita(rapita_set(RAPITA_CREATE_MOTIONS));
    int i, pos, j;
    motionctr = 0;
    Vector3d * new_pos = 0;
    pos = 0;
    for (i = 0; i < currentFrame->planeCnt; i++) {

        float x = currentFrame->positions[3 * i], y = currentFrame->positions[3 * i + 1], z = currentFrame->positions[3 * i + 2];

        // extract the call sign of plane i
        char* cs = (char *) checkedMalloc(currentFrame->lengths[i] + 1); // freed
        for (j = 0; j < currentFrame->lengths[i]; j++)
            cs[j] = currentFrame->callsigns[pos + j];
        cs[j] = 0;
        pos += currentFrame->lengths[i];
        CallSign * v = (CallSign *) checkedMalloc(sizeof (CallSign)); // freed
        v->length = j;
        v->val = cs;

        new_pos = (Vector3d*) checkedMalloc(sizeof (Vector3d)); // freed
        VECTOR3D_init2(new_pos, x, y, z);

        Vector3d * old_pos = (Vector3d *) checkedMalloc(sizeof (Vector3d)); // freed

        createdMotions[motionctr].aircraft = (Aircraft *) checkedMalloc(sizeof (Aircraft)); // free in TRANSIENTDETECTOR_run

        createdMotions[motionctr].aircraft->callsign = (char *) checkedMalloc(strlen(cs) + 1); // free in TRANSIENTDETECTOR_run

        createdMotions[motionctr].pos_one = (Vector3d *) checkedMalloc(sizeof (Vector3d)); // free in TRANSIENTDETECTOR_run
        createdMotions[motionctr].pos_two = (Vector3d *) checkedMalloc(sizeof (Vector3d)); // free in TRANSIENTDETECTOR_run

        strcpy(createdMotions[motionctr].aircraft->callsign, cs);


        if (STATETABLE_get(v, old_pos) != MAP_OK) {
            // Ales : we have detected a new aircraft

            //here, we create a new callsign and store the aircraft into the state table.
            STATETABLE_put(v, new_pos->x, new_pos->y, new_pos->z);

            //MOTION_init2(&ret[motionctr], craft, new_pos);
            VECTOR3D_init2(createdMotions[motionctr].pos_one, new_pos->x, new_pos->y, new_pos->z);
            VECTOR3D_init2(createdMotions[motionctr].pos_two, new_pos->x, new_pos->y, new_pos->z);
            if (DEBUG_DETECTOR /*|| SYNCHRONOUS_DETECTOR*/) {
                printf("createMotions: old position is null, adding motion: %s\n", MOTION_toString(linetoprint, &createdMotions[motionctr]));
            }
            motionctr++;

        } else {
            // this is already detected aircraft, we we need to update its position

            Vector3d * save_old_position = (Vector3d *) checkedMalloc(sizeof (Vector3d)); // freed
            VECTOR3D_init2(save_old_position, old_pos->x, old_pos->y, old_pos->z);

            //updating position in the StateTable
            VECTOR3D_set(old_pos, new_pos->x, new_pos->y, new_pos->z);
            STATETABLE_put(v, new_pos->x, new_pos->y, new_pos->z);

            //MOTION_init1(&ret[motionctr], craft, save_old_position, new_pos);
            VECTOR3D_init2(createdMotions[motionctr].pos_one, save_old_position->x, save_old_position->y, save_old_position->z);
            VECTOR3D_init2(createdMotions[motionctr].pos_two, new_pos->x, new_pos->y, new_pos->z);

            if (DEBUG_DETECTOR /*|| SYNCHRONOUS_DETECTOR*/) {
                printf("createMotions: adding motion: %s\n", MOTION_toString(linetoprint, &createdMotions[motionctr]));
            }
            motionctr++;
            free(save_old_position);
        }
        // free memory
        free(old_pos);
        free(new_pos);
        free(v);
        free(cs);
    }
    benchmarkRapita(rapita_set(RAPITA_DONE+RAPITA_CREATE_MOTIONS));
    return createdMotions;
}

void TRANSIENTDETECTOR_dumpFrame(char * debugPrefix) {
    printFrame(frameno, debugPrefix, currentFrame->planeCnt, currentFrame->positions,
            currentFrame->lengths,
            currentFrame->callsigns);
}

void TRANSIENTDETECTOR_setFrame(RawFrame * f) {

    if (DEBUG_DETECTOR || DUMP_RECEIVED_FRAMES /*|| SYNCHRONOUS_DETECTOR*/) {
        frameno++;
    }
    currentFrame = f;
    if (DUMP_RECEIVED_FRAMES) {
        TRANSIENTDETECTOR_dumpFrame("CD-R-FRAME:");
    }
}

int TRANSIENTDETECTOR_checkCollisionsDuplicates(collisions * c, Motion * one, Motion * two) {
    benchmarkRapita(rapita_set(RAPITA_COLLISION_DUPLICATES));
    while (c->next) {
        if (c->one == one->aircraft && c->two == two->aircraft) {
            benchmarkRapita(rapita_set(RAPITA_DONE+RAPITA_COLLISION_DUPLICATES));
            return 0;
        }
        c = c->next;
    }
    benchmarkRapita(rapita_set(RAPITA_DONE+RAPITA_COLLISION_DUPLICATES));
    return 1;
}

int TRANSIENTDETECTOR_determineCollisions(motions * mm, collisions * c) {
    benchmarkRapita(rapita_set(RAPITA_DETERMINE_COLLISIONS));
    int returned = 0;
    motions * m = mm, *m2 = mm;
    Vector3d * v = (Vector3d *) checkedMalloc(sizeof (Vector3d)); // freed
    while (m->next->next) {
        m2 = m->next;
        while (m2->next) {
            // make sure we did not detect this before!!!
            //if (TRANSIENTDETECTOR_checkCollisionsDuplicates(c, m->val, m2->val)) {
                // get vector3d collision
                if (MOTION_findIntersection(m->val, m2->val, v)) {
                    collisions * ptr = c;
                    while (ptr->next)ptr = ptr->next;
                    ptr->next = (collisions *) checkedMalloc(sizeof (collisions)); // free in TRANSIENTDETECTOR_run
                    ptr->next->one = 0;
                    ptr->next->two = 0;
                    ptr->next->location = 0;
                    ptr->next->next = 0;

                    ptr->one = m->val->aircraft;
                    ptr->two = m2->val->aircraft;
                    ptr->location = (Vector3d *) checkedMalloc(sizeof (Vector3d)); // free in TRANSIENTDETECTOR_run
                    VECTOR3D_init2(ptr->location, v->x, v->y, v->z);
                    returned++;
                }
            //}
            m2 = m2->next;
        }
        m = m->next;
    }
    free(v);
    benchmarkRapita(rapita_set(RAPITA_DONE+RAPITA_DETERMINE_COLLISIONS));
    return returned;
}
