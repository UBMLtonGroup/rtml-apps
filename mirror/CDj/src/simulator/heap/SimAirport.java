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
 * This class encapsulates an airport within the simulation, including its
 * position on the map as well as its incoming (planes per hour) capacity.
 * 
 * @author Ben L. Titzer
 */
class SimAirport {

	protected final Vector2d position;
	protected final int capacity;
	protected final Region noflyzone;
	protected final String code;

	/**
	 * The constructor for the <code>BaseAircraft</code> class takes an
	 * airport code, a 2d position, a no-fly region, and a capacity as arguments
	 * and produces a new instance.
	 * 
	 * @param code
	 *            the unique string name of this airport
	 * @param pos
	 *            the 2d center of the airport
	 * @param nofly
	 */
	public SimAirport(String code, Vector2d pos, Region nofly, int cap) {
		this.position = new Vector2d(pos);
		this.noflyzone = nofly;
		this.code = code;

		if (!nofly.contains(new Vector3d(pos.x, pos.y, 0)))
			throw new IllegalArgumentException(
					"Invalid centerpoint: not contained in no-fly zone");
		if (cap < 1)
			throw new IllegalArgumentException(
					"Invalid airport landing capacity");

		this.capacity = cap;
	}

	/**
	 * The <code>getAirportCode</code> method returns a unique string
	 * identifying the identity of this airport. It is guaranteed to be unique
	 * across the airports in the simulation.
	 * 
	 * @returns a string representing the name of the airport
	 */
	public String getAirportCode() {
		return code;
	}

	/**
	 * The <code>getNoFlyZone</code> method returns the region that describes
	 * the no-fly zone surrounding the airport.
	 * 
	 * @returns an instance of the <code>Region</code> interface that
	 *          describes the no-fly zone
	 */
	public Region getNoFlyZone() {
		return noflyzone;
	}

	/**
	 * The <code>contains</code> method determines whether a position lies
	 * within the airport's region. This allows the Simulator to detect when an
	 * aircraft has landed within at the airport
	 * 
	 * @param pos
	 *            a 3d vector describing the point which to test
	 * @returns true if the point is contained within the Airport's region
	 * @returns false if the point is not contained within the Airport's region
	 */
	public boolean contains(Vector3d pos) {
		return noflyzone.contains(pos);
	}

	/**
	 * The <code>capacity</code> method returns the landing capacity of the
	 * airport that represents the maximum number of aircraft that can land per
	 * hour.
	 */
	public int capacity() {
		return capacity;
	}

	public Vector2d getPosition() {
		return position;
	}

}
