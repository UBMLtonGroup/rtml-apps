/**
 * 
 * Copyright (c) 2001-2010, Purdue University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   * Neither the name of the Purdue University nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package heap;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import immortal.ImmortalEntry;
import immortal.NanoClock;

/**
 * The <code>BaseSimulator</code> class implements the functionality of
 * driving the simulation. It manages the locations of aircrafts in the
 * simulation and updates their positions as the simulation executes. All units
 * are kilometers.
 * 
 * @author Ben L. Titzer
 */
public class Simulator {

	public static int position;
	public static int counter;

	public static class Parameters {
		public int fps;
		public int max_craft;
		public int max_frames;
		public SimulationRegion region;
		public float timescale;
		public SimAircraftProducer director;
		public SimAircraftProducer producer;
	}

	public static int recordedFrames = 0;
	public static long timesBefore[];
	public static long timesAfter[];
	public static long heapFreeBefore[];
	public static long heapFreeAfter[];

	private final int fps;
	private final int max_craft;
	private final SimulationRegion region;
	private final float timescale;
	private final Vector3d vec0 = new Vector3d(); // temporary
	private final SimAircraftProducer director;
	private final SimAircraftProducer producer;
	private double current_time;

	/**
	 * The <code>BaseSimulator</code> class has a constructor that takes an
	 * instance of the <code>BaseSimulator.Parameters</code> class that
	 * encapsulate the settings for the new simulator being created.
	 * 
	 * @param p
	 *            the settings for the simulator instance to be created.
	 */
	public Simulator(final Simulator.Parameters p) {
		fps = checkArgument(p.fps, Constants.MIN_FPS, Constants.MAX_FPS,
				"Invalid fps");
		max_craft = checkArgument(p.max_craft, Constants.MIN_CRAFT,
				Constants.MAX_CRAFT, "Invalid maximum craft count");
		region = p.region;
		timescale = checkArgument(p.timescale, 1, Constants.MAX_TIMESCALE,
				"Invalid timescale");
		director = p.director;
		producer = p.producer;

		timesBefore = new long[immortal.Constants.MAX_FRAMES];
		timesAfter = new long[immortal.Constants.MAX_FRAMES];
		heapFreeBefore = new long[immortal.Constants.MAX_FRAMES];
		heapFreeAfter = new long[immortal.Constants.MAX_FRAMES];
	}

	private int checkArgument(final int val, final int min, final int max,
			final String error) {
		if (val < min || val > max)
			throw new IllegalArgumentException(error);
		return val;
	}

	private float checkArgument(final float val, final float min,
			final float max, final String error) {
		if (val < min || val > max)
			throw new IllegalArgumentException(error);
		return val;
	}

	/**
	 * The <code>run</code> method of the simulator begins execution of the
	 * simulation with the parameters that were supplied to the constructor. It
	 * executes the simulation until the maximum number of steps is reached or
	 * until the <code>stop</code> method is called (by code reachable from
	 * the <code>run</code> method.
	 */
	public void run() {
		run_();
	}

	class SerializedFrame {
		float[] positions;
		byte[] callsigns;
		int[] lengths;
	}

	private SerializedFrame translate(final SimFrame frame) {
		final SerializedFrame sf = new SerializedFrame();
		sf.positions = new float[3 * frame.used];
		int length = 0;
		for (int i = 0; i < frame.used; i++)
			length += frame.aircraft[i].callsign.getBytes().length;
		// note that use of callsign.length is incorrect due to char -> byte
		// conversions

		sf.callsigns = new byte[length];
		sf.lengths = new int[frame.used];
		for (int i = 0, pos = 0; i < frame.used; i++) {
			final SimAircraft a = frame.aircraft[i];
			final byte[] b = a.callsign.getBytes();
			sf.lengths[i] = b.length;

			for (int bi = 0; bi < b.length; bi++) {
				sf.callsigns[pos++] = b[bi];
			}

			sf.positions[3 * i] = frame.positions[i].x;
			sf.positions[3 * i + 1] = frame.positions[i].y;
			sf.positions[3 * i + 2] = frame.positions[i].z;
		}
		return sf;
	}

	private void run_() {
		// amount of time that passes between two frames.
		final double virtual_delta = 1.0 / fps;
		double real_delta = 1.0 / (1.0 * timescale * fps);
		double next_time = 0;
		FrameFactory.Builder builder = SimMediator.frameFactory.getBuilder(
				max_craft, 0);
		producer.addInitialAircraft(region, builder);
		SimFrame prev = builder.makeFrame();

		if (immortal.Constants.PRESIMULATE) {
			System.out
					.println("Simulator will pre-simulate all frames and then run the detector.");
			real_delta = 0;
		} else {
			System.out.println("Simulator will generate "
					+ immortal.Constants.MAX_FRAMES
					+ " frames with simulated time interval "
					+ (1000.0 * virtual_delta)
					+ "ms and real time pause between frames at least "
					+ (1000.0 * real_delta)
					+ "ms, running from time 0 to time "
					+ (immortal.Constants.MAX_FRAMES * virtual_delta)
					+ "s, using priority "
					+ Thread.currentThread().getPriority()
					+ " (NORM_PRIORITY is " + Thread.NORM_PRIORITY
					+ ", MIN_PRIORITY is " + Thread.MIN_PRIORITY
					+ ", MAX_PRIORITY is " + Thread.MAX_PRIORITY + ")");
		}
		System.out.println("Simulator runs at priority "
				+ Thread.currentThread().getPriority());
		System.out.println("Simulator: timescale = " + timescale);
		System.out.println("Simulator: fps = " + fps);
		System.out
				.println("Simulator: virtual_delta = " + virtual_delta + " s");
		System.out.println("Simulator: real_delta = " + real_delta + " s");
		System.out.println("Simulator: number of frames (MAX_FRAMES) = "
				+ immortal.Constants.MAX_FRAMES);
		System.out.println("Simulator: debug mode (SYNCHRONOUS_DETECTOR) = "
				+ immortal.Constants.SYNCHRONOUS_DETECTOR);
		System.out.println("Simulator: pre-simulation (PRESIMULATE) = "
				+ immortal.Constants.PRESIMULATE);

		if (immortal.Constants.PRESIMULATE
				&& (immortal.Constants.BUFFER_FRAMES < immortal.Constants.MAX_FRAMES)) {
			throw new RuntimeException(
					"Cannot presimulate when the simulator buffer is not large enough to hold all simulated frames");
		}

		synchronized (ImmortalEntry.initMonitor) {

			if (!immortal.Constants.PRESIMULATE) {
				ImmortalEntry.simulatorReady = true;
				ImmortalEntry.initMonitor.notifyAll();
			}

			while (!ImmortalEntry.detectorReady) {
				try {
					ImmortalEntry.initMonitor.wait();
				} catch (InterruptedException e) {
				}
				;
			}
		}

		long simStart = NanoClock.now() / 1000000; // ms

		for (int idx = 0; idx < immortal.Constants.MAX_FRAMES; idx++) {
			if (!immortal.Constants.SYNCHRONOUS_DETECTOR && idx % 10 == 0) {
				System.out.print(".");
			}

			long now = NanoClock.now() / 1000000; // ms
			long toSleepNow = (long) (((1000.0 * idx * real_delta + simStart) - now));

			if (toSleepNow > 0) {
				try {
					Thread.sleep(toSleepNow);
				} catch (Exception e) {
				}
			}

			heapFreeBefore[recordedFrames] = Runtime.getRuntime().freeMemory();
			timesBefore[recordedFrames] = NanoClock.now();

			next_time = current_time + virtual_delta;
			builder = SimMediator.frameFactory.getBuilder(max_craft, next_time);
			addRemainingAircraft(prev, builder);
			final FrameFactory.Mutator mutator = SimMediator.frameFactory
					.getMutator(builder);
			director.computeNewPositions(region, mutator, virtual_delta);
			mutator.commit(); // make sure the changes are finished
			producer.addAircraft(region, prev, builder, virtual_delta);
			final SimFrame next = builder.makeFrame();

			final SerializedFrame nsf = translate(next);
			immortal.ImmortalEntry.frameBuffer.putFrame(nsf.positions,
					nsf.lengths, nsf.callsigns);

			current_time = next_time;
			prev = next;

			timesAfter[recordedFrames] = NanoClock.now();
			heapFreeAfter[recordedFrames] = Runtime.getRuntime().freeMemory();
			recordedFrames++;

		}

		if (immortal.Constants.PRESIMULATE) {
			synchronized (ImmortalEntry.initMonitor) {
				ImmortalEntry.simulatorReady = true;
				ImmortalEntry.initMonitor.notifyAll();
			}
		}
		/*
		 * // sleep for the time we are in advance try{ Thread.sleep( (int)
		 * (1000.0*(virtual_delta - real_delta)*imm.Constants.MAX_FRAMES) ); }
		 * catch( Exception e ) {};
		 */
	}

	/**
	 * The <code>addRemainingAircraft</code> method is used by the simulator
	 * to effectively remove aircraft from a frame. It adds all of the aircraft
	 * from the previous frame that will remain and does not add the ones that
	 * should be removed. In this way, it effectively removes planes from the
	 * next frame of the simulation.
	 * 
	 * @param prev
	 *            the previous frame of the simulation
	 * @param builder
	 *            the builder to add the new planes to
	 */
	private void addRemainingAircraft(final SimFrame prev,
			final FrameFactory.Builder builder) {
		final SimFrame.PositionIterator iterator = prev.iterator();

		while (iterator.hasNext()) {
			final SimAircraft a = iterator.next(vec0);
			final SimAirport dest = a.getDestination();
			if (isOutsideRegion(vec0))
				continue;
			// don't add planes that have reached their destination
			if (dest.contains(vec0))
				continue;
			// don't add planes that have crashed into the ground
			if (vec0.z <= 0)
				continue;
			builder.addPosition(a, vec0);
		}
	}

	private boolean isOutsideRegion(final Vector3d vec) {
		if (vec0.z > Constants.MAX_ALTITUDE)
			return true;
		return !region.getLegalRegion().contains(vec0);
	}

	public static void generate(final String[] args) {
		final SimMediator m = new SimMediator();
		final Simulator s = m.buildSimulator(args[0]);
		s.run();
	}
	
	public static void dumpStats() {

		if (immortal.Constants.SIMULATOR_STATS != "") {

			PrintWriter out = null;
			try {
				out = new PrintWriter(new FileOutputStream(
						immortal.Constants.SIMULATOR_STATS));
			} catch (FileNotFoundException e) {
				System.out
				.println("Failed to create output file for simulator statistics ("
						+ immortal.Constants.SIMULATOR_STATS + "): " + e);
			}

			for (int i = 0; i < Simulator.recordedFrames; i++) {
				String line = NanoClock.asString(Simulator.timesBefore[i]) + " "
				+ NanoClock.asString(Simulator.timesAfter[i]) + " "
				+ Simulator.heapFreeBefore[i] + " "
				+ Simulator.heapFreeAfter[i];

				if (out != null) {
					out.println(line);
				}
			}

			if (out != null) {
				out.close();
				out = null;
			}
		}
	}

}
