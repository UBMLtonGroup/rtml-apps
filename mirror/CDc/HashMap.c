// This hashmap is reimplemented from scratch. PETA 

#include "HashMap.h"
#include "utils.h"
#include "config.h"
#include "Aircraft.h"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#define INITIAL_SIZE 1024

// -------------------------------------------------------------------------------------------------
// Convenience macros

#define hashInteger(map,x) _HashMap_hashInteger(map,x)
#define _HashMap_isUsed(x) (x!=0) && (x!=-1)

// -------------------------------------------------------------------------------------------------
// Internal functions declaration
void _HashMap_rehash(HashMap* map);
uint _HashMap_hashInteger(HashMap* map, uint key);
int _HashMap_findPlace(HashMap* map, int key);
int _HashMap_findIndex(HashMap* map, int key);



// -------------------------------------------------------------------------------------------------
/** \brief Creates a hash map with default initial size.
 */
HashMap* HashMap_create() {
    return HashMap_create2(INITIAL_SIZE);
}

// -------------------------------------------------------------------------------------------------
/** \brief Creates a hash map with specified size.
 */
HashMap* HashMap_create2(int size) {
    HashMap* map=(HashMap*)malloc(sizeof(HashMap));
    assert(map!=NULL,"Unable to allocate memory for the hashmap1");
    map->items=(HashMapItem*)malloc(sizeof(HashMapItem)*size);
    assert(map->items!=NULL,"Unable to allocate memory for the hashmap");
    memset(map->items,0, sizeof(HashMapItem)*size); // Clear the area
    map->allocatedSize=size;
    map->size=0;
    map->lastItem=-2;
    return map;
}

// -------------------------------------------------------------------------------------------------
/** \brief Hashes an integer.
 *
 * This is used internally by the hash map.
 */
uint _HashMap_hashInteger(HashMap* map, uint key) {
    /* Robert Jenkins' 32 bit Mix Function */
    key += (key << 12);
    key ^= (key >> 22);
    key += (key << 4);
    key ^= (key >> 9);
    key += (key << 10);
    key ^= (key >> 2);
    key += (key << 7);
    key ^= (key >> 12);

    /* Knuth's Multiplicative Method */
    key = (key >> 3) * 2654435761U;
    return key % map->allocatedSize;
}
// -------------------------------------------------------------------------------------------------
/** \brief Finds place for a new element of given key
 *
 * If the key exists, returns it location, otherwise returns first unused location in the table.
 */
int _HashMap_findPlace(HashMap* map, int key) {
    int index=hashInteger(map,key);
    while (_HashMap_isUsed(map->items[index].used>0)) {
        if (map->items[index].key==key) return index;
        index=(index+1)%map->allocatedSize;
    }
    // Now we point to first free location after our key
    return index;
}

// -------------------------------------------------------------------------------------------------
/** \brief Inserts an element into the hash map.
 *
 * Preserves the chain of elements for constant time iteration if possible.
 */
int HashMap_put(HashMap* map, int key, HashMapItemData data) {
    assert(map!=NULL,"Cannot perform operation on NULL HashMap. Initialize first.");
    if (map->size==map->allocatedSize) _HashMap_rehash(map);
    int index=_HashMap_findPlace(map,key);
    map->items[index].data=data;
    map->items[index].key=key;
    map->items[index].used=(map->lastItem+1); // This is guaranteed to be nozero
    // If the chain is valid
    if (map->lastItem!=-3) map->lastItem=index;
    map->size++;
    return MAP_OK;
}

// -------------------------------------------------------------------------------------------------
/** \brief Returns the index of the given key in the hashmap.
 *
 * Returns -1 if key is not found.
 */
int _HashMap_findIndex(HashMap* map, int key) {
    int index=hashInteger(map,key);
    int visited=map->size;
    int i;
    while (visited>0) {
        if (map->items[index].used==0) break; // Element is missing
        if (map->items[index].used>=-1) { // The place is used
            visited--;
            if (map->items[index].key==key) return index;
        }
        index=(index+1)%map->allocatedSize;
    }
    // Not found
    return -1;
}

// -------------------------------------------------------------------------------------------------
/** \brief Retrieves an element from the table.
 *
 * If element is not found, returns MAP_MISSING.
 */
int HashMap_get(HashMap*map, int key, HashMapItemData* data) {
    assert(map!=NULL,"Cannot perform operation on NULL HashMap. Initialize first.");
    int index=_HashMap_findIndex(map,key);
    if (index==-1) {
        return MAP_MISSING;
    }
    if (data!=NULL) *data=map->items[index].data;
    return MAP_OK;
}

// -------------------------------------------------------------------------------------------------
/** \brief Removes a key from the table.
 *
 * Tries to preserve the chain. However this won't execute in many cases.
 */
int HashMap_remove(HashMap* map, int key) {
    assert(map!=NULL,"Cannot perform operation on NULL HashMap. Initialize first.");
    int index=_HashMap_findIndex(map,key);
    if (index==-1) return MAP_MISSING;
    if (index==map->lastItem) {
        map->lastItem=map->items[map->lastItem].used-1; // different metrics so that 0 means not used
    } else map->lastItem=-3; // Invalidate the sequence
    map->items[index].used=-2; // deleted
    map->items[index].key=0;
    map->size--;
    return MAP_OK;
}

// -------------------------------------------------------------------------------------------------
/** \brief Deletes the hashmap. Deprecated
 *
 * Use HashMap_free() macro instead.
 */
void _HashMap_free(HashMap* map) {
    assert(map!=NULL,"Cannot perform operation on NULL HashMap. Initialize first.");
    free(map->items);
    free(map);
}

// -------------------------------------------------------------------------------------------------
/** \brief Clears the hashmap.
 */
void HashMap_clear(HashMap* map) {
    assert(map!=NULL,"Cannot perform operation on NULL HashMap. Initialize first.");
    map->size=0;
    map->lastItem=-2;
    int i;
    memset(map->items,0,sizeof(HashMapItem)*map->allocatedSize);
}

// -------------------------------------------------------------------------------------------------
/** \brief Returns the size of the hashmap.
 */
int HashMap_size(HashMap* map) {
    assert(map!=NULL,"Cannot perform operation on NULL HashMap. Initialize first.");
    return map->size;
}

// -------------------------------------------------------------------------------------------------
/** \brief Returns the actual allocated memory
 */
int HashMap_allocatedSize(HashMap* map) {
    assert(map!=NULL,"Cannot perform operation on NULL HashMap. Initialize first.");
    return map->allocatedSize;
}

// -------------------------------------------------------------------------------------------------
/** \brief Doubles the size of the hash table.
 *
 * Also after rehasning, iterators are constant time.
 */
void _HashMap_rehash(HashMap* map) {
    HashMap* _tmp=HashMap_create2(map->allocatedSize*2);
    if (map->lastItem>=0) {
        int index=map->lastItem;
        while (index>=0) {
            HashMap_put(_tmp,map->items[index].key,map->items[index].data);
            index=map->items[index].used-1;
        }
    } else if (map->size!=0) {
        int index=0;
        int visited=map->size;;
        while (visited>0) {
            if (_HashMap_isUsed(map->items[index].used)) {
                visited--;
                HashMap_put(_tmp,map->items[index].key,map->items[index].data);
            }
            index++;
        }
    }
    free(map->items);
    map->items=_tmp->items;
    map->allocatedSize=map->allocatedSize*2;
    map->lastItem=_tmp->lastItem;
    free(_tmp);
}

// -------------------------------------------------------------------------------------------------
/** \brief Calls given function on each element in the hash table.
 *
 * Either linear with respect to number of elements (if chain is preserved) or with respect to the
 * allocated space, if the chain is broken.
 */
void HashMap_map(HashMap* map, void (*fnc)(int,HashMapItemData)) {
    assert(map!=NULL,"Cannot perform operation on NULL HashMap. Initialize first.");
    if (fnc==NULL) return;
    if (map->lastItem>=0) {
        int index=map->lastItem;
        while (index>=0) {
            fnc(map->items[index].key,map->items[index].data);
            index=map->items[index].used-1;
        }
    } else if (map->size!=0) {
        int index=0;
        int visited=map->size;;
        while (visited>0) {
            if (_HashMap_isUsed(map->items[index].used)) {
                visited--;
                fnc(map->items[index].key,map->items[index].data);
            }
            index++;
        }
    }
}

// -------------------------------------------------------------------------------------------------
/** \brief Returns iterator over the hash table elements
 */
HashMapIterator HashMap_getIterator(HashMap* map) {
    assert(map!=NULL,"Cannot perform operation on NULL HashMap. Initialize first.");
    HashMapIterator result;
    result.map=map;
    result.lastItem=map->lastItem;
    result.toGo=map->size;
    if (map->lastItem>=0) {
        result.iter=map->lastItem;
    } else result.iter=-1;
    return result;
}

// -------------------------------------------------------------------------------------------------
/** \brief Returns true if iterator cannot progress more.
 */
int HashMapIterator_done(HashMapIterator* iter) {
    assert(iter!=NULL,"Cannot perform operation on NULL iterator. Initialize first.");
    return (iter->toGo==0);
}

// -------------------------------------------------------------------------------------------------
/** \brief Returns next element in the hash table using the iterator.
 *
 * If chain is preserved has constant complexity, otherwise linear.
 */
HashMapItemData HashMapIterator_next(HashMapIterator* iter) {
    assert(iter!=NULL,"Cannot perform operation on NULL iterator. Initialize first.");
    assert(iter->toGo>0,"All items have been already iterated.");
    HashMapItemData result;
    if (iter->map->lastItem>=0) {
        result=iter->map->items[iter->iter].data;
        iter->iter=iter->map->items[iter->iter].used-1;
    } else {
        while (1) {
            (iter->iter)++;
            if (_HashMap_isUsed(iter->map->items[iter->iter].used)) break;
        }
        result=iter->map->items[iter->iter].data;
    }
    iter->toGo--;
    return result;
}
