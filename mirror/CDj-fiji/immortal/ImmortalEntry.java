package immortal;

import heap.Simulator;
import immortal.persistentScope.PersistentDetectorScopeEntry;
import java.io.DataOutputStream;
import realtime.LTMemory;
import realtime.PeriodicParameters;
import realtime.PriorityParameters;
import realtime.RealtimeThread;
import realtime.RelativeTime;
import realtime.Clock;
import realtime.AbsoluteTime;

import com.fiji.fivm.ThreadPriority;
/* Uncomment this if you plan on using Rapita benchmarking.
 */
//import com.fiji.fivm.util.GCControl;
import com.fiji.fivm.r1.*;


/** This thread runs only during start-up to run other threads. It runs in immortal memory, is allocated in immortal memory,
 * and it's constructor runs in immortal memory. It is a singleton, allocation from the Main class */
public class ImmortalEntry extends RealtimeThread {
    // RAPITA stuff
    /* Uncomment this code only if you plan to use RAPITA for benchmarking. 
    
    @Import
    @GodGiven
    @NoThrow
    private static native boolean rapita_setMask(int mask);

    @Import
    @GodGiven
    @NoThrow
    private static native boolean rapita_output(int what);
    
    public static void setMask(int mask) {
        rapita_setMask(mask);
    }

    public static void output(int what) {
        rapita_output(what);
    }
    // End of RAPITA stuff
    */
    /* Dummy placeholders. Remove them if you wish to use RAPITA and have already
     * uncommented the previous section.
     */
    public static void setMask(int mask) { }
    public static void output(int what) { }
    // End of RAPITA placeholders
    

	static public Object initMonitor = new Object();
	static public boolean detectorReady = false;
	static public boolean simulatorReady = false;

	static public int maxDetectorRuns;

	static public long detectorFirstRelease = -1;
	
	static public long[] timesBefore; 
	static public long[] timesAfter;
	static public long[] heapFreeBefore;
	static public long[] heapFreeAfter;
	static public int[] detectedCollisions;
	static public int[] suspectedCollisions;
	static public int[][] fijiStats;

	static public long detectorThreadStart;    
	static public long[] detectorReleaseTimes;
	static public long[] detectorWaitTimes;
	static public boolean[] detectorReportedMiss;

	static public int reportedMissedPeriods = 0;
	static public int frameNotReadyCount = 0;
	static public int droppedFrames = 0;    
	static public int framesProcessed = 0;
	static public int recordedRuns = 0;

	static public int recordedDetectorReleaseTimes = 0;
//	frameBuffer = new FrameBuffer();
	static private LTMemory persistentDetectorScope = new LTMemory(Constants.PERSISTENT_DETECTOR_SCOPE_SIZE, Constants.PERSISTENT_DETECTOR_SCOPE_SIZE);

	static public FrameBuffer frameBuffer = new FrameBuffer();//null;
	static public PersistentDetectorScopeEntry persistentDetectorScopeEntry = null;

	static public DataOutputStream binaryDumpStream = null;
	
	public ImmortalEntry() {
		super(new PriorityParameters(Constants.DETECTOR_STARTUP_PRIORITY));

		maxDetectorRuns = Constants.MAX_FRAMES;

		timesBefore = new long[ maxDetectorRuns ]; 
		timesAfter = new long[ maxDetectorRuns ];
		heapFreeBefore = new long[ maxDetectorRuns ];
		heapFreeAfter = new long[ maxDetectorRuns ];
		detectedCollisions = new int[ maxDetectorRuns ];
		suspectedCollisions = new int[ maxDetectorRuns ];   
		fijiStats=new int[maxDetectorRuns][];

		detectorReleaseTimes = new long[ maxDetectorRuns + 10]; // the 10 is for missed deadlines
                detectorWaitTimes = new long[maxDetectorRuns + 10];
		detectorReportedMiss = new boolean[ maxDetectorRuns + 10];        
	}

	/** Called only once during initialization. Runs in immortal memory */
	public void run() {

		detectorReady = true;
		

		/* start the detector at rounded-up time, so that the measurements are not subject
		 * to phase shift
		 */
		AbsoluteTime releaseAt = NanoClock.roundUp(Clock.getRealtimeClock().getTime().add(Constants.DETECTOR_STARTUP_OFFSET_MILLIS, 0));
		detectorFirstRelease = NanoClock.convert(releaseAt);


		persistentDetectorScopeEntry = new PersistentDetectorScopeEntry(
				new PriorityParameters(Constants.DETECTOR_PRIORITY), 
				new PeriodicParameters( releaseAt, // start
				new RelativeTime(Constants.DETECTOR_PERIOD, 0), // period
				null, //cost
				null, // deadline
				null, null), persistentDetectorScope);
		Simulator.generate(null);
        persistentDetectorScopeEntry.setPriority(ThreadPriority.FIFO_MAX); // RR_MIN
        output(0xf000);
		persistentDetectorScopeEntry.start();
		try {
			persistentDetectorScopeEntry.joinReal();
                        setMask(0x0000);
                } catch (InterruptedException e) {
			e.printStackTrace();
		}	
		//persistentDetectorScopeEntry.run();
	}
}
