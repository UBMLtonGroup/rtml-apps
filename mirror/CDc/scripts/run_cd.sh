#! /bin/bash

ARGS="lcol.bin MAX_FRAMES 100 PERIOD 500";
#socat -v EXEC:"./cd_runner.pl $ARGS" /dev/ttyUSB0,b38400,raw,echo=0

killall socat
killall minicom

socat -v EXEC:"./cd_runner.pl $ARGS" /dev/ttyUSB0,b38400,icanon=1,echo=0

for F in release.rin detector.rin simulator.rin ; do
  if [ -r $F ] ; then
    mv $F $F.raw
    tr -d '\r' < $F.raw > $F
  fi
done

exit

# on scale with the real board over serial/USB - debugging

ARGS="mono10.bin MAX_FRAMES 100 DUMP_SENT_FRAMES DUMP_RECEIVED_FRAMES DEBUG_DETECTOR";
#socat -v EXEC:"./cd_runner.pl $ARGS" /dev/ttyUSB0,b38400,raw,echo=0
socat -v EXEC:"./cd_runner.pl $ARGS" /dev/ttyUSB0,b38400,icanon=1,echo=0

exit

# on peleton from wedge (OVM)

ARGS="-period=500 -gc-aperiodic-scheduler -gc-thread-priority=15 -effective-memory-size=512m -gc-threshold=1024m heap.Main ./quad_oscillator2 MAX_FRAMES 100 MEMSIZE 50000000 CDMEM_SIZE 10000000 PERIOD 10 SIMULATOR_PRIORITY 1 DETECTOR_PRIORITY 20 TIME_SCALE 1 FPS 100 PRESIMULATE BUFFER_FRAMES 11000 DETECTOR_STARTUP_OFFSET_MILLIS 1000 DETECTOR_WARMUP_ITERATIONS 0 DETECTOR_NOISE DETECTOR_NOISE_ALLOCATE_POINTERS 43000 DETECTOR_STATS STDOUT DETECTOR_RELEASE_STATS STDOUT SIMULATOR_STATS STDOUT"

./socat -v EXEC:"./cd_runner.pl $ARGS" /dev/ttyS0,b9600,raw,echo=0
# - does not work well./socat -v EXEC:"./cd_runner.pl $ARGS" /dev/ttyS0,b9600,crnl,raw,echo=0
exit

# in vmware

ARGS="heap.Main ./quad_oscillator2 DETECTOR_STATS STDOUT DETECTOR_RELEASE_STATS STDOUT SIMULATOR_STATS STDOUT"

socat -v EXEC:"./cd_runner.pl $ARGS" /tmp/rtems_serial_socket

exit

# in filesystem

ARGS="heap.Main /home/kalibera/svn7/collisionDetector/src/Input/quad_oscillator2 DETECTOR_STATS STDOUT DETECTOR_RELEASE_STATS STDOUT SIMULATOR_STATS STDOUT"
socat -v EXEC:"./cd_runner.pl $ARGS" EXEC:"./ovm -stdin-cmdline",stderr,pty,ctty
 
exit
