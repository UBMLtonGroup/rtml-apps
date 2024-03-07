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

//PTCompiler.java
//Jeffrey Hagelberg
package command.evaluator;

import java.io.IOException;

import command.java_cup.runtime.Symbol;
import command.parser.Parser;
import command.scanner.Yylex;

public class EvaluationContext {
    public static final int DEGREES = 1;
    public static final int RADIANS = 2;

    public static final int TYPE_NUMBER = 1;
    public static final int TYPE_LIST = 2;
    public static final int TYPE_FUNCTION = 3;
    public static final int TYPE_MATRIX = 4;

    public static final int VAR_EXPAND_CUR_SCOPE = 1;
    public static final int VAR_EXPAND_ALL = 2;
    public static final int VAR_EXPAND_NONE = 3;

    private VariableTable varTable;
    // consider adding a Data Structure for defined functions
    private int angleMode;
    private double tol;
    private int decimalPlaces; // # of decimal places to display
    private boolean displayFractions; // if true, rational numbers are displayed as fractions

    public EvaluationContext() {
        varTable = new VariableTable();
        angleMode = RADIANS;
        tol = 1.0e-6;
        decimalPlaces = 6;
        displayFractions = true;

    }

    /*******************************************************************************************************************
     * OUTPUT OPTIONS
     ******************************************************************************************************************/

    public void setDisplayFractions(boolean option) {
        displayFractions = option;
    }

    public boolean displayFractions() {
        return displayFractions;
    }

    public String formatNumber(double number) {
        return number + "";
    }

    public double getTol() {
        return tol;
    }

    public void setTol(double tol) {
        this.tol = tol;
    }

    /*******************************************************************************************************************
     * VARIABLE MANIPULATION FUNCTIONS
     ******************************************************************************************************************/

    // VARIABLE METHODS
    public double getVarValue(String v) throws UndefinedVarException {
        Expr simplified = ((Expr) varTable.getValue(v)).simplify(this);
        if (simplified instanceof NumberExpr) {
            return ((NumberExpr) simplified).getValue();
        } else {
            throw new UndefinedVarException();
        }
    }

    public Expr getVarExpr(String v) throws UndefinedVarException {
        return (Expr) varTable.getValue(v);
    }

    public void setVarValue(String v, Expr value, int type, int expandOption) {
        varTable.define(v, value.simplify(this, expandOption), type);
    }

    public void setVarValue(String v, Expr value, int type) {
        // TODO: Prevent recursive variable declarations
        // i.e. 1) define sinx = 2 * x
        // 2) define x = 3*sinx <- this should not be allowed
        // can go 3 or more layers deep (x = y, y = z, z = h, h = p, p = x)

        // simplify the expression without removing any variables
        // (i.e. replace 1+1 with 2 and so forth.

        // varTable.define(v,value);
        setVarValue(v, value, type, Expr.VAR_EXPAND_NONE);
    }

    public void setVarValue(String v, int type, double value) {
        varTable.define(v, command.evaluator.RealNumber.createRealNumber(value), type);
    }

    public int getVarType(String v) {
        return varTable.getType(v);
    }

    public boolean varIsDefinedInCurrentScope(String v) {
        return varTable.isDefinedInCurrentScope(v);
    }

    public boolean varIsDefined(String v) {
        return varTable.isDefined(v);
    }

    public void unDefineVar(String v) {
        varTable.undefine(v);
    }

    public void beginVarScope() {
        varTable.beginScope();
    }

    public void endVarScope() {
        varTable.endScope();
    }

    public void lockVar(String name) {
        varTable.lock(name);
    }

    public void unLockVar(String name) {
        varTable.unlock(name);
    }

    public void makeVarConstant(String name) {
        varTable.makeConstant(name);
    }

    public void makeVarNonConstant(String name) {
        varTable.makeUnConstant(name);
    }

    // ANGLE MODE METHODS
    public int getAngleMode() {
        return angleMode;
    }

    public double convertToRadians(double value) {
        if (angleMode == RADIANS) {
            return value;
        } else {
            return value * Math.PI / 180;
        }
    }

    /*******************************************************************************************************************
     * EXPRESSION / FUNCTION EVALUATION FUNCTIONS
     ******************************************************************************************************************/
    // EVALUATING STRING EXPRESSIONS
    public ProgLine createTree(String expr) throws IOException, Exception {
        Symbol sym;

        Yylex yy = new Yylex(new StringInputStream(expr));
        Parser parser = new Parser(yy);

        parser.setEvaluationContext(this);
        sym = parser.parse();
        if (sym == null || !(sym.value instanceof ProgLine)) return null;
        else return ((ProgLine) sym.value);
    }

    public double evaluateFunction(String expr, String var1, double val1) throws IOException, Exception {
        double value;
        beginVarScope();
        varTable.define(var1, RealNumber.createRealNumber(val1), VariableTable.TYPE_NUMBER);
        value = eval(expr);
        endVarScope();
        return value;
    }

    public double evaluateFunction(String expr, String var1, double val1, String var2, double val2) throws IOException,
            Exception {
        double value;
        beginVarScope();
        varTable.define(var1, RealNumber.createRealNumber(val1), VariableTable.TYPE_NUMBER);
        varTable.define(var2, RealNumber.createRealNumber(val2), VariableTable.TYPE_NUMBER);
        value = eval(expr);
        endVarScope();
        return value;

    }

    public double evaluateFunction(String expr, String vars[], double vals[]) throws IOException, Exception {
        double value;
        if (vars.length != vals.length) throw new IllegalArgumentException(
                "The number of variables is not the same as the number of values of variables.");

        beginVarScope();
        for (int i = 0; i < vars.length; i++) {
            varTable.define(vars[i], RealNumber.createRealNumber(vals[i]), VariableTable.TYPE_NUMBER);
        }
        value = eval(expr);
        endVarScope();
        return value;
    }

    private double eval(String expr) throws Exception {
        Symbol sym;
        Yylex yy = new Yylex(new StringInputStream(expr));
        Parser parser = new Parser(yy);

        parser.setEvaluationContext(this);
        sym = parser.parse();
        if (!(sym.value instanceof StmtListExpr)) { throw new IllegalArgumentException("The expression has no value."); }
        Expr simplified = ((StmtListExpr) sym.value).getExpr().simplify(this, Expr.VAR_EXPAND_ALL);

        if (simplified instanceof NumberExpr) {
            return ((NumberExpr) simplified).getValue();
        } else {
            System.out.println("The expression you entered looks like this: ");
            ((Expr) sym.value).print();
            throw new UndefinedVarException();
        }
    }

    // EVALUATION OF Expr Trees
    public double evaluateFunction(Expr expr, String var1, double val1) throws Exception {
        double value;
        beginVarScope();
        varTable.define(var1, RealNumber.createRealNumber(val1), VariableTable.TYPE_NUMBER);
        value = eval(expr);
        endVarScope();
        return value;
    }

    public double evaluateFunction(Expr expr, String var1, double val1, String var2, double val2) throws IOException,
            Exception {
        double value;
        beginVarScope();
        varTable.define(var1, RealNumber.createRealNumber(val1), VariableTable.TYPE_NUMBER);
        varTable.define(var2, RealNumber.createRealNumber(val2), VariableTable.TYPE_NUMBER);
        value = eval(expr);
        endVarScope();
        return value;
    }

    public double evaluateFunction(Expr expr, String vars[], double vals[]) throws IOException, Exception {
        if (vars.length != vals.length) throw new IllegalArgumentException(
                "The number of variables is not the same as the number of values of variables.");

        double retValue;
        beginVarScope();
        for (int i = 0; i < vars.length; i++) {
            varTable.define(vars[i], new Double(vals[i]), VariableTable.TYPE_NUMBER);
        }

        retValue = eval(expr);
        endVarScope();
        return retValue;
    }

    private double eval(Expr expr) throws UndefinedVarException {
        Expr expr2;
        if (expr instanceof StmtListExpr) {
            expr2 = ((StmtListExpr) expr).getExpr().simplify(this, Expr.VAR_EXPAND_ALL);
        } else {
            expr2 = expr.simplify(this, Expr.VAR_EXPAND_ALL);
        }

        if (expr2 instanceof NumberExpr) {
            return ((NumberExpr) expr2).getValue();
        } else {
            throw new UndefinedVarException();
        }
    }
} // end of Evaluation Context Class

