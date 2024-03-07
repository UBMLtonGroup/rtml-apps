/*
 * Detector.c
 *
 *  Created on: Jul 26, 2009
 *      Author: ghaitho
 */
#include "config.h"
#include "utils.h"
#include "Constants.h"
#include "Detector.h"
#include "TransientDetector.h"
#include "FrameBuffer.h"
#include <stdio.h>
#include "rapita.h"
#ifdef RTEMS
#include <rtems.h>
#endif

#include <stdlib.h>

#ifdef USE_PTHREADS
#include <pthread.h>
#include <unistd.h>
#include <sys/types.h>
#include <stdlib.h>
#include <errno.h>
#include <sys/param.h>
#include <time.h>
#endif


#ifdef BENCHMARK_RTEMS
struct DetectorStatEntry* detectorStats;
struct ReleaseStatEntry* releaseStats;
int statsCurrentFrame;

void outputDetectorStatistics();
#endif


// -------------------------------------------------------------------------------------------------
/** \brief Initializes the detector.
 *
 * If benchmarking is enabled, allocates space required for the benchmarking statistics.
 */
void DETECTOR_init() {
    // Allocate the memory for the results
    benchmarkRtems(
        detectorStats=(struct DetectorStatEntry*)checkedMalloc(sizeof(struct DetectorStatEntry)*MAX_FRAMES);
        releaseStats=(struct ReleaseStatEntry*)checkedMalloc(sizeof(struct ReleaseStatEntry)*MAX_FRAMES);
        statsCurrentFrame=0;
    )
    TRANSIENTDETECTOR_init();
}

// -------------------------------------------------------------------------------------------------
/** \brief Runs periodically the detector for each frame.
 *
 * Now also performs release benchmarking. Detector benchmarking is done elsewhere, see
 * TransientDetector.c for more information.
 */

#ifdef USE_PTHREADS
void* DETECTOR_run(void* ignore) {
#else
void DETECTOR_run() {
#endif
    debug("Detector invoked.");

    #ifdef CDC_PERIODIC_DETECTOR /* periodic mode */
    debug("  Starting in periodic mode");
    // Declarations
    rtems_name name=rtems_build_name('D','P','E','R');
    rtems_id   detectorPeriod;
    // Rescale the detector period to the specified number of ticks
    int tickPeriod=(DETECTOR_PERIOD*rtems_clock_get_ticks_per_second())/1000;
    debugp("Detector period rescaled to %u ticks",tickPeriod);
    // Create the period
    if (rtems_rate_monotonic_create(name,&detectorPeriod)!=RTEMS_SUCCESSFUL) {
        error("! Unable to register the detector period. Exitting");
        return;
    }
    #else
    debug("  Starting in loop mode");
    #endif
    // Initialize the benchmark
    benchmarkRtems(
        #ifdef CDC_PERIODIC_DETECTOR
        rtems_rate_monotonic_period_status status;
        #endif
        cdc_clock real_time=0;
        int missed=0;
    )
    #ifdef CDC_PERIODIC_DETECTOR
    benchmarkRapita(rapita_set(RAPITA_ROUNDUP));
    roundUp(rtems_task_wake_after(1));
    benchmarkRapita(rapita_set(RAPITA_DONE+RAPITA_ROUNDUP));
    #endif
    #ifdef USE_PTHREADS
    struct timespec period;
    DETECTOR_PERIOD=10;
    period.tv_sec=0;
    period.tv_nsec=1;
    struct timeval t;
    pthread_mutex_t cond_mutex=PTHREAD_MUTEX_INITIALIZER;
    pthread_cond_t cond=PTHREAD_COND_INITIALIZER;
    struct sched_param sp;
    pthread_mutex_lock(&cond_mutex);
    sp.sched_priority=sched_get_priority_max(SCHED_FIFO);
    pthread_setschedparam(pthread_self(), SCHED_FIFO, &sp);
    #endif
    while (1) {
        if (statsCurrentFrame>0) {
            releaseStats[statsCurrentFrame-1].wait_time=clock_now();
        }
        #ifdef USE_PTHREADS
        pthread_cond_timedwait(&cond,&cond_mutex,&period);
        gettimeofday(&t, NULL);
        period.tv_sec=t.tv_sec;
        period.tv_nsec=t.tv_usec*1000+DETECTOR_PERIOD*1000000;
        if (period.tv_nsec>1000000000) {
            period.tv_sec++;
            period.tv_nsec-=1000000000;
        }
        #endif
        #ifdef CDC_PERIODIC_DETECTOR
        // Wait for the period
        benchmarkRtems(missed=0);
        // Initiate the period
        if (rtems_rate_monotonic_period(detectorPeriod,RTEMS_PERIOD_STATUS)==RTEMS_TIMEOUT) {
            debug("Detector missed its deadline.");
            benchmarkRtems(missed=1);
#ifdef CDC_HARD_REALTIME
            error("! Exitting because of missed deadline..");
            return;
#endif
        }
        //benchmarkRapita(rapita_set(RAPITA_PERIOD));
        rtems_rate_monotonic_period(detectorPeriod,tickPeriod);
        //benchmarkRapita(rapita_set(RAPITA_DONE+RAPITA_PERIOD));
        #endif
        // get the time if benchmarking
        benchmarkRtems(real_time==clock_now();releaseStats[statsCurrentFrame].release_time=real_time;);
        // now check the period status
        // Execute the detector step (we are inside the period now)
        //benchmarkRapita(rapita_set(RAPITA_DETECTOR_STEP));
        if (!DETECTOR_step()) break;
        
        //benchmarkRapita(rapita_set(RAPITA_DONE+RAPITA_DETECTOR_STEP));
        benchmarkRtems(
            releaseStats[statsCurrentFrame].reported_misses=missed;
            statsCurrentFrame++; // move to next frame
        )
    }
    //clock_now();
    benchmarkRapita(rapita_set(RAPITA_DONE+RAPITA_DETECTOR_STEP));
    // Cleanup - delete the period
    #ifdef CDC_PERIODIC_DETECTOR
    if (rtems_rate_monotonic_delete(detectorPeriod)!=RTEMS_SUCCESSFUL) {
        error("! Unable to cleanup the detector period.");
        return;
    }
    #endif
    // now report the statistics if benchmarking
    benchmarkRtems(
        debug("Detector benchmark statistics follow:");
        outputDetectorStatistics();
    )

    debug("Detector done.");
}

// -------------------------------------------------------------------------------------------------
/** \brief Executes one iteration of the detector.
 *
 * I.e. analyzes one more frame. returns 0 if there are no frames left and 1 if successfull.
 */
int DETECTOR_step() {
    RawFrame* frm = FRAMEBUFFER_getFrame();
    if (!frm) return 0;
    // record the beginning time
    benchmarkRtems(detectorStats[statsCurrentFrame].time_before=clock_now());
    // perform the computation
    benchmarkRapita(rapita_set(RAPITA_SETFRAME));
    TRANSIENTDETECTOR_setFrame(frm);
    benchmarkRapita(rapita_set(RAPITA_DONE+RAPITA_SETFRAME));
    TRANSIENTDETECTOR_run();
    // record the end time and perform rest of operations
    benchmarkRtems(
        detectorStats[statsCurrentFrame].time_after=clock_now();
        detectorStats[statsCurrentFrame].heap_free_after=0;
        detectorStats[statsCurrentFrame].heap_free_before=0;
    )
    return 1;
}

// -------------------------------------------------------------------------------------------------
/** \brief Cleans up after the detector.
 *
 * Deallocated benchmarking statistics if benchmarking is enabled.
 */
void DETECTOR_finish() {
    // delete stats
    benchmarkRtems(
        free(detectorStats); detectorStats=NULL;
        free(releaseStats); releaseStats=NULL;
    )
    printf("Detector is finished, processed all frames.");
    TRANSIENTDETECTOR_destroy();
}

// -------------------------------------------------------------------------------------------------
// BENCHMARKING SUPPORT
// -------------------------------------------------------------------------------------------------
#ifdef BENCHMARK_RTEMS
/** \brief Displays the benchmarking statistics.
 *
 * Displays the detector and detector release statistics in the format used also by Tomas for the
 * OVM. Also computes the ideal time (this can be done offline I believe).
 */
void outputDetectorStatistics() {
    int detector_period_micros=DETECTOR_PERIOD*1000;
    int i=0;
    printf("!!!!!!\n");
    printf("cdc\n");
    printf(" \n");
    printf("make \n");
    printf("=====DETECTOR-STATS-START-BELOW====\n");
    for (i=0;i<statsCurrentFrame;i++) {
        printf("%llu %llu %d %d %d %d %llu %llu %llu %d\n",detectorStats[i].time_before*1000,
                                     detectorStats[i].time_after*1000,
                                     detectorStats[i].heap_free_before,
                                     detectorStats[i].heap_free_after,
                                     detectorStats[i].detected,
                                     detectorStats[i].suspected,
                                     (detectorStats[i].time_after-detectorStats[i].time_before),
                                     (detectorStats[i].time_afterReducer-detectorStats[i].time_before),
                                     (detectorStats[i].time_after-detectorStats[i].time_afterReducer),
                                     i
                );
    }
    printf("=====DETECTOR-STATS-END-ABOVE====\n");
    printf("=====DETECTOR-RELEASE-STATS-START-BELOW====\n");
    cdc_clock ideal_time=releaseStats[0].release_time;
    for (i=0;i<statsCurrentFrame;i++) {
        printf("%llu %llu %llu %d %d\n",(unsigned long long)(releaseStats[i].wait_time*1000),
                            releaseStats[i].release_time*1000,
                            //releaseStats[i].release_ideal,
                            ideal_time*1000,
                            releaseStats[i].reported_misses,i);
        ideal_time+=detector_period_micros;

    }
    printf("=====DETECTOR-RELEASE-STATS-END-ABOVE====\n");
}
#endif