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

public class ComplexNumber extends NumberExpr
{
	
	private RealNumber real;
	private RealNumber imaginary;
	
	// if true, displays a + bi.  Otherwise displays a cis b
	private boolean showRealImag=true;
	
/**************************************************************************************
 *  Class:  ComplexNumber
 *  Method:  ComplexNumber()
 *  Purpose:  To initialize a ComplexNumber object with the default value 0 + 0i
 *  Parameters:  none
 *  Methods Invoked:  none
 ***************************************************************************************/
	public ComplexNumber(ComplexNumber c)
	{
		//copy constructor
		real = RealNumber.copy(c.getRealPart());
		imaginary = RealNumber.copy(c.getImaginaryPart());
	}
	public ComplexNumber()
	{
		// initialize the real portion to 0
		real = RealNumber.createRealNumber(0);
		// initialize the imaginary portion to 0
		imaginary = RealNumber.createRealNumber(0);
		
	}
	
	public ComplexNumber(RealNumber n)
	{
		real = n;
		imaginary = RealNumber.createRealNumber(0);
	}
	public ComplexNumber(RealNumber real, RealNumber imaginary, boolean displayType)
	{
		this.real = real;
		this.imaginary = imaginary;
		showRealImag = displayType;
		
	}
	public ComplexNumber(boolean displayType)
	{
		real = RealNumber.createRealNumber(0);
		imaginary = RealNumber.createRealNumber(0);
		showRealImag = displayType;
	}
	public static ComplexNumber makeComplex(NumberExpr n)
	{
		if (n instanceof ComplexNumber)
			return (ComplexNumber)n;
		else
			return new ComplexNumber((RealNumber)n);
	}
	public void setDisplay(boolean displayType)
	{
		showRealImag = displayType;
	}
	
	
	//Override
	public Expr simplify(EvaluationContext e, int expandOption)
	{
		return new ComplexNumber(real,imaginary);
	}
	
	
	
/**************************************************************************************
 *  Class:   ComplexNumber
 *  Method:  ComplexNumber(RationalNumber real, RationalNumber imaginary)
 *  Purpose:  To create a ComplexNumber object using the data specified.
 *  Parameters:  real - real portion of the complex number
 *			      imaginary - imaginary portion of the complex number
 *  Methods Invoked:
 ***************************************************************************************/
	public ComplexNumber(double real, double imaginary)
	{
		this.real = RealNumber.createRealNumber(real);
		this.imaginary = RealNumber.createRealNumber(imaginary);
		
	}
	public ComplexNumber(RealNumber real, RealNumber imaginary)
	{
		// change the real component
		this.real = real;
		// change the imaginary component
		this.imaginary = imaginary;
	}

 /**************************************************************************************
  *  Class:   ComplexNumber
  *  Method:  getRealPart()
  *  Purpose:  To return the real portion of the current Complex NumberExpr Object
  *  Parameters:  none
  *  Methods Invoked:
  **************************************************************************************/
	//Override
	public RealNumber getRealPart()
	{
		// return the real component
		return real;
	}
	//Override
	public double getRealValue()
	{
		return real.getValue();
	}
	
/**************************************************************************************
 *  Class:   ComplexNumber
 *  Method:  getImaginaryPart()
 *  Purpose:  To return the imaginary portion of the current Complex NumberExpr Object
 *  Parameters:  none
 *  Methods Invoked:
 **************************************************************************************/
	//Override
	public RealNumber getImaginaryPart()
	{
		// return imaginary component
		return imaginary;
	}
	//Override
	public double getImaginaryValue()
	{
		return imaginary.getValue();
	}
/**************************************************************************************

  *  Class:   ComplexNumber
  *  Method:  setImaginaryPart(RationalNumber imaginary)
  *  Purpose:  To set the Imaginary part of the current ComplexNumber object to the
  *			   value passed as a parameter
  *  Parameters:  imaginary - new value of the imaginary part of the current ComplexNumber object
  *  Methods Invoked:

***************************************************************************************/
	public void setImaginaryPart(RealNumber imaginary)
	{
		// set imaginary component
		this.imaginary = imaginary;
		//calcToCIS();
	}
	public void setImaginaryPart(double imaginary)
	{
		this.imaginary = RealNumber.createRealNumber(imaginary);
	}
 /**************************************************************************************
  *  Class:   ComplexNumber
  *  Method:  setRealPart(RationalNumber real)
  *  Purpose:  To set the Real part of the current ComplexNumber object to the
  *			   value passed as a parameter
  *  Parameters:  real - new value of the real part of the current ComplexNumber object
  *  Methods Invoked:
  ***************************************************************************************/
	public void setRealPart(RealNumber real)
	{
		// set the real component
		this.real = real;
		//calcToCIS();
	}
	public void setRealPart(double real)
	{
		this.real = RealNumber.createRealNumber(real);
	}
  /**************************************************************************************
  *  Class:   ComplexNumber
  *  Method:  getMaginutde()
  *  Purpose:  To return the magintude (modulus) of the number ( = sqrt(a^2 + b^2) )
  *  Parameters:  none
  *  Methods Invoked:
  **************************************************************************************/
	public RealNumber getMagnitude()
	{
		// return the real component
		return RealNumber.createRealNumber(
			Math.pow(
				real.multiply(real)
					.add(imaginary.multiply(imaginary))
					.getValue(),.5));
	}
/**************************************************************************************
 *  Class:   ComplexNumber
 *  Method:  getImaginaryPart()
 *  Purpose:  To return the imaginary portion of the current Complex NumberExpr Object
 *  Parameters:  none
 *  Methods Invoked:
 **************************************************************************************/
	public RealNumber getAngle()
	{
		double angle = 0;
			if (real.isGreaterThanOrEqualTo(0) && imaginary.isGreaterThanOrEqualTo(0))
				// first quadrant
				angle = (Math.acos(real.divide(getMagnitude()).getValue()));		
			else if (real.isLessThan(0) && imaginary.isGreaterThanOrEqualTo(0))
				// second quadrant
				angle = (Math.PI - Math.asin(imaginary.divide(getMagnitude()).getValue()));		
			else if (real.isLessThan(0) && imaginary.isLessThan(0))
				// third quadrant
				angle = (Math.PI + Math.acos(real.multiply(-1).divide(getMagnitude()).getValue()));		
			else if (real.isGreaterThanOrEqualTo(0) && imaginary.isLessThan(0))
				// fourth quadrant
				angle = (2*Math.PI-Math.acos(real.divide(getMagnitude()).getValue()));		
		
		return RealNumber.createRealNumber(angle);
	}
/**************************************************************************************

  *  Class:   ComplexNumber
  *  Method:  setImaginaryPart(RationalNumber imaginary)
  *  Purpose:  To set the Imaginary part of the current ComplexNumber object to the
  *			   value passed as a parameter
  *  Parameters:  imaginary - new value of the imaginary part of the current ComplexNumber object
  *  Methods Invoked:

***************************************************************************************/
	public void setAngleMagnitude(RealNumber magnitude, RealNumber angle)
	{
		// set imaginary component
		//this.magnitude=magnitude;
		calcFromCIS(magnitude,angle);
	}
	public void setAngleMagnitude(double magnitude, double angle)
	{
		calcFromCIS(RealNumber.createRealNumber(magnitude),
					RealNumber.createRealNumber(angle));
	}
	//Override
	public NumberExpr divide(NumberExpr n)
	{
		return multiply(n.reciprocal());
	}
	//Override
	public NumberExpr multiply(NumberExpr n)
	{
		ComplexNumber n2 = makeComplex(n);
		return multiply(n2);
		
	}
/**************************************************************************************
 *  Class:   ComplexNumber
 *  Method:  add(ComplexNumber x)
 *  Purpose:  To add the Current Complex NumberExpr to another and return the result
 *  Parameters: x - the complex number to be added
 *  Methods Invoked:
 ***************************************************************************************/
	//Override
	public NumberExpr add(NumberExpr x)
	{
		RealNumber realPart;
		RealNumber imaginaryPart;
		
		if(x instanceof ComplexNumber)
		{
			// add the real components
			realPart = real.add(((ComplexNumber)(x)).real);
			// add the imaginary components
			imaginaryPart = imaginary.add(((ComplexNumber)(x)).imaginary);
			// return the result
			
		}
		else
		{
			realPart = ((RealNumber)(x)).add(getRealPart());
			imaginaryPart = getImaginaryPart();
		}
									   
		return new ComplexNumber(realPart, imaginaryPart);
	}
		
	//Override
	public NumberExpr subtract(NumberExpr x)
	{
		return add(x.multiply(-1));
	}
	//Override
	public NumberExpr mod (NumberExpr x)
	{
		
		if(imaginary.getImaginaryValue() != 0 || x.getImaginaryValue() != 0)
		{
			throw new EvaluationException("The arithmetic modulus is not defined for imaginary numbers.");
		}
		else
		{
			return getRealPart().mod(x.getRealPart());
		}
			
			
	}
	
/**************************************************************************************
 *  Class:   ComplexNumber
 *  Method:  multiply(ComplexNumber x)
 *  Purpose:  To multiply the Current Complex NumberExpr by another and return the result
 *  Parameters: x - the complex number to be multiplied by
 *  Methods Invoked:
 ***************************************************************************************/
	public ComplexNumber multiply(ComplexNumber x)
	{
		// perform the multiplication
		RealNumber realPart = real.multiply(x.real).subtract(imaginary.multiply(x.imaginary)); // find real component
		RealNumber imaginaryPart = real.multiply(x.imaginary).add(imaginary.multiply(x.real)); // find imaginary component
		// return the result
		return new ComplexNumber(realPart,imaginaryPart);
	}
	public ComplexNumber multiply(RealNumber x)
	{
		RealNumber realPart = real.multiply(x);
		RealNumber imaginaryPart = imaginary.multiply(x);
		return new ComplexNumber(realPart,imaginaryPart);
	}
	public ComplexNumber divide(ComplexNumber x)
	{
	// returns current complex number divided by x
	RealNumber realPart = (RealNumber)(real.multiply(x.real).add(imaginary.multiply(x.imaginary)).divide((x.real.toPower(2).add(x.imaginary.toPower(2)))));
	RealNumber imaginaryPart = (RealNumber)(imaginary.multiply(x.real).subtract(real.multiply(x.imaginary)).divide(x.real.toPower(2).add(x.imaginary.toPower(2))));
	return new ComplexNumber(realPart,imaginaryPart);
	}
	public ComplexNumber divide (RealNumber x)
	{
		RealNumber realPart = real.divide(x);
		RealNumber imaginaryPart = imaginary.divide(x);
		return new ComplexNumber(realPart,imaginaryPart);
	}
	//Override
	public NumberExpr reciprocal()
	{
	
	RealNumber realPart = (RealNumber)real.divide(real.toPower(2).add(imaginary.toPower(2)));
	RealNumber imaginaryPart = (RealNumber)imaginary.multiply(-1).divide(real.toPower(2).add(imaginary.toPower(2)));
	return new ComplexNumber(realPart, imaginaryPart);
	}
	
/**************************************************************************************
 *  Class:   ComplexNumber
 *  Method:  congugate()
 *  Purpose:  To return the congugate of the current Complex NumberExpr
 *  Parameters: none
 *  Methods Invoked:
 ***************************************************************************************/
	public ComplexNumber congugate()
	{
		RealNumber imaginaryPart;
		// change the sign of the imaginary portion of the complex number,
		// store it in a temporary variable
		imaginaryPart = (RealNumber)imaginary.multiply(-1);
		// return the new complex number
		return new ComplexNumber(real, imaginaryPart);
	}
/**************************************************************************************
 *  Class:   ComplexNumber
 *  Method:  mod()
 *  Purpose:  To find the modulus of the current complex number
 *  Parameters: none
 *  Methods Invoked:
 ***************************************************************************************/
	public RealNumber mod()
	{
		// initialize variable to store the result of the operation
		RealNumber result;
		// perform the operation, store the result
		result = (RealNumber)real.toPower(2).add(imaginary.toPower(2)).toPower(.5);
		// return the result
		return result;
	} // end of mod method
	//Override
	public boolean isReal()
	{
		return(getImaginaryPart().isEqualTo(0));
	}
	public boolean isImaginary()
	{
		return ! isReal();
	}
	public RealNumber round(RealNumber number,int places)
	{
		double value = number.getValue();
		return RealNumber.createRealNumber(Math.floor((5*Math.pow(10,-1*(places+1))+value)*Math.pow(10,places))/Math.pow(10,places));
	}
 /**************************************************************************************
  *  Class:   ComplexNumber
  *  Method:  toString()
  *  Purpose:  Display the complex number in the traditional format
  *  Parameters:  none
  *  Methods Invoked:  none
  **************************************************************************************/
	//Override
	public String toString(EvaluationContext e)
	{
		RealNumber realToShow = null;
		RealNumber imagToShow = null;
		RealNumber angleToShow = null;
		RealNumber magToShow = null;
			
		if(showRealImag)
		{
			realToShow = getRealPart();
			imagToShow = getImaginaryPart();
		}
		else
		{
			angleToShow = getAngle();
			magToShow = getMagnitude();
		}
		
		String result="";
		// determine how to display the number
		if (showRealImag)
		
		{
			String real;
			String imag;
					
			if(getRealValue() == 0)
			{
				real = null;
			}
			else
			{
				real = realToShow.toString(e);
			}
			
			if(getImaginaryValue() == 0)
			{
				imag = null;
			}
			else if(getImaginaryValue() > 0)
			{
				if(real != null)
				{
					imag = " + " + ((getImaginaryValue() != 1) ? imagToShow.toString(e) : "" ) + " i";
				}
				else
				{
					imag = ((getImaginaryValue() != 1) ? imagToShow.toString(e) : "" )+ " i";
				}
			}
			else
			{
				if(real != null)
				{
					if(getImaginaryValue() == -1)
					{
						imag = " - i";
					}
					else
					{
						imag = " - " + imagToShow.multiply(NumberExpr.MINUSONE) + "i";
					}					
				}
				else
				{
					if(getImaginaryValue() == -1)
					{
						imag = "-i";
					}
					else
					{
						imag = imagToShow + " i";
					}
				}
			}
			if(real == null & imag == null)
			{
				result = "0.0";
			}
			else
			{
				result = (real != null ? real : "") + (imag != null ? imag : "");
			}
		}
		else
		{
			result = magToShow + " cis " + angleToShow;
		}
		return result;
	} // end of toString method
	
		public void calcFromCIS(RealNumber magnitude, RealNumber angle)
		{
			real = (RealNumber)magnitude.multiply(Math.cos(angle.getValue()));
			imaginary = (RealNumber)magnitude.multiply(Math.sin(angle.getValue()));
		}
		//Override
		public NumberExpr toPower(RealNumber power)
		{
			ComplexNumber result = new ComplexNumber();
			// find new angle and magnitude
			result.setAngleMagnitude((RealNumber)getMagnitude().toPower(power),getAngle().multiply(power));
			// find new angle
			return result;
		}
		//Override
		public NumberExpr toPower(NumberExpr power)
		{
			if(power instanceof RealNumber)
				return toPower((RealNumber)power);
			double r = getMagnitude().getValue();
			double theta = getAngle().getValue();
			double c = ((ComplexNumber)power).getRealValue();
			double d = ((ComplexNumber)power).getImaginaryValue();
			double re = Math.pow(r,c) * Math.exp(-d*theta)*Math.cos(d*Math.log(r)+c*theta);
			//re = r^c*(e^(-d*theta))*cos(d ln(r) + c*theta)
			double im = Math.pow(r,c) * Math.exp(-d*theta)*Math.sin(d*Math.log(r)+c*theta);
			return new ComplexNumber(re,im);
		}

		public ComplexNumber[] findRoots (int root)
		{
			if(root == 0)
			{
				throw new IllegalArgumentException("The root must be non-zero.");
			}
			// initialize result as an array of complex numbers
			ComplexNumber[] result=new ComplexNumber[root];
			
			// find first root
			
			// power that number will be raised
			RealNumber rootMagnitude = (RealNumber)getMagnitude().toPower(1/root);
			// the angle as we move around the unit circle
			
			// calculate theta for the first root
			
			// this stems from the fact that
			// (a cis b)^p = a^p cis (b/a).  
			RealNumber rotAngle = (RealNumber)getAngle().divide(root); // same as angle * (1/root)
			
			RealNumber theta;
			
			// find others by moving around the unit circle
			for(int counter=0 ;counter<root;counter++)
			{
				// calculate the current position around the unit circle
				theta = rotAngle.add(RealNumber.createRealNumber(counter*(2*Math.PI)/root));
				
				// initialize Complex NumberExpr object
				result[counter]=new ComplexNumber();
				
				// store magnitude and angle
				result[counter].setAngleMagnitude(rootMagnitude, theta);
			}
				
			return result;
		}
public static ComplexNumber[] solveQuadratic (NumberExpr a, NumberExpr b, NumberExpr c)
		{
			// initialize result as an array of complex numbers
			// solves equation using x =(-b +/- sqrt(b^2 - 4*a*c))/(2a)	
			
			ComplexNumber numa;
			ComplexNumber numb;
			ComplexNumber numc;
			if(a instanceof ComplexNumber)
				numa = (ComplexNumber)a;
			else
				numa = new ComplexNumber((RealNumber)a);
			
			if(b instanceof ComplexNumber)
				numb = (ComplexNumber)b;
			else
				numb = new ComplexNumber((RealNumber)b);
			
			if(c instanceof ComplexNumber)
				numc = (ComplexNumber)c;
			else
				numc = new ComplexNumber((RealNumber)c);
			
			
			ComplexNumber[] result=new ComplexNumber[2];
			ComplexNumber discriminant = (ComplexNumber)numb.toPower(2).subtract(numa.multiply(4).multiply(numc)).toPower(.5);
			
			result[0] = (ComplexNumber)discriminant.add(numb.multiply(-1)).divide(a.multiply(2));
			result[1] = (ComplexNumber)discriminant.multiply(-1).add(numb.multiply(-1)).divide(a.multiply(2));	
			
			return result;
		}				
private static String repeatStr(String characters,int numTimes)
{
	// when I first conceived the idea of creating this method,
	// I thought that a for loop would be ideal.  However, for some reason,
	// I could not get the for loop to work, so I changed it to a while loop.
	
	// stores the result of the method ; 
	String result="";
	
	int n = 1; // counter variable
	while(n <= numTimes)
	{
		
		//append the string to the end of "result"
		result=result+characters;
		
		//augment the counter
		n++;
	} // end of while loop
	return result;
} // end of repeatStr method				

//Override
public double getValue()
{
	return real.getValue() + imaginary.getValue() * Math.sqrt(-1);
}
//Override
public void print(int indentLevel)
{
	printSpaces(indentLevel);
	System.out.println("Complex Number");
	printSpaces(indentLevel + 1);
	System.out.println("Real Part: " + real);
	printSpaces(indentLevel + 1);
	System.out.println("Imaginary Part: " + imaginary);
}
	
} // end of ComplexNumber class
