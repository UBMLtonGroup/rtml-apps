/*
 * helper.h
 *
 *  Created on: Jul 27, 2009
 *      Author: ghaitho
 */

#ifndef HELPER_H_
#define HELPER_H_

#include <stdint.h>

char * fixnum(char * dest, float f);
void printFrame(int32_t frameno, char * prefix, int32_t length, float* positions_, int32_t lengths_[], char callsigns_[]);

#endif /* HELPER_H_ */
