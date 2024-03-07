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

public abstract class Expr extends ProgLine
{
	
	public static final int VAR_EXPAND_CUR_SCOPE = 1;
	public static final int VAR_EXPAND_ALL = 2;
	public static final int VAR_EXPAND_NONE = 3;
	
	//public abstract double evaluate(EvaluationContext e) throws UndefinedVarException;
	//Override
	public Expr evaluate(EvaluationContext e)
	{
		return simplify(e,VAR_EXPAND_ALL);
	}
	//force the program to try to reduce expr to a number
	//should simplify variables to numbers, etc.
	//even if can't reduce whole thing
	public abstract Expr simplify(EvaluationContext e, int expandOption);
	public Expr simplify(EvaluationContext e)
	{
		return simplify(e,VAR_EXPAND_ALL);
	}
		
	//public abstract boolean evaluatesToNumber();
	//implement this later
	public abstract String toString(EvaluationContext e);
	public abstract void print(int indentLevel);
	
	public void print() { print(0);		}
	public void printSpaces(int num)
	{
		for(int i = 0; i < num; i++)
			System.out.print(" ");
	}
	//used mostly in matrix manipulation
	private Expr performOp(int op, Expr other)
	{
		return new OpExpr(this,op,other);
	}
	public Expr addExpr(Expr e) { return performOp(OpExpr.PLUS,e); }
	public Expr subtractExpr(Expr e) { return performOp(OpExpr.MINUS,e); }
	public Expr multiplyExpr(Expr e) { return performOp(OpExpr.MULT,e); }
	public Expr divideExpr(Expr e) { return performOp(OpExpr.DIV,e); }
	public Expr exprReciprocal() { return new OpExpr(NumberExpr.ONE,OpExpr.DIV,this); }
		
}
