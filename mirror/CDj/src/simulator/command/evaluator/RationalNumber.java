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
import javacp.util.StringTokenizer;

public class RationalNumber extends RealNumber
{
	long numerator;
	long denominator;
	double tol = .000000000001;
	
	//sign always stored in numerator;
	RationalNumber()
	{
		numerator = 0;
		denominator = 1;
	}
	RationalNumber(long numerator, long denominator)
	{
		if(denominator == 0)
		{
			throw new IllegalArgumentException("You cannot have a zero in the denominator!");
		}
		else if(numerator == 0)
		{
			this.numerator = 0;
			this.denominator = 1;
		}
		if(Math.abs((double)numerator/denominator) < tol)
		{
			this.numerator = 0;
			this.denominator = 1;
		}
		else
		{
			this.numerator = numerator;
			this.denominator = denominator;
			reduce();
		}
	}
	RationalNumber(RationalNumber r)
	{
		//copy constructor
		numerator = r.getNumerator();
		denominator = r.getDenominator();
	}
	RationalNumber(String fraction)
	{
		StringTokenizer getParts = new StringTokenizer(fraction,"/");
		if (getParts.countTokens() == 2)
		{
			numerator = Long.parseLong(getParts.nextToken());
			denominator = Long.parseLong(getParts.nextToken());
		}
		else
		{
			throw new IrreducableNumberException("Invalid fraction");
		}
		reduce();
	}



	public void setNumerator(long numerator)
	{
		
		this.numerator = numerator;
		if (denominator == 0)
		{
			this.denominator = 1;
		}
		reduce();
		
		
	}
	public void setDenominator(long denominator)
	{
		this.denominator = denominator;
		reduce();
	}
	public long getNumerator()
	{
		return numerator;
	}
	public long getDenominator()
	{
		return denominator;
	}

	//Override
	public NumberExpr mod(NumberExpr n)
	{
		if(n instanceof RealNumber)
		{
			if(n instanceof RationalNumber)
			{
				RationalNumber r = (RationalNumber)n;
				return new RationalNumber((numerator * r.getDenominator()) % (denominator * r.getNumerator()),denominator*r.getDenominator());
			}
			else
			{
				return RealNumber.createRealNumber(getRealValue() % n.getRealValue());
			}
		}
		else if(n.getImaginaryValue() == 0)
		{
			return mod(n.getRealPart());
		}
		else
		{
			throw new EvaluationException("The arithmetic modulus of an imaginary number is undefined.");	
		}
	}

	//Override
	public double getRealValue()
	{
		return (double)numerator / denominator;
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
	public Expr simplify(EvaluationContext e, int expandOption)
	{
		return new RationalNumber(this);
	}
	//Override
	public NumberExpr reciprocal()
	{
		if (getNumerator() == 0) { return new RationalNumber(0,1); }
		return new RationalNumber(getDenominator(),getNumerator());
	}
	//Override
	public NumberExpr multiply(NumberExpr n)
	{
		if(n instanceof RationalNumber)
		{
			return new RationalNumber(getNumerator() * ((RationalNumber)(n)).getNumerator(), getDenominator()*((RationalNumber)(n)).getDenominator());
		}
		else if(n instanceof IrrationalNumber)
		{
			return new IrrationalNumber(getValue()*n.getValue());
		}
		else if(n instanceof ComplexNumber)
		{
			return ((ComplexNumber)(n)).multiply(this);
		}
		else
		{
			return null;
		}
	}
	//Override
	public RealNumber multiply(RealNumber n)
	{
		if(n instanceof RationalNumber)
		{
			
			return new RationalNumber(getNumerator() * ((RationalNumber)(n)).getNumerator(), getDenominator()*((RationalNumber)(n)).getDenominator());
		}
		else if(n instanceof IrrationalNumber)
		{
			return new IrrationalNumber(getValue()*n.getValue());
		}
		else
		{
			return null;
		}
	}
	//Override
	public RealNumber divide(RealNumber n) throws IllegalArgumentException
	{
		if(n.isEqualTo(0))
		{
			throw new IllegalArgumentException("You cannot divide by zero!");
		}
		
		return (RealNumber)multiply(n.reciprocal());
	}
	//Override
	public NumberExpr divide(NumberExpr n)
	{
		return multiply(n.reciprocal());
	}
	//Override
	public RealNumber add(RealNumber n)
	{
		//find common denominator
		if(n instanceof RationalNumber)
		{
			if(((RationalNumber)n).getNumerator() == 0)
			{
				return this;
			}
			long denom = lcm(getDenominator(),((RationalNumber)(n)).getDenominator());
			long newNum1 = getNumerator() * denom / getDenominator();
			long newNum2 = ((RationalNumber)(n)).getNumerator() * denom / ((RationalNumber)(n)).getDenominator();
			return new RationalNumber(newNum1 + newNum2,denom);
		}
		else if(n instanceof IrrationalNumber)
		{
			return new IrrationalNumber(n.getValue() + getValue());
		}
		else
		{
			return null;
		}
	}
	//Override
	public NumberExpr add(NumberExpr n)
	{
		//find common denominator
		if(n instanceof RationalNumber)
		{
			long denom = lcm(getDenominator(),((RationalNumber)(n)).getDenominator());
			long newNum1 = getNumerator() * denom / getDenominator();
			long newNum2 = ((RationalNumber)(n)).getNumerator() * denom / ((RationalNumber)(n)).getDenominator();
			return new RationalNumber(newNum1 + newNum2,denom);
		}
		else if(n instanceof IrrationalNumber)
		{
			return new IrrationalNumber(n.getValue() + getValue());
		}
		else if(n instanceof ComplexNumber)
		{
			return ((ComplexNumber)(n)).add(this);
		}
		else
		{
			return null;
		}
	}
	//Override
	public NumberExpr subtract(NumberExpr n)
	{
		return add(n.multiply(-1));
	}
	//Override
	public RealNumber subtract(RealNumber n)
	{
		if(n instanceof RationalNumber)
		{
			return add(new RationalNumber((((RationalNumber)(n)).getNumerator() * -1), ((RationalNumber)(n)).getDenominator()));
		}
		else if (n instanceof IrrationalNumber)
		{
			return new IrrationalNumber(n.getValue()*getValue());
		}
		else
		{
			return null;
		}
	}
	private boolean isEven(long n)
	{
		return(Math.abs(n) % 2 == 0);
	}
	//Override
	public NumberExpr toPower(NumberExpr n)
	{
		if (n instanceof RealNumber)
			return toPower((RealNumber)n);
		ComplexNumber c = (ComplexNumber)n;
		double a = c.getRealValue();
		double b = c.getImaginaryValue();
		double re = Math.pow(getValue(),a)*Math.cos(b*Math.log(getValue()));
		double im = Math.pow(getValue(),a)*Math.sin(b*Math.log(getValue()));
		return new ComplexNumber(re,im);
	}
	//Override
	public NumberExpr toPower(RealNumber n)
	{
		
		if(getValue() >= 0 || Math.abs(n.getValue()) >= 1)
		{
			if (n instanceof RationalNumber)
			{
				if(n.isGreaterThanOrEqualTo(0))
				{
					return new RationalNumber((long)Math.pow(getNumerator(),n.getValue()),(long)Math.pow(getDenominator(),n.getValue()));
				}
				else
				{
					return new RationalNumber((long)Math.pow(getDenominator(),-1*n.getValue()),(long)Math.pow(getNumerator(),-1*n.getValue()));
				}
			}
			else
			{
				return RealNumber.createRealNumber(Math.pow(getValue(),((IrrationalNumber)n).getValue()));
			}
			
		}
		else
		{
			return ComplexNumber.makeComplex(this).toPower(n);
		}
	}

	//Override
	public boolean isGreaterThan(RealNumber n1)
	{
		return (this.getValue() > n1.getValue());
	}
	//Override
	public boolean isLessThan(RealNumber n1)
	{
		return (this.getValue() < n1.getValue());
	}		
	//Override
	public boolean isGreaterThanOrEqualTo(RealNumber n1)
	{
		return (this.getValue() >= n1.getValue());
	}
	//Override
	public boolean isLessThanOrEqualTo(RealNumber n1)
	{
		return (this.getValue() < n1.getValue());
	}	
	//Override
	public boolean isEqualTo(RealNumber n1)
	{
		return (this.getValue() == n1.getValue());
	}
	private RationalNumber createFraction(String number)
	{
		try
		{
			double num = Double.valueOf(number).doubleValue();
			return createFraction(num);
		}
		catch(ArithmeticException ae)
		{
			throw new ArithmeticException("You must enter either a number or a fraction");
		}
		
	}
	private RationalNumber createFraction(double number)
	{
		long numer = 0, denom = 0;
		double num = 0;
		int power = 0;
		int sign = number >= 0 ? 1 : -1;
		number = Math.abs(number);
		long intPart = (long)(number);
		double fracPart = number - intPart;
		if(! (fracPart == 0))
		{
			power = (int)Math.floor(Math.log(fracPart)/Math.log(10.0d));
			num = fracPart * Math.pow(10,power);
			denom = (long)Math.pow(10,-1*power);
			
			while(!((long)num == num))
			{
				num = num * 10;
				denom = denom * 10;
			}
			
			numer = (long)num;
		}
		else
		{
			numer = 0;
			denom = 1;
		}
		numer = sign * (numer + intPart * denom);
		
		return new RationalNumber(numer,denom);
	}

	private void reduce()
	{
		//first, make sure sign is stored in numerator
		if (getDenominator() < 0)
		{
			numerator = -1 * numerator;
			denominator = -1 * denominator;
		}
		//based on Euclid's Algorithm -- p.9 of Algorithms in C++
		long gcd1 = gcd(numerator,denominator);
		numerator = numerator / gcd1;
		denominator = denominator / gcd1;
		if (numerator == 0)
		{
			denominator = 1;
		}
	}
	public static long gcd(long u, long v)
	{
		
		if(u == 1 || v == 1)
		{
			return 1;
		}
		u = Math.abs(u);
		v = Math.abs(v);
		long t=0;
		
		if (v == 0)
		{
			return 1;
		}
		if(u == v)
		{
			return u;
		}
		
		while(u > 0)
		{
			if (u < v)
			{
				t = u;
				u = v;
				v = t;
			}
			u = u - v;
		}
		if (v == 0)
		{
			v = 1;
		}
		return v;
	}
	public static long lcm(long num1, long num2)
	{
		return num1 * num2 / gcd(num1,num2);
	}
	public static int lcm(int num1, int num2)
	{
		return num1 * num2 / (int)gcd(num1, num2);
	}
	//Override
	public double getValue()
	{
		return (double)numerator / (double)denominator;
	}


	//Override
	public String toString(EvaluationContext e)
	{
		if(! e.displayFractions())
		{
			return e.formatNumber(getValue());
		}
		
		String output = "";
		if(denominator == 1 || numerator == 0)
		{
			//numerator is of type long;
			output = String.valueOf(numerator);
		}
		else
		{
			//numerator and denomiator are of type long;
			output = String.valueOf(numerator)+"/"+String.valueOf(denominator);
		}
		return output;
	}
	public int sign(long n)
	{
		return n >= 0 ? 1 : -1;
	}
	//Override
	public void print(int indentLevel)
	{
		printSpaces(indentLevel);
		System.out.println("Rational Number");
		printSpaces(indentLevel + 1);
		System.out.println("Numerator: " + numerator);
		printSpaces(indentLevel + 1);
		System.out.println("Denominator: " + denominator);
	}
}
