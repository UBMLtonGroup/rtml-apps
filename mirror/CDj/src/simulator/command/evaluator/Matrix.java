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

public class Matrix extends Expr {

	private final boolean spaceBetween = false; // true if there is a column of spaces
	// between the brackets in the display
	// and the numbers
	private Expr[][] data;

	Matrix(int rows, int cols) {
		if (rows >= 0 && cols >= 0) {
			data = new Expr[rows][cols];
		}
	}

	public Matrix(Expr[][] data) {
		this.data = data;
	}

	//Override
	public Expr simplify(EvaluationContext e, int expandOption) {
		Expr simplifiedData[][] = new Expr[getNumRows()][getNumCols()];
		for (int r = 0; r < getNumRows(); r++) {
			for (int c = 0; c < getNumCols(); c++) {
				simplifiedData[r][c] = getEntry(r, c).simplify(e, expandOption);
			}
		}
		return new Matrix(simplifiedData);
	}

	public void makeIdentity() {
		for (int row = 0; row < getNumRows(); row++) {
			for (int col = 0; col < getNumCols(); col++) {
				setEntry(row, col, row == col ? 1 : 0);
			}
		}
	}

	public void setEntry(int row, int col, Expr value) {
		if (isValidIndex(row, col)) {
			data[row][col] = value;
		}

	}

	public void setEntry(int row, int col, double value) {
		if (isValidIndex(row, col)) {
			data[row][col] = RealNumber.createRealNumber(value);
		}

	}

	public Expr getEntry(int row, int col) {
		if (isValidIndex(row, col)) {
			return data[row][col];
		} else {
			throw new IllegalArgumentException("The specified row and column is invalid.");
		}
	}

	public int getNumRows() {
		return data.length;
	}

	public int getNumCols() {
		if (data.length == 0) {
			return 0;
		} else {
			return data[data.length - 1].length;
		}
	}

	public Matrix getTranspose() {
		Matrix result = new Matrix(getNumCols(), getNumRows());

		for (int row = 0; row < getNumRows(); row++) {
			for (int col = 0; col < getNumCols(); col++) {
				result.setEntry(col, row, getEntry(row, col));
			}
		}
		return result;
	}

	public Matrix removeCol(int colToRemove) {
		// 0 = 1st col
		if (colToRemove < 0 || colToRemove >= getNumCols()) { throw new IllegalArgumentException(
				"The specified column is invalid."); }

		Matrix output = new Matrix(getNumRows(), getNumCols() - 1);

		// addExpr columns before
		for (int col = 0; col < colToRemove; col++) {
			for (int row = 0; row < getNumRows(); row++) {
				output.setEntry(row, col, getEntry(row, col));

			}
		}

		// addExpr columns after
		for (int col = colToRemove + 1; col < getNumCols(); col++) {
			for (int row = 0; row < getNumRows(); row++) {
				output.setEntry(row, col - 1, getEntry(row, col));
			}
		}
		return output;
	}

	public Matrix removeRow(int rowToRemove) {
		// 0 = 1st col
		if (rowToRemove < 0 || rowToRemove >= getNumCols()) { throw new IllegalArgumentException(
				"The specified row is invalid."); }

		Matrix output = new Matrix(getNumRows() - 1, getNumCols());

		// addExpr rows before row to remove
		for (int row = 0; row < rowToRemove; row++) {
			for (int col = 0; col < getNumCols(); col++) {
				output.setEntry(row, col, getEntry(row, col));

			}
		}

		// addExpr columns after row to remove
		for (int row = rowToRemove + 1; row < getNumRows(); row++) {
			for (int col = 0; col < getNumCols(); col++) {
				output.setEntry(row - 1, col, getEntry(row, col));
			}
		}
		return output;
	}

	public Matrix subtract(Matrix m) {
		return add(m.multiply(NumberExpr.MINUSONE));
	}

	public Matrix add(Matrix m) {
		if (m.getNumRows() == getNumRows() && m.getNumCols() == getNumCols()) {
			Matrix output = new Matrix(getNumRows(), getNumCols());

			for (int row = 0; row < getNumRows(); row++) {
				for (int col = 0; col < getNumCols(); col++) {
					output.setEntry(row, col, getEntry(row, col).addExpr(m.getEntry(row, col)));
				}
			}
			return output;
		} else {
			throw new IllegalArgumentException("The specified matrix has the wrong dimensions.");
		}
	}

	public Matrix getInverse() {
		if (!(getNumRows() == getNumCols())) {
			// inverse not defined
			throw new IllegalArgumentException("The inverse of this matrix is not defined.");
		}
		Matrix augmented = new Matrix(getNumRows(), 2 * getNumRows());
		Matrix result = new Matrix(getNumRows(), getNumCols());
		// set entries equal
		for (int row = 0; row < getNumRows(); row++) {
			for (int col = 0; col < getNumCols(); col++) {
				augmented.setEntry(row, col, getEntry(row, col));
				augmented.setEntry(row, col + getNumCols(), new RationalNumber());
			}
		}
		// add identity matrix on right
		for (int col = getNumRows(); col < getNumRows() * 2; col++) {
			int row = col - getNumRows();
			augmented.setEntry(row, col, new RationalNumber(1, 1));
		}

		augmented = augmented.getRowReducedEchelonForm();
		// extract right-most cols

		// should check first to see if the left part is the identity matrix

		for (int col = getNumRows(); col < getNumRows() * 2; col++) {
			for (int row = 0; row < getNumRows(); row++) {
				result.setEntry(row, col - getNumCols(), augmented.getEntry(row, col));
			}
		}
		return result;
	}

	public Expr getDeterminant() {

		if (!(getNumRows() == getNumCols())) {
			// determinant not defined
			throw new IllegalArgumentException("The determinant of this matrix is not defined.");
		} else {
			return determinant(this);
		}
	}

	private Expr determinant(Matrix a) {
		Expr output = new RationalNumber();

		if (a.getNumRows() == 1) // base case
		{
			return a.getEntry(0, 0);
		} else {
			for (int i = 0; i < a.getNumCols(); i++) {
				// output +=
				// Math.pow(-1,i)*a.getValue(0,i)*determinant(a.removeCol(i).removeRow(0));
				Expr sign = new RationalNumber((long) Math.pow(-1, i), 1);
				Expr coef = a.getEntry(0, i);
				Expr det = determinant(a.removeCol(i).removeRow(0));
				Expr temp = new RationalNumber();
				temp = coef.multiplyExpr(det).multiplyExpr(sign);
				output = output.addExpr(temp);
			}
			return output;
		}
	}

	public Matrix changeSize(int rows, int cols) {
		return null;
	}

	public Matrix getEchelonForm() {
		return getEchelonForm(false);
	}

	public Matrix getEchelonForm(boolean makeOnes) {
		Matrix result = new Matrix(getNumRows(), getNumCols());
		// 1st, subtract multiples of the first row from the 2nd to last row
		// then, subtract multiples of the second row from the 3rd to last row,
		// etc.

		// fill the matrix with this matrix's data
		for (int row = 0; row < getNumRows(); row++) {
			for (int col = 0; col < getNumCols(); col++) {
				result.setEntry(row, col, getEntry(row, col));
			}
		}

		for (int baseRow = 0; baseRow < getNumRows(); baseRow++)
		// this is the row that is multiplied by a scalar and
		// added to other rows
		{
			// swap row with maximal row
			int maxRow = findMaximalRow(result, baseRow, baseRow);
			if (maxRow != baseRow && maxRow != -1) {
				result.swapRows(baseRow, maxRow, true);
				// System.out.println("Swapping rows " + baseRow + " and " + maxRow);
			}

			for (int rowBelow = baseRow + 1; rowBelow < getNumRows(); rowBelow++) {

				result = result.multRowAdd(rowBelow, baseRow, NumberExpr.MINUSONE.multiplyExpr(
						result.getEntry(rowBelow, baseRow)).divideExpr(result.getEntry(baseRow, baseRow)));
				// System.out.println(result);
			}
			// System.out.println(result);

		}
		// this part puts ones on the diagnal
		if (makeOnes) {
			for (int entry = 0; entry < getNumCols(); entry++) {
				if (result.isValidIndex(entry, entry)
						&& (!(result.getEntry(entry, entry) instanceof NumberExpr) || !(((NumberExpr) result.getEntry(
								entry, entry)).getValue() == 0))) {
					result = result.multiplyExprRow(entry, result.getEntry(entry, entry).exprReciprocal());
				}
			}
		}
		return result;
	}

	public Matrix getRowReducedEchelonForm() {
		Matrix result = getEchelonForm(true);
		// System.out.print(result);
		for (int baseRow = min(result.getNumCols(), result.getNumRows()) - 1; baseRow >= 1; baseRow--) {
			for (int aboveRow = baseRow - 1; aboveRow >= 0; aboveRow--) {
				result = result.multRowAdd(aboveRow, baseRow, new RationalNumber(-1, 1).multiplyExpr(
						result.getEntry(aboveRow, baseRow)).divideExpr(result.getEntry(baseRow, baseRow)));
			}
		}
		return result;
	}

	public Matrix multiplyExprRow(int row, double scalar) {
		return multiplyExprRow(row, RealNumber.createRealNumber(scalar));
	}

	public Matrix multiplyExprRow(int row, Expr scalar) {
		Matrix result = new Matrix(getNumRows(), getNumCols());
		for (int rowNum = 0; rowNum < getNumRows(); rowNum++) {
			for (int colNum = 0; colNum < getNumCols(); colNum++) {
				if (rowNum == row) {
					result.setEntry(rowNum, colNum, scalar.multiplyExpr(getEntry(rowNum, colNum)));
				} else {
					result.setEntry(rowNum, colNum, getEntry(rowNum, colNum));
				}
			}
		}
		return result;
	}

	public Matrix multRowAdd(int rowToAddTo, int rowToAdd, double scalar) {
		return multRowAdd(rowToAddTo, rowToAdd, scalar, false);
	}

	public Matrix multRowAdd(int rowToAddTo, int rowToAdd, double scalar, boolean inPlace) {
		return multRowAdd(rowToAddTo, rowToAdd, RealNumber.createRealNumber(scalar), inPlace);
	}

	public Matrix multRowAdd(int rowToAddTo, int rowToAdd, Expr scalar) {
		return multRowAdd(rowToAddTo, rowToAdd, scalar, false);
	}

	public Matrix multRowAdd(int rowToAddTo, int rowToAdd, Expr scalar, boolean inPlace) {
		if (rowToAddTo >= getNumRows() || rowToAddTo < 0 || rowToAdd >= getNumRows() || rowToAdd < 0) { throw new IllegalArgumentException(
				"The specified parameters are invalid."); }
		if (inPlace) {
			for (int col = 0; col < getNumCols(); col++) {
				setEntry(rowToAddTo, col, getEntry(rowToAddTo, col).addExpr(
						(getEntry(rowToAdd, col).multiplyExpr(scalar))));
			}
			return this;
		} else {

			Matrix result = new Matrix(getNumRows(), getNumCols());
			for (int row = 0; row < getNumRows(); row++) {
				for (int col = 0; col < getNumCols(); col++) {
					if (!(row == rowToAddTo)) {

						result.setEntry(row, col, getEntry(row, col));

					} else {
						result.setEntry(rowToAddTo, col, getEntry(rowToAddTo, col).addExpr(
								(getEntry(rowToAdd, col).multiplyExpr(scalar))));
					}
				}
			}
			return result;
		}
	}

	public Matrix swapRows(int row1, int row2) {
		return swapRows(row1, row2, false);
	}

	public Matrix swapRows(int row1, int row2, boolean inPlace) {
		if (row1 >= getNumRows() || row1 < 0 || row2 >= getNumRows() || row2 < 0) { throw new IllegalArgumentException(
				"At least one of the specified rows is invalid."); }
		if (inPlace) {
			for (int col = 0; col < getNumCols(); col++) {
				Expr temp;
				temp = getEntry(row1, col);
				setEntry(row1, col, getEntry(row2, col));
				setEntry(row2, col, temp);
			}
			return this;
		} else {
			Matrix result = new Matrix(getNumRows(), getNumCols());
			for (int row = 0; row < getNumRows(); row++) {
				for (int col = 0; col < getNumCols(); col++) {
					if (!(row == row1) && !(row == row2)) {
						result.setEntry(row, col, getEntry(row, col));
					} else {
						if (row == row1) {
							result.setEntry(row, col, getEntry(row2, col));
						} else if (row == row2) {
							result.setEntry(row, col, getEntry(row1, col));
						} // if row == row1
					} // if
				} // for, cola
			} // for, rows
			return result;
		} // if inPlace
	} // method

	public Matrix multiplyExpr(Matrix m) {
		return multiplyExpr(m, true, false);
	}

	public Matrix multiply(Expr n) {
		Matrix m = new Matrix(getNumRows(), getNumCols());
		for (int row = 0; row < getNumRows(); row++) {
			for (int col = 0; col < getNumCols(); col++) {
				m.setEntry(row, col, getEntry(row, col).multiplyExpr(n));
			}
		}
		return m;
	}

	public Matrix divide(Expr n) {
		if (n instanceof NumberExpr && ((NumberExpr) n).getValue() == 0) { throw new EvaluationException(
				"You cannot divideExpr by zero."); }
		Matrix m = new Matrix(getNumRows(), getNumCols());
		for (int row = 0; row < getNumRows(); row++) {
			for (int col = 0; col < getNumCols(); col++) {
				m.setEntry(row, col, getEntry(row, col).divideExpr(n));
			}
		}
		return m;
	}

	public Matrix multiplyExpr(Matrix m, boolean onRight, boolean inPlace) {
		// on right : A x m
		// on left : m x A
		if (!(getNumCols() == m.getNumRows())) { throw new IllegalArgumentException(
				"The specified multiplication in undefined."); }
		Matrix result = new Matrix(getNumRows(), m.getNumCols());
		for (int row = 0; row < result.getNumRows(); row++) {
			for (int col = 0; col < result.getNumCols(); col++) {
				Expr newEntry;
				if (onRight) {
					newEntry = dot(row, m, col);
				} else {
					// double res = 0;
					Expr res = new RationalNumber(0, 1);

					for (int entryNum = 0; entryNum < getNumRows(); entryNum++) {
						res = res.addExpr(m.getEntry(row, entryNum).multiplyExpr(getEntry(entryNum, col)));
					}
					newEntry = res;
				}
				if (inPlace) {
					setEntry(row, col, newEntry);
				} else {
					result.setEntry(row, col, newEntry);
				}
			}
		}
		if (inPlace)
			return this;
		else return result;
	}

	private Expr dot(int rowOfThis, Matrix other, int colOfOther) {
		// takes the dot product of a row in this matrix
		// with a column in anothe matrix
		// used in multiplication
		Expr result = new RationalNumber(0, 1);
		if (!(getNumCols() == other.getNumRows())) {
			throw new IllegalArgumentException("The specified multiplication in undefined.");
		} else if (rowOfThis >= getNumRows() || rowOfThis < 0 || colOfOther >= other.getNumCols() || colOfOther < 0) { throw new IllegalArgumentException(
				"The row or column specified does not exist."); }
		for (int entryNum = 0; entryNum < getNumCols(); entryNum++) {
			result = result.addExpr(getEntry(rowOfThis, entryNum).multiplyExpr(other.getEntry(entryNum, colOfOther)));
		}
		return result;

	}

	private boolean isValidIndex(int row, int col) {
		if (row >= 0 && row <= data.length - 1 && col >= 0 && col <= data[data.length - 1].length) {
			return true;
		} else {
			return false;
		}
	}

	public Matrix getSubMatrix(int startRow, int endRow, int startCol, int endCol) {
		// 1st col = 0
		// 1st row = 0
		Matrix newMatrix = new Matrix(endRow - startRow + 1, endCol - startCol + 1);
		for (int row = startRow; row <= endRow; row++) {
			for (int col = startCol; col <= endCol; col++) {
				newMatrix.setEntry(row - startRow, col - startCol, getEntry(row, col));
			}
		}
		return newMatrix;
	}

	public void LUfactorization(Matrix L, Matrix U) {
		// make L = indentity, make U = this matrix
		for (int row = 0; row < getNumRows(); row++) {
			for (int col = 0; col < getNumCols(); col++) {
				L.setEntry(row, col, row == col ? 1 : 0);
				U.setEntry(row, col, getEntry(row, col));
			}
		}

		// make U upper triangular, update L with multipliers
		for (int curRow = 0; curRow < getNumRows() - 1; curRow++) {
			// there is no need for the bottom row to be a current Row;
			// nothing below it is ever zeroed out

			// curRow = row we are using to zero out entries in other rows
			for (int workingRow = curRow + 1; workingRow < getNumRows(); workingRow++) {
				// workingRow = row whose entry is being zeroed out
				// TODO: worry about dividing by zero
				Expr multiplier = (new RationalNumber(-1, 1)).multiplyExpr(U.getEntry(workingRow, curRow)).divideExpr(
						U.getEntry(curRow, curRow));
				L.setEntry(workingRow, curRow, multiplier.multiplyExpr(NumberExpr.MINUSONE));
				U.multRowAdd(workingRow, curRow, multiplier, true);
			}
		}
	}

	public void LUwithPivoting(Matrix L, Matrix U, Matrix P) {
		// make P,L = indentity, make U = this matrix
		for (int row = 0; row < getNumRows(); row++) {
			for (int col = 0; col < getNumCols(); col++) {
				P.setEntry(row, col, row == col ? 1 : 0);
				L.setEntry(row, col, row == col ? 1 : 0);
				U.setEntry(row, col, getEntry(row, col));
			}
		}
		Matrix curP;
		Matrix curU;
		// make U upper triangular, update L with multipliers
		for (int curRow = 0; curRow < getNumRows() - 1; curRow++) {
			// there is no need for the bottom row to be a current Row;
			// nothing below it is ever zeroed out

			// determine pivot, update P, swap the rows
			int pivotRow = findMaximalRow(U, curRow, curRow);
			if (pivotRow != curRow) {
				U.swapRows(curRow, pivotRow, true);
				P.swapRows(curRow, pivotRow, true);
			}

			// curRow = row we are using to zero out entries in other rows
			for (int workingRow = curRow + 1; workingRow < getNumRows(); workingRow++) {
				// workingRow = row whose entry is being zeroed out
				// TODO: worry about dividing by zero
				Expr multiplier = (new RationalNumber(-1, 1)).multiplyExpr(U.getEntry(workingRow, curRow)).divideExpr(
						U.getEntry(curRow, curRow));
				// double multiplier = -1 * U.getEntry(workingRow,curRow).getValue() /
				// U.getEntry(curRow,curRow).getValue();
				// L.setEntry(workingRow,curRow,new IrrationalNumber(-1*multiplier));
				L.setEntry(workingRow, curRow, multiplier);
				U.multRowAdd(workingRow, curRow, multiplier, true);
			}
			// System.out.println(L.toString(null)+U.toString(null)+P.toString(null));
		}
	}

	private int findMaximalRow(Matrix m, int startRow, int col) {
		// used for pivoting
		double max = -1; // max entry (in absolute value)
		int corrRow = -1; // corresponding row
		for (int i = startRow; i < getNumRows(); i++) {
			Expr curEntry = m.getEntry(i, col);
			NumberExpr curNumber;
			if (curEntry instanceof NumberExpr) {
				curNumber = (NumberExpr) curEntry;
			} else {
				curNumber = NumberExpr.ONE;
				// give all non-numeric expressions an (arbitrary) value of 1
				// I'm trying to weight all of them equally
				// this way, the first one will be chosen
			}
			if (Math.abs(curNumber.getValue()) > max) {
				corrRow = i;
				max = Math.abs(curNumber.getValue());
			}
		}
		return corrRow;
	}

	//Override
	public String toString(EvaluationContext e) {
		/*
		 * 218 196 191 179 179 is a box with single lines 192 196 217
		 * 
		 * 210 205 187 186 186 is a box with double lines 200 205 188
		 * 
		 */
		String output = "\n"; // start with a blank line to make sure that everything lines up
		for (int row = -1; row < getNumRows() + 1; row++) {
			for (int col = 0; col < getNumCols() + 1; col++)
			// last col is for matrix bracket ; 1st one is included w/ number
			{
				if (row == -1) {
					// header row

					if (col == 0) {
						// first col, header row
						output += (char) (218) + (spaceBetween ? " " : "") + repeatStr(" ", colWidth(col, e));
					} else if (col == getNumCols()) {
						// bracket ; after all cols
						output += (spaceBetween ? " " : "") + (char) (191) + "\n";
					} else {
						// middle columns, header row
						output += " " + repeatStr(" ", colWidth(col, e));
					}

				} else if (row == getNumRows()) {
					// last row

					if (col == 0) {
						// first col, last row
						output += (char) (192) + (spaceBetween ? " " : "") + repeatStr(" ", colWidth(col, e));
					} else if (col == getNumCols()) {
						// bracket; after all cols
						output += (spaceBetween ? " " : "") + (char) (217) + "\n";
					} else {
						// middle columns, last row
						output += " " + repeatStr(" ", colWidth(col, e));
					}

				} else if (col == 0) {
					// first col
					output += (char) (179) + (spaceBetween ? " " : "") + entry(row, col, e);
				} else if (col == getNumCols()) {
					// bracket; after all cols
					output += (spaceBetween ? " " : "") + (char) (179) + "\n";
				} else {
					output += " " + entry(row, col, e);
				}
			}
		}

		return output;
	}

	private String entry(int row, int col, EvaluationContext e) {
		// used with toString
		int maxLength = colWidth(col, e);
		int lengthOfCur = getEntry(row, col).toString(e).length();
		return repeatStr(" ", maxLength - lengthOfCur) + getEntry(row, col).toString(e);
	}

	private int colWidth(int col, EvaluationContext e) throws IllegalArgumentException {
		if (col > getNumCols() - 1 || col < 0) { throw new IllegalArgumentException("The specified column is invalid."); }

		int maxLength = 0;
		// determine max length in column
		for (int row = 0; row < getNumRows(); row++) {
			if (getEntry(row, col).toString(e).length() > maxLength) {
				maxLength = getEntry(row, col).toString(e).length();
			}
		}
		return maxLength;
	}

	public void copyInto(Matrix m) {
		for (int i = 0; i < getNumRows(); i++) {
			for (int j = 0; j < getNumCols(); j++) {
				m.setEntry(i, j, getEntry(i, j));
			}
		}
	}

	private static String repeatStr(String characters, int numTimes) {
		// stores the result of the method ;
		String result = "";

		int n = 1; // counter variable

		while (n <= numTimes) {
			// append the string to the end of "result"
			result = result + characters;
			// augment the counter
			n++;
		} // end of while loop
		return result;
	} // end of repeatStr method

	private int min(int int1, int int2) {
		return (int1 < int2 ? int1 : int2);
	}

	//Override
	public void print(int indentLevel) {
		printSpaces(indentLevel);
		System.out.println("Matrix");
		for (int row = 0; row < getNumRows(); row++) {
			printSpaces(indentLevel + 1);
			System.out.println("Row " + row);
			for (int col = 0; col < getNumCols(); col++) {
				printSpaces(indentLevel + 2);
				System.out.println("Column " + col);
				getEntry(row, col).print(indentLevel + 3);
			}
		}
	}
}
