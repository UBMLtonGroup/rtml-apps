/*
 * Simulator.c
 *
 *  Created on: Jul 25, 2009
 *      Author: ghaitho
 */

#include "Simulator.h"
#include "Detector.h"
#include "Constants.h"
#include "FrameBuffer.h"

#include "config.h"
#include "utils.h"
#include "rapita.h"

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

#ifdef USE_PTHREADS
#include <pthread.h>
#endif

#ifdef CDC_LITTLE_ENDIAN
#define fread_bigendiandata freadswap
#else
#define fread_bigendiandata freadnoswap
#endif

// #define DEBUG_SIMULATOR

void freadnoswap(void* ptr, int size, int cnt, FILE* f) {
    if (fread(ptr, size, cnt, f) != cnt) {
        fprintf(stderr, "Error reading input file at %s:%d\n", __FILE__, __LINE__);
        exit(1);
    }
}

void freadswap(void* ptr, int size, int cnt, FILE* f) {

    int i = 0, j = 0;
    char * p = checkedMalloc(size * cnt);

    if (!p) {
        fprintf(stderr, "Out of memory in checkedMalloc at %s:%d.\n", __FILE__, __LINE__);
        exit(1);
    }

    if (fread(p, size, cnt, f) != cnt) {
        fprintf(stderr, "Error reading input file at %s:%d\n", __FILE__, __LINE__);
        exit(1);
    }

    for (i = 0; i < cnt; i++) {
        for (j = 0; j < size; j++) {
            *(char *) (ptr + i * size + j) = *(char *) (p + (i * size + size - j - 1));
        }
    }
    free(p);
}

void generate(char* inputFile) {
    #ifdef FRAME_ON_THE_GO
    FRAMEBUFFER_init();
    #else
    FILE *fp;
    int32_t nframes, nplanes, callsigns_length;
    int frameCounter, i, j;
    float *positions_temp;
    int32_t* lengths_temp;
    char* callsigns_temp;
    benchmarkRapita(rapita_set(RAPITA_GENERATOR));
    fprintf(stderr, "Cdc simulator : opening input file %s with frames dump.\n",
        inputFile);  
   
    /* open the file */
    fp = fopen(inputFile, "r");
    if (!fp) {
        fprintf(stderr, "Cannot open file with frames binary dump.\n");
        exit(1);
    }

    fprintf(stderr, "Cdc simulator : reading number of frames from frames dump.\n");  
    fread_bigendiandata(&nframes, 4, 1, fp);

    if (nframes < MAX_FRAMES) {
        fprintf(stderr, "Not enough frames in binary dump.\n"
            "The dump has %d frames, but MAX_FRAMES is %d\n",
            nframes, MAX_FRAMES);
        exit(1);
    }

    fprintf(stderr, "Binary input has %d frames.\n", nframes);
    frameCounter = 0;

    FRAMEBUFFER_init();

    for (frameCounter=0; frameCounter < MAX_FRAMES ; frameCounter++) {
        fread_bigendiandata(&nplanes, 4, 1, fp);

#ifdef DEBUG_SIMULATOR
        fprintf(stderr, "Frame no %d has %d planes.\n", frameCounter, nplanes);
#endif

        positions_temp = (float *) checkedMalloc(sizeof (float) * nplanes * 3);
        for (i = 0; i < nplanes; i++) {
            fread_bigendiandata(&positions_temp[3 * i], 4, 3, fp);
        }

        lengths_temp = (int32_t *) checkedMalloc(sizeof (int32_t) * nplanes);
        for (i = 0; i < nplanes; i++) {
            fread_bigendiandata(&lengths_temp[i], 4, 1, fp);
        }

        fread_bigendiandata(&callsigns_length, 4, 1, fp);
        callsigns_temp = (char *) checkedMalloc(sizeof (char) * callsigns_length);
        for (j = 0; j < callsigns_length; j++)
            freadnoswap(&callsigns_temp[j], 1, 1, fp);

        FRAMEBUFFER_putFrame(nplanes, positions_temp, lengths_temp, callsigns_temp);

        free(positions_temp);
        free(lengths_temp);
        free(callsigns_temp);
    }

    /* close the file */
    fclose(fp);
    #endif

    // Notify Detector there is a new frame in the buffer
    // TODO: the Java version notifies using threads, maybe need to do something similar?!
    benchmarkRapita(rapita_set(RAPITA_DONE+RAPITA_GENERATOR));
    #ifdef USE_PTHREADS
    pthread_t threadDetector;
    int iret;
    iret=pthread_create(&threadDetector,NULL,DETECTOR_run,NULL);
    pthread_join(threadDetector,NULL);
    #else
    DETECTOR_run();
    #endif

    FRAMEBUFFER_destroy();

    return;
}
