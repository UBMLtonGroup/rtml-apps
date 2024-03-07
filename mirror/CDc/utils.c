
#include "utils.h"
#include <stdlib.h>
#include <stdio.h>

void *checkedMallocInternal(size_t size, const char *file, int line) {

  void *res = malloc(size);
  
  if (!res) {
    fprintf(stderr, "Malloc failed when trying to allocate %d bytes, at source line %s:%d.\n", size, file, line);

    for(;;) {
       size-=1024;
       if (size<=0) break;
       res = malloc(size);
       if (res != NULL) {
        free(res);
        fprintf(stderr, "Maximum allocation possible (stepping down by 1kB) was %d\n", size);
        break;
       }
    }
    exit(2);
  }  
  return res;
}
