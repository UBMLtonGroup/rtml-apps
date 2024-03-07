package realtime;

import java.util.HashMap;

public class RealtimeThread extends Thread implements Runnable {

	protected Runnable logic = null;
	protected Thread thread = null;
	protected long startTime = 0;
	protected long period = -1;
	protected static HashMap wrappers = new HashMap(1024);

	public RealtimeThread(SchedulingParameters scheduling) {
		thread = new Thread(this);

		if (scheduling != null && scheduling instanceof PriorityParameters) {
			this.thread.setPriority(((PriorityParameters) scheduling)
					.getPriority());
		}
	}

	protected RealtimeThread(ReleaseParameters release, Runnable logic) {
		this.logic = logic;

		if (logic != null) { // should allocate it here, as we can be in a
			// different scope from
			// where "start" executes
			thread = new Thread(logic);
		} else {
			thread = new Thread(this);
		}

		if ((release != null) && (release instanceof PeriodicParameters)) {
			RelativeTime rt = ((PeriodicParameters) release)
					.getPeriod();
			period = rt.getNanoseconds() + rt.getMilliseconds() * 1000000;
		}
	}

	public static boolean waitForNextPeriod() {

		RealtimeThread wrapper = (RealtimeThread) wrappers.get(Thread
				.currentThread());

		if (wrapper.period < 0) {
			Thread.yield();
			return true;
		}

		long now = nanoTime();
		long tosleep = wrapper.period
				- ((now - wrapper.startTime) % wrapper.period);

		try {
			long tmilis = tosleep / 1000000;
			int tnanos = (int) (tosleep - tmilis * 1000000);
			Thread.sleep(tmilis, tnanos);
		} catch (InterruptedException iex) {
		}

		return true;
	}

	// the run method will run as a "logic" of "thread"
	// and as well it will run as the normal run method of RealtimeThread
	//
	// RealtimeThread with logic != null
	// user does not subclass RealtimeThread
	// "thread" is new Thread(logic)
	// start() thus has to start both the "thread" and also us, so
	// that join on us works for "thread" as well
	//
	// RealtimeThread with logic == null
	// user subclasses RealtimeThread
	// "thread" is new Thread(this), so that the overriden run() and/or start()
	// method is honored
	// (they should call our start(), otherwise we are screwed)
	// so start also has to run us, so that join works
	// -> but it cannot start us, because our run method is overriden, this
	// simply does not work

	public void run() {

		// not overriden, logic runs synchronously
		logic.run();

		/*
		 * try { thread.join(); // needed to } catch ( InterruptedException e ) { }
		 */
	}

	public void start() {
		startTime = nanoTime();
		wrappers.put(thread, this);
		thread.start();
		/*
		 * if (logic!=null) { super.start(); }
		 */
	}

	/*
	 * no way, join is final public void join() { thread.join(); }
	 */

	public void joinReal() throws InterruptedException {
		thread.join();
	}

	private static long nanoTime() {
		AbsoluteTime t = Clock.getRealtimeClock().getTime();
		return t.getMilliseconds() * 1000000L + t.getNanoseconds();
	}

}
