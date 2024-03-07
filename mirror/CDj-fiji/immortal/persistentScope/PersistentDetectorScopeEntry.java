package immortal.persistentScope;

import immortal.Constants;
import immortal.Benchmarker;
import immortal.FrameSynchronizer;
import immortal.ImmortalEntry;
import immortal.NanoClock;
import immortal.RawFrame;
import immortal.persistentScope.transientScope.TransientDetectorScopeEntry;

import realtime.LTMemory;
import realtime.NoHeapRealtimeThread;
import realtime.PeriodicParameters;
import realtime.PriorityParameters;
import realtime.RealtimeThread;

//import com.fiji.fivm.*;
import java.util.*;
/** 
 * This thread is the periodic real-time threads that periodically wakes-up to run the collision detector.
 * Its constructor runs in immortal memory. The instance lives in immortal memory. 
 * The thread itself runs in the persistent detector scope.
 */
public class PersistentDetectorScopeEntry extends NoHeapRealtimeThread {

    public PersistentDetectorScopeEntry(final PriorityParameters p, final PeriodicParameters q, final LTMemory l) {
        super(p, q, null, l, null, null);
    }

    public boolean stop = false;

    public void run() {
        NoiseGenerator noiseGenerator = new NoiseGenerator();
        final LTMemory transientDetectorScope = new LTMemory(immortal.Constants.TRANSIENT_DETECTOR_SCOPE_SIZE, immortal.Constants.TRANSIENT_DETECTOR_SCOPE_SIZE);
        try {
            final TransientDetectorScopeEntry cd = new TransientDetectorScopeEntry(new StateTable(), Constants.GOOD_VOXEL_SIZE);
            if (immortal.Constants.DEBUG_DETECTOR) {
                System.out.println("Detector thread is "+Thread.currentThread());
                System.out.println("Entering detector loop, detector thread priority is "+
                                   +Thread.currentThread().getPriority()+
                                   " (NORM_PRIORITY is "+Thread.NORM_PRIORITY+
                                   ", MIN_PRIORITY is "+Thread.MIN_PRIORITY+
                                   ", MAX_PRIORITY is "+Thread.MAX_PRIORITY+")");  
            }
            while (!stop) {
                Benchmarker.set(13);
                if (ImmortalEntry.recordedDetectorReleaseTimes>0) {
                    ImmortalEntry.detectorWaitTimes[ ImmortalEntry.recordedDetectorReleaseTimes-1 ] = NanoClock.now();
                }
                boolean missed=!RealtimeThread.waitForNextPeriod();
                Benchmarker.done(13);
                long now = NanoClock.now();
                ImmortalEntry.detectorReleaseTimes[ ImmortalEntry.recordedDetectorReleaseTimes ] = now;
                ImmortalEntry.detectorReportedMiss [ ImmortalEntry.recordedDetectorReleaseTimes ] = missed;
                ImmortalEntry.recordedDetectorReleaseTimes++;
                runDetectorInScope(cd, transientDetectorScope, noiseGenerator);
            }
            ImmortalEntry.detectorWaitTimes[ ImmortalEntry.recordedDetectorReleaseTimes-1 ] = NanoClock.now();
        } catch (final Throwable t) {
            throw new Error(t);
        }
    }

    public void runDetectorInScope(final TransientDetectorScopeEntry cd, final LTMemory transientDetectorScope, final NoiseGenerator noiseGenerator) {
        Benchmarker.set(14);
        final RawFrame f = immortal.ImmortalEntry.frameBuffer.getFrame();
        if (f == null) {
            ImmortalEntry.frameNotReadyCount++;
            System.out.println("Frame not ready");
            Benchmarker.done(14);
            return;
        }
        if ( (immortal.ImmortalEntry.framesProcessed + immortal.ImmortalEntry.droppedFrames) == immortal.Constants.MAX_FRAMES) {
            stop = true;
            Benchmarker.done(14);
            return;
        }  // should not be needed, anyway
        final long heapFreeBefore = Runtime.getRuntime().freeMemory();
        final long timeBefore = NanoClock.now();
        noiseGenerator.generateNoiseIfEnabled();
        Benchmarker.set(Benchmarker.RAPITA_SETFRAME);
        cd.setFrame(f);
        Benchmarker.done(Benchmarker.RAPITA_SETFRAME);
        // actually runs the detection logic in the given scope
        transientDetectorScope.enter(cd);
        final long timeAfter = NanoClock.now();
        final long heapFreeAfter = Runtime.getRuntime().freeMemory();
        if (ImmortalEntry.recordedRuns < ImmortalEntry.maxDetectorRuns) {
            ImmortalEntry.timesBefore[ ImmortalEntry.recordedRuns ] = timeBefore;
            ImmortalEntry.timesAfter[ ImmortalEntry.recordedRuns ] = timeAfter;
            ImmortalEntry.heapFreeBefore[ ImmortalEntry.recordedRuns ] = heapFreeBefore;
            ImmortalEntry.heapFreeAfter[ ImmortalEntry.recordedRuns ] = heapFreeAfter;
            ImmortalEntry.recordedRuns ++;
        }
        immortal.ImmortalEntry.framesProcessed++;
        if ( (immortal.ImmortalEntry.framesProcessed + immortal.ImmortalEntry.droppedFrames) == immortal.Constants.MAX_FRAMES) {
            stop = true;
        }
        Benchmarker.done(14);
    }

    public void start() {
        ImmortalEntry.detectorThreadStart = NanoClock.now();
        super.start();
    }
}