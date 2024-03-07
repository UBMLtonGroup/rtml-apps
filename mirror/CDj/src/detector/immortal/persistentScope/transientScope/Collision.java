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

package immortal.persistentScope.transientScope;

import javacp.util.ArrayList;
import javacp.util.Collections;
import javacp.util.Iterator;
import javacp.util.List;

/**
 * Represents a definite collision that has occured.
 * @author Filip Pizlo
 */
class Collision {
	/** The aircraft that were involved.  */
	private ArrayList aircraft;

	/** The location where the collision happened. */
	private Vector3d location;

	/** Construct a Collision with a given set of aircraft and a location.  */
	public Collision(List aircraft, Vector3d location) {
		this.aircraft = new ArrayList(aircraft);
		Collections.sort(this.aircraft);
		this.location = location;
	}

	/** Construct a Coollision with two aircraft an a location. */
	public Collision(Aircraft one, Aircraft two, Vector3d location) {
		aircraft = new ArrayList();
		aircraft.add(one);
		aircraft.add(two);
		Collections.sort(aircraft);
		this.location = location;
	}

	/** Returns the list of aircraft involved. You are not to modify this list. */
	public ArrayList getAircraftInvolved() { return aircraft; }

	/** Returns the location of the collision. You are not to modify this location. */
	public Vector3d getLocation() { return location; }

	/** Returns a hash code for this object. It is based on the hash codes of the aircraft. */

	public int hashCode() {
		int ret = 0;
		for (Iterator iter = aircraft.iterator(); iter.hasNext();) 
			ret += ((Aircraft) iter.next()).hashCode();	
		return ret;
	}

	/** Determines collision equality. Two collisions are equal if they have the same aircraft.*/

	public boolean equals(Object _other) {
		if (_other == this)  return true;
		if (!(_other instanceof Collision)) return false;
		Collision other = (Collision) _other;
		ArrayList a = getAircraftInvolved();
		ArrayList b = other.getAircraftInvolved();
		if (a.size() != b.size()) return false;
		Iterator ai = a.iterator();
		Iterator bi = b.iterator();
		while (ai.hasNext()) 
			if (!ai.next().equals(bi.next())) return false;		
		return true;
	}

	/** Returns a helpful description of this object. */

	public String toString() {
		StringBuffer buf = new StringBuffer("Collision between ");
		boolean first = true;
		for (Iterator iter = getAircraftInvolved().iterator(); iter.hasNext();) {
			if (first) first = false;
			else buf.append(", ");	    
			buf.append(iter.next().toString());
		}
		buf.append(" at "); buf.append(location.toString());
		return buf.toString();
	}
}
