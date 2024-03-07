/*
 * StateTable.c
 *
 *  Created on: Jul 26, 2009
 *      Author: ghaitho
 */


#include "Vector3d.h"
#include "StateTable.h"
#include "HashMap.h"
#include "config.h"
#include "utils.h"

#include <stdlib.h>
#include <stdio.h>

Vector3d * allocatedVectors;
int usedVectors;
HashMap* motionVectors;

/** Mapping Aircraft -> Vector3d. */

void STATETABLE_init() {
    int i = 0;
    allocatedVectors = (Vector3d *) checkedMalloc(sizeof (Vector3d) * MAX_AIRPLANES);
    for (; i < MAX_AIRPLANES; i++) {
        VECTOR3D_init(&allocatedVectors[i]);
    }
    usedVectors = 0;
    motionVectors = HashMap_create();
}

void STATETABLE_destroy() {
    HashMap_free(motionVectors);
    free(allocatedVectors);
}

void STATETABLE_put(CallSign * callsign, float x, float y, float z) {
    int i = -1;
    if (HashMap_get(motionVectors, CALLSIGN_HashCode(callsign), (HashMapItemData *) & i) != MAP_OK) {
        allocatedVectors[usedVectors].x = x;
        allocatedVectors[usedVectors].y = y;
        allocatedVectors[usedVectors].z = z;
        if (HashMap_put(motionVectors, CALLSIGN_HashCode(callsign), (HashMapItemData) usedVectors) != MAP_OK) {
            exit(0);
        }
        usedVectors++;
    } else {
        allocatedVectors[i].x = x;
        allocatedVectors[i].y = y;
        allocatedVectors[i].z = z;
    }
}

int STATETABLE_get(CallSign * callsign, Vector3d * v) {
    int i = -1;
    int j = HashMap_get(motionVectors, CALLSIGN_HashCode(callsign), &i);
    if (j == MAP_OK)VECTOR3D_set(v, allocatedVectors[i].x, allocatedVectors[i].y, allocatedVectors[i].z);
    return j;
}

