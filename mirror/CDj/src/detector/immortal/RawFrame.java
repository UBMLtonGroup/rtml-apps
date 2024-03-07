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

/**
 *
 */
package immortal;

public class RawFrame {
    static private int MAX_PLANES = 1000;
    static private int MAX_SIGNS = 10 * MAX_PLANES;

    public final int[] lengths = new int[MAX_PLANES];
    public final byte[] callsigns = new byte[MAX_SIGNS];
    public final float[] positions = new float[3 * MAX_PLANES];
    public int planeCnt;

    public void copy(final int[] lengths_, final byte[] signs_, final float[] positions_) {
        for (int i = 0, pos = 0, pos2 = 0, pos3 = 0, pos4 = 0; i < lengths_.length; i++) {
            lengths[pos++] = lengths_[i];
            positions[pos2++] = positions_[3 * i];
            positions[pos2++] = positions_[3 * i + 1];
            positions[pos2++] = positions_[3 * i + 2];
            for (int j = 0; j < lengths_[i]; j++)
                callsigns[pos3++] = signs_[pos4 + j];
            pos4 += lengths_[i];
        }
        planeCnt = lengths_.length;
    }
}