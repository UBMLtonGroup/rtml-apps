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

package dump;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class DumpToCSV {

	public static void main(String[] args) {

		try {
			DataInputStream ds = new DataInputStream(new FileInputStream(args[0]));		
			PrintWriter pw = new PrintWriter( new File(args[1]) );

			// the binary format:
			//   nframes <INT>
			//
			//   nplanes <INT> 1
			//   positions <FLOAT> nplanes*3
			//   lengths <INT> nplanes
			//   callsigns_length <INT> 1
			//   callsigns <BYTE> callsigns_length

			int nframes = ds.readInt();

			for(int frameIndex=0; frameIndex<nframes;frameIndex++) {

				int nplanes = ds.readInt();
		
				float[] xpositions = new float [nplanes];
				float[] ypositions = new float [nplanes];
				float[] zpositions = new float [nplanes];

				for(int i=0; i<nplanes; i++) {
					xpositions[i] = ds.readFloat();
					ypositions[i] = ds.readFloat();
					zpositions[i] = ds.readFloat();
				}

				int[] lengths = new int [nplanes];
				for(int i=0;i<nplanes;i++) {
					lengths[i] = ds.readInt();
				}

				int callsigns_length = ds.readInt();
				byte[] callsigns = new byte[callsigns_length];
				ds.read(callsigns);
				int[] callsigns_starts = new int[nplanes];
				callsigns_starts[0] = 0;

				for(int i=1;i<nplanes;i++) {
					callsigns_starts[i] = callsigns_starts[i-1]+lengths[i-1];
				}

				int cpos = 0;
				for(int i=0;i<nplanes;i++) {
					pw.print(frameIndex);
					pw.print(" ");
					pw.print( new String( callsigns, cpos, lengths[i]));
					pw.print(" ");
					pw.print(xpositions[i]);
					pw.print(" ");
					pw.print(ypositions[i]);
					pw.print(" ");
					pw.print(zpositions[i]);
					pw.println();
					cpos += lengths[i];
				}
			}			
			pw.close();
			ds.close();

		} catch (FileNotFoundException e) {
			throw new RuntimeException("Cannot open file with frames binary dump "+e);
		} catch (IOException e) {
			throw new RuntimeException("Error reading frames binary dump "+e);
		}
	}

}
