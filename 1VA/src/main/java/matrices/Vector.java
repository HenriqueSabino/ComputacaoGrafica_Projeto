package main.java.matrices;

/**
 * Vector
 */
public class Vector extends Matrix {

    public Vector(int dim) {
        super(dim, 1);
    }

    public static Vector add(Vector a, Vector b) {
        if (a.getRows() != b.getRows()) {
            throw new IncompatibleMatricesException("Matrices must be of the same order " +
                    "to perform this operation");
        }

        Vector result = new Vector(a.getRows());

        for (int i = 0; i < result.getRows(); i++) {
            double value = a.getValue(i) + b.getValue(i);
            result.setValue(i, value);
        }

        return result;
    }

    public static Vector sub(Vector a, Vector b) {
        if (a.getRows() != b.getRows()) {
            throw new IncompatibleMatricesException("Matrices must be of the same order " +
                    "to perform this operation");
        }

        Vector result = new Vector(a.getRows());

        for (int i = 0; i < result.getRows(); i++) {
            double value = a.getValue(i) - b.getValue(i);
            result.setValue(i, value);
        }

        return result;
    }

    public static Vector scalarMult(Vector a, double scalar) {
        Vector result = a.copy();

        result.applyForEach(x -> x * scalar);

        return result;
    }

    public static Vector scalarDiv(Vector a, double scalar) {

        if (scalar == 0) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }

        Vector result = a.copy();

        result.applyForEach(x -> x / scalar);

        return result;
    }

    public static Vector fromArray(double[] array) {
        Vector result = new Vector(array.length);

        for (int i = 0; i < array.length; i++) {
            result.setValue(i, array[i]);
        }

        return result;
    }

    public static double dot(Vector a, Vector b) {
        if (a.getRows() != b.getRows()) {
            throw new IncompatibleMatricesException("The vectors must have the same amount of elements");
        }

        double dot = 0;

        for (int i = 0; i < a.getRows(); i++) {
            dot += a.getValue(i) * b.getValue(i);
        }

        return dot;
    }

    public static Vector cross(Vector a, Vector b) {
        if (a.getRows() != 3 || b.getRows() != 3) {
            throw new IncompatibleMatricesException("The vectors must in R3");
        }

        // formula from https://en.wikipedia.org/wiki/Cross_product#Coordinate_notation
        Vector cross = new Vector(3);

        cross.setValue(0, a.getValue(1) * b.getValue(2) - a.getValue(2) * b.getValue(1));
        cross.setValue(1, a.getValue(2) * b.getValue(0) - a.getValue(0) * b.getValue(2));
        cross.setValue(2, a.getValue(0) * b.getValue(1) - a.getValue(1) * b.getValue(0));

        return cross;
    }

    public static Vector proj(Vector a, Vector b) {
        if (a.getRows() != b.getRows()) {
            throw new IncompatibleMatricesException("The vectors must have the same amount of elements");
        }

        double scale = dot(a, b) / dot(b, b);

        return (Vector) Vector.scalarMult(b, scale);
    }

    public static double norm(Vector a) {

        double sum = 0;

        for (int i = 0; i < a.getRows(); i++) {
            sum += a.getValue(i) * a.getValue(i);
        }

        return Math.sqrt(sum);
    }

    public static Vector normalize(Vector a) {
        return scalarDiv(a, norm(a));
    }

    // endregion

    public double norm() {

        double sum = 0;

        for (int i = 0; i < getRows(); i++) {
            sum += getValue(i) * getValue(i);
        }

        return Math.sqrt(sum);
    }

    public void normalize() {

        double norm = norm();

        for (int i = 0; i < getRows(); i++) {
            setValue(i, getValue(i) / norm);
        }
    }

    public Vector copy() {
        Vector copy = new Vector(getRows());

        for (int i = 0; i < copy.getRows(); i++) {
            copy.setValue(i, getValue(i));
        }

        return copy;
    }

    public double getValue(int row) {
        return super.getValue(row, 0);
    }

    public void setValue(int row, double value) {
        super.setValue(row, 0, value);
    }
}