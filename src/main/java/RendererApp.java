package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.java.matrices.Matrix;
import main.java.matrices.Vector;
import processing.core.*;

public class RendererApp extends PApplet {

    // Object variables
    private List<Vector> vertices;
    private List<List<Integer>> triangles;

    // Camera variables
    private Vector pointC;
    private Vector vectorN;
    private Vector vectorV;
    double d, hx, hy;

    public static void main(String[] args) {

        // Processing setup
        String className = RendererApp.class.getName();

        try {
            PApplet.main(Class.forName(className));
        } catch (ClassNotFoundException e) {
            System.out.println("Could not find class " + className);
            e.printStackTrace();
        }
    }

    @Override
    public void settings() {
        // Creates a drawing canvas
        size(500, 500);
    }

    @Override
    public void setup() {
        // Draws every pixel on the screen as black
        background(0);

        if (loadObjectFile() && loadCameraFile())
            drawObject();
    }

    @Override
    public void keyPressed() {
        if (keyCode == 'R') {
            // reloading files
            background(0);

            if (loadObjectFile() && loadCameraFile())
                drawObject();
        }
    }

    // keyPressed() needs this function declared to work
    @Override
    public void draw() {
    }

    private boolean loadObjectFile() {

        // Initializing or reseting lists
        vertices = new ArrayList<>();
        triangles = new ArrayList<>();

        File folder = new File(".");
        File[] files = folder.listFiles();

        File chosenFile = null;

        for (File f : files) {
            if (f.isFile()) {

                String name = f.getName();
                String ext = name.substring(name.lastIndexOf(".") + 1, name.length());

                if (ext.equals("byu")) {
                    chosenFile = f;
                    break;
                }
            }
        }

        Scanner reader = null;

        if (chosenFile != null) {
            try {
                reader = new Scanner(chosenFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            System.out.println("No .byu file was found");
            return false;
        }

        // reading file
        try {
            int verticeCount = reader.nextInt();
            int triangleCount = reader.nextInt();
            reader.nextLine();

            for (int i = 0; i < verticeCount; i++) {

                double[] point = new double[3];
                point[0] = reader.nextDouble();
                point[1] = reader.nextDouble();
                point[2] = reader.nextDouble();

                reader.nextLine();

                vertices.add(Vector.fromArray(point));
            }

            for (int i = 0; i < triangleCount; i++) {

                triangles.add(new ArrayList<>());

                for (int j = 0; j < 3; j++) {

                    int verticeIndex = reader.nextInt() - 1;

                    triangles.get(i).add(verticeIndex);
                }

                reader.nextLine();
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            reader.close();
            System.out.println("File format was not as specified!");
        }

        return true;
    }

    private boolean loadCameraFile() {

        File folder = new File(".");
        File[] files = folder.listFiles();

        File chosenFile = null;

        for (File f : files) {
            if (f.isFile()) {

                String name = f.getName();
                String ext = name.substring(name.lastIndexOf(".") + 1, name.length());

                if (ext.equals("cam")) {
                    chosenFile = f;
                    break;
                }
            }
        }

        Scanner reader = null;

        if (chosenFile != null) {
            try {
                reader = new Scanner(chosenFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            System.out.println("No .cam file was found");
            return false;
        }

        // reading file
        try {

            double[] temp = new double[3];

            temp[0] = reader.nextDouble();
            temp[1] = reader.nextDouble();
            temp[2] = reader.nextDouble();
            pointC = Vector.fromArray(temp);

            reader.nextLine();

            temp[0] = reader.nextDouble();
            temp[1] = reader.nextDouble();
            temp[2] = reader.nextDouble();
            vectorN = Vector.fromArray(temp);

            reader.nextLine();

            temp[0] = reader.nextDouble();
            temp[1] = reader.nextDouble();
            temp[2] = reader.nextDouble();
            vectorV = Vector.fromArray(temp);

            reader.nextLine();

            hx = reader.nextDouble();
            hy = reader.nextDouble();

            reader.nextLine();

            d = reader.nextDouble();

            reader.nextLine();

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("File format was not as specified!");
            reader.close();

            return false;
        }

        return true;
    }

    private void drawObject() {

        Vector vectorV1 = Vector.sub(vectorV, Vector.proj(vectorV, vectorN));
        Vector vectorU = Vector.cross(vectorN, vectorV1);

        vectorN.normalize();
        vectorV1.normalize();
        vectorU.normalize();

        Matrix world2camera = new Matrix(3, 3);

        for (int i = 0; i < world2camera.getRows(); i++) {
            for (int j = 0; j < world2camera.getColumns(); j++) {
                switch (i) {
                    case 0:
                        world2camera.setValue(i, j, vectorU.getValue(j));
                        break;
                    case 1:
                        world2camera.setValue(i, j, vectorV1.getValue(j));
                        break;
                    case 2:
                        world2camera.setValue(i, j, vectorN.getValue(j));
                        break;
                }
            }
        }

        List<Vector> screenVertices = new ArrayList<>();

        for (Vector point : vertices) {

            double[] transformedPoint = Matrix.mult(world2camera, Matrix.sub(point, pointC))
                    .toArray();
            Vector vertex = new Vector(2);

            // Perspective Projection and normalization
            vertex.setValue(0, d / hx * transformedPoint[0] / transformedPoint[2]);
            vertex.setValue(1, d / hy * transformedPoint[1] / transformedPoint[2]);

            // Camera to screen conversion
            vertex.setValue(0, floor((float) ((vertex.getValue(0) + 1) / 2 * width + 0.5)));
            vertex.setValue(1, floor((float) (height - (vertex.getValue(1) + 1) / 2 * height + 0.5)));

            screenVertices.add(vertex);
        }

        for (List<Integer> t : triangles) {

            List<Vector> points = new ArrayList<>();
            for (int i : t) {
                points.add(screenVertices.get(i));
            }

            // Ordering list by y value
            points.sort((p1, p2) -> Double.compare(p1.getValue(1), p2.getValue(1)));
            // straight base
            drawTriangle(
                    (int) points.get(0).getValue(0), (int) points.get(0).getValue(1),
                    (int) points.get(1).getValue(0), (int) points.get(1).getValue(1),
                    (int) points.get(2).getValue(0), (int) points.get(2).getValue(1), color(255));
        }
    }

    private void drawTriangle(int x0, int y0, int x1, int y1, int x2, int y2, int color) {

        double area = 0.5 * (x0 * (y1 - y2) + x1 * (y2 - y0) + x2 * (y0 - y1));

        // All three points are colinear
        if (area == 0) {
            drawLine(x0, y0, x2, y2, color);
            return;
        }

        double invA1 = (x1 - x0) / (double) (y1 - y0);
        double invA2 = (x2 - x0) / (double) (y2 - y0);

        if (invA1 > invA2) {
            drawTriangle(x0, y0, x2, y2, x1, y1, color);
            return;
        }

        double xMin = x0;
        double xMax = xMin;

        int y = y0;

        set((int) Math.round(xMin), y, color(255));

        y++;
        xMin += invA1;
        xMax += invA2;

        while (y <= Math.min(y1, y2)) {

            for (int i = (int) Math.round(xMin); i < xMax; i++) {
                set(i, y, color);
            }

            y++;
            xMin += invA1;
            xMax += invA2;
        }

        if (y <= Math.max(y1, y2)) {

            if (y1 > y2) {
                drawStraightBaseTriangleInv(x1, y1, (int) Math.round(xMin), y, x2, y2, color);
            } else {
                drawStraightBaseTriangleInv(x2, y2, x1, y1, (int) Math.round(xMax), y, color);
            }
        }
    }

    private void drawStraightBaseTriangleInv(int x0, int y0, int x1, int y1, int x2, int y2,
            int color) {

        double invA1 = (x1 - x0) / (double) (y1 - y0);
        double invA2 = (x2 - x0) / (double) (y2 - y0);

        double xMin = x0;
        double xMax = x0;

        int y = (int) Math.round(y0);

        set((int) Math.round(xMin), y, color(255));

        y--;
        xMin -= invA1;
        xMax -= invA2;

        while (y >= Math.max(y1, y2)) {

            for (int i = (int) Math.round(xMin); i < xMax; i++) {
                set(i, y, color);
            }

            y--;
            xMin -= invA1;
            xMax -= invA2;
        }
    }

    private void drawLine(int x0, int y0, int x1, int y1, int color) {

        int deltaX = x1 - x0;
        int deltaY = y1 - y0;

        double error = 0;
        double deltaErr;

        if (deltaX == 0) {
            deltaErr = 1;
        } else {
            deltaErr = Math.abs(deltaY / (double) deltaX);
        }

        if (deltaX > 0) {

            int y = y0;

            for (int x = x0; x <= x1; x++) {
                set(x, y, color);

                error += deltaErr;
                while (error >= 0.5) {

                    y++;

                    error -= 1;
                }
            }
        } else if (deltaX < 0) {

            int y = y0;

            for (int x = x0; x >= x1; x--) {
                set(x, y, color);

                error += deltaErr;
                while (error >= 0.5) {

                    y++;

                    error -= 1;
                }
            }
        } else {
            if (deltaY > 0) {
                for (int y = y0; y <= y1; y++) {
                    set(x0, y, color);
                }
            } else {
                for (int y = y0; y >= y1; y--) {
                    set(x0, y, color);
                }
            }
        }
    }
}
