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
 * The <code>Constants</code> class encapsulates the constants in the
 * simulation, such as how large the simulation region is, maximum altitude,
 * maximum velocity, framerates, etc.
 * 
 * @author Ben L. Titzer
 */
final class Constants {

	public static final float MAX_X_EXTENT = 5000; // maximum map size in x
													// direction (km)
	public static final float MAX_Y_EXTENT = 5000; // maximum map size in y
													// direction (km)
	public static final float MAX_ALTITUDE = 12; // maximum flight altitude
													// (km)
	public static final float PROXIMITY_RADIUS = 1; // radius of an aircraft

	public static final float MAX_SPEED = 1000; // maximum speed in km/h

	public static final int MIN_FPS = 1; // frames per second
	public static final int MAX_FPS = 1000; // frames per second
	public static final int MIN_CRAFT = 5; // minimum number of craft that must
											// be supported
	public static final int MAX_CRAFT = 4096; // maximum number of craft in
												// any simulation

	public static final float MIN_TIMESCALE = 1; // speed relative to reality
	public static final float MAX_TIMESCALE = 1000; // speed relative to reality

}
