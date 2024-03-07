/* 
 * File:   rapita.h
 * Author: devel
 *
 * Created on October 18, 2009, 5:49 PM
 */

#ifndef _RAPITA_H
#define	_RAPITA_H

// FIXME Delete this in production release, I am using it in netbeans for proper syntax highlighting
#include "config.h"


#ifdef	__cplusplus
extern "C" {
#endif

// TODO Add comments, for the time being it is pretty self-explanatory
#define uint32 unsigned int
#define rapita_base 0x80000800

extern uint32* _rapita_output;
extern uint32* _rapita_mask;
extern uint32 _rapita_flag;

extern int _ri;

void rapita_initialize();

#define rapita_clear() *_rapita_output=0 & 0xffff

#define rapita_set(o) *_rapita_output=(o | _rapita_flag); _rapita_flag^= 0x8000; _ri++;

#define rapita_debug(msg) debug(msg)

#define rapita_setMask(m) (*_rapita_mask=m)

#ifdef BENCHMARK_RAPITA
    #define benchmarkRapita(x) x
#else
    #define benchmarkRapita(x) /* x */
#endif

// Rapita events

#define RAPITA_BENCHMARK 0x7fff
#define RAPITA_DONE 0x4000

#define RAPITA_GENERATOR 1
#define RAPITA_ROUNDUP 2
#define RAPITA_PERIOD 3
#define RAPITA_DETECTOR_STEP 4
#define RAPITA_SETFRAME 5
#define RAPITA_REDUCER_INIT 6
#define RAPITA_CREATE_MOTIONS 7
#define RAPITA_DETECTOR_CLEANUP 8
#define RAPITA_REDUCER 9
#define RAPITA_VOXELHASHING 10
#define RAPITA_VOXELHASH_DFS 11
#define RAPITA_ISINVOXEL 12
#define RAPITA_VOXELHASH_EXPANDING 13
#define RAPITA_DETERMINE_COLLISIONS 14
#define RAPITA_COLLISION_DUPLICATES 15
#define RAPITA_FIND_INTERSECTION 16

#ifdef	__cplusplus
}
#endif


#endif	/* _RAPITA_H */

