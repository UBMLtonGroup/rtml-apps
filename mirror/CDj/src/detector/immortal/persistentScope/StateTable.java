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

package immortal.persistentScope;

import realtime.MemoryArea;

import javacp.util.HashMap;

/**
 * The instance lives and the constructor runs in the persistent detector scope.
 * The put method and the get method are called from the transient detector scope - see below.
 * 
 * previous_state is map call signs to 3D vectors
 *   - the call signs are in persistent detector scope
 *   - the vectors are in persistent detector scope (allocated here)
 */
public class StateTable {

    final private static int MAX_AIRPLANES = 10000;
    
    private Vector3d[] allocatedVectors;
    private int usedVectors;

    /** Mapping Aircraft -> Vector3d. */
    final private HashMap motionVectors = new HashMap();


    StateTable() {
        allocatedVectors = new Vector3d[MAX_AIRPLANES];
        for (int i = 0; i < allocatedVectors.length; i++)
            allocatedVectors[i] = new Vector3d();
        
        usedVectors = 0;
    }


    private class R implements Runnable {
        CallSign callsign;
        float x, y, z;

        public void run() {
            Vector3d v = (Vector3d) motionVectors.get(callsign);
            if (v == null) {
                v = allocatedVectors[usedVectors++];
                motionVectors.put(callsign, v);
            }
            v.x = x;
            v.y = y;
            v.z = z;
        }
    }
    private final R r = new R();

    public void put(final CallSign callsign, final float x, final float y, final float z) {
        r.callsign = callsign;
        r.x = x;
        r.y = y;
        r.z = z;
        MemoryArea.getMemoryArea(this).executeInArea(r);
    }
    
    public Vector3d get(final CallSign callsign) {
    	return (Vector3d) motionVectors.get(callsign);
    }
}
