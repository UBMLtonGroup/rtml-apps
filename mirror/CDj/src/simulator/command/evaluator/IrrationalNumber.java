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
//import java.text.NumberFormat;
public class IrrationalNumber extends RealNumber
{
	public static final NumberExpr PI = new IrrationalNumber(Math.PI);
	public static final NumberExpr e = new IrrationalNumber(Math.E);
	private double number = 0.0;
										
	IrrationalNumber()
	{
		number = 0;
	}
					 
	IrrationalNumber(double n)
	{
		number = n;
		
	}
	IrrationalNumber(IrrationalNumber ir)
	{
		//copy constructor
		number = ir.getValue();
	}
	//Override
	public double getRealValue()
	{
		return number;
	}
	//Override
	public double getImaginaryValue()
	{
		return 0.0;
	}
	//Override
	public RealNumber getRealPart()
	{
		return this;
	}
	//Override
	public RealNumber getImaginaryPart()
	{
		return NumberExpr.ZERO;
	}
	//Override
	public Expr simplify(EvaluationContext e,int expandOption)
	{
		return new IrrationalNumber(this);
	}
	//Override
	public NumberExpr toPower(RealNumber n)
	{
		return new IrrationalNumber(Math.pow(getValue(),n.getValue()));
	}
	//Override
	public NumberExpr reciprocal() throws IllegalArgumentException
	{
		if (getValue() == 0)
		{
			throw new IllegalArgumentException("You cannot divide by zero!");
		}

		return new IrrationalNumber(1/getValue());
	}
	//Override
	public NumberExpr multiply(NumberExpr n)
	{
		if(n instanceof ComplexNumber)
		{
			return ((ComplexNumber)(n)).multiply(getValue());
		}
		else if (n instanceof RealNumber)
		{
			return new IrrationalNumber(n.getValue() * getValue());
		}
		else
		{
			return null;
		}
	}
	//Override
	public RealNumber multiply(RealNumber n)
	{
		return RealNumber.createRealNumber(n.getValue() * getValue());
		
	}
	//Override
	public NumberExpr divide(NumberExpr n)
	{
		return RealNumber.createRealNumber(getValue() / n.getValue());
	}
	public void setValue(double n)
	{
		this.number = n;
	}
	//Override
	public double getValue()
	{
		return number;
	}
	//Override
	public RealNumber divide(RealNumber n)
	{
		return RealNumber.createRealNumber(getValue()/n.getValue());
	}
	
	//Override
	public NumberExpr add(NumberExpr n)
	{
		if (n instanceof RealNumber)
		{
			return add((RealNumber)n);
		}
		else if(n instanceof ComplexNumber)
		{
			return add(n);
		}
		else
		{
			return null;
		}
	}
		//Override
		public RealNumber add(RealNumber n)
		{
			return new IrrationalNumber(getValue() + n.getValue());
		}
		//Override
		public RealNumber subtract(RealNumber n)
		{
			return (RealNumber)add(n.multiply(-1));
		}
	//Override
	public NumberExpr subtract(NumberExpr n)
	{
		return add(n.multiply(-1));
	}
	//Override
	public NumberExpr mod(NumberExpr n)
	{
		if(n instanceof RealNumber)
		{
			return RealNumber.createRealNumber(number % n.getRealValue());
		}
		else if(((ComplexNumber)n).getImaginaryValue() == 0)
		{
			return mod(n.getRealPart());	
		}
		else
		{
			throw new EvaluationException("The arithmetic modulus is not defined for imaginary numbers.");
		}
		
	}
	//Override
	public boolean isGreaterThan(RealNumber a)
	{
		return(getValue() > a.getValue());
	}
		//Override
		public boolean isLessThan(RealNumber a)
	{
		return(getValue() < a.getValue());
	}
		//Override
		public boolean isGreaterThanOrEqualTo(RealNumber a)
	{
		return(getValue() >= a.getValue());
	}
		//Override
		public boolean isLessThanOrEqualTo(RealNumber a)
	{
		return(getValue() <= a.getValue());
	}
		//Override
		public boolean isEqualTo(RealNumber a)
		{
			return(getValue() == a.getValue());
		}	
			
	//Override
	public NumberExpr toPower(NumberExpr n)
	{
		if(n instanceof RealNumber)
		{
			return new IrrationalNumber(Math.pow(getValue(),n.getValue()));
		}
		else
		{
			ComplexNumber val = (ComplexNumber)n;
			RealNumber re = val.getRealPart();
			RealNumber im = val.getImaginaryPart();
			ComplexNumber Ca = ComplexNumber.makeComplex(re).toPower(n).makeComplex();
			ComplexNumber Cb = ComplexNumber.makeComplex(im).toPower(n).makeComplex();

			//if number = 1/2 and power = 1/2, for example, result will be complex
			//I am treating the general case here
			RealNumber x = Ca.getRealPart();
			RealNumber y = Ca.getImaginaryPart();
			RealNumber l = Cb.getRealPart();
			RealNumber m = Cb.getImaginaryPart();
			double q;
			double r;
			if(m.getValue() == 0)
			{
				if(l.getValue() < 0)
				{
					q = Math.log(Math.abs(l.getValue()));
					r = Math.PI;
				}
				else
				{
					q = Math.log(l.getValue());
					r = 0;
				}
			}
			else
			{
				q = Math.log(l.getValue()*l.getValue() + m.getValue()*m.getValue())/2;
				r = sign(m.getValue())*Math.PI/2 - Math.atan(l.getValue()/m.getValue());
			}
			
			RealNumber realPart = RealNumber.createRealNumber(Math.exp(-r)*(x.getValue()*Math.cos(q) - y.getValue()*Math.sin(q)));
			RealNumber imagPart = RealNumber.createRealNumber(Math.exp(-r)*(y.getValue()*Math.cos(q) + x.getValue()*Math.sin(q)));
					   
			//see toPower() in RationalNumber
			return new ComplexNumber(realPart,imagPart);
		}
	}
	public double sign(double d)
	{
		if(d >= 0)
		{
			return 1;
		}
		else
		{
			return -1;
		}
	}
	//Override
	public String toString(EvaluationContext e)
	{
		return e.formatNumber(number);
	}
	//Override
	public void print(int indentLevel)
	{
		printSpaces(indentLevel);
		System.out.println("Irrational Number");
		printSpaces(indentLevel + 1);
		System.out.println("Value: " + number);
	}
		
	
}
