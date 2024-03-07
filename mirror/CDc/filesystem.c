
#ifdef RTEMS

#include <bsp.h>
#include <rtems/untar.h>
#include <stdio.h>
#include "filesystem.h"

extern char _binary_FilesystemImage_start;
extern char _binary_FilesystemImage_end;

rtems_status_code load_filesystem_image() {

    rtems_status_code status;

    void * embedded_start = &_binary_FilesystemImage_start;
    void * embedded_end = &_binary_FilesystemImage_end;
    size_t embedded_size = ((char *)embedded_end) - ((char *)embedded_start) + 1;


    fprintf(stderr,"about to load fs-image which is at 0x%x, it's size is %d\n",
      embedded_start, embedded_size
    );
    status = Untar_FromMemory( embedded_start, embedded_size );

    if (status) {
      fprintf(stderr, "Unpacking of the file system image has failed with code %d\n", status);
    } else {
      fprintf(stderr, "Filesystem loaded.\n");
    }

    return status;
}

#endif
