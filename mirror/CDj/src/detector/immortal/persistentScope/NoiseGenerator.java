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

/**
 * Noise generator for the detector. The generator lives in the persistent detector scope. 
 * Its constructor runs in the persistent detector scope. Noise is generated in the detector scope 
 * (and the allocated objects live in the detector scope).
 */
public class NoiseGenerator {

	private Object[] noiseRoot;
	private int noisePtr;

	public NoiseGenerator() {
		if (immortal.Constants.DETECTOR_NOISE) {
			noiseRoot = new  Object[immortal.Constants.DETECTOR_NOISE_REACHABLE_POINTERS];
			noisePtr = 0;
		}
	}

	private void generateNoise() {

		for(int i=0 ; i<immortal.Constants.DETECTOR_NOISE_ALLOCATE_POINTERS ; i++) {
			noiseRoot [ ( noisePtr++ ) % noiseRoot.length ] = new byte[immortal.Constants.DETECTOR_NOISE_ALLOCATION_SIZE];
		}
		
	}

	private void generateNoiseWithVariableObjectSize() {

		int currentIncrement = 0;
		int maxIncrement = immortal.Constants.DETECTOR_NOISE_MAX_ALLOCATION_SIZE - immortal.Constants.DETECTOR_NOISE_MIN_ALLOCATION_SIZE;

		for(int i=0 ; i<immortal.Constants.DETECTOR_NOISE_ALLOCATE_POINTERS ; i++) {
			noiseRoot [ ( noisePtr++ ) % noiseRoot.length ] = new byte[ immortal.Constants.DETECTOR_NOISE_MIN_ALLOCATION_SIZE + (currentIncrement % maxIncrement) ];
			currentIncrement += immortal.Constants.DETECTOR_NOISE_ALLOCATION_SIZE_INCREMENT;
		}
	}

	public void generateNoiseIfEnabled() {
		if (immortal.Constants.DETECTOR_NOISE) {

			if (immortal.Constants.DETECTOR_NOISE_VARIABLE_ALLOCATION_SIZE) {
				generateNoiseWithVariableObjectSize();
			} else {
				generateNoise();
			}
		}
	}
}
