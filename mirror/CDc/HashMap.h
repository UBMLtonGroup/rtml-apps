/*
 * HashMap.h
 *
 *  Created on: Jul 29, 2009
 *      Author: ghaitho
 */

// Tomas: this code is apparently taken from http://elliottback.com/wp/hashmap-implementation-in-c/
// (we have addes a few fixes later, though)

#ifndef HASHMAP_H_
#define HASHMAP_H_

#define MAP_MISSING -3  /* No such element */
#define MAP_FULL -2 	/* Hashmap is full */
#define MAP_OMEM -1 	/* Out of Memory */
#define MAP_OK 0 	/* OK */

/*
 * any_t is a pointer.  This allows you to put arbitrary structures in
 * the hashmap.
 *
 * not really: there are pointer size issues on 64-bit architectures
 */

//typedef void *any_t;

typedef int HashMapItemData;

typedef struct HashMapItem {
    int key;
    int used;
    HashMapItemData data;
} HashMapItem;

typedef struct HashMap {
    int size;
    int allocatedSize;
    int lastItem;
    HashMapItem* items;
} HashMap;

typedef struct HashMapIterator {
    HashMap* map;
    int lastItem;
    int toGo;
    int items;
    int iter;
} HashMapIterator;


HashMap* HashMap_create();

HashMap* HashMap_create2(int size);

int HashMap_put(HashMap* map, int key, HashMapItemData data);

int HashMap_get(HashMap* map, int key, HashMapItemData* data);

int HashMap_remove(HashMap* map, int key);

#define HashMap_free(map) _HashMap_free(map); map=NULL
void _HashMap_free(HashMap* map);

void HashMap_clear(HashMap* map);

int HashMap_size(HashMap* map);

int HashMap_allocatedSize(HashMap* map);

void HashMap_map(HashMap* map, void (*fnc)(int,HashMapItemData));

HashMapIterator HashMap_getIterator(HashMap* map);

int HashMapIterator_done(HashMapIterator* iter);

HashMapItemData HashMapIterator_next(HashMapIterator* iter);


#endif /* HASHMAP_H_ */
