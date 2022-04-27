package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import main.java.matrices.Matrix;
import main.java.matrices.Vector;
import processing.core.*;

public class RendererApp extends PApplet {

    // Object variables
    private List<Vector> vertices;
    private List<List<Integer>> triangles;

    // Camera
    private VirtualCamera camera;

    // Ilumination
    private Vector Iamb, Il;
    private double Ka, Ks, n;
    private Vector Kd, Od;
    private Vector Pl;

    // zBuffer
    private double[][] zBuffer;

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
        zBuffer = new double[width][height];
        // Draws every pixel on the screen as black
        background(0);

        if (loadObjectFile() && loadCameraFile() && loadIluminationFile())
            drawObject();
    }

    @Override
    public void keyPressed() {
        if (keyCode == 'R') {
            // reloading files
            background(0);

            if (loadObjectFile() && loadCameraFile() && loadIluminationFile())
                drawObject();
        } else if (keyCode == 'P') {
            println(mouseX + " " + mouseY);
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
            Vector pointC = Vector.fromArray(temp);

            reader.nextLine();

            temp[0] = reader.nextDouble();
            temp[1] = reader.nextDouble();
            temp[2] = reader.nextDouble();
            Vector vectorN = Vector.fromArray(temp);

            reader.nextLine();

            temp[0] = reader.nextDouble();
            temp[1] = reader.nextDouble();
            temp[2] = reader.nextDouble();
            Vector vectorV = Vector.fromArray(temp);

            reader.nextLine();

            double hx = reader.nextDouble();
            double hy = reader.nextDouble();

            reader.nextLine();

            double d = reader.nextDouble();

            reader.nextLine();

            camera = new VirtualCamera(pointC, vectorN, vectorV, d, hx, hy);

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("File format was not as specified!");
            reader.close();

            return false;
        }

        return true;
    }

    private boolean loadIluminationFile() {

        File folder = new File(".");
        File[] files = folder.listFiles();

        File chosenFile = null;

        for (File f : files) {
            if (f.isFile()) {

                String name = f.getName();
                String ext = name.substring(name.lastIndexOf(".") + 1, name.length());

                if (ext.equals("lux")) {
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
            System.out.println("No .lux file was found");
            return false;
        }

        // reading file
        try {

            double[] temp = new double[3];

            temp[0] = reader.nextInt();
            temp[1] = reader.nextInt();
            temp[2] = reader.nextInt();
            Iamb = Vector.fromArray(temp);

            reader.nextLine();

            Ka = reader.nextDouble();

            reader.nextLine();

            temp[0] = reader.nextInt();
            temp[1] = reader.nextInt();
            temp[2] = reader.nextInt();
            Il = Vector.fromArray(temp);

            reader.nextLine();

            temp[0] = reader.nextDouble();
            temp[1] = reader.nextDouble();
            temp[2] = reader.nextDouble();
            Pl = Vector.fromArray(temp);

            reader.nextLine();

            temp[0] = reader.nextDouble();
            temp[1] = reader.nextDouble();
            temp[2] = reader.nextDouble();
            Kd = Vector.fromArray(temp);

            reader.nextLine();

            temp[0] = reader.nextDouble();
            temp[1] = reader.nextDouble();
            temp[2] = reader.nextDouble();
            Od = Vector.fromArray(temp);

            reader.nextLine();

            Ks = reader.nextDouble();

            reader.nextLine();

            n = reader.nextDouble();

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

        // Resetting zBuffer
        for (int i = 0; i < zBuffer.length; i++) {
            for (int j = 0; j < zBuffer[i].length; j++) {
                zBuffer[i][j] = Double.POSITIVE_INFINITY;
            }
        }

        List<Vector> sightVertices = new ArrayList<>();

        for (Vector point : vertices) {
            sightVertices.add(camera.world2Sight(point));
        }

        List<Vector> normals = calculateNormals(triangles, sightVertices);

        triangles.sort((t1, t2) -> sortBarycenter(t1, t2, sightVertices));

        for (List<Integer> t : triangles) {

            Map<Vector, Vector> pointsAndNormals = new LinkedHashMap<>();

            for (int i : t) {
                pointsAndNormals.put(sightVertices.get(i), normals.get(i));
            }

            pointsAndNormals = pointsAndNormals.entrySet().stream().sorted((e1, e2) -> Double.compare(
                    camera.sight2Screen(
                            e1.getKey(), width, height).getValue(1),
                    camera.sight2Screen(
                            e2.getKey(), width, height).getValue(1)))
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            List<Vector> points = pointsAndNormals.keySet().stream().collect(Collectors.toList());
            List<Vector> verticesNormals = pointsAndNormals.values().stream().collect(Collectors.toList());

            // straight base
            drawTriangle(points, verticesNormals);
        }
    }

    private int sortBarycenter(List<Integer> t1, List<Integer> t2, List<Vector> points) {
        Vector barycenter1 = new Vector(3);
        Vector barycenter2 = new Vector(3);

        for (Integer i : t1) {
            barycenter1 = Vector.add(barycenter1, Vector.scalarDiv(points.get(i), 3));
        }

        for (Integer i : t2) {
            barycenter2 = Vector.add(barycenter2, Vector.scalarDiv(points.get(i), 3));
        }

        return Double.compare(barycenter2.getValue(2), barycenter1.getValue(2));
    }

    private List<Vector> calculateNormals(List<List<Integer>> triangles, List<Vector> vertices) {

        // calculating triangle normals
        List<Vector> vertexNormals = new ArrayList<>();

        for (int i = 0; i < vertices.size(); i++) {
            vertexNormals.add(new Vector(3));
        }

        for (List<Integer> t : triangles) {
            List<Vector> points = new ArrayList<>();
            for (int i : t) {
                points.add(vertices.get(i));
            }

            Vector normal = Vector.cross(
                    Vector.sub(points.get(2), points.get(0)), Vector.sub(points.get(1), points.get(0)));
            normal = Vector.normalize(normal);

            for (int i : t) {
                Vector tmp = vertexNormals.get(i);
                vertexNormals.set(i, Vector.add(tmp, normal));
            }
        }

        for (int i = 0; i < vertexNormals.size(); i++) {
            vertexNormals.set(i, Vector.normalize(vertexNormals.get(i)));
        }

        return vertexNormals;
    }

    private void drawTriangle(List<Vector> points, List<Vector> normals) {

        List<Vector> screenPoints = points.stream().map((p) -> camera.sight2Screen(p, width, height))
                .collect(Collectors.toList());

        int x0 = (int) screenPoints.get(0).getValue(0);
        int y0 = (int) screenPoints.get(0).getValue(1);
        int x1 = (int) screenPoints.get(1).getValue(0);
        int y1 = (int) screenPoints.get(1).getValue(1);
        int x2 = (int) screenPoints.get(2).getValue(0);
        int y2 = (int) screenPoints.get(2).getValue(1);

        // Offscreen triangles
        if (y0 > height || y2 < 0)
            return;

        if (x1 > x2 && (x1 < 0 || x2 > width))
            return;
        else if (x2 > x1 && (x2 < 0 || x1 > width))
            return;

        // defining the slopes
        double dx1 = (y1 - y0 > 0) ? (x1 - x0) / (double) (y1 - y0) : 0;
        double dx2 = (y2 - y0 > 0) ? (x2 - x0) / (double) (y2 - y0) : 0;

        double xMin = x0, xMax = x0;
        int y = y0;

        // first half
        while (y <= y1) {

            int xStart = (xMin < xMax) ? (int) xMin : (int) xMax;
            int xEnd = (xMin < xMax) ? (int) xMax : (int) xMin;

            for (int x = xStart; x <= xEnd; x++) {
                drawPoint(x, y, screenPoints, points, normals);
            }

            y++;
            xMin += dx1;
            xMax += dx2;
        }

        dx1 = (y2 - y1 > 0) ? (x2 - x1) / (double) (y2 - y1) : 0;

        xMin = x1;
        y = y1;
        xMax = (int) (dx2 * (y - y0)) + x0;

        // second half
        while (y <= y2) {

            int xStart = (xMin < xMax) ? (int) xMin : (int) xMax;
            int xEnd = (xMin < xMax) ? (int) xMax : (int) xMin;

            for (int x = xStart; x <= xEnd; x++) {
                drawPoint(x, y, screenPoints, points, normals);
            }

            y++;
            xMin += dx1;
            xMax += dx2;
        }

    }

    private void drawPoint(int x, int y, List<Vector> screenTriangle, List<Vector> triangle,
            List<Vector> normals) {

        Vector screenP = new Vector(2);
        screenP.setValue(0, x);
        screenP.setValue(1, y);

        if (isLine(screenTriangle)) {

            double percent = (y - screenTriangle.get(0).getValue(1)) /
                    (screenTriangle.get(2).getValue(1) - screenTriangle.get(0).getValue(1));

            Vector P = Vector.lerp(triangle.get(0), triangle.get(1), percent);
            Vector normal = Vector.lerp(normals.get(0), normals.get(1), percent);

            set(x, y, calculateIlumination(P, normal));
        } else {

            Vector baryCoord = barycentricCoords(screenTriangle, screenP);

            Vector P = Vector.scalarMult(triangle.get(0), baryCoord.getValue(0));
            P = Vector.add(P, Vector.scalarMult(triangle.get(1), baryCoord.getValue(1)));
            P = Vector.add(P, Vector.scalarMult(triangle.get(2), baryCoord.getValue(2)));

            if (P.getValue(2) > 0 && zBuffer[x][y] > P.getValue(2)) {
                zBuffer[x][y] = P.getValue(2);

                Vector normal = Vector.scalarMult(normals.get(0), baryCoord.getValue(0));
                normal = Vector.add(normal, Vector.scalarMult(normals.get(1), baryCoord.getValue(1)));
                normal = Vector.add(normal, Vector.scalarMult(normals.get(2), baryCoord.getValue(2)));

                // normal = Vector.normalize(normal);
                // if (baryCoord.getValue(0) > 1 || baryCoord.getValue(1) > 1 ||
                // baryCoord.getValue(2) > 1
                // || baryCoord.getValue(0) < 0 || baryCoord.getValue(1) < 0
                // || baryCoord.getValue(2) < 0) {
                // set(x, y, color(0, 255, 0));
                // println();
                // } else
                set(x, y, calculateIlumination(P, normal));
            }
        }
    }

    private boolean isLine(List<Vector> points) {

        // checks if the area of the triangle is zero
        double a = points.get(0).getValue(0) - points.get(2).getValue(0);
        double b = points.get(1).getValue(0) - points.get(2).getValue(0);
        double c = points.get(0).getValue(1) - points.get(2).getValue(1);
        double d = points.get(1).getValue(1) - points.get(2).getValue(1);

        return a * d - b * c == 0;
    }

    private Vector barycentricCoords(List<Vector> triangle, Vector P) {

        Vector A = triangle.get(0), B = triangle.get(1), C = triangle.get(2);

        double a = A.getValue(0) - C.getValue(0);
        double b = B.getValue(0) - C.getValue(0);
        double c = A.getValue(1) - C.getValue(1);
        double d = B.getValue(1) - C.getValue(1);

        double invDet = 1 / (a * d - b * c);

        Matrix invT = new Matrix(2, 2);

        invT.setValue(0, 0, d);
        invT.setValue(0, 1, -b);
        invT.setValue(1, 0, -c);
        invT.setValue(1, 1, a);

        invT = Matrix.scalarMult(invT, invDet);

        Matrix pC = new Matrix(2, 1);
        pC.setValue(0, 0, P.getValue(0) - C.getValue(0));
        pC.setValue(1, 0, P.getValue(1) - C.getValue(1));

        Matrix alphaBeta = Matrix.mult(invT, pC);

        double alpha = alphaBeta.getValue(0, 0);
        double beta = alphaBeta.getValue(1, 0);
        double gamma = 1 - alpha - beta;

        Vector barCoord = new Vector(3);
        barCoord.setValue(0, alpha);
        barCoord.setValue(1, beta);
        barCoord.setValue(2, gamma);

        return barCoord;
    }

    private int calculateIlumination(Vector P, Vector N) {

        Vector Ia, Id, Is;
        Ia = new Vector(3);
        Id = new Vector(3);
        Is = new Vector(3);

        Vector V = Vector.scalarMult(P, -1);
        V = Vector.normalize(V);
        Vector L = Vector.sub(Pl, P);
        L = Vector.normalize(L);
        // ambient ilumination
        Ia = Vector.scalarMult(Iamb, Ka);

        boolean ignoreLightSource = false, ignoreSpecular = false;

        double dotNL = Vector.dot(N, L);

        if (dotNL < 0) {
            if (Vector.dot(V, N) < 0) {
                N = Vector.scalarMult(N, -1);
                dotNL = Vector.dot(N, L);
                // diffuse ilumination
            } else {
                ignoreLightSource = true;
            }
        }

        Vector R = Vector.sub(Vector.scalarMult(N, 2 * dotNL), L);

        if (Vector.dot(V, R) < 0) {
            ignoreSpecular = true;
        }

        if (!ignoreLightSource) {
            Id = Vector.componentMult(Kd, Il);
            Id = Vector.componentMult(Id, Od);
            Id = Vector.scalarMult(Id, dotNL);

            if (!ignoreSpecular) {
                // phong ilumination
                Is = Vector.scalarMult(Il, Math.pow(Vector.dot(R, V), n) * Ks);
            }
        }

        float r = 0, g = 0, b = 0;

        r += (float) Ia.getValue(0);
        g += (float) Ia.getValue(1);
        b += (float) Ia.getValue(2);

        r += (float) Id.getValue(0);
        g += (float) Id.getValue(1);
        b += (float) Id.getValue(2);

        r += (float) Is.getValue(0);
        g += (float) Is.getValue(1);
        b += (float) Is.getValue(2);

        if (r > 255)
            r = 255;
        if (g > 255)
            g = 255;
        if (b > 255)
            b = 255;

        return color(r, g, b);
    }
}
