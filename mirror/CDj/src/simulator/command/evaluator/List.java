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

public class List extends Expr implements Summable, Multipliable
{
	public static final int APPLY_ON_LEFT = 1;
	public static final int APPLY_ON_RIGHT = 2;
	
	ExprList list;
	public List(ExprList list)
	{
		this.list = list;
	}
	//Override
	public Expr simplify(EvaluationContext e, int expandOption)
	{
		return new List(list.simplify(e,expandOption));
	}
	public Expr getSum(EvaluationContext e, int expandOption)
	{
		Ammasser sumGetter = new Ammasser(OpExpr.PLUS);
		this.list.applyToEach(sumGetter);
		return sumGetter.getResult(e,expandOption);
	}
	public Expr getProduct(EvaluationContext e, int expandOption)
	{
		Ammasser prodGetter = new Ammasser(OpExpr.MULT);
		this.list.applyToEach(prodGetter);
		return prodGetter.getResult(e,expandOption);
	}
	public int getItemCount()
	{
		return list.getItemCount();
	}
	public void applyToAll(ListApplicator l)
	{
		list.applyToEach(l);
	}
	//Override
	public String toString(EvaluationContext e)
	{
		return "{" + list.toString(e) + "}";
	}
	
	public List applyOp(List l, int operation)
	{
		//I don't have corresponding L & R options here
		//since you can choose to call l.applyOp(r) or r.applyOp(l).
		//You don't have that option when dealing with generic expressions
		if(l.getItemCount() != getItemCount())
		{
			throw new EvaluationException("You cannot apply operations to lists that are different lengths.");
		}
		ListOperation theOp = new ListOperation(operation);
		list.applyToList(theOp,l.list);
		return theOp.getResult();
	}
	public List applyToRight(Expr e, int operation)
	{
		return applyOp(e, operation, false);
	}
	public List applyToLeft(Expr e, int operation)
	{
		return applyOp(e, operation, true);
	}
		
	private List applyOp(Expr e, int operation, boolean applyOnLeft)
	{
		ListOperation theOp = new ListOperation(e,operation, applyOnLeft ? APPLY_ON_LEFT : APPLY_ON_RIGHT);
		list.applyToList(theOp,null);
		return theOp.getResult();
	}
	//Override
	public void print(int indentLevel)
	{
		for(int i = 0; i < indentLevel; i++)
			System.out.print(" ");
		System.out.println("List");
		list.print(indentLevel + 1);
	}
	public Expr elementAt(int index)
	{
		//1-based index
		return list.getExprAt(index);
	}
	public void setElementAt(int index, Expr element)
	{
		list.setExprAt(index,element);
	}
	class Ammasser implements ListApplicator
	{
		//creates a sum or product, or whatever else is desired
		Expr sum;
		Expr curTerm;
		int operator;
		
		Ammasser(int operator)
		{
			sum = null;
			this.operator = operator;
		}
		public void apply(Object o,int termNumber, int totalTerms)
		{
			if(totalTerms == 1)
			{
				sum = ((Expr)o);
				return;
			}
			if(termNumber == 1)
			{
				sum = new OpExpr((Expr)o,operator,null);
				if(totalTerms > 2)
				{
					curTerm = new OpExpr(null,operator,null);
					((OpExpr)sum).setRight(curTerm);
				}
				else
				{
					curTerm = sum;
				}
			}
			else
			{
				if(termNumber == totalTerms)
				{
					((OpExpr)curTerm).setRight((Expr)o);
				}
				else if (totalTerms == termNumber + 1)
				{
					((OpExpr)curTerm).setLeft((Expr)o);	
				}
				else
				{
					((OpExpr)curTerm).setLeft((Expr)o);
					OpExpr newExpr = new OpExpr(null,operator,null);
					((OpExpr)curTerm).setRight(newExpr);
					curTerm = newExpr;
				}
			}
		}
		
		Expr getResult(EvaluationContext e, int expandOption)
		{
			return sum.simplify(e,expandOption);
		}
																 
	} // class
	
	class ListOperation implements List2ListApplicator
	{
		//add, subtract, multiply, or divide each item of a list by a number
		//the number can be a corresponding item in another list or just a number
		private Expr otherExpr = null;
		private ExprList result;
		private ExprList cur;
		private int op;
		
		private int applyOnSide;
	
		public ListOperation(Expr other, int op)
		{
			this(other,op,APPLY_ON_RIGHT);
		}
		public ListOperation(Expr other, int op, int applyOnSide)
		{
			//constructor for ops involving 2 lists
			//other null passed as parameter to "applyToList" method
			this.otherExpr = other;
			this.op = op;
			this.applyOnSide = applyOnSide;
		}
		public ListOperation(int op)
		{
			this(op,APPLY_ON_RIGHT);
		}
		public ListOperation(int op, int applyOnSide)
		{
			//constructor for ops involving 2 lists
			//other list passed as parameter to "applyToList" method
			this(null,op,applyOnSide);
		}
		public void apply(Object l1, Object l2, int termNumber, int totalTerms)
		{
			Expr otherExpr = l2 != null ? (Expr)l2 : this.otherExpr;
			ExprList nextExprList;
			if(applyOnSide == APPLY_ON_RIGHT)
			{
				nextExprList = new ExprList(new OpExpr((Expr)l1,op,otherExpr),null);
			}
			else
			{
				nextExprList = new ExprList(new OpExpr(otherExpr,op,(Expr)l1),null);
			}
			if(cur == null)
			{
				result = nextExprList;
				cur = nextExprList;
			}
			else
			{
				cur.setNext(nextExprList);
				cur = nextExprList;
			}
		}
				
		public List getResult()
		{
			return new List(result);
		}
			
	}
}
