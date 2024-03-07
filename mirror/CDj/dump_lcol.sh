#! /bin/bash

# Creates a binary representation of the COL workload (creates frames.bin).

JARS=dist/lib

java -cp $JARS/common_realtime.jar:$JARS/utils.jar:$JARS/java_memory.jar:$JARS/java_threads.jar:$JARS/detector.jar:$JARS/simulator.jar:$JARS/main.jar \
 	-Xms1500m -Xmx1500m  \
 	heap/Main input/lcol \
	MAX_FRAMES 200 DETECTOR_PERIOD 500 DETECTOR_PRIORITY 9 SIMULATOR_FPS 2 \
	BUFFER_FRAMES 200 \
 	PRESIMULATE \
	FRAMES_BINARY_DUMP \
	SIMULATE_ONLY \
	2>&1
