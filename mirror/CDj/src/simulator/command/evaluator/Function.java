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

public abstract class Function extends Expr {
	protected Expr inside;

	public Function(Expr inside) {
		this.inside = inside;
	}

	//Override
	public Expr simplify(EvaluationContext ec, int expandOption) {
		Expr newInside = inside.simplify(ec, expandOption);
		if (newInside instanceof NumberExpr) {
			if (((NumberExpr) newInside).isReal()) {
				return RealNumber.createRealNumber(getValue((NumberExpr) newInside, ec));
			} else {
				return getValue((ComplexNumber) newInside, ec);
			}
		} else if (newInside instanceof List) {
			Functionizer fun = new Functionizer(this);
			((List) newInside).applyToAll(fun);
			return fun.getList().simplify(ec, expandOption);
		} else {
			Object o = null;
			Object params[] = { newInside };
			o = make(newInside);
			return (Expr) o;
		}
	}

	abstract public Expr make(Expr e);

	//Override
	public String toString(EvaluationContext e) {
		return getName() + "(" + inside.toString(e) + ")";
	}

	//Override
	public void print(int indentLevel) {
		String typeName = getName();

		for (int i = 0; i < indentLevel; i++)
			System.out.print(" ");

		System.out.println("Function: " + typeName);
		inside.print(indentLevel + 1);
	}

	public abstract ComplexNumber getValue(ComplexNumber c, EvaluationContext ec);

	public abstract double getValue(double d, EvaluationContext ec);

	public abstract String getName();

	public double getValue(NumberExpr n, EvaluationContext ec) {
		return getValue(n.getValue(), ec);
	}

	protected double sinh(double d, EvaluationContext ec) {
		return (Math.exp(d) - Math.exp(-d)) / 2;
	}

	protected double cosh(double d, EvaluationContext ec) {
		return (Math.exp(d) + Math.exp(-d)) / 2;
	}

	class Functionizer implements ListApplicator {
		// create a new List, such that, for example, each member of this list
		// is the sine of the element in the previous list ( = the List that the applicator is
		// called on)

		ExprList newExprList;
		ExprList curLocation;
		Function cl;

		private Functionizer(Function cl) {
			newExprList = null;
			curLocation = null;
			this.cl = cl;
		}

		public void apply(Object o, int termNumber, int totalTerms) {
			Function newFunc = createFunc(o);

			if (newExprList == null) {
				newExprList = new ExprList(newFunc, null);
				curLocation = newExprList;
			} else {
				ExprList nextList = new ExprList(newFunc, null);
				curLocation.setNext(nextList);
				curLocation = nextList;
			}
		}

		private Function createFunc(Object o) {
			return (Function) cl.make((Expr) o);
		}

		public List getList() {
			return new List(newExprList);
		}

	}
}
