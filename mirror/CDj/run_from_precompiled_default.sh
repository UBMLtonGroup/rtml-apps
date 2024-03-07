#! /bin/bash

MEMORY=java
THREADS=java
JARS=dist/lib

java -cp $JARS/common_realtime.jar:$JARS/utils.jar:$JARS/${MEMORY}_memory.jar:$JARS/${THREADS}_threads.jar:$JARS/detector.jar:$JARS/precompiled_simulator.jar:$JARS/main.jar \
 	-Xms40m -Xmx40m  \
 	heap/Main unused \
 	MAX_FRAMES 400 \
 	DETECTOR_PERIOD 50 DETECTOR_PRIORITY 9
 	
