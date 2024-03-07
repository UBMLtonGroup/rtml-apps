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
						  
public abstract class NumberExpr extends Expr
{
	public static final RationalNumber ZERO = new RationalNumber(0,1);
	public static final RationalNumber ONE = new RationalNumber(1,1);
	public static final RationalNumber MINUSONE = new RationalNumber(-1,1);
	
	//Override
	public abstract String toString(EvaluationContext e);
	public double getValue() { return getRealValue(); }
	
	public abstract  NumberExpr multiply(NumberExpr n);
	public abstract  NumberExpr divide(NumberExpr n);
	public abstract  NumberExpr add(NumberExpr n);
	public abstract  NumberExpr subtract(NumberExpr n);
	public abstract  NumberExpr mod(NumberExpr n);
	public abstract	 NumberExpr toPower(RealNumber n);
	public abstract  NumberExpr reciprocal();
	
	public abstract double getRealValue();
	public abstract double getImaginaryValue();
	public abstract RealNumber getRealPart();
	public abstract RealNumber getImaginaryPart();
	
	public NumberExpr toPower(NumberExpr n)
	{
		if(n instanceof ComplexNumber)
		{
			return ComplexNumber.makeComplex(this).toPower(n);
		}
		else
		{
			return toPower((RealNumber)n);
		}
	}
	
	public NumberExpr multiply(double n)
	{
		return multiply(RealNumber.createRealNumber(n));
	}
	public NumberExpr divide(double n)
	{
		return divide(RealNumber.createRealNumber(n));
	}
	public NumberExpr add(double n)
	{
		return add(RealNumber.createRealNumber(n));
	}
	public NumberExpr subtract(double n)
	{
		return subtract(RealNumber.createRealNumber(n));
	}
		
	public NumberExpr toPower(double n)
	{
			return toPower(RealNumber.createRealNumber(n));
	}	
	
	public static NumberExpr createNumber(double real, double complex)
	{
		RealNumber realPart = null;
		RealNumber imagPart = null;
		if( Math.abs((long)real) - Math.abs(real) == 0)
		{
			realPart = new RationalNumber((long)real,1);
		}
		else
		{
			realPart = new IrrationalNumber(real);
		}
		
		if( Math.abs((long)complex) - Math.abs(complex) == 0)
		{
			imagPart = new RationalNumber((long)complex,1);
		}
		else
		{
			imagPart = new IrrationalNumber(complex);
		}
		return new ComplexNumber(realPart,imagPart);
	}
		
	public static NumberExpr createNumber(double d)
	{
		return createNumber(d,0);
	}
	public ComplexNumber makeComplex()
	{
		return ComplexNumber.makeComplex(this);
	}
	public boolean isReal()
	{
		return (this instanceof RealNumber || getImaginaryValue() == 0);
	}
	public boolean isComplex()
	{
		return (this instanceof ComplexNumber && getImaginaryValue() != 0);
	}
		
}
