package heap;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

import immortal.Constants;
import immortal.ImmortalEntry;
import immortal.NanoClock;
import immortal.Benchmarker;
import realtime.ImmortalMemory;

import com.fiji.fivm.r1.*;

//import com.fiji.fivm.*;

/**
 * Real-time Java runner for the collision detector.
 */
public class Main {

	public static boolean PRINT_RESULTS = true;

	public static Object junk;

    public static String[] v;
    
	public static void main(final String[] w) throws Throwable {
       v=new String[13];
       v[0]="input/frames.bin";
       v[1]="MAX_FRAMES";
       v[2]="1000";
       v[3]="PERSISTENT_DETECTOR_SCOPE_SIZE";
       v[4]="5000000";
       v[5]="TRANSIENT_DETECTOR_SCOPE_SIZE";
       v[6]="6000000";
       v[7]="PERIOD";
       v[8]="120"; //"250";
       v[9]="DETECTOR_PRIORITY";
       v[10]="9";
       v[11]="TIME_SCALE";
       v[12]="1";
       Benchmarker.initialize();
       Benchmarker.set(Benchmarker.RAPITA_BENCHMARK);
       parse(v);
	   NanoClock.init();
       final ImmortalEntry immortalEntry=new ImmortalEntry();
		immortalEntry.run();
        Benchmarker.set(Benchmarker.RAPITA_DONE);
		dumpResults();
	}
	
	public static void dumpResults() {

		if (PRINT_RESULTS) {
			System.out
			.println("Dumping output [ timeBefore timeAfter heapFreeBefore heapFreeAfter detectedCollisions ] for "
					+ ImmortalEntry.recordedRuns
					+ " recorded detector runs, in ns");
		}

		PrintWriter out = null;
        
		System.out.println("=====DETECTOR-STATS-START-BELOW====");	
		for (int i = 0; i < ImmortalEntry.recordedRuns; i++) {
			String line = NanoClock.asString(ImmortalEntry.timesBefore[i])
			+ " " + NanoClock.asString(ImmortalEntry.timesAfter[i])
			+ " " + ImmortalEntry.heapFreeBefore[i] + " "
			+ ImmortalEntry.heapFreeAfter[i] + " "
			+ ImmortalEntry.detectedCollisions[i] + " "
			+ ImmortalEntry.suspectedCollisions[i]+" 0 0 0 "+i;
			/*String line = ""+NanoClock.asMicros(ImmortalEntry.timesBefore[i])
			            +" "+NanoClock.asMicros(ImmortalEntry.timesAfter[i])
			            +" "+ImmortalEntry.heapFreeBefore[i]
			            +" "+ImmortalEntry.heapFreeAfter[i]
			            +" "+ImmortalEntry.detectedCollisions[i]
			            +" "+ImmortalEntry.suspectedCollisions[i]
			            +" "+(NanoClock.asMicros(ImmortalEntry.timesAfter[i]-ImmortalEntry.timesBefore[i])/1000.0)
			            +" 0 0 "+i; */
			if (out != null) {
				out.println(line);
                System.out.println(line);
			}
			if (PRINT_RESULTS) {
				//System.err.println(line);
                System.out.println(line);
			}
		}

		if (out != null) {
			out.close();
			out = null;
		}
		System.out.println("=====DETECTOR-STATS-END-ABOVE====");	

		System.out
		.println("Generated frames: " + immortal.Constants.MAX_FRAMES);
		System.out.println("Received (and measured) frames: "
				+ ImmortalEntry.recordedRuns);
		System.out.println("Frame not ready event count (in detector): "
				+ ImmortalEntry.frameNotReadyCount);
		System.out.println("Frames dropped due to full buffer in detector: "
				+ ImmortalEntry.droppedFrames);
		System.out.println("Frames processed by detector: "
				+ ImmortalEntry.framesProcessed);
		System.out.println("Detector stop indicator set: "
				+ ImmortalEntry.persistentDetectorScopeEntry.stop);
		System.out
		.println("Reported missed detector periods (reported by waitForNextPeriod): "
				+ ImmortalEntry.reportedMissedPeriods);
		System.out.println("Detector first release was scheduled for: "
				+ NanoClock.asString(ImmortalEntry.detectorFirstRelease));
        System.out.println("WE ARE HERE");
		// heap measurements
		Simulator.dumpStats();

		// detector release times
		if (immortal.Constants.DETECTOR_RELEASE_STATS != "") {
			System.out.println("=====DETECTOR-RELEASE-STATS-START-BELOW====");	
			for (int i = 0; i < ImmortalEntry.recordedDetectorReleaseTimes; i++) {
				// real expected
				//String x=ImmortalEntry.detectorReleaseTimes[i])
				String line = 
                                    NanoClock.asString(ImmortalEntry.detectorWaitTimes[i])+ " " +
                                    NanoClock.asString(ImmortalEntry.detectorReleaseTimes[i])+ " ";

				line = line
				+ NanoClock.asString(i
						* immortal.Constants.DETECTOR_PERIOD * 1000000L
						+ ImmortalEntry.detectorReleaseTimes[0]);

				line = line + " "
				+ (ImmortalEntry.detectorReportedMiss[i] ? "1" : "0");
				line+=" "+i;
				if (out != null) {
					out.println(line);
				} else System.out.println(line);
			}

			if (out != null) {
				out.close();
				out = null;
			}
			System.out.println("=====DETECTOR-RELEASE-STATS-END-ABOVE====");	
		}
	}

	private static void parse(final String[] v) {
		for (int i = 1; i < v.length; i++) {
			if (v[i].equals("PERSISTENT_DETECTOR_SCOPE_SIZE")) { /* flags with parameters */
				Constants.PERSISTENT_DETECTOR_SCOPE_SIZE = Long
				.parseLong(v[i + 1]);
				i++;
			} else if (v[i].equals("PERIOD")) {
				Constants.DETECTOR_PERIOD = Long.parseLong(v[i + 1]);
				i++;
			} else if (v[i].equals("TRANSIENT_DETECTOR_SCOPE_SIZE")) {
				Constants.TRANSIENT_DETECTOR_SCOPE_SIZE = Long
				.parseLong(v[i + 1]);
				i++;
			} else if (v[i].equals("DETECTOR_PRIORITY")) {
				//Constants.DETECTOR_PRIORITY = Integer.parseInt(v[i + 1]);
				//Constants.DETECTOR_STARTUP_PRIORITY = Constants.DETECTOR_PRIORITY - 1;
				i++;
			} else if (v[i].equals("SIMULATOR_PRIORITY")) {
				Constants.SIMULATOR_PRIORITY = Integer.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("NOISE_RATE")) {
				Constants.NOISE_RATE = Integer.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("MAX_FRAMES")) {
				Constants.MAX_FRAMES = Integer.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("TIME_SCALE")) {
				Constants.TIME_SCALE = Integer.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("BUFFER_FRAMES")) {
				Constants.BUFFER_FRAMES = Integer.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("FPS")) {
				Constants.FPS = Integer.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("DETECTOR_NOISE_REACHABLE_POINTERS")) {
				Constants.DETECTOR_NOISE_REACHABLE_POINTERS = Integer
				.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("DETECTOR_NOISE_ALLOCATION_SIZE")) {
				Constants.DETECTOR_NOISE_ALLOCATION_SIZE = Integer
				.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("DETECTOR_NOISE_ALLOCATE_POINTERS")) {
				Constants.DETECTOR_NOISE_ALLOCATE_POINTERS = Integer
				.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("DETECTOR_NOISE_MIN_ALLOCATION_SIZE")) {
				Constants.DETECTOR_NOISE_MIN_ALLOCATION_SIZE = Integer
				.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("DETECTOR_NOISE_MAX_ALLOCATION_SIZE")) {
				Constants.DETECTOR_NOISE_MAX_ALLOCATION_SIZE = Integer
				.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("DETECTOR_NOISE_ALLOCATION_SIZE_INCREMENT")) {
				Constants.DETECTOR_NOISE_ALLOCATION_SIZE_INCREMENT = Integer
				.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("DETECTOR_STARTUP_OFFSET_MILLIS")) {
				Constants.DETECTOR_STARTUP_OFFSET_MILLIS = Integer
				.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("SPEC_NOISE_ARGS")) {
				Constants.SPEC_NOISE_ARGS = v[i + 1];
				i++;
            } else if (v[i].equals("PRINT_RESULTS")) {
                PRINT_RESULTS=true;
            } else if (v[i].equals("DETECTOR_STATS")) {
                Constants.DETECTOR_STATS="true";
            } else if (v[i].equals("DETECTOR_RELEASE_STATS")) {
                Constants.DETECTOR_RELEASE_STATS="true";
			} else if (v[i].equals("SYNCHRONOUS_DETECTOR")) { /*
			 * flags without
			 * a parameter
			 */
				Constants.SYNCHRONOUS_DETECTOR = true;
			} else if (v[i].equals("PRESIMULATE")) {
				Constants.PRESIMULATE = true;
			} else if (v[i].equals("FRAMES_BINARY_DUMP")) {
				Constants.FRAMES_BINARY_DUMP = true;
			} else if (v[i].equals("DEBUG_DETECTOR")) {
				Constants.DEBUG_DETECTOR = true;
			} else if (v[i].equals("DUMP_RECEIVED_FRAMES")) {
				Constants.DUMP_RECEIVED_FRAMES = true;
			} else if (v[i].equals("DUMP_SENT_FRAMES")) {
				Constants.DUMP_SENT_FRAMES = true;
			} else if (v[i].equals("USE_SPEC_NOISE")) {
				Constants.USE_SPEC_NOISE = true;
			} else if (v[i].equals("SIMULATE_ONLY")) {
				Constants.SIMULATE_ONLY = true;
			} else if (v[i].equals("DETECTOR_NOISE")) {
				Constants.DETECTOR_NOISE = true;
			} else if (v[i].equals("DETECTOR_NOISE_VARIABLE_ALLOCATION_SIZE")) {
				Constants.DETECTOR_NOISE_VARIABLE_ALLOCATION_SIZE = true;
			}

			else
				throw new Error("Unrecognized option: " + v[i]);
		}
	}
}
