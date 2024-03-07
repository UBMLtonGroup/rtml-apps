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

public class ExprList extends ObjectList
{
	public ExprList(Expr arg, ExprList next)
	{
		super(arg,next);
	}
	public ExprList(Expr arg)
	{
		super(arg);
	}
		
	//Override
	public Object[] getItems()
	{
		Expr[] paramNames = new Expr[getItemCount()];
		return getItems(paramNames,0);
	}
	public void setNext(ExprList next)
	{
		this.next = next;
	}
	public void setExpr(Expr e)
	{
		this.obj = e;
	}
	public Expr getExprAt(int index)
	{
		//returns object at (1 based) index
		return (Expr)super.getItemAt(index);
	}
	public void setExprAt(int index, Expr expr)
	{
		super.setItemAt(index,expr);
	}
	public ExprList simplify(EvaluationContext e, int expandOption)
	{
		if(next != null)
		{
			return new ExprList(((Expr)obj).simplify(e,expandOption),((ExprList)next).simplify(e,expandOption));
		}
		else
		{
			return new ExprList(((Expr)obj).simplify(e,expandOption),null);
		}
	}
	//Override
	public String toString(EvaluationContext e)
	{
		if(next == null)
		{
			return ((ProgLine)obj).toString(e);
		}
		else
		{
			return ((ProgLine)obj).toString(e) + ", " + next.toString(e);
		}
	}
		

}


