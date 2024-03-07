/*
 * Aircraft.h
 *
 *  Created on: Jul 29, 2009
 *      Author: ghaitho
 */

#ifndef AIRCRAFT_H_
#define AIRCRAFT_H_

typedef struct Aircraft {
    char * callsign; // zero terminated string
} Aircraft;

Aircraft * AIRCRAFT_init(Aircraft * ac, char * cs);
char * AIRCRAFT_toString(char * buf, Aircraft * ac);

#endif /* AIRCRAFT_H_ */
