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

/**  
 * The <code>ZeroVectorException</code> exception is thrown by
 * utilities that perform calculations on vectors. If a particular
 * operation is undefined for the zero vector, a
 * <code>ZeroVectorException</code> exception is thrown. The motivation
 * for this class (as opposed to using a standard <code>ArithmeticException</code>)
 * is to allow filtering based on the type of the exception.
 *
 * @author Ben L. Titzer
 **/ class ZeroVectorException extends ArithmeticException {

	 /**
	  * 
	  */
	 private static final long serialVersionUID = 6064932560449189963L;

	 /**
	  * The only constructor for the <code>ZeroVectorException</code> class
	  * takes a string as an argument and simply calls the super constructor.
	  * @param msg a message describing the operation that caused the exception
	  **/
	 public ZeroVectorException(String msg) {
		 super(msg);
	 }
 }
