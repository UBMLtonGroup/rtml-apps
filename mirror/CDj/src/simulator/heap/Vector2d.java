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
 * The <code>Vector2d</code> class implements a 2-dimensional vector that can
 * represent the position or velocity of an object within a 2D space. This
 * implementation uses public, non-final fields to avoid as much object creation
 * as possible. Java does not have value types per se, but these vector classes
 * are the closest thing that is possible.
 * 
 * @author Ben L. Titzer
 */
public final class Vector2d {

	public float x, y;

	/**
	 * The default constructor for the <code>Vector2d</code> returns an object
	 * representing the zero vector.
	 */
	public Vector2d() {
	}

	/**
	 * The main constructor for the <code>Vector2d</code> class takes the two
	 * coordinates as parameters and produces an object representing that
	 * vector.
	 * 
	 * @param x
	 *            the coordinate on the x (east-west) axis
	 * @param y
	 *            the coordinate on the y (north-south) axis
	 */
	public Vector2d(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * The secondary constructor for the <code>Vector2d</code> class takes a
	 * vector to copy into this new instance and returns an instance that
	 * represents a copy of that vector.
	 * 
	 * @param v
	 *            the vale of the vector to copy into this new instance
	 */
	public Vector2d(Vector2d v) {
		this.x = v.x;
		this.y = v.y;
	}

	/**
	 * The <code>set</code> is basically a convenience method that resets the
	 * internal values of the coordinates.
	 * 
	 * @param x
	 *            the coordinate on the x (east-west) axis
	 * @param y
	 *            the coordinate on the y (north-south) axis
	 */
	public void set(Vector2d val) {
		this.x = val.x;
		this.y = val.y;
	}

	/**
	 * The <code>zero</code> method is a convenience method to zero the
	 * coordinates of the vector.
	 */
	public void zero() {
		x = y = 0;
	}

	// Override
	public boolean equals(Object o) throws ClassCastException {
		try {
			return equals((Vector2d) o);
		} catch (ClassCastException e) {
			return false;
		}
	}

	public boolean equals(Vector2d b) {
		if (x != b.x)
			return false;
		if (y != b.y)
			return false;
		return true;
	}

	// Override
	public int hashCode() {
		
		 long rawBytes = ((long) Float.floatToIntBits(y) << 32) | Float.floatToIntBits(x);
		 int hash = 0xAAAAAAAA;
		 for (int i = 0; i < 8; i++, rawBytes >>= 8) {
			 byte curByte = (byte) (rawBytes & 0xFF);
			 hash ^= ((i & 1) == 0) ? ((hash << 7) ^ curByte * (hash >>> 3)) :
				 (~((hash << 11) + curByte ^ (hash >>> 5)));
		 }
		 return hash;
	}

	// Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
