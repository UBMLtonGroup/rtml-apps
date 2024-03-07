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

package command.evaluator.functions;

import command.evaluator.ComplexNumber;
import command.evaluator.EvaluationContext;
import command.evaluator.EvaluationException;
import command.evaluator.Expr;
import command.evaluator.Function;
import command.evaluator.integrator.TooManyStepsException;

public class Factorial extends Function {
	public Factorial(Expr inside) {
		super(inside);
	}

	//Override
	public double getValue(double d, EvaluationContext ec) throws TooManyStepsException {
		if ((int) d == d) {
			return factorial((int) d);
		} else {
			return Math.exp(gammaln(d + 1.0));
		}
	}

	//Override
	public ComplexNumber getValue(ComplexNumber c, EvaluationContext ec) {
		throw new EvaluationException("Imaginary arguments are not supported for this function.");
	}

	//Override
	public String getName() {
		return "Factorial";
	}
	//Override
	public Expr make(Expr e) { return new Factorial(e); }

	// overrides Function class method
	//Override
	public String toString() {
		return inside.toString() + "!";
	}

	// based on Numerical Recipes in C, p. 214
	private static final double coef[] = { 76.18009172947146, -86.50532032941677, 24.01409824083091,
			-1.231739572450155, 0.1208650973866179e-2, -0.5395239384953e-5 };

	private double gammaln(double xx) throws EvaluationException {
		double x, y, tmp, ser;
		int j;
		y = x = xx;
		tmp = x + 5.5;
		tmp -= (x + 0.5) * Math.log(tmp);
		ser = 1.000000000190015;
		for (j = 0; j <= 5; j++) {
			ser += coef[j] / ++y;
		}
		return -tmp + Math.log(2.5066282746310005 * ser / x);
	}

	private static double factorials[] = initFactorials();
	private static int ntop = 4;

	private static double[] initFactorials() {
		double[] temp = new double[33];
		temp[0] = 1.0;
		temp[1] = 1.0;
		temp[2] = 2.0;
		temp[3] = 6.0;
		temp[4] = 24.0;
		return temp;
	}

	// based on Numerical Recipes in C, p. 214
	public double factorial(int n) {
		int j;
		if (n < 0) throw new EvaluationException("You cannot take the factorial of a negative number.");

		if (n > 32) { return Math.exp(gammaln(n + 1.0)); }
		// expand table, if necessary
		while (ntop < n) {
			j = ntop++;
			factorials[ntop] = factorials[j] * ntop;
		}
		return factorials[n];
	}
}
