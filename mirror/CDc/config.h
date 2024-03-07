
#ifndef _CONFIG_H
#define _CONFIG_H


// -------------------------------------------------------------------------------------------------
// Typedefs and constants
// -------------------------------------------------------------------------------------------------

// Type used for clock
#define cdc_clock long long

// Executable name
#define CDC_PROGRAM_NAME "cdc"

// -------------------------------------------------------------------------------------------------
// Configuration Macros
// -------------------------------------------------------------------------------------------------

// Generate frames and don't use the pregenerated ones
#define FRAME_ON_THE_GO

// Rounds up the detector
#define CDC_DETECTOR_ROUNDUP

// Enable benchmarking
#define BENCHMARK_RTEMS

// Enable RAPITA benchmarking, see rapita.h
#define BENCHMARK_RAPITA_

// Enables the old order (Java-like) of the condition in VoxelHashRecursive, see TODO for
// further details
#define OLD_ORDER_

// Use command command line for arguments (if not defined, a default arguments defined in cdcrtems.c
// will be used.
// #define STDIN_ARGUMENTS

// Use for arguments hard-coded in cdcrtems.c
//#define HARDCODED_ARGUMENTS

// For NO_RTEMS, you can use the POSIX style command line arguments
// uncomment both STDIN_ARGUMENT and HARDCODED_ARGUMENTS for that

// Use to run in a POSIX system (Linux,...)
// #define NO_RTEMS


// Enables debug and error reporting & handling
#define DEBUG_

// -------------------------------------------------------------------------------------------------
// In-code macro shortcuts
// -------------------------------------------------------------------------------------------------
#ifdef DEBUG
    #define error(msg) { fprintf(stderr,msg); fprintf(stderr,"\n"); }
    #define fatalError(msg) { fprintf(stderr,msg); fprintf(stderr,"\nEXITTING DUE TO FATAL ERROR\n"); exit(1); }
    #define debug(msg) { printf(msg); printf("\n"); }
    #define debugp(msg,par) { printf(msg,par); printf("\n"); }
#else
    #define error(msg) // ERROR: msg
    #define fatalError(msg) // FATAL ERROR: msg
    #define debug(msg) // msg
    #define debugp(msg,par) /* printf(msg,par); printf("\n"); */
#endif

#ifdef CDC_DETECTOR_ROUNDUP
    #define roundUp(x) x
#else
    #define roundUp(x) /* x */
#endif

#ifdef BENCHMARK_RTEMS
    #define benchmarkRtems(x) x
#else
    #define benchmarkRtems(x) /* x */
#endif

// My assert macro for testing, keep it here for the time being please (PETA)
#define assert(cond,msg) if (!(cond)) { debug(msg); exit(1);}



#endif
