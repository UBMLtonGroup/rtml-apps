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

/** CallSign (name) of the plane. Constructor runs and instance lives in the persistent 
 * detector scope, so that call signs can be linked in the (persistent) state - StateTable.
 */
public class CallSign {

	final private byte[] val;

	public CallSign(final byte[] v) {
		val = v;
	}

	/** Returns a valid hash code for this object. */
	public int hashCode() {
		int h = 0;
		for(int i=0; i<val.length; i++) {
			h += val[i];
		}
		return h;
	}

	/** Performs a comparison between this object and another. */
	public boolean equals(final Object other) {
		if (other == this) return true;
		else if (other instanceof CallSign) {
			final byte[] cs = ((CallSign) other).val;
			if (cs.length != val.length) return false;
			for (int i = 0; i < cs.length; i++)
				if (cs[i] != val[i]) return false;
			return true;
		} else return false;
	}

	/** Performs comparison with ordering taken into account. */
	public int compareTo(final Object _other) throws ClassCastException {
		final byte[] cs = ((CallSign) _other).val;
		if (cs.length < val.length) return -1;
		if (cs.length > val.length) return +1;
		for (int i = 0; i < cs.length; i++)
			if (cs[i] < val[i]) return -1;
			else if (cs[i] > val[i]) return +1;
		return 0;
	}
}
