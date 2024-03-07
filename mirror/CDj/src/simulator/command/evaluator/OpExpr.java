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

public class OpExpr extends Expr
{
	public static final int PLUS = 1;
	public static final int MINUS = 2;
	public static final int MULT = 3;
	public static final int DIV = 4;
	public static final int POW = 5;
	public static final int MOD = 6;
	
	private Expr left;
	private Expr right;
	private int type;
	
	public OpExpr(Expr left, int type, Expr right)
	{
		this.left = left;
		this.type = type;
		this.right = right;
	}
	public Expr getLeft() { return left; }
	public void setLeft(Expr left) { this.left = left; }
	
	public Expr getRight() { return this.right; }
	public void setRight(Expr right) { this.right = right; }
	
	public int getOperator() { return this.type; }
	public void setOperator(int op) { this.type = op; }
	//Override
	public Expr simplify(EvaluationContext e, int expandOption)
	{
		Expr lside = left == null ? null : left.simplify(e,expandOption); //can be null (factorial)
		Expr rside = right.simplify(e,expandOption);
		
		if( //if the left side is a number and the right side is a number then
		   lside != null && lside instanceof NumberExpr
		   && rside instanceof NumberExpr 
		   || lside == null & rside instanceof NumberExpr)
		{
					
			switch(type)
			{
			case PLUS:
				return ((NumberExpr)lside).add((NumberExpr)rside);
			case MINUS:
				return ((NumberExpr)lside).subtract((NumberExpr)rside);
			case DIV:
			return ((NumberExpr)lside).divide((NumberExpr)rside);
			case MULT:
				return ((NumberExpr)lside).multiply((NumberExpr)rside);
			case MOD:
				return ((NumberExpr)lside).mod((NumberExpr)rside);
			case POW:
				return ((NumberExpr)lside).toPower((NumberExpr)rside);
			default:
				throw new IllegalArgumentException("Invalid operator type.");
			}
		}
		else if(lside instanceof List || rside instanceof List)
		{
			if(lside instanceof List && !(rside instanceof List))
			{
				return ((List)lside).applyToRight(rside,type).simplify(e,expandOption);
			}
			else if (rside instanceof List && !(lside instanceof List))
			{
				return ((List)rside).applyToLeft(lside,type).simplify(e,expandOption);
			}
			else
			{
				return ((List)lside).applyOp((List)rside,type);
			}
		}
		else	
		{
			return new OpExpr(lside,type,rside);
		}
		
	}
	//Override
	public String toString(EvaluationContext e)
	{
		String opName;
		switch(type)
		{
		case PLUS:
			opName = "+";
			break;
		case MINUS:
			opName = "-";
			break;
		case MULT:
			opName = "*";
			break;
		case DIV:
			opName = "/";
			break;
		case POW:
			opName = "^";
			break;
		case MOD:
			opName = "%";
		default:
			opName = "?";
		}
		return  "(" + left.toString(e) + opName +  right.toString(e) + ")";
	} 	
				   
	
//Override
public void print(int indentLevel)
	
{
	
	String opName;
		switch(type)
		{
		case PLUS:
			opName = "PLUS";
			break;
		case MINUS:
			opName = "MINUS";
			break;
		case MULT:
			opName = "MULT";
			break;
		case DIV:
			opName = "DIV";
			break;
		case POW:
			opName = "POW";
			break;
		default:
			opName = "UNKNOWN: Type " + type;
		}
		for(int i = 0; i < indentLevel; i++)
			System.out.print(" " );
		
		System.out.println("Operator: " + opName);
		
		for(int i = 0; i < indentLevel + 1; i++)
			System.out.print(" ");
		System.out.println("Left: ");
		left.print(indentLevel + 2);
		for(int i = 0; i < indentLevel + 1; i++)
			System.out.print(" ");
		
		System.out.println("Right: ");
		right.print(indentLevel + 2);
		
		}		
		 
}
