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
       //v[13]="SYNCHRONOUS_DETECTOR";
       //v[14]="PRESIMULATE";
       //System.out.println("THIS IS NEW VERSION");
       // FIJI
       //System.out.println("count names: "+Arrays.toString(SPC.getNames()));
      // Thread.currentThread().setPriority(ThreadPriority.FIFO_MAX);
/*       if (GCControl.getPriority() != 1) {
       System.out.println("GC Thread Priority is "
                       + GCControl.getPriority()
                       + " should be 1");
       } */
       Benchmarker.initialize();
       //ProfilerThread.startProfiler();
       Benchmarker.set(Benchmarker.RAPITA_BENCHMARK);
       //System.out.println("1");
		parse(v);
	       //System.out.println("2");
		NanoClock.init();
	       //System.out.println("3");
		//final ImmortalEntry immortalEntry = (ImmortalEntry) ImmortalMemory
		//.instance().newInstance(ImmortalEntry.class);
	       final ImmortalEntry immortalEntry=new ImmortalEntry();
	      // System.out.println("4");
/*		Thread specThread = null;
		// Start competing allocation thread to cause frequent GCs 
		String alloc = System.getProperty("ALLOCATION_RATE");
		if (alloc != null && alloc.length() > 0) {
			// String allocDebug = System.getProperty("DEBUG_ALLOCATETHREAD");
			// if (allocDebug != null && allocDebug.length() > 0) {
			// MemoryAllocator.DEBUG_ALLOCATETHREAD =
			// Boolean.getBoolean(allocDebug);
			// }
			int rate = Integer.parseInt(alloc);
			System.out.println("Memory alloc: " + (rate * 1024));
			MemoryAllocator.setupMemoryAllocation(rate * 1024);
		}

		if (Constants.FRAMES_BINARY_DUMP) {
			try {
				immortal.ImmortalEntry.binaryDumpStream = new DataOutputStream( new FileOutputStream("frames.bin"));
				immortal.ImmortalEntry.binaryDumpStream.writeInt(immortal.Constants.MAX_FRAMES);

			} catch (FileNotFoundException e) {
				throw new RuntimeException("Cannot create output file for the binary frames dump: " + e);
			} catch (IOException e) {
				throw new RuntimeException("Error writing header to file for the binary frames dump: " +e);
			}
		} */
		//simulatorThread.start();
		immortalEntry.run();

		//simulatorThread.join();
        Benchmarker.set(Benchmarker.RAPITA_DONE);
        
/*		if (!immortal.Constants.SIMULATE_ONLY) {
			immortalEntry.joinReal(); 
			ImmortalEntry.persistentDetectorScopeEntry.joinReal();
			dumpResults();
		}
		if (Constants.FRAMES_BINARY_DUMP) {
			try {
				immortal.ImmortalEntry.binaryDumpStream.close();
			} catch (IOException e) {
				throw new RuntimeException("Cannot close file with binary frames dump "+e);
			}
		} */

        dumpFijiStats();
		dumpResults();
//		Benchmarker.dump();	
		// the SPEC thread can still be running, but "stop" is not implemented
		// in Ovm
		//dumpResults();
		//System.out.println("done");
		//System.out.println("****************************************************************************************************");
		//System.exit(0);
	}
	
	public static void dumpFijiStats() {
        /*
		for (int i = 0; i < ImmortalEntry.recordedRuns; i++) {
			System.out.print(i+" ");
			for (int x: ImmortalEntry.fijiStats[i]) {
				System.out.print(" "+x);
			}
			System.out.println("");
			;
		} */
	}

	public static void dumpResults() {

		if (PRINT_RESULTS) {
			System.out
			.println("Dumping output [ timeBefore timeAfter heapFreeBefore heapFreeAfter detectedCollisions ] for "
					+ ImmortalEntry.recordedRuns
					+ " recorded detector runs, in ns");
		}

		PrintWriter out = null;
        
/*		if (immortal.Constants.DETECTOR_STATS != "") {
			try {
				out = new PrintWriter(new FileOutputStream(
						immortal.Constants.DETECTOR_STATS));
			} catch (FileNotFoundException e) {
				System.out
				.println("Failed to create output file for detector statistics ("
						+ immortal.Constants.DETECTOR_STATS + "): " + e);
			}
		}
*/
//Automatically generated code
System.out.println("!!!!!!");System.out.println("cdj_hf_cw_1500K");System.out.println("--g-def-max-mem=1500K --g-def-trigger=200K -G hf --g-pred-level cw");System.out.println("../fivm/bin/fivmc -o cdj_hf_cw_1500K cdj_hf_cw_1500K.jar --main heap/Main --target=sparc-rtems4.9 --reflect cdj.reflectlog --more-opt --g-def-max-mem=1500K --g-def-trigger=200K -G hf --g-pred-level cw");
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

			/*try {
				out = new PrintWriter(new FileOutputStream(
						immortal.Constants.DETECTOR_RELEASE_STATS));
			} catch (FileNotFoundException e) {
				System.out
				.println("Failed to create output file for detector release statistics ("
						+ immortal.Constants.DETECTOR_RELEASE_STATS
						+ "): "
						+ e);
			} */
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
				// I have chaned this so that it is more consistent with the C version
						

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
