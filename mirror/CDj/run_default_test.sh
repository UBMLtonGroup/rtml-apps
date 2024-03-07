#! /bin/bash

# Runs CDx with the TEST workload and default (plain) Java. The simulator
# runs concurrently with the detector.

JARS=dist/lib

java -cp $JARS/common_realtime.jar:$JARS/utils.jar:$JARS/java_memory.jar:$JARS/java_threads.jar:$JARS/detector.jar:$JARS/simulator.jar:$JARS/main.jar \
 	-Xms40m -Xmx40m  \
 	heap/Main input/test \
 	MAX_FRAMES 400 DETECTOR_PERIOD 50 SIMULATOR_FPS 20 DETECTOR_PRIORITY 9
 	
 