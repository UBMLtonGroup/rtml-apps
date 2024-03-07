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

package command.evaluator.integrator;
import command.evaluator.EvaluationContext;

public abstract class Integrator
{
	private int maxSteps; //max # of steps = 2^maxSteps
	protected EvaluationContext ec;
	
	Integrator(EvaluationContext ec)
	{
		maxSteps = 20;
		this.ec = ec;
	}

protected abstract double function(double param);
	
public double integrate(double a, double b) throws TooManyStepsException
{
	
	beforeIntegration();
	IterativeTrapIntegrator inttrap = new IterativeTrapIntegrator(a,b);
	int j;
	double s, st, oldst, olds;
	oldst = olds = -1.0e30;
	for(j = 1; j <= maxSteps; j++)
	{
		st = inttrap.iterate();
		s = (4.0*st - oldst)/3.0; 
		if(Math.abs(s - olds) < ec.getTol()*Math.abs(olds))
		{
			afterIntegration();
			return s;
		}
		olds = s;
		oldst = st;
	}
	afterIntegration();
	throw new TooManyStepsException();

}

protected void beforeIntegration() { }
protected void afterIntegration() { }


		
	

	class IterativeTrapIntegrator
	{
		//adapted from Numerical Recipes in C, p. 137

        private int curIter;
		private double s;
		double b;
		double a;
		
		IterativeTrapIntegrator(double a, double b)
		{
			this.b = b;
			this.a = a;
			curIter = 1;
			s = 0;
		}
		/*
		This computes the "curIter"th stage of refinement of an
		extended trapezoidal rule.  When curIter = 1, the routine
		returns the crudest estimate of the integral from a to b.
		Subsequent calls with curIter = 2,3,4,.. in that order will
		improve the accuracy by adding 2^(n-2) additional interior points
		*/
		public double iterate()
		{
			double x, tnm, sum, del;
			int it, j;
			if(curIter == 1)
			{
				curIter++;
				return ( s = 0.5*(b-a)*(function(a) + function(b)));
			}
			else
			{
				for(it = 1, j= 1; j < curIter - 1; j++)
				{
					it <<= 1;
				}
				
				tnm = it;
				del = (b - a)/tnm;	//this is the spacing of the points to be added
				x = a + .5*del;
				for(sum = 0.0,j = 1; j <= it; j++,x+=del)
				{
					sum += function(x);
					if(Double.isInfinite(sum))
						break;
				}
				
				s = .5*(s + (b-a)*sum / tnm);
				curIter++;
				
				return s;
			}
		}
	}	
			
		
		
}


