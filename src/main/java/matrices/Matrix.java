// This class was based from
// another project: https://github.com/HenriqueSabino/neural-network-library/blob/master/src/main/java/io/github/henriquesabino/math/Matrix.java
package main.java.matrices;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

public class Matrix implements Serializable {

    private static final long serialVersionUID = 2186517158094722057L;
    private double[][] matrix;
    private int rows;
    private int columns;

    public Matrix(int rows, int columns) {

        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("Matrices must have at least one " +
                    "row and one column");
        }

        this.rows = rows;
        this.columns = columns;
        matrix = new double[rows][columns];
    }

    // region Class methods

    public static Matrix add(Matrix a, Matrix b) {
        if (a.getRows() != b.getRows() || a.getColumns() != b.getColumns()) {
            throw new IncompatibleMatricesException("Matrices must be of the same order " +
                    "to perform this operation");
        }

        Matrix result = new Matrix(a.getRows(), a.getColumns());

        for (int i = 0; i < result.getRows(); i++) {
            for (int j = 0; j < result.getColumns(); j++) {
                double value = a.getValue(i, j) + b.getValue(i, j);
                result.setValue(i, j, value);
            }
        }

        return result;
    }

    public static Matrix sub(Matrix a, Matrix b) {
        if (a.getRows() != b.getRows() || a.getColumns() != b.getColumns()) {
            throw new IncompatibleMatricesException("Matrices must be of the same order " +
                    "to perform this operation");
        }

        Matrix result = new Matrix(a.getRows(), a.getColumns());

        for (int i = 0; i < result.getRows(); i++) {
            for (int j = 0; j < result.getColumns(); j++) {
                double value = a.getValue(i, j) - b.getValue(i, j);
                result.setValue(i, j, value);
            }
        }

        return result;
    }

    public static Matrix scalarMult(Matrix a, double scalar) {
        Matrix result = a.copy();

        result.applyForEach(x -> x * scalar);

        return result;
    }

    public static Matrix scalarDiv(Matrix a, double scalar) {

        if (scalar == 0) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }

        Matrix result = a.copy();

        result.applyForEach(x -> x / scalar);

        return result;
    }

    public static Matrix mult(Matrix a, Matrix b) {

        if (a.getColumns() != b.getRows()) {
            throw new IncompatibleMatricesException("The number of columns of the first matrix must " +
                    "equal the number of rows of the other");
        }

        Matrix result = new Matrix(a.getRows(), b.getColumns());

        for (int i = 0; i < result.getRows(); i++) {
            for (int j = 0; j < result.getColumns(); j++) {
                for (int k = 0; k < a.getColumns(); k++) {
                    double value = result.getValue(i, j) + a.getValue(i, k) * b.getValue(k, j);
                    result.setValue(i, j, value);
                }
            }
        }

        return result;
    }

    public static Matrix fromArrayToColumnMatrix(double[] array) {
        Matrix result = new Matrix(array.length, 1);

        for (int i = 0; i < array.length; i++) {
            result.setValue(i, 0, array[i]);
        }

        return result;
    }

    public static Matrix fromArrayToRowMatrix(double[] array) {
        Matrix result = new Matrix(1, array.length);

        for (int i = 0; i < array.length; i++) {
            result.setValue(0, i, array[i]);
        }

        return result;
    }

    public static Matrix transpose(Matrix a) {

        Matrix result = new Matrix(a.getColumns(), a.getRows());

        for (int i = 0; i < result.getRows(); i++) {
            for (int j = 0; j < result.getColumns(); j++) {
                double value = a.getValue(j, i);
                result.setValue(i, j, value);
            }
        }

        return result;
    }

    // endregion

    // region Instance methods

    public void add(Matrix other) {
        if (getRows() != other.getRows() || getColumns() != other.getColumns()) {
            throw new IncompatibleMatricesException("Matrices must be of the same order " +
                    "to perform this operation");
        }

        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                double value = getValue(i, j) + other.getValue(i, j);
                setValue(i, j, value);
            }
        }
    }

    public void sub(Matrix other) {
        if (getRows() != other.getRows() || getColumns() != other.getColumns()) {
            throw new IncompatibleMatricesException("Matrices must be of the same order " +
                    "to perform this operation");
        }

        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                double value = getValue(i, j) - other.getValue(i, j);
                setValue(i, j, value);
            }
        }
    }

    public void scalarMult(double scalar) {
        applyForEach(x -> x * scalar);
    }

    public void scalarDiv(double scalar) {

        if (scalar == 0) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }

        applyForEach(x -> x / scalar);
    }

    public void transpose() {

        double[][] newValues = new double[getColumns()][getRows()];

        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                newValues[j][i] = getValue(i, j);
            }
        }

        this.matrix = newValues;
    }

    public void randomize() {
        applyForEach(p -> Math.random() * 2 - 1);
    }

    public void applyForEach(Function<Double, Double> function) {

        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                setValue(i, j, function.apply(getValue(i, j)));
            }
        }
    }

    public Matrix copy() {
        Matrix copy = new Matrix(rows, columns);

        for (int i = 0; i < copy.getRows(); i++) {
            for (int j = 0; j < copy.getColumns(); j++) {
                copy.setValue(i, j, getValue(i, j));
            }
        }

        return copy;
    }

    public double[] toArray() {
        // Transforms the matrix to an array, but the matrix dimentions are lost
        double[] result = new double[getRows() * getColumns()];

        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                int index = j + i * getColumns();
                result[index] = getValue(i, j);
            }
        }

        return result;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public double getValue(int row, int column) {
        return matrix[row][column];
    }

    public void setValue(int row, int column, double value) {
        matrix[row][column] = value;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(rows, columns);
        result = 31 * result + Arrays.deepHashCode(matrix);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Matrix matrix1 = (Matrix) o;
        return rows == matrix1.rows &&
                columns == matrix1.columns &&
                Arrays.deepEquals(matrix, matrix1.matrix);
    }

    // endregion

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < getRows(); i++) {
            stringBuilder.append("| ");
            for (int j = 0; j < getColumns(); j++) {
                stringBuilder.append(getValue(i, j));
                stringBuilder.append(" | ");
            }
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}