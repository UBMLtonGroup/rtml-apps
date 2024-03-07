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

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javacp.util.StringTokenizer;

public class SimMediator {
	public static final FrameFactory frameFactory = new FrameFactory();
	public static final int DEFAULT_RESOLUTION = 600;
	public static final float MIN_Y = 0.0f;
	public static final float MIN_X = 0.0f;
	public static final float MAX_Y = 1000.0f;
	public static final float MAX_X = 1000.0f;

	public Simulator buildSimulator(final String s) {
		final SimAircraftProducer prod = new SimAircraftProducer();
		final float param_min_x = MIN_X;
		final float param_min_y = MIN_Y;
		final float param_max_x = MAX_X;
		final float param_max_y = MAX_Y;
		final Simulator.Parameters params = new Simulator.Parameters();
		params.fps = immortal.Constants.SIMULATOR_FPS;
		params.max_craft = Constants.MAX_CRAFT;
		params.timescale = immortal.Constants.SIMULATOR_TIME_SCALE;
		params.max_frames = immortal.Constants.MAX_FRAMES;

		MasterAirportCollection.readAirportAndNoFlyConfig("sim.conf");
		createSimulation(s, prod);

		final SimulationRegion region = new SimulationRegion(
				new RectangularRegion(param_min_x, param_min_y, param_max_x,
						param_max_y), null, null);
		params.director = prod;
		params.producer = prod;
		params.region = region;
		return new Simulator(params);
	}

	// SuppressWarnings("deprecation")
	protected static void createSimulation(final String filename,
			final SimAircraftProducer prod) {
		DataInputStream fin = null;
		try {
			fin = new DataInputStream(new FileInputStream(filename));
		} catch (final FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (true) {
			String line = null;
			try {
				line = fin.readLine();
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (line == null)
				break;
			final StringTokenizer tox = new StringTokenizer(line, "\t");
			if (!tox.hasMoreElements())
				break;
			final String command = tox.nextToken();
			final String name = tox.nextToken();
			final String[] param = new String[] { tox.nextToken(),
					tox.nextToken(), tox.nextToken() };
			try {
				prod.addAircraft(name, param, MasterAirportCollection
						.lookup(tox.nextToken()), MasterAirportCollection
						.lookup(tox.nextToken()));
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}
}
