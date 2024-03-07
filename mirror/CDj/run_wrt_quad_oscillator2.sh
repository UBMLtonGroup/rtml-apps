#! /bin/bash

#
# Runs CDx with IBM WebSphere Real-Time Java, the QUAD_OSCILLATOR2 workload. 
# Thread and memory style can be specified on command line
#  
#  run_wrt_quad_oscillator2 memory threads
#	memory is "rtsj" or "java"
#	threads is "rtsj" or "java"
#  
#


MEMORY=rtsj
THREADS=rtsj
JARS=dist/lib

if [ "X$1" != X ] ; then
	MEMORY=$1
	shift
	if [ "X$1" != X ] ; then
		THREADS="$1"
		shift
	fi
fi

echo "Running with $THREADS threads and $MEMORY memory"

# Examples of VM useful parameters 
#
#  	-Xgc:scopedMemoryMaximumSize=512m -Xgc:immortalMemorySize=512m \
#	-Xdump:java:events=throw,filter=java/lang/OutOfMemoryError \

/opt/ibm-wrt-i386-60/bin/java -Xrealtime -cp $JARS/common_realtime.jar:$JARS/utils.jar:$JARS/${MEMORY}_memory.jar:$JARS/${THREADS}_threads.jar:$JARS/detector.jar \
 	-Xms1024m -Xmx1024m -Xdisableexplicitgc \
 	-Xgc:scopedMemoryMaximumSize=512m -Xgc:immortalMemorySize=512m -Xnojit -Xaot \
 	heap/Main input/quad_oscillator2 \
 	MAX_FRAMES 5000 PERSISTENT_DETECTOR_SCOPE_SIZE 50000000 TRANSIENT_DETECTOR_SCOPE_SIZE 10000000 \ 
 	DETECTOR_PERIOD 10 DETECTOR_PRIORITY 20 SIMULATOR_FPS 100 \
 	PRESIMULATE BUFFER_FRAMES 5001 $*


