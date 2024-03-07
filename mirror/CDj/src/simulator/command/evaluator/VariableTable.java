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
import javacp.util.Hashtable;
import javacp.util.Stack;

public class VariableTable
{
	public static final int TYPE_NUMBER = 1;
	public static final int TYPE_LIST = 2;
	public static final int TYPE_FUNCTION = 3;
	public static final int TYPE_MATRIX = 4;
	
	private int currentLevel;
	private Hashtable table;
	private Stack levelPointers;
	
	public VariableTable()
	{
		table = new Hashtable();
		levelPointers = new Stack();
		currentLevel = -1; //this way levelPointers.elementAt(i) = pointer to first item at level i
						   //start at level 0 (begin scope increments level to 0)
		beginScope();
		defineVars();
	}
	private void defineVars()
	{
		//done once, when table is initialized
		define("pi",RealNumber.createRealNumber(Math.PI),TYPE_NUMBER,true,true,true);
		define("e", RealNumber.createRealNumber(Math.E),TYPE_NUMBER,true,true,true);
		define("i", new ComplexNumber(0.0,1.0),TYPE_NUMBER,true,true,true);
	}		
	public boolean isProtected(String name)
	{
		if(! table.containsKey(name))
		{
			return false;
		}
		else
		{
			Entry theEntry = (Entry)table.get(name);
			return theEntry.isProtected;
		}
	}
	public boolean isLocked(String name)
	{
		//if it is locked, you cannot delete it
		
		if(! table.containsKey(name))
			return false;
		else
		{
			Entry entry = (Entry)table.get(name);
			return entry.isLocked;
		}
	}
	public boolean isConstant(String name)
	{
		if(! table.containsKey(name))
			return false;
		else
		{
			Entry entry = (Entry)table.get(name);
			return entry.isConstant;
		}
	}
	public void define(String name, Object value, int type)
	{
		define(name,value,type,false,false,false);
	}
	public void define(String name, Object value, int type, 
					   boolean isProtected, boolean isLocked, boolean isConstant)
	{
		Entry newEntry = new Entry(name,value,type,currentLevel,isConstant,isLocked,isProtected);
		
		//update nextAtCurLevel
		if(levelPointers.peek() == null)
		{
			levelPointers.pop();
			levelPointers.push(newEntry);
		}
		else
		{
			//remove old link from stack,
			//make new node's next at same level pointer point to that object
			//put new node in stack
			Entry oldHead = (Entry)levelPointers.pop();
			newEntry.nextAtSameLevel = oldHead;
			oldHead.prevAtSameLevel = newEntry;
			levelPointers.push(newEntry);
		}
		
		//update next with Same Name
		if(! table.containsKey(name))
		{
			table.put(name,newEntry);
		}
		else
		{
			Entry oldEntry = (Entry)table.get(name);
			if(oldEntry.level == currentLevel)
			{
				if(oldEntry.isProtected || oldEntry.isConstant)
					throw new EvaluationException("You cannot change the value of '" + name + "'.  It is either protected or a constant.");
				//redefine entry, only update definition and type
				//entry was already defined in this scope
				oldEntry.entry = value;
				oldEntry.type = type;
			}
			else
			{
				//add new entry 
				//entry not defined in this scope
				Entry oldHead = (Entry)table.remove(name);
				newEntry.nextWithSameName = oldHead;
				table.put(name,newEntry);
			}
		}
	}
	public void undefine(String name)
	{
		Entry entry = (Entry)table.get(name);
		if(entry.isProtected || entry.isLocked)
			throw new EvaluationException("You cannot undefine '" + name + "'.  It is either locked or protected.");

		Entry oldEntry = (Entry)table.remove(name);
		if(oldEntry == null)
			return;
		//repair same name pointers
		if(oldEntry.nextWithSameName != null)
		{
			table.put(name,oldEntry.nextWithSameName);
		}
		//repair same level pointers
		if(oldEntry.nextAtSameLevel != null)
		{
			if(oldEntry.prevAtSameLevel != null)
			{
				oldEntry.prevAtSameLevel.nextAtSameLevel = oldEntry.nextAtSameLevel;
			}
			else
			{
				// put nextAtSameLevel in proper place in stack
				levelPointers.setElementAt(oldEntry.nextAtSameLevel,oldEntry.level);
			}
		}
	}
	public Object getValue(String name) throws UndefinedVarException
	{
		if(table.containsKey(name))
		{
			return ((Entry)table.get(name)).entry;
		}
		else
		{
			throw new UndefinedVarException("The variable " + name + " is not defined.");
		}
	}
	public boolean isDefined(String name)
	{
		return table.containsKey(name);
	}
	public int getType(String name)
	{
		Entry e = (Entry)table.get(name);
		return e.type;
	}
	public boolean isType(String name, int type)
	{
		Entry e = (Entry)table.get(name);
		return type == e.type;
	}
	public boolean isDefinedInCurrentScope(String name)
	{
		if(!isDefined(name))
			return false;
		Entry e = (Entry)table.get(name);
		return e.level == currentLevel;
	}
	
	public void beginScope()
	{
		currentLevel++;
		levelPointers.push(null);
	}
	public void endScope()
	{
		Entry curVar = (Entry)levelPointers.pop();
		
		do
		{
			if(curVar.nextWithSameName != null)
			{
				//hashtable was pointing to this entry;
				//overwrite previous entry with the next item with same name
				table.put(curVar.name,curVar.nextWithSameName);
			}
			else
			{
				table.remove(curVar.name);
			}
			curVar = curVar.nextAtSameLevel;
		}
			while(curVar != null);
		
		System.gc();
		
		currentLevel--;
	}
	
	public void lock(String name)
	{
		changeLockStatus(name,true);
	}
	public void unlock(String name)
	{
		changeLockStatus(name,false);
	}
	private void changeLockStatus(String name, boolean isLocked)
	{
		if(! table.containsKey(name))
			throw new EvaluationException(name + "is not defined.");
		else
		{
			Entry entry = (Entry)table.get(name);
			if( entry.isProtected)
				throw new EvaluationException("You cannot change a protected variable.");
			entry.isLocked = isLocked;
		}
	}
	
	public void makeConstant(String name)
	{
		changeConstantStatus(name,true);
	}
	public void makeUnConstant(String name)
	{
		changeConstantStatus(name,false);
	}
	
	private void changeConstantStatus(String name, boolean isConstant)
	{
		if(! table.containsKey(name))
			throw new EvaluationException(name + "is not defined.");
		else
		{
			Entry entry = (Entry)table.get(name);
			if( entry.isProtected)
				throw new EvaluationException("You cannot change a protected variable.");
			entry.isConstant = isConstant;
		}
	}
	
	private class Entry
	{
		
		public Entry nextAtSameLevel;
		public Entry prevAtSameLevel;
		public Entry nextWithSameName;
		
		public boolean isConstant; //if true, cannot change value
		public boolean isLocked; //if true cannot delete
		public boolean isProtected; // true if cannot unlock or make unConstant; user cannot set this;
									// used for things such as e, pi, etc.
		public Object entry;
		public String name;
		public int level;
		public int type;
		public Entry(String name, Object entry, int type, int level)
		{
			this.name = name;
			this.entry = entry;
			this.level = level;
			this.type = type;
			this.isConstant = false;
			this.isLocked = false;
			this.isProtected = false;
		}
		public Entry(String name, Object entry, int type, int level, boolean isConstant)
		{
			this.name = name;
			this.entry = entry;
			this.level = level;
			this.type = type;
			this.isConstant = isConstant;
			this.isLocked = false;
			this.isProtected = false;
		}
		public Entry(String name, Object entry, int type, int level, 
					 boolean isConstant, boolean isLocked, boolean isProtected)
		{
			this.name = name;
			this.entry = entry;
			this.level = level;
			this.type = type;
			this.isConstant = isConstant;
			this.isLocked = isLocked;
			this.isProtected = isProtected;
		}
	}
}
