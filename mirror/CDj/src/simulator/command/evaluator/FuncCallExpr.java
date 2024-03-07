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

public class FuncCallExpr extends Expr
{
	private ExprList argList; // contains Exprs
	private String name;
	
	public FuncCallExpr(String name, ExprList argList)
	{
		this.name = name;
		this.argList = argList;
	}
	//Override
	public Expr simplify(EvaluationContext e, int expandOption)
	{
		if(expandOption == Expr.VAR_EXPAND_NONE)
			return new FuncCallExpr(name,argList);
		
		if(expandOption == Expr.VAR_EXPAND_CUR_SCOPE &&
		   ! e.varIsDefinedInCurrentScope(name))
			return new FuncCallExpr(name,argList);
		
		Expr result = null;
		if(! isValidCall(e))
		{
			return new FuncCallExpr(name,argList.simplify(e,expandOption));
		}
		try
		{
			CustomFunction c = (CustomFunction)e.getVarExpr(name);
			Object[] values = argList.getItems();
			e.beginVarScope();
				for(int i = 0; i < c.numParams(); i++)
				{
					
					//we want to evaluate the function to a number.
					//thus, simplify the arguments
					e.setVarValue(c.getParamName(i),((Expr)values[i]).simplify(e,expandOption),expandOption);
				}
				result = c.getFuncDecl().simplify(e,expandOption);
			e.endVarScope();
		}
		catch(UndefinedVarException uve)
		{
			//this will not happen since we check first
		}
		
		return result;
	}
	
	//Override
	public void print(int indentLevel)
	{
		for(int i = 0; i < indentLevel; i++)
			System.out.print(" ");
		System.out.println("FuncCallExpr");
		for(int i = 0; i < indentLevel+1; i++)
			System.out.print(" ");
		System.out.println("Name: " + name);
		for(int i = 0; i < indentLevel+1; i++)
			System.out.print(" ");
		System.out.println("Arguments: " + argList);
	}	
		
	//Override
	public String toString(EvaluationContext e)
	{
		return name + "(" + argList.toString(e) + ")";
	}
		
	private boolean isValidCall(EvaluationContext ec)
	{
		if(ec.varIsDefined(name))
		{
			try
			{
				Expr e = ec.getVarExpr(name);
	
				if (e instanceof CustomFunction)
				{
					CustomFunction c = ((CustomFunction)e);
					
					if(c.numParams() == argList.getItemCount())
					{
						return true;
					}
					else
					{
						throw new IllegalArgumentException("The function \"" + name + "\" takes " + c.numParams() + " parameter(s).");
					}
				}
				else
				{
					return false;
				}
			}
			catch(UndefinedVarException uve)
			{
				//this should not happen
				return false;
			}
		}			
		else
		{
			return false;
		}
	
	}
}