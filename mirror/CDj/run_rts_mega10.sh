#! /bin/bash

#
# Runs CDx with Sun RTS Real-Time Java, the MEGA10 workload. 
# Thread and memory style can be specified on command line
#  
#  run_rts_mega10 memory threads
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

# Use options like these to increase Sun's RTS memory areas
# 	-XX:ScopedSize=128m -XX:ImmortalSize=128m 

/home/sunrts/bin/java -cp $JARS/common_realtime.jar:$JARS/utils.jar:$JARS/${MEMORY}_memory.jar:$JARS/${THREADS}_threads.jar:$JARS/detector.jar:$JARS/simulator.jar:$JARS/main.jar  \
 	-Xms50m -Xmx50m  \
 	heap/Main input/mega10 \
 	MAX_FRAMES 400 PERSISTENT_DETECTOR_SCOPE_SIZE 500000 TRANSIENT_DETECTOR_SCOPE_SIZE 600000 \
 	DETECTOR_PERIOD 5 SIMULATOR_FPS 200 DETECTOR_PRIORITY 30 $*

