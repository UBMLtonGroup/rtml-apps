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
public abstract class RealNumber extends NumberExpr
{
	//Override
	public abstract double getValue();
	public abstract RealNumber add(RealNumber n);
	public abstract RealNumber subtract(RealNumber n);
	public abstract RealNumber multiply(RealNumber n);
	public abstract RealNumber divide(RealNumber n);
	
	public abstract boolean isGreaterThanOrEqualTo(RealNumber n);
	public abstract boolean isLessThanOrEqualTo(RealNumber n);
	public abstract boolean isGreaterThan(RealNumber n);
	public abstract boolean isLessThan(RealNumber n);
	public abstract boolean isEqualTo(RealNumber n);

    public boolean isGreaterThanOrEqualTo(double n)
	{
		return isGreaterThanOrEqualTo(createRealNumber(n));
	}
	
	public boolean isLessThanOrEqualTo(double n)
	{
		return isLessThanOrEqualTo(createRealNumber(n));
	}
	public boolean isGreaterThan(double n)
	{
		return isGreaterThan(createRealNumber(n));
	}
	public boolean isLessThan(double n)
	{
		return isLessThan(createRealNumber(n));
	}
	
	public boolean isEqualTo(double n)
	{
		return isEqualTo(createRealNumber(n));
	}
	
	public static RealNumber createRealNumber(double real)
	{
		RealNumber realPart;
		
		if( Math.abs((long)real) - Math.abs(real) == 0)
		{
			realPart = new RationalNumber((long)real,1);
		}
		else
		{
			realPart = new IrrationalNumber(real);
		}
		return realPart;
	}
	public static RealNumber copy(RealNumber r)
	{
		if(r instanceof RationalNumber)
		{
			return new RationalNumber((RationalNumber)r);
		}
		else
		{
			return new IrrationalNumber((IrrationalNumber)r);
		}
	}
}
