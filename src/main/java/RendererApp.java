package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.stream.Collector;
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
        zBuffer = new double[width][height];
    }

    @Override
    public void setup() {
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

            Vector normal = Vector.cross(Vector.sub(points.get(1), points.get(0)),
                    Vector.sub(points.get(2), points.get(0)));
            normal.normalize();

            for (int i : t) {
                vertexNormals.get(i).add(normal);
            }
        }

        for (Vector n : vertexNormals) {
            n.normalize();
        }

        return vertexNormals;
    }

    private void drawTriangle(List<Vector> points, List<Vector> normals) {

        List<Vector> screenPoints = points.stream().map(p -> camera.sight2Screen(p, width, height))
                .collect(Collectors.toList());

        int x0 = (int) screenPoints.get(0).getValue(0);
        int y0 = (int) screenPoints.get(0).getValue(1);
        double z0 = points.get(0).getValue(2);

        int x1 = (int) screenPoints.get(1).getValue(0);
        int y1 = (int) screenPoints.get(1).getValue(1);
        double z1 = points.get(1).getValue(2);

        int x2 = (int) screenPoints.get(2).getValue(0);
        int y2 = (int) screenPoints.get(2).getValue(1);
        double z2 = points.get(2).getValue(2);

        double area = 0.5 * (x0 * (y1 - y2) + x1 * (y2 - y0) + x2 * (y0 - y1));

        // All three points are colinear
        if (area == 0) {
            points.remove(1);
            normals.remove(1);
            drawLine(points, normals);
            return;
        }

        double invA1 = (x1 - x0) / (double) (y1 - y0);
        double invA2 = (x2 - x0) / (double) (y2 - y0);

        if (invA1 > invA2) {
            Vector tmp = points.get(1);
            points.set(1, points.get(2));
            points.set(2, tmp);

            tmp = normals.get(1);
            normals.set(1, normals.get(2));
            normals.set(2, tmp);

            drawTriangle(points, normals);
            return;
        }

        double xMin = x0;
        double xMax = xMin;

        int y = y0;

        drawTrianglePoint(x0, y0, z0, points.get(0), normals.get(0));

        y++;
        xMin += invA1;
        xMax += invA2;

        while (y <= Math.min(y1, y2)) {

            for (int x = (int) Math.round(xMin); x <= xMax; x++) {

                Vector screenP = new Vector(2);
                screenP.setValue(0, x);
                screenP.setValue(1, y);

                Vector barCoord = barycentricCoords(screenPoints, screenP);

                double z = z0 * barCoord.getValue(0) + z1 * barCoord.getValue(1) + z2 * barCoord.getValue(2);

                Vector P = Vector.scalarMult(points.get(0), barCoord.getValue(0));
                P.add(Vector.scalarMult(points.get(1), barCoord.getValue(1)));
                P.add(Vector.scalarMult(points.get(2), barCoord.getValue(2)));

                Vector normal = Vector.scalarMult(normals.get(0), barCoord.getValue(0));
                normal.add(Vector.scalarMult(normals.get(1), barCoord.getValue(1)));
                normal.add(Vector.scalarMult(normals.get(2), barCoord.getValue(2)));

                drawTrianglePoint(x, y, z, P, normal);
            }

            y++;
            xMin += invA1;
            xMax += invA2;
        }

        if (y <= Math.max(y1, y2)) {

            if (y1 > y2) {

                List<Vector> p = new ArrayList<>();
                double percent = (y - y0) / (double) (y1 - y0);

                p.add(points.get(1));
                p.add(Vector.lerp(points.get(0), points.get(1), percent));
                p.add(points.get(2));

                List<Vector> n = new ArrayList<>();
                n.add(normals.get(1));
                n.add(Vector.lerp(normals.get(0), normals.get(1), percent));
                n.add(normals.get(2));

                drawStraightBaseTriangleInv(p, n);
            } else {

                List<Vector> p = new ArrayList<>();
                double percent = (y - y0) / (double) (y2 - y0);

                p.add(points.get(2));
                p.add(points.get(1));
                p.add(Vector.lerp(points.get(0), points.get(2), percent));

                List<Vector> n = new ArrayList<>();
                n.add(normals.get(2));
                n.add(normals.get(1));
                n.add(Vector.lerp(normals.get(0), normals.get(2), percent));

                drawStraightBaseTriangleInv(p, n);
            }
        }
    }

    private void drawStraightBaseTriangleInv(List<Vector> points, List<Vector> normals) {

        List<Vector> screenPoints = points.stream().map(p -> camera.sight2Screen(p, width, height))
                .collect(Collectors.toList());

        int x0 = (int) screenPoints.get(0).getValue(0);
        int y0 = (int) screenPoints.get(0).getValue(1);
        double z0 = points.get(0).getValue(2);

        int x1 = (int) screenPoints.get(1).getValue(0);
        int y1 = (int) screenPoints.get(1).getValue(1);
        double z1 = points.get(1).getValue(2);

        int x2 = (int) screenPoints.get(2).getValue(0);
        int y2 = (int) screenPoints.get(2).getValue(1);
        double z2 = points.get(2).getValue(2);

        double invA1 = (x1 - x0) / (double) (y1 - y0);
        double invA2 = (x2 - x0) / (double) (y2 - y0);

        double xMin = x0;
        double xMax = x0;

        int y = (int) Math.round(y0);

        drawTrianglePoint(x0, y0, z0, points.get(0), normals.get(0));

        y--;
        xMin -= invA1;
        xMax -= invA2;

        while (y >= Math.max(y1, y2)) {

            for (int x = (int) Math.round(xMin); x <= xMax; x++) {

                Vector screenP = new Vector(2);
                screenP.setValue(0, x);
                screenP.setValue(1, y);

                Vector barCoord = barycentricCoords(screenPoints, screenP);

                double z = z0 * barCoord.getValue(0) + z1 * barCoord.getValue(1) + z2 * barCoord.getValue(2);

                Vector P = Vector.scalarMult(points.get(0), barCoord.getValue(0));
                P.add(Vector.scalarMult(points.get(1), barCoord.getValue(1)));
                P.add(Vector.scalarMult(points.get(2), barCoord.getValue(2)));

                Vector normal = Vector.scalarMult(normals.get(0), barCoord.getValue(0));
                normal.add(Vector.scalarMult(normals.get(1), barCoord.getValue(1)));
                normal.add(Vector.scalarMult(normals.get(2), barCoord.getValue(2)));

                drawTrianglePoint(x, y, z, P, normal);
            }

            y--;
            xMin -= invA1;
            xMax -= invA2;
        }
    }

    private void drawTrianglePoint(int x, int y, double z, Vector P, Vector normal) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            if (z > 0 && zBuffer[x][y] > z) {
                zBuffer[x][y] = z;
                set(x, y, calculateIlumination(P, normal));
            }
        }
    }

    private void drawLine(List<Vector> points, List<Vector> normals) {

        List<Vector> screenPoints = points.stream().map(p -> camera.sight2Screen(p, width, height))
                .collect(Collectors.toList());

        int x0 = (int) screenPoints.get(0).getValue(0);
        int y0 = (int) screenPoints.get(0).getValue(1);
        double z0 = points.get(0).getValue(2);

        int x1 = (int) screenPoints.get(1).getValue(0);
        int y1 = (int) screenPoints.get(1).getValue(1);
        double z1 = points.get(1).getValue(2);

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

                double percent = (x - x0) / (double) (x1 - x0);
                double z = lerp(z0, z1, percent);
                Vector P = Vector.lerp(points.get(0), points.get(1), percent);
                Vector normal = Vector.lerp(normals.get(0), normals.get(1), percent);

                drawLinePoint(x, y, z, P, normal);

                error += deltaErr;
                while (error >= 0.5) {

                    y++;

                    error -= 1;
                }
            }
        } else if (deltaX < 0) {

            int y = y0;

            for (int x = x0; x >= x1; x--) {

                double percent = (x - x1) / (double) (x0 - x1);
                double z = lerp(z1, z0, percent);
                Vector P = Vector.lerp(points.get(1), points.get(0), percent);
                Vector normal = Vector.lerp(normals.get(1), normals.get(0), percent);

                drawLinePoint(x, y, z, P, normal);

                error += deltaErr;
                while (error >= 0.5) {

                    y++;

                    error -= 1;
                }
            }
        } else {
            if (deltaY > 0) {
                for (int y = y0; y <= y1; y++) {

                    double percent = (y - y0) / (double) (y1 - y0);
                    double z = lerp(z0, z1, percent);
                    Vector P = Vector.lerp(points.get(0), points.get(1), percent);
                    Vector normal = Vector.lerp(normals.get(0), normals.get(1), percent);

                    drawLinePoint(x0, y, z, P, normal);
                }
            } else {
                for (int y = y0; y >= y1; y--) {
                    double percent = (y - y1) / (double) (y0 - y1);
                    double z = lerp(z1, z0, percent);
                    Vector P = Vector.lerp(points.get(1), points.get(0), percent);
                    Vector normal = Vector.lerp(normals.get(1), normals.get(0), percent);

                    drawLinePoint(x0, y, z, P, normal);
                }
            }
        }
    }

    private void drawLinePoint(int x, int y, double z, Vector P, Vector normal) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            if (zBuffer[x][y] > z) {
                zBuffer[x][y] = z;
                set(x, y, calculateIlumination(P, normal));
            }
        }
    }

    private double lerp(double start, double end, double percent) {
        return (end - start) * percent + start;
    }

    private Vector barycentricCoords(List<Vector> triangle, Vector p0) {

        Vector p1 = triangle.get(0), p2 = triangle.get(1), p3 = triangle.get(2);

        double a = p1.getValue(0) - p3.getValue(0);
        double b = p2.getValue(0) - p3.getValue(0);
        double c = p1.getValue(1) - p3.getValue(1);
        double d = p2.getValue(1) - p3.getValue(1);

        double invDet = 1 / (a * d - b * c);

        if (!Double.isFinite(invDet)) {
            println("not finite");
        }

        Matrix invT = new Matrix(2, 2);

        invT.setValue(0, 0, d);
        invT.setValue(0, 1, -b);
        invT.setValue(1, 0, -c);
        invT.setValue(1, 1, a);

        invT.scalarMult(invDet);

        Matrix p = new Matrix(2, 1);
        p.setValue(0, 0, p0.getValue(0) - p3.getValue(0));
        p.setValue(1, 0, p0.getValue(1) - p3.getValue(1));

        Matrix alphaBeta = Matrix.mult(invT, p);

        double alpha = alphaBeta.getValue(0, 0);
        double beta = alphaBeta.getValue(1, 0);
        double gamma = 1 - alpha - beta;

        Vector barCoord = new Vector(3);
        barCoord.setValue(0, alpha);
        barCoord.setValue(1, beta);
        barCoord.setValue(2, gamma);

        return barCoord;
    }

    private int calculateIlumination(Vector P, Vector normal) {

        Vector Ia, Id, Is;
        Ia = new Vector(3);
        Id = new Vector(3);
        Is = new Vector(3);
        Vector L = Vector.sub(Pl, P);
        L.normalize();
        Vector R = Vector.sub(Vector.scalarMult(normal, 2 * Vector.dot(normal, L)), L);
        Vector V = Vector.sub(camera.getPointC(), P);
        V.normalize();

        // ambient ilumination
        Ia = Vector.scalarMult(Iamb, Ka);

        boolean ignoreLightSource = false, ignoreSpecular = false;

        if (Vector.dot(normal, L) < 0) {
            if (Vector.dot(V, normal) < 0) {
                normal.scalarMult(-1);
            } else {
                ignoreLightSource = true;
            }
        }

        if (Vector.dot(V, R) < 0) {
            ignoreSpecular = true;
        }

        if (!ignoreLightSource) {
            // diffuse ilumination
            Id = Vector.scalarMult(
                    Vector.componentMult(Vector.componentMult(Kd, Od), Il), Vector.dot(normal,
                            L));

            if (!ignoreSpecular) {
                // phong ilumination
                Is = Vector.scalarMult(Il, Math.pow(Vector.dot(R, V), n) * Ks);
            }
        }

        float r = 0, g = 0, b = 0;

        r += (float) (Ia.getValue(0) + Id.getValue(0) + Is.getValue(0));
        g += (float) (Ia.getValue(1) + Id.getValue(1) + Is.getValue(1));
        b += (float) (Ia.getValue(2) + Id.getValue(2) + Is.getValue(2));

        if (r > 255)
            r = 255;
        if (g > 255)
            g = 255;
        if (b > 255)
            b = 255;

        return color(r, g, b);
        // return color(255);
    }
}
