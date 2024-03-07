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

/**
 * The <code>BaseSimulationRegion</code> class tracks the airports and no fly
 * zones that are part of a simulation region. It encapsulates this information
 * for both the simulator and any client code that may be interested in
 * collision detection, for example.
 * 
 * @author Ben L. Titzer
 */
class SimulationRegion {

	private final RectangularRegion region;

	/**
	 * The constructor for the <code>SimulatorRegion</code> class takes
	 * parameters describing the region that the simulation covers, the airports
	 * that lie within the region, and the no-fly zones that have been
	 * established within the region.
	 * 
	 * @param r
	 *            the <code>RectangularRegion</code> object that represents
	 *            the legal extent of the simulation
	 * @param a
	 *            an array of <code>Airport</code> objects that represent the
	 *            position and characteristics of the airports in the simulation
	 * @param nf
	 *            an array of <code>Region</code> objects that represent the
	 *            regions where no airplanes are permitted
	 */
	public SimulationRegion(RectangularRegion r, SimAirport[] a, Region[] nf) {
		region = r;
	}

	/**
	 * The <code>getLegalRegion</code> method returns a
	 * <code>RectangularRegion</code> object that describes the legal region
	 * in which the simulation takes place.
	 * 
	 * @returns the <code>RectangularRegion</code> that encloses the
	 *          simulation region
	 */
	public RectangularRegion getLegalRegion() {
		return region;
	}
}
