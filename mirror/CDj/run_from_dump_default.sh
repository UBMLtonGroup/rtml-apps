#! /bin/bash

MEMORY=java
THREADS=java
JARS=dist/lib

java -cp $JARS/common_realtime.jar:$JARS/utils.jar:$JARS/${MEMORY}_memory.jar:$JARS/${THREADS}_threads.jar:$JARS/detector.jar:$JARS/binary_dump_reader.jar:$JARS/main.jar \
 	-Xms40m -Xmx40m  \
 	heap/Main input/test.bin \
 	MAX_FRAMES 400 \
 	DETECTOR_PERIOD 50 DETECTOR_PRIORITY 9  \
	FRAMES_BINARY_DUMP DUMP_SENT_FRAMES
