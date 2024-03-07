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
import command.evaluator.Expr;
import command.evaluator.NumberExpr;
import command.evaluator.RealNumber;

public class IntegralEvaluator extends Integrator {
	private Expr func;
	private NumberExpr lowerBound;
	private NumberExpr upperBound;
	private String varName;
	private int expandOption;

	public IntegralEvaluator(Expr expression, String variable, NumberExpr lowerBound, NumberExpr upperBound,
			EvaluationContext ec, int expandOption) {
		super(ec);
		this.func = expression;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		// this.ec = ec;
		this.varName = variable;
		this.expandOption = expandOption;
	}

	public NumberExpr integrate() {
		return RealNumber.createRealNumber(integrate(lowerBound.getValue(), upperBound.getValue()));
	}

	//Override
	protected void beforeIntegration() {
		ec.beginVarScope();
	}

	//Override
	protected void afterIntegration() {
		ec.endVarScope();
	}

	//Override
	public double function(double param) {
		ec.setVarValue(varName, RealNumber.createRealNumber(param), EvaluationContext.TYPE_NUMBER, EvaluationContext.VAR_EXPAND_ALL);
		Expr simplified = func.simplify(ec, expandOption);
		if (!(simplified instanceof NumberExpr)) { throw new command.evaluator.EvaluationException(
				"The function you are trying to integrate contains undefined variables."); }
		return ((NumberExpr) simplified).getValue();
	}
}
