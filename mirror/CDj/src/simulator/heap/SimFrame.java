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

import javacp.util.NoSuchElementException;

/**
 * The <code>BaseFrame</code> class is the default implementation of the
 * <code>Frame</code> interface that the Simulator uses to represent the
 * positions of aircraft within the simulation at specific instant of time.
 * 
 * @author Ben L. Titzer
 */
public class SimFrame {

	public final SimAircraft aircraft[];
	public final Vector3d positions[];
	protected int used;
	protected double timestamp;

	/**
	 * The constructor for the <code>BaseFrame</code> class takes the maximum
	 * number of aircraft and constructs a new frame to represent the positions
	 * of the aircraft.
	 * 
	 * @param max_craft
	 *            the maximum number of aircraft in the simulation at any given
	 *            time
	 * @param timestamp
	 *            the timestamp representing what time this frame corresponds to
	 *            in the simulation
	 */
	SimFrame(int max_craft, double timestamp) {
		this.aircraft = new SimAircraft[max_craft];
		this.positions = new Vector3d[max_craft];
		this.timestamp = timestamp;
	}

	public class PositionIterator {
		protected int cursor;

		public boolean hasNext() {
			return cursor < used;
		}

		public SimAircraft next(Vector3d vec) throws NoSuchElementException {
			if (cursor >= used)
				throw new NoSuchElementException("Iterator exhausted.");
			int which = cursor++;
			vec.set(positions[which]);
			return aircraft[which];
		}
	}

	/**
	 * The <code>iterator</code> method returns an iterator for this frame
	 * that will iterate over all of the positions of aircraft recorded in this
	 * frame.
	 */
	public SimFrame.PositionIterator iterator() {
		return new PositionIterator();
	}

}