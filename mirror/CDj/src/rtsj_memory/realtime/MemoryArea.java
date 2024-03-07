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


package realtime;

public class MemoryArea extends MemoryAreaWrapper {

	private final javax.realtime.MemoryArea realArea;

	boolean isReal() {
		return true;
	}


	Object getRealArea() {
		return realArea;
	}

	protected MemoryArea ( javax.realtime.MemoryArea realArea ) {
		this.realArea = realArea;
	}

	public static MemoryArea getMemoryArea(Object o) {
		return new MemoryArea( javax.realtime.MemoryArea.getMemoryArea(o) );
		// note that we cannot easily have a HashMap mapping RTSJ Area -> Wrapper Area,
		// because of memory reference restrictions ... where would the HashMap live ?

		// maybe we could instead somehow extend RTSJ areas to hold the reference to the
		// Wrapper Area ; maybe it would confuse the RTSJ implementation, I don't know
	}

	public void executeInArea(Runnable logic) {
		realArea.executeInArea( logic );
	}

	public void enter(Runnable logic) {
		realArea.enter(logic);
	}

	public long memoryConsumed() {
		return realArea.memoryConsumed();
	}

	public long memoryRemaining() {
		return realArea.memoryRemaining();
	}

	public Object newInstance(Class type) throws IllegalAccessException,
	InstantiationException  {

		return realArea.newInstance(type);
	}

	public String toString() {
		return "proxy memory area of "+realArea.toString();
	}

	public int hashCode() {
		return realArea.hashCode();
	}

	public boolean equals(Object obj) {

		if (obj instanceof MemoryArea) {
			return realArea.equals( ((MemoryArea)obj).realArea);
		} else {
			return realArea.equals(obj);  
		}
	}

}
