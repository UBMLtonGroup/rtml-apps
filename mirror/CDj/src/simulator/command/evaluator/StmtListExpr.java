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

public class StmtListExpr extends Expr
{
	private StmtList sl;
	private Expr e;
	public StmtListExpr(StmtList sl, Expr e)
	{
		this.sl = sl;
		this.e = e;
	}
	//Override
	public String toString(EvaluationContext ec)
	{
		return (sl == null ? "" : sl.toString(ec)) + " " + e.toString(ec);
	}
	//Override
	public Expr simplify(EvaluationContext c, int expandOption)
	{
	
		if (sl != null) sl.execute(c);
		return new StmtListExpr(sl,e.simplify(c,expandOption));
	}
	public Expr getExpr() { return e; }
	//Override
	public void print(int indentLevel)
	{
		for(int i = 0; i < indentLevel; i++)
			System.out.print(" ");
		System.out.println("Statement List Expression");
		for(int i = 0; i < indentLevel + 1; i++)
			System.out.print(" ");
		System.out.println("StmtList: " + sl.toString(null));
		for(int i = 0; i < indentLevel + 1; i++)
			System.out.print(" ");
		System.out.println("Expr");
		e.print(indentLevel + 2);
	}
}
