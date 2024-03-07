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

public class CreateListExpr extends Expr implements Summable, Multipliable
{
	Expr expression;
	String var;
	Expr startExpr;
	Expr endExpr;
	Expr incrementExpr;
	
	public CreateListExpr(Expr expression, 
						  Variable var, 
						  Expr start,
						  Expr end,
						  Expr increment)
	{
		this.expression = expression;
		this.var = (var).getName();
		this.startExpr = start;
		this.endExpr = end;
		this.incrementExpr = increment;
	}
	
	public Expr getSum(EvaluationContext e, int expandOption)
	{
		//Expr simplified = simplify(e,expandOption)
		Expr simplified = simplify(e,expandOption);
		if (simplified instanceof CreateListExpr)
		{
			return new SumExpr((CreateListExpr)simplified);
		}
		else
		{
			return ((List)simplified).getSum(e,expandOption);
		}
	}
	
	public Expr getProduct(EvaluationContext e, int expandOption)
	{
		//Expr simplified = simplify(e,expandOption)
		Expr simplified = simplify(e,expandOption);
		if (simplified instanceof CreateListExpr)
		{
			return new SumExpr((CreateListExpr)simplified);
		}
		else
		{
			return ((List)simplified).getSum(e,expandOption);
		}
	}
	//Override
	public Expr simplify(EvaluationContext e, int expandOption)
	{
		int expand;
		if(expandOption == VAR_EXPAND_NONE)
		{
			expand = VAR_EXPAND_CUR_SCOPE;
		}
		else
		{
			expand = VAR_EXPAND_ALL;
		}
		Expr startExpr = this.startExpr.simplify(e,expandOption);
		Expr endExpr = this.endExpr.simplify(e,expandOption);
		Expr incrementExpr = this.incrementExpr.simplify(e,expandOption);
		
		double start;
		double end;
		double increment;
		
		if(startExpr instanceof NumberExpr &&
		   incrementExpr instanceof NumberExpr &&
		   endExpr instanceof NumberExpr)
		{
			//in the future, this should not be necessary,
			//as the expr class would probably have
			//built-in add, subtract, multiply, and devide functions
			start = ((NumberExpr)startExpr).getValue();
			end = ((NumberExpr)endExpr).getValue();
			increment = ((NumberExpr)incrementExpr).getValue();
		}
		else
		{
			return new CreateListExpr(expression,new Variable(var),startExpr,endExpr,incrementExpr);
		}
		
		//do variable checking
		if(increment == 0 ||
		   increment < 0 && end > start ||
		   increment > 0 && end < start)
		{
			throw new IllegalArgumentException("The parameters to 'seq' were invalid.\nThe correct syntax is seq(Expression,variable,start,end,increment)");
		}
		
		//unclear what to do if expandOption = EXPAND_CUR_SCOPE
		ExprList theList = null;
		ExprList curList = null;
		e.beginVarScope();
		double i = start;
		do
		{
			e.setVarValue(var,RealNumber.createRealNumber(i),VariableTable.TYPE_LIST,expand);
			ExprList nextList = new ExprList(expression.simplify(e,expand));
			if(theList == null)
			{
				theList = nextList;
				curList = nextList;
			}
			else
			{
				curList.setNext(nextList);
				curList = nextList;
			}
			i += increment;
		}
			while( increment > 0 && i <= end && start < end ||
				   increment < 0 && i >= end && start > end ||
				   i == end && start == end);
		
		e.endVarScope();
		return new List(theList);
	}
	
	//Override
	public String toString(EvaluationContext e)
	{
		return "seq( " + expression.toString(e) + ", " + var + ", " + startExpr + ", " + endExpr + ", " + incrementExpr + " )";
	}
	
	//Override
	public void print(int indentLevel)
	{
		for(int i = 0; i < indentLevel; i++)
			System.out.print(" ");
		System.out.println("CreateListExpr");
		expression.print(indentLevel + 1);
		for(int i = 0; i < indentLevel+1; i++)
			System.out.print(" ");
		System.out.println("Start");
		startExpr.print(indentLevel + 2);
		for(int i = 0; i < indentLevel+1; i++)
			System.out.print(" ");
		System.out.println("End");
		endExpr.print(indentLevel + 2);
		for(int i = 0; i < indentLevel+1; i++)
			System.out.print(" ");
		System.out.println("Increment");
		incrementExpr.print(indentLevel + 2);
	}
						  
						  
}
