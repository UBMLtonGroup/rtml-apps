package immortal.persistentScope.transientScope;

public class CHashMap {
	protected class Item {
		Object data;
		int key;
	}
	protected Item[] items;
	protected int lastItem=-2;
	protected int size;
	
	public CHashMap() {
		this(1024);
	}
	
	public CHashMap(int size) {
		items=new Item[size];
		size=0;
	}
	int hashInteger(int key) {
		return key % items.length; // who cares
	}
  
}


/*

HashMap* HashMap_create() {
    return HashMap_create2(INITIAL_SIZE);
}

int _HashMap_findPlace(HashMap* map, int key) {
    int index=hashInteger(map,key);
    while (_HashMap_isUsed(map->items[index].used>0)) {
        if (map->items[index].key==key) return index;
        index=(index+1)%map->allocatedSize;
    }
    // Now we point to first free location after our key
    return index;
}

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

int HashMap_get(HashMap*map, int key, HashMapItemData* data) {
    assert(map!=NULL,"Cannot perform operation on NULL HashMap. Initialize first.");
    int index=_HashMap_findIndex(map,key);
    if (index==-1) {
        return MAP_MISSING;
    }
    if (data!=NULL) *data=map->items[index].data;
    return MAP_OK;
}

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

void _HashMap_free(HashMap* map) {
    assert(map!=NULL,"Cannot perform operation on NULL HashMap. Initialize first.");
    free(map->items);
    free(map);
}

void HashMap_clear(HashMap* map) {
    assert(map!=NULL,"Cannot perform operation on NULL HashMap. Initialize first.");
    map->size=0;
    map->lastItem=-2;
    int i;
    memset(map->items,0,sizeof(HashMapItem)*map->allocatedSize);
}

int HashMap_size(HashMap* map) {
    assert(map!=NULL,"Cannot perform operation on NULL HashMap. Initialize first.");
    return map->size;
}

int HashMap_allocatedSize(HashMap* map) {
    assert(map!=NULL,"Cannot perform operation on NULL HashMap. Initialize first.");
    return map->allocatedSize;
}

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

int HashMapIterator_done(HashMapIterator* iter) {
    assert(iter!=NULL,"Cannot perform operation on NULL iterator. Initialize first.");
    return (iter->toGo==0);
}

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
*/