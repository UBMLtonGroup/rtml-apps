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

import immortal.ImmortalEntry;

public abstract class PrecompiledSimulator {

	public static void dumpStats() {}

	protected Object[] positions;
	protected Object[] lengths;
	protected Object[] callsigns;

	// args .. ignored
	public static void generate(final String[] args) {

		(new Simulator()).generate();
	}
	
	public void generate() {

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
			}
		}


		if (positions.length < immortal.Constants.MAX_FRAMES) {
			throw new RuntimeException("Not enough frames in pre-compiled simulator.");
		}

		for(int frameIndex=0; frameIndex<immortal.Constants.MAX_FRAMES;frameIndex++) {

			immortal.ImmortalEntry.frameBuffer.putFrame( (float[])positions[frameIndex], 
					(int[])lengths[frameIndex], 
					(byte[])callsigns[frameIndex]);
		}

		if (immortal.Constants.PRESIMULATE) {
			synchronized (ImmortalEntry.initMonitor) {
				ImmortalEntry.simulatorReady = true;
				ImmortalEntry.initMonitor.notifyAll();
			}
		}

	}
}

