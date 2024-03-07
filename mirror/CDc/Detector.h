/*
 * Detector.h
 *
 *  Created on: Jul 26, 2009
 *      Author: ghaitho
 */

#ifndef DETECTOR_H_
#define DETECTOR_H_

#include "config.h"

// -------------------------------------------------------------------------------------------------
// Benchmarking Statistics
// -------------------------------------------------------------------------------------------------
#ifdef BENCHMARK_RTEMS
// -------------------------------------------------------------------------------------------------
/** \brief Detector Statistical Information
 *
 * See Tomas' notes about the format. The names are pretty self-explanatory.
 *
 * TODO heap information is currently not supported in CDc, zeroes are outputted.
 */
struct DetectorStatEntry {
    cdc_clock time_before;
    cdc_clock time_afterReducer;
    cdc_clock time_after;
    int heap_free_before;
    int heap_free_after;
    int detected;
    int suspected;
} DetectorStatEntry;

// -------------------------------------------------------------------------------------------------
/** \brief Detector Release Statistical Information
 *
 * See DetectorStatEntry.
 */
struct ReleaseStatEntry {
    cdc_clock release_time;
    cdc_clock release_ideal;
    cdc_clock wait_time;
    int reported_misses;
} ReleaseStatEntry;
#endif


// -------------------------------------------------------------------------------------------------
// Translated functions, see documentation in .c
// -------------------------------------------------------------------------------------------------
void DETECTOR_init();
#ifdef USE_PTHREADS
void* DETECTOR_run(void* ignore);
#else
void DETECTOR_run();
#endif
int DETECTOR_step();
void DETECTOR_finish();

#endif /* DETECTOR_H_ */
