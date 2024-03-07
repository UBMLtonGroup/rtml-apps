#! /bin/bash

# Creates a binary representation of the TEST workload (creates frames.bin).

JARS=dist/lib

java -cp $JARS/common_realtime.jar:$JARS/utils.jar:$JARS/java_memory.jar:$JARS/java_threads.jar:$JARS/detector.jar:$JARS/simulator.jar:$JARS/main.jar \
 	-Xms40m -Xmx40m  \
 	heap/Main input/test \
 	MAX_FRAMES 400 SIMULATOR_FPS 20 DETECTOR_PERIOD 50 DETECTOR_PRIORITY 9 \
	FRAMES_BINARY_DUMP DUMP_SENT_FRAMES SIMULATE_ONLY PRESIMULATE
