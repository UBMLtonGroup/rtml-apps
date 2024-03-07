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

public class ObjectList implements Printable
{
	//usages
	//1)  When defining function, holds names of parameters - ParamList
	//2)  When using function, holds values of arguments - ExprList
	//3)  The rows of a matrix - RowList
	//4)  The expresions within a row of a matrix - ExprList 
	protected Object obj;
	protected ObjectList next;
	
	public ObjectList(Object obj, ObjectList next)
	{
		this.obj = obj;
		this.next = next;
	}
	public ObjectList(Object obj)
	{
		this.obj = obj;
		this.next = null;
	}
	public int getItemCount()
	{
		if(next == null)
		{
			return 1;
		}
		else
		{
			return 1 + next.getItemCount();
		}
	}
	public Object getObj() { return obj; }
	public ObjectList getNextObjList() { return next; }
	public Object[] getItems()
	{
		Object[] paramNames = new Object[getItemCount()];
		return getItems(paramNames,0);
	}
	public void applyToEach(ListApplicator a)
	{
		applyToEach(a,1,getItemCount());
	}
	public void applyToList(List2ListApplicator la, ExprList other)
	{
		//I'm relaxing the restruction that lists need to be
		//same length for more flexibility -- 
		//list length can be checked elsewhere
		applyToList(la,other,1,getItemCount());
	}
	private void applyToEach(ListApplicator a, int termNumber, int totalTerms)
	{
		
		a.apply(obj,termNumber,totalTerms);
		if(next != null)
			next.applyToEach(a,termNumber+1,totalTerms);
	}
	private void applyToList(List2ListApplicator la, ObjectList other, int termNumber, int totalTerms)
	{
		la.apply(obj,other != null ? other.getObj() : null,termNumber,totalTerms);
		if(next != null)
		{
			next.applyToList(la,other != null && other.getNextObjList() != null ? other.getNextObjList() : null,termNumber+1,totalTerms);
		}
	}
	public Object getItemAt(int location)
	{
		//1st item = 1
		
		if(location > getItemCount() || location <= 0)
		{
			throw new IndexOutOfBoundsException("The list you referenced only has " + getItemCount() + " items.  The valid indices are between 1 and " + getItemCount() + ".");
		}
		ObjectList curList = this;
		for(int i = 1; i < location; i++)
		{
			//do this location - 1 times
			curList = curList.next;
		}
		return curList.obj;
	}
	public void setItemAt(int location, Object obj)
	{
				//1st item = 1
		
		if(location > getItemCount() || location <= 0)
		{
			throw new IndexOutOfBoundsException("The list you referenced only has " + getItemCount() + " items.  The valid indices are between 1 and " + getItemCount() + ".");
		}
		ObjectList curList = this;
		for(int i = 1; i < location; i++)
		{
			//do this location - 1 times
			curList = curList.next;
		}
		curList.obj = obj;
	}
	//called recursively; when done, names contains the values/names of the parameters
	public Object[] getItems(Object[] names, int position)
	{
		names[position] = obj;
		if(next != null)
		{
			return next.getItems(names,position + 1);
		}
		else
		{
			return names;
		}
	}
	public void print(int indentLevel)
	{
		for(int i = 0; i < indentLevel; i++)
			System.out.print(" ");
		System.out.println(getClass().getName());
		if(obj instanceof Expr)
		{
			((Expr)obj).print(indentLevel + 1);
		}
		else
		{
			for(int i = 0; i < indentLevel + 1; i++)
			{
				System.out.println(obj.getClass().getName() + ": " + obj.toString());
			}
		}
		if(next != null)
			next.print(indentLevel + 1);
	}

	public String toString(EvaluationContext e)
	{
		if(next == null)
			return ((Printable)obj).toString(e);
		else
			return ((Printable)obj).toString(e) + ", " + next.toString(e);
	}
		
	
	
}
