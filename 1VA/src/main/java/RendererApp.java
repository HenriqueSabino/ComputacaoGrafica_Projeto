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
        loadObjectFile();
        loadCameraFile();
        drawObject();
    }

    @Override
    public void keyPressed() {
        if (keyCode == 'R') {
            // reloading files
            background(0);
            loadObjectFile();
            loadCameraFile();
            drawObject();
        }
    }

    // keyPressed() needs this function declared to work
    @Override
    public void draw() {
    }

    private void loadObjectFile() {

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
                return;
            }
        } else {
            System.out.println("No .byu file was found");
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
            System.out.println("File format was not as specified!");
        }
    }

    private void loadCameraFile() {

        double[] temp = new double[3];
        temp[0] = 1;
        temp[1] = 1;
        temp[2] = 2;
        pointC = Vector.fromArray(temp);

        temp[0] = -1;
        temp[1] = -1;
        temp[2] = -1;
        vectorN = (Vector) Vector.fromArray(temp);

        temp[0] = 0;
        temp[1] = 0;
        temp[2] = 1;
        vectorV = (Vector) Vector.fromArray(temp);

        d = 1;
        hx = 1;
        hy = 1;
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

            double[] transformedPoint = Matrix.mult(world2camera, Vector.sub(point, pointC)).toArray();
            Vector vertex = new Vector(2);

            // Perspective Projection and normalization
            vertex.setValue(0, d / hx * transformedPoint[0] / transformedPoint[2]);
            vertex.setValue(1, d / hy * transformedPoint[1] / transformedPoint[2]);

            // Camera to screen conversion
            vertex.setValue(0, floor((float) ((vertex.getValue(0) + 1) / 2 * width + 0.5)));
            vertex.setValue(1, floor((float) (height - (vertex.getValue(1) + 1) / 2 * height + 0.5)));

            screenVertices.add(vertex);
        }

        // TODO: Change these calls to point draw only
        fill(255);
        for (List<Integer> t : triangles) {
            beginShape();
            for (int i : t) {
                vertex((float) screenVertices.get(i).getValue(0), (float) screenVertices.get(i).getValue(1));
            }
            endShape();
        }
    }
}
