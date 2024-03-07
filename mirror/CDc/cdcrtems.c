
#ifdef RTEMS
  // Include the RTEMS board specification
  #include <bsp.h>
  #include <rtems/untar.h>
  #include <rtems.h>
#else
  #include <string.h>
#endif  

#include <stdio.h>
#include <stdlib.h>

// Include the CDc code
#include "Constants.h"
#include "config.h"
#include "filesystem.h"
#include "utils.h"
#include "rapita.h"

// -------------------------------------------------------------------------------------------------
// Global Variables
// -------------------------------------------------------------------------------------------------

char* program_name;
char* input_file;
extern int _ri;

// -------------------------------------------------------------------------------------------------
// Supporting functions
// -------------------------------------------------------------------------------------------------
inline char* copyString(char* from) {
    char* result=(char*)checkedMalloc(strlen(from)+1);
    return strcpy(result,from);
}

// -------------------------------------------------------------------------------------------------
/** \brief Displays application help.
 * usage -- tell the user how to use this program and   *
 *              exit                                    *
 */
void usage() {
    fprintf(stderr, "Usage is %s air_traffic.bin [options]\n"
        "  PERIOD <num>             Period of the detector in milliseconds.\n"
        "  DETECTOR_PRIORITY <num>  Priority of the detector thread.\n"
        "  MAX_FRAMES <num>         Number of frames to process.\n"
        "  BUFFER_FRAMES <num>      Size of internal frame buffer. \n"
        "                           Must be at least MAX_FRAMES.\n"
        "  DEBUG_DETECTOR           Turns on some debugging messages in \n"
        "                           collision detection.\n"
        "  DUMP_RECEIVED_FRAMES     For debugging only. Dumps frames received\n"
        "                           by detector.\n"
        "  DUMP_SENT_FRAMES         For debugging only. Dumps frames sent to\n"
        "                           frame buffer at initialization.\n"
        "  REDUCER_GRID_SIZE        Size of the grid used by the reducer.\n"
        "\n",

            program_name);
    exit(0);
}


// -------------------------------------------------------------------------------------------------
// Clock
// -------------------------------------------------------------------------------------------------
#ifdef RTEMS

inline cdc_clock clock_fromTimespec(struct timespec from) {
    // ugly patch to be on NS as java is
    return (from.tv_nsec/1000)+(from.tv_sec*1000000);
}

struct timespec _clk;
inline cdc_clock clock_now() {
    rtems_clock_get_uptime(&_clk); // FIXME: Is this the same precision as clock_gettime(CLOCK_REALTIME,&_clk) ?
    return clock_fromTimespec(_clk);
}

#else

inline cdc_clock clock_fromTimeval(struct timeval from) {
    return (from.tv_usec + from.tv_sec * 1000000);
}

struct timeval _clk;
inline cdc_clock clock_now() {
    gettimeofday(&_clk, NULL);
    return clock_fromTimeval(_clk);
}

#endif

cdc_clock clock_roundUp(cdc_clock from) {
    if (from%1000!=0) from+=(from%1000); // round-up to millisecond
    if (DETECTOR_PERIOD>0) {
        from=((from+DETECTOR_PERIOD*1000-1)/(DETECTOR_PERIOD*1000))*(DETECTOR_PERIOD*1000);
    }
    return from;
}

int clock_roundUpTicks() {
    cdc_clock c=clock_now();
    c=(500000-c%500000)/1000;
    return c;
}



// -------------------------------------------------------------------------------------------------
// CDc Arguments Generator
// -------------------------------------------------------------------------------------------------
/* Arguments can be either hardcoded, or stdin ones in which they are read from standard input,
 * or a command-line ones.
 */

char **effectiveArgv;
int effectiveArgc;
int _argcc=0;

#ifndef STDIN_ARGUMENTS
char* getNextArgument() {
    if (_argcc>=effectiveArgc) {
        return NULL;
    }
    return effectiveArgv[_argcc++];
}
#endif

#ifdef HARDCODED_ARGUMENTS

char* defaultArgv[] ={ CDC_PROGRAM_NAME, "frames.bin", "MAX_FRAMES", "1000", "PERIOD", "250", "REDUCER_GRID_SIZE", "10" };
int defaultArgc = 8;

#elif STDIN_ARGUMENTS

/** \brief Reads new argument from the standard input and returns it.
 *
 * The result is owned by the function. If no other argument is present, returns NULL. First call
 * initializes the remote connection - awaits the handshake, then reads number of parameters and
 * returns the first parameter which is not read from the input, but is always "cdcrtems".
 */
#define CMD_BUFFER_LEN 4096
char _buffer[CMD_BUFFER_LEN];
int  _argc=-1;
//int  _argcc=0; // counter
int  _pos;
char* getNextArgument() {
    // Not initialized, perform handshake and
    if (_argc==-1) {
        printf("CLIENT_READY\n"); // was OVM something ready...
        while (1) {
            debug("Awaiting remote handshake");
            fgets(_buffer, CMD_BUFFER_LEN,stdin);
            if (!strncmp(_buffer,"CMDLINE-FOLLOWS",15)) {
                // ok handshake done, now read number of parameters
                debug("Number of arguments:");
                fgets(_buffer,CMD_BUFFER_LEN,stdin);
                _argc=atoi(_buffer);
                _argcc=0;
                return CDC_PROGRAM_NAME; // return program name, which in our case is cdc-rtems
            }
        }
        debug("Enter CMDLINE-FOLLOWS to continue");
    } else {
        // read a single argument and return it, or return NULL if no arguments are present
        if (_argcc>=_argc) return NULL;
        debug("Enter argument:");
        fgets(_buffer,CMD_BUFFER_LEN,stdin);
        if (strlen(_buffer)>1) {
            _buffer[strlen(_buffer)-1]=0;
            _argcc++;
        } else {
            fatalError("Argument not finished with a new line.");
        }
        return _buffer;
    }
}
#endif

// -------------------------------------------------------------------------------------------------
char *getNextRequiredArgument() {
    char *arg = getNextArgument();

    if (!arg) {
        fprintf(stderr, "Too few command line arguments.\n");
        usage();
    }

    return arg;
}

// -------------------------------------------------------------------------------------------------
/** \brief Incremental parser of the command line arguments.
 *
 * Uses the getNextArgument() as a generator. Parses all arguments given by this function in the
 * same way as CDc old parse function does.
 */
void parseIncremental() {
    char* arg;
    while ((arg=getNextArgument())!=NULL) {
        if (strcmp("PERIOD", arg) == 0) {
            DETECTOR_PERIOD = atol(getNextRequiredArgument());
            // Rescale to RTEMS ticks
            // CHECK This is not 100% accurate if the number of ticks per millisecond is not
            // an integer, but I believe this would be (a) very rare and (b) imprecise even with
            // floating points as the detector period is long.
            DETECTOR_PERIOD = DETECTOR_PERIOD;
        } else if (strcmp("DETECTOR_PRIORITY", arg) == 0) {
            DETECTOR_PRIORITY = atoi(getNextRequiredArgument());
        } else if (strcmp("MAX_FRAMES", arg) == 0) {
            MAX_FRAMES = atoi(getNextRequiredArgument());
        } else if (strcmp("BUFFER_FRAMES", arg) == 0) {
            BUFFER_FRAMES = atoi(getNextRequiredArgument());
        } else if (strcmp("REDUCER_GRID_SIZE", arg)==0) {
            GOOD_VOXEL_SIZE = atof(getNextRequiredArgument());
        } else if (strcmp("DEBUG_DETECTOR", arg) == 0) {
            DEBUG_DETECTOR = 1;
        } else if (strcmp("DUMP_RECEIVED_FRAMES", arg) == 0) {
            DUMP_RECEIVED_FRAMES = 1;
        } else if (strcmp("DUMP_SENT_FRAMES", arg) == 0) {
            DUMP_SENT_FRAMES = 1;
        } else usage(); // Wrong argument
    }

    if (MAX_FRAMES > BUFFER_FRAMES) {
        fatalError("MAX_FRAMES is larger than BUFFER_FRAMES");
    }
}

// -------------------------------------------------------------------------------------------------
/** Dumps some of the runtime arguments of the detector.
 */
void dump_parameters(void) {
  fprintf(stderr, "Execution parameters: \n"
    " PERIOD = %d\n DETECTOR_PRIORITY = %d\n MAX_FRAMES = %d\n"
    " BUFFER_FRAMES = %d\n DEBUG_DETECTOR = %d\n DUMP_RECEIVED_FRAMES = %d\n"
    " DUMP_SENT_FRAMES = %d\n REDUCER_GRID_SIZE = %f\n\n",
        DETECTOR_PERIOD, DETECTOR_PRIORITY, MAX_FRAMES, BUFFER_FRAMES,
        DEBUG_DETECTOR, DUMP_RECEIVED_FRAMES, DUMP_SENT_FRAMES,GOOD_VOXEL_SIZE);
}

// -------------------------------------------------------------------------------------------------
// CDc Application
// -------------------------------------------------------------------------------------------------

void the_main(void) {
    benchmarkRapita(rapita_initialize());
    benchmarkRapita(rapita_set(RAPITA_BENCHMARK));
    #ifdef RTEMS
    // load the filesystem at first
    load_filesystem_image();
    #endif
    debug("CDc : initializing");
    // Set the program name and input file
    program_name = copyString(getNextArgument());
    char *_input_file = getNextArgument();
    if (!_input_file) {
        fprintf(stderr, "Too few command line arguments: missing file with air traffic dump.\n");
        exit(1);
    }
    input_file = copyString(_input_file);
    // parse input arguments
    parseIncremental();
    dump_parameters();
    // printf("Running with java threads and java memory\n"); not so much
    debug("CDc : initialized, starting...");
    IMMORTALENTRY_run();
    debug("CDc : reading input dump and then running detector...");
    /* read input dump */
    generate(input_file);
    debug("CDc : cleaning up...");
    /* clean-up */
    DETECTOR_finish();
    free(program_name);
    free(input_file);
    // ok, we are done
    debug("CDc : iteration done.");
    benchmarkRapita(rapita_set(RAPITA_DONE));
    debugp("Total rapita marks %u",_ri);
}

// -------------------------------------------------------------------------------------------------
/** \brief Main application task.
 * 
 * Loads the workload and executes the detector.
 */

#ifdef RTEMS


rtems_task cdc_task_main(rtems_task_argument ignored) {

    #ifdef HARDCODED_ARGUMENTS
    effectiveArgc = defaultArgc;
    effectiveArgv = defaultArgv;
    #else
    #ifndef STDIN_ARGUMENTS
    error("RTEMS cannot use standard command line arguments.")
    #endif
    #endif
    the_main();
    exit(0);
}

// The CDc main task
rtems_id cdc_task;
/** \brief RTEMS Initialization task.
 *
 * Initializes the system, starts the main task and shuts itself down.
 */
rtems_task Init(rtems_task_argument ignored) {
    // create the task
    if (rtems_task_create(
            rtems_build_name('C','D','c',' '),
            DETECTOR_PRIORITY,
            RTEMS_MINIMUM_STACK_SIZE,
            RTEMS_NO_PREEMPT,
            (RTEMS_LOCAL | RTEMS_FLOATING_POINT),
            &cdc_task)!=RTEMS_SUCCESSFUL) {
        fatalError("Unable to create the detector task...");
    }
    // start the task
    if (rtems_task_start(cdc_task,cdc_task_main,0)!=RTEMS_SUCCESSFUL) {
        fatalError("Unable to start the detector task...");
    }
    // we are now finished and should kill ourselves
    if (rtems_task_delete(RTEMS_SELF)!=RTEMS_SUCCESSFUL) {
        fatalError("Unable to delete the initialization task...");
    }
    exit(1);
}

// -------------------------------------------------------------------------------------------------
// RTEMS Configuration variables
// -------------------------------------------------------------------------------------------------
// We need the configuration here so that we might adjust the command line arguments according to
// current RTEMS settings
#define CONFIGURE_APPLICATION_NEEDS_CLOCK_DRIVER
#define CONFIGURE_APPLICATION_NEEDS_CONSOLE_DRIVER
#define CONFIGURE_RTEMS_INIT_TASKS_TABLE
#define CONFIGURE_USE_IMFS_AS_BASE_FILESYSTEM
#define CONFIGURE_MICROSECONDS_PER_TICK             1000
#define CONFIGURE_MAXIMUM_TASKS                     2
#define CONFIGURE_MAXIMUM_SEMAPHORES                5
#define CONFIGURE_MAXIMUM_PERIODS                   1
#define CONFIGURE_LIBIO_MAXIMUM_FILE_DESCRIPTORS    6
#define CONFIGURE_INIT_TASK_ATTRIBUTES              RTEMS_LOCAL
#define CONFIGURE_INIT_TASK_INITIAL_MODES          (RTEMS_PREEMPT | \
                                                    RTEMS_NO_TIMESLICE | \
                                                    RTEMS_NO_ASR | \
                                                    RTEMS_INTERRUPT_LEVEL(0))
#define CONFIGURE_INIT

#include <rtems/confdefs.h>

#else

int main(int argc, char **argv) {

    #ifdef HARDCODED_ARGUMENTS
    effectiveArgc = defaultArgc;
    effectiveArgv = defaultArgv;
    #else
    #ifndef STDIN_ARGUMENTS
    effectiveArgc = argc;
    effectiveArgv = argv;
    #endif
    #endif
    the_main();
    exit(0);
}

#endif 
