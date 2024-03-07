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

package command.evaluator;

import java.io.IOException;
import java.io.InputStream;
public class StringInputStream extends InputStream
	{
		private String s;
		private int position;
		private int markSize;
		private int markPos;
		StringInputStream(String s)
		{
			this.s = s;
			position = 0;
			markSize = 0;
			markPos = 0;
		}
		//Override
		public int available()
		{
			return s.length() - position;
		}
		//Override
		public void mark(int size)
		{
			markPos = position;
			markSize = size;
		}
		//Override
		public boolean markSupported() { return true; }
		
		//Override
		public int read() throws IOException
		{
			if(position >= s.length())
			{
				return -1;
			}
			return s.charAt(position++);
		}
		public int read(char[] dest, int offset, int size)
		{
			if(position + size  >= s.length())
			{
				//return the rest of the string
				s.getChars(position,s.length(),dest,offset);
				position = s.length();
				return -1;
			}
			else
			{
				//only return size;
				s.getChars(position,position + size,dest,offset);
				position += size;
				return size;
			}
			//return s[position] to s[position+size-1]
			//start at byte[offset]
			//return # of bytes read or -1 if reach end of string
			//if dest == null, throw the bytes away
		}
		public int read(char[] dest) //throws IOException
		{
			return read(dest,0,s.length());
		}
		//Override
		public synchronized void reset() throws IOException
		{
			if (position <= markPos + markSize)
			{
				position = markPos;
			}
			else
			{
				throw new IOException("Read too far past mark.");
			}

		}
		//Override
		public long skip(long offset) //throws IOException
		{
			if (position + offset >= s.length())
			{
				position = s.length();
				return s.length() - position;
			}
			else
			{
				position += offset;
				return offset;
			}
		}
}