/*
 * VoxelMap.c
 *
 *  Created on: Aug 4, 2009
 *      Author: ghaitho
 */

#include "VoxelMap.h"
#include "HashMap.h"
#include "Vector2d.h"
#include "Motion.h"

#include "utils.h"
#include "config.h"


#include <stdlib.h>

// FIXME: remove unused comments

Motion *** allocatedMotions;
int usedMotions;
int *usedMotionsInList;
HashMap* voxel_map;

void VOXELMAP_init() {
    int i = 0, j = 0;

    allocatedMotions = (Motion ***) checkedMalloc(MAX_LIST * sizeof (Motion**));
    for (i = 0; i < MAX_LIST; i++)
        allocatedMotions[i] = (Motion **) checkedMalloc(MAX_ITEMS_PER_LIST * sizeof (Motion *));

    usedMotions = 0;
    usedMotionsInList = (int *) checkedMalloc(sizeof (int) * MAX_ITEMS_PER_LIST);
    for (j = 0; j < MAX_ITEMS_PER_LIST; j++)usedMotionsInList[j] = 0;
    voxel_map = HashMap_create();
}

void VOXELMAP_destroy() {
    int i = 0;
    HashMap_free(voxel_map);
    free(usedMotionsInList);
    for (i = 0; i < MAX_LIST; i++)
        free(allocatedMotions[i]);
    free(allocatedMotions);
}

void VOXELMAP_reset() {
    int j = 0;
    usedMotions = 0;
    for (j = 0; j < MAX_ITEMS_PER_LIST; j++)usedMotionsInList[j] = 0;
    HashMap_clear(voxel_map);
}

void VOXELMAP_put(Vector2d * v, Motion * m) {
    int i = -1;
    if (HashMap_get(voxel_map, VECTOR2D_hashCode(v), (HashMapItemData *) & i) != MAP_OK) {
        // It's not in the map ^
        // add the motion to the motion list
        allocatedMotions[usedMotions][usedMotionsInList[usedMotions]] = m;
        // put it into the voxel_map, its data is the usedMotions
        if (HashMap_put(voxel_map, VECTOR2D_hashCode(v), (HashMapItemData) usedMotions) != MAP_OK) {
            exit(0);
        }
        // Increase the number of motions
        usedMotionsInList[usedMotions]++;
        usedMotions++;
    } else {
        // it is in the voxel map -> only add it to its list of motions
        allocatedMotions[i][usedMotionsInList[i]] = m;
        usedMotionsInList[i]++;
    }
}

int VOXELMAP_getMotionCount(Vector2d * v) {
    int i = -1;
    int j = HashMap_get(voxel_map, VECTOR2D_hashCode(v), (HashMapItemData *) & i);
    if (j == MAP_OK)return usedMotionsInList[i];
    else return 0;
}

int VOXELMAP_get(Vector2d * v, int location, Motion ** m) {
    int i = -1;
    int j = HashMap_get(voxel_map, VECTOR2D_hashCode(v), &i);
    if (j == MAP_OK)*m = allocatedMotions[i][location];
    return j;
}

int VOXELMAP_getMotionListsCount() {
    return usedMotions;
}

int VOXELMAP_getMotionCountUsingLocation(int loc) {
    return usedMotionsInList[loc];
}

void VOXELMAP_getUsingLocation(int loc, int location, Motion ** m) {
    *m = allocatedMotions[loc][location];
}

int iterresult; // FIXME deprecated
int itercnt; // FIXME deprecated
int iterval; // FIXME deprecated
int currentmotions; // FIXME deprecated


int _iterIndex; // My

// FIXME Deprecated
int iter(HashMapItemData l, HashMapItemData m) {
    if (usedMotionsInList[(int) m] == currentmotions) {
        itercnt++;
        if (itercnt == iterval) {
            iterresult = (int) m;
            return -1;
        }
    }
    return MAP_OK;
}

/*void VOXELMAP_initIter() {
    iterval = 1; // FIXME deprecated
    currentmotions = 1; // FIXME deprecated
    _iterIndex=-1;
}

/** \brief Iterates over the voxel hash map.
 *
 * Now executes with linear complexity (for the whole hash map). However it is a quick & dirty fix
 * as the whole hash map is very awkward and it may be a very good idea to rewrite it from scratch.
 */
HashMapIterator VOXELMAP_getIterator() {
    return HashMap_getIterator(voxel_map);
}

/*int VOXELMAP_iterNext() {
    int r=0;
    while (1) {
        _iterIndex=hashmap_nextFreeIndex(voxel_map,_iterIndex);
        if (_iterIndex==-1) return -1;
        r=hashmap_getIndexData(voxel_map,_iterIndex);
        if (r!=-1) return r;
    } 
/*
    if (iterval == hashmap_length(voxel_map) + 1) return -1;
    itercnt = 0;
    iterresult = -1;
    currentmotions = 0;
    while (iterresult == -1) {
        hashmap_iterate(voxel_map, (PFany) & iter, (any_t) "");
        if (iterresult == -1) {
            currentmotions++;
        } else {
            iterval++;
        }
    }
    itercnt = 0;
    //printf("%d %d %d\n",iterresult,usedMotionsInList[iterresult],iterval);
    return iterresult; */
/* } */
