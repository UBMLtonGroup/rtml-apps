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

public class IntegrateExpr extends Expr
{
	private Expr function;
	private Expr lowerBound;
	private Expr upperBound;
	private String var;
	
	public IntegrateExpr(Expr function, String var, Expr lowerBound, Expr upperBound)
	{
		this.function = function;
		this.var = var;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	
	//Override
	public Expr simplify(EvaluationContext e, int expandOption)
	{
		//think about what to do with expandOption
		//if lowerBound simplifies to a number
		//and upperBound simplifies to a number,
		//evaluate it
		
		Expr low = lowerBound.simplify(e,expandOption);
		Expr high = upperBound.simplify(e,expandOption);
		if(low instanceof NumberExpr &&
			high instanceof NumberExpr)
	    {
			return (new command.evaluator.integrator.
				IntegralEvaluator(function,var,(NumberExpr)low,(NumberExpr)high,e,expandOption)).integrate();
		}
		else
		{
			return new IntegrateExpr(function.simplify(e,expandOption),
									 var,low,high);
		}
	}
	//Override
	public String toString(EvaluationContext e)
	{
		return "int(" + function.toString(e) + ", " + var + ", " + lowerBound.toString(e) + ", " + upperBound.toString(e) + ")";
	}
	
	
	//Override
	public void print(int indentLevel)
	{
		printSpaces(indentLevel);
		System.out.println("IntegrateExpr");
		printSpaces(indentLevel + 1);
		System.out.println("Function");
		function.print(indentLevel + 2);
		printSpaces(indentLevel + 1);
		System.out.println("Variable: " + var);
		printSpaces(indentLevel + 1);
		System.out.println("Lower Bound");
		lowerBound.print(indentLevel + 2);
		printSpaces(indentLevel + 1);
		System.out.println("Upper Bound");
		upperBound.print(indentLevel + 2);
	}
		
																  
}
