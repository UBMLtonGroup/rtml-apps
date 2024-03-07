#! /bin/bash

# Runs CDx with Sun's RTS Real-Time Java, using the NOI workload which is
# read from a binary file into a buffer prior to benchmarking. Runs with
# SPECjvm98 benchmark JAVAC on the background. Uses RTSJ threads and Java
# memory (RTGC). 

# For benchmarking with RTSJ scoped memory, please use "rtsj_memory.jar"
# instead of "java_memory.jar" for RTSJ and specify sizes for the scopes.

# Please modify the directory to SPEC JVM 98 benchmarks below.

JARS=dist/lib
SPECDIR=/home/kalibera/SPECjvm98

if [ ! -r spec ] ; then
	ln -s ${SPECDIR}/spec
fi
                                
if [ ! -r props ] ; then
	ln -s ${SPECDIR}/props
fi

/home/sunrts/bin/java -server -d32 -cp $SPECDIR:$JARS/common_realtime.jar:$JARS/utils.jar:$JARS/java_memory.jar:$JARS/rtsj_threads.jar:$JARS/detector.jar:$JARS/binary_dump_reader.jar:$JARS/main.jar \
 	-Xms2000m -Xmx2000m \
 	-XX:RTGCCriticalReservedBytes=600m -XX:RTGCCriticalPriority=15 \
 	heap/Main input/noi.bin \
 	MAX_FRAMES 20000 DETECTOR_PERIOD 4 SIMULATOR_PRIORITY 1 \
 	DETECTOR_PRIORITY 20 BUFFER_FRAMES 20000 \
 	PRESIMULATE \
 	DETECTOR_STARTUP_OFFSET_MILLIS 1000 \
 	USE_SPEC_NOISE \
 	SPEC_NOISE_ARGS "-a -b -s100 -m20 -M20 -t _213_javac" \
 	DETECTOR_NOISE DETECTOR_NOISE_ALLOCATE_POINTERS 20 DETECTOR_NOISE_VARIABLE_ALLOCATION_SIZE \
 	 $*

