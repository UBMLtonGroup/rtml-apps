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

public class CustomFunction extends Expr
{
	//this is just used to hold the declaration
	//in the var table -- not to be evaluated
	private String name;
	private String[] paramNames;
	private Expr funcDecl;
	
	CustomFunction(String name, String[] paramNames, Expr funcDecl)
	{
		this.paramNames = paramNames;
		this.funcDecl = funcDecl;
		this.name = name;
	}
	public int numParams()
	{
		return paramNames.length;
	}
	//Override
	public Expr simplify(EvaluationContext e, int expandOption)
	{
		return new CustomFunction(name,paramNames,funcDecl.simplify(e,expandOption));
	}
	
	public Expr getFuncDecl()
	{
		return funcDecl;
	}
	public String getParamName(int paramNumber)
	{
		return paramNames[paramNumber];
	}
	
	//Override
	public String toString(EvaluationContext e) 
	{
		String result = name + "(";
		for(int i = 0; i < paramNames.length; i++)
		{
			if( i != paramNames.length - 1)
			{
				result = result + paramNames[i] + ", ";
			}
			else
			{
				result = result + paramNames[i];
			}
		}
		result = result + ") = " + funcDecl.toString(e);
		return result;
	}
	//Override
	public void print(int indentLevel) { }
	
}
