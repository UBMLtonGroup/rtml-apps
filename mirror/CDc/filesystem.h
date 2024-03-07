/* 
 * File:   filesystem.h
 * Author: devel
 *
 * Created on October 14, 2009, 10:59 PM
 */

#ifndef _FILESYSTEM_H
#define	_FILESYSTEM_H

#include "config.h"
#ifdef RTEMS

#ifdef	__cplusplus
extern "C" {
#endif

    rtems_status_code load_filesystem_image();

#ifdef	__cplusplus
}
#endif
#endif /* NO_RTEMS */

#endif	/* _FILESYSTEM_H */

