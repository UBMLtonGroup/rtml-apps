/*
 * FrameBuffer.c
 *
 *  Created on: Jul 25, 2009
 *      Author: ghaitho
 */

#include "RawFrame.h"
#include "FrameBuffer.h"
#include "Constants.h"
#include "helper.h"
#include "config.h"
#include "utils.h"

#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>

RawFrame * RawFrames;

#ifdef FRAME_ON_THE_GO
RawFrame onTheGoFrame;
double t;
int i;
#endif

int FRAMEBUFFER_frameno = 0, FRAMEBUFFER_droppedFrames = 0;

void FRAMEBUFFER_init() {
    #ifdef FRAME_ON_THE_GO
    int k;
    for (k=0;k<NUMBER_OF_PLANES;k++) {
        onTheGoFrame.lengths[k]=6;
        onTheGoFrame.callsigns[6*k]=112;
        onTheGoFrame.callsigns[6*k+1]=108;
        onTheGoFrame.callsigns[6*k+2]=97;
        onTheGoFrame.callsigns[6*k+3]=110;
        onTheGoFrame.callsigns[6*k+4]=101;
        onTheGoFrame.callsigns[6*k+5]=48+k;
    }

    /**onTheGoFrame.lengths[1]=6;
    onTheGoFrame.lengths[2]=6;
    onTheGoFrame.lengths[3]=7;
    onTheGoFrame.lengths[4]=7;
    onTheGoFrame.lengths[5]=7; */

    onTheGoFrame.planeCnt=NUMBER_OF_PLANES;
    /*
    onTheGoFrame.callsigns[0]=112;
    onTheGoFrame.callsigns[1]=108;
    onTheGoFrame.callsigns[2]=97;
    onTheGoFrame.callsigns[3]=110;
    onTheGoFrame.callsigns[4]=101;
    onTheGoFrame.callsigns[5]=48;
    onTheGoFrame.callsigns[6]=112;
    onTheGoFrame.callsigns[7]=108;
    onTheGoFrame.callsigns[8]=97;
    onTheGoFrame.callsigns[9]=110;
    onTheGoFrame.callsigns[10]=101;
    onTheGoFrame.callsigns[11]=49;
    onTheGoFrame.callsigns[12]=112;
    onTheGoFrame.callsigns[13]=108;
    onTheGoFrame.callsigns[14]=97;
    onTheGoFrame.callsigns[15]=110;
    onTheGoFrame.callsigns[16]=101;
    onTheGoFrame.callsigns[17]=50;
    onTheGoFrame.callsigns[18]=112;
    onTheGoFrame.callsigns[19]=108;
    onTheGoFrame.callsigns[20]=97;
    onTheGoFrame.callsigns[21]=110;
    onTheGoFrame.callsigns[22]=101;
    onTheGoFrame.callsigns[23]=52;
    onTheGoFrame.callsigns[24]=48;
    onTheGoFrame.callsigns[25]=112;
    onTheGoFrame.callsigns[26]=108;
    onTheGoFrame.callsigns[27]=97;
    onTheGoFrame.callsigns[28]=110;
    onTheGoFrame.callsigns[29]=101;
    onTheGoFrame.callsigns[30]=52;
    onTheGoFrame.callsigns[31]=49;
    onTheGoFrame.callsigns[32]=112;
    onTheGoFrame.callsigns[33]=108;
    onTheGoFrame.callsigns[34]=97;
    onTheGoFrame.callsigns[35]=110;
    onTheGoFrame.callsigns[36]=101;
    onTheGoFrame.callsigns[37]=52;
    onTheGoFrame.callsigns[38]=50;
    onTheGoFrame.positions[1]=100;
    onTheGoFrame.positions[2]=5;
    onTheGoFrame.positions[4]=100;
    onTheGoFrame.positions[5]=5;
    onTheGoFrame.positions[7]=100;
    onTheGoFrame.positions[8]=5;
    onTheGoFrame.positions[10]=100;
    onTheGoFrame.positions[11]=5;
    onTheGoFrame.positions[13]=100;
    onTheGoFrame.positions[14]=5;
    onTheGoFrame.positions[16]=100;
    onTheGoFrame.positions[17]=5;
    */
    t=0;
    i=0;
    #else
    RawFrames = (RawFrame *) checkedMalloc(sizeof (RawFrame) * BUFFER_FRAMES);
    if (RawFrames==NULL) {
        fatalError("Insufficient memory to allocate frame buffer.");
    }
    #endif
}

void FRAMEBUFFER_destroy() {
    free(RawFrames);
}

void FRAMEBUFFER_putFrameInternal(int32_t length, float * positions_, int32_t * lengths_, char * callsigns_) {
    if ((FRAMEBUFFER_last + 1) % BUFFER_FRAMES == FRAMEBUFFER_first) {
        FRAMEBUFFER_droppedFrames++;
        return;
    }
    RAWFRAME_copy(&RawFrames[FRAMEBUFFER_last], length, lengths_, callsigns_, positions_);
    FRAMEBUFFER_last = (FRAMEBUFFER_last + 1) % BUFFER_FRAMES;
}

void FRAMEBUFFER_putFrame(int32_t length, float* positions_, int32_t * lengths_, char * callsigns_) {

    if (/*SYNCHRONOUS_DETECTOR ||*/ DUMP_SENT_FRAMES) {
        printFrame(FRAMEBUFFER_frameno, "S-FRAME", length, positions_, lengths_, callsigns_);
        FRAMEBUFFER_frameno++;
    }
    FRAMEBUFFER_putFrameInternal(length, positions_, lengths_, callsigns_);
}

RawFrame * FRAMEBUFFER_getFrame() {
    #ifdef FRAME_ON_THE_GO
    if (i==MAX_FRAMES) return NULL;
    int k;
    //printf("Planes: %u\n",NUMBER_OF_PLANES);
    for (k=0;k<NUMBER_OF_PLANES/2;k++) {
        onTheGoFrame.positions[k*3]=(float)(100*cos(t)+500+50*k);
        onTheGoFrame.positions[k*3+1]=100.0f;
        onTheGoFrame.positions[k*3+2]=5.0f;
        onTheGoFrame.positions[(NUMBER_OF_PLANES/2)*3+k*3]=(float)(100*sin(t)+500+50*k);
        onTheGoFrame.positions[(NUMBER_OF_PLANES/2)*3+k*3+1]=100.0f;
        onTheGoFrame.positions[(NUMBER_OF_PLANES/2)*3+k*3+2]=5.0f;
    }
    // increase the time
    t=t+0.25;
    i++;
    return &onTheGoFrame;
    #else
    if (FRAMEBUFFER_last == FRAMEBUFFER_first) {
        return 0;
    } else {
        int f = FRAMEBUFFER_first;
        FRAMEBUFFER_first = (FRAMEBUFFER_first + 1) % BUFFER_FRAMES;
        return &RawFrames[f]; // NOTE: if the simulator could run between this and the previous line,
        // it could corrupt the present frame
    }
    #endif
}
