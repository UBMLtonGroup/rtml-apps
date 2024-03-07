#! /bin/bash

# Runs CDx with Sun's RTS Real-Time Java, using the COL workload which is
# read from a binary file into a buffer prior to benchmarking. Runs with
# SPECjvm98 benchmark JAVAC on the background. Uses RTSJ threads and Java
# memory (RTGC). 

# For benchmarking with RTSJ scoped memory, please use "rtsj_memory.jar"
# instead of "java_memory.jar" for RTSJ and specify sizes for the scopes.

# Please modify the PATH to the SPEC benchmarks below.


JARS=dist/lib

/home/sunrts/bin/java -server -d32 -cp $JARS/common_realtime.jar:$JARS/utils.jar:$JARS/java_memory.jar:$JARS/rtsj_threads.jar:$JARS/detector.jar:$JARS/binary_dump_reader.jar:$JARS/main.jar \
 	-Xms300m -Xmx300m -XX:+PrintGC -XX:+RTGCPrintStatistics -Xloggc:rtsgc.out \
 	-XX:RTGCCriticalReservedBytes=50m -XX:RTGCCriticalPriority=15 \
 	heap/Main input/col.bin \
 	MAX_FRAMES 10000 DETECTOR_PERIOD 10 SIMULATOR_PRIORITY 1 \
 	DETECTOR_PRIORITY 20 BUFFER_FRAMES 10000 \
 	PRESIMULATE \
 	DETECTOR_STARTUP_OFFSET_MILLIS 1000 \
 	 $*

