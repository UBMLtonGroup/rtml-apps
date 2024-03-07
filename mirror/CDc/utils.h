
#ifndef UTILS_H
#define UTILS_H

#include <stdlib.h>

void *checkedMallocInternal( size_t size, const char *file, int line );

//#define checkedMalloc(x) malloc(x)

#define checkedMalloc(x) checkedMallocInternal( x, __FILE__, __LINE__ )

#endif