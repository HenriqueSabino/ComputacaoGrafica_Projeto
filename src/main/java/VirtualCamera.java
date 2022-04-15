package main.java;

import main.java.matrices.Matrix;
import main.java.matrices.Vector;

public class VirtualCamera {

    // Camera variables
    private Vector pointC;
    private Vector vectorN;
    private Vector vectorV;
    private double d, hx, hy;

    private Matrix w2sMat;

    public VirtualCamera(Vector pointC, Vector vectorN, Vector vectorV, double d, double hx, double hy) {
        this.pointC = pointC;
        this.vectorN = vectorN;
        this.vectorV = vectorV;
        this.d = d;
        this.hx = hx;
        this.hy = hy;

        computeConversionMatrix();
    }

    private void computeConversionMatrix() {

        Vector vectorV1 = Vector.sub(vectorV, Vector.proj(vectorV, vectorN));
        Vector vectorU = Vector.cross(vectorN, vectorV1);

        vectorN.normalize();
        vectorV1.normalize();
        vectorU.normalize();

        w2sMat = new Matrix(3, 3);

        for (int i = 0; i < w2sMat.getRows(); i++) {
            for (int j = 0; j < w2sMat.getColumns(); j++) {
                switch (i) {
                    case 0:
                        w2sMat.setValue(i, j, vectorU.getValue(j));
                        break;
                    case 1:
                        w2sMat.setValue(i, j, vectorV1.getValue(j));
                        break;
                    case 2:
                        w2sMat.setValue(i, j, vectorN.getValue(j));
                        break;
                }
            }
        }
    }

    public Vector world2Sight(Vector point) {

        double[] transformedPoint = Matrix.mult(w2sMat, Matrix.sub(point, pointC)).toArray();

        return Vector.fromArray(transformedPoint);
    }

    public Vector sight2Screen(Vector point, int width, int height) {
        Vector vertex = new Vector(2);

        // Perspective Projection and normalization
        vertex.setValue(0, d / hx * point.getValue(0) / point.getValue(2));
        vertex.setValue(1, d / hy * point.getValue(1) / point.getValue(2));

        // Camera to screen conversion
        vertex.setValue(0, Math.floor(((vertex.getValue(0) + 1) / 2 * width + 0.5)));
        vertex.setValue(1, Math.floor((height - (vertex.getValue(1) + 1) / 2 * height + 0.5)));

        return vertex;
    }

    public Vector getPointC() {
        return pointC;
    }
}