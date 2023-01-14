package shape;


import java.util.ArrayList;
import java.util.Collections;


public class Rectangle {

    public String name;
    public ArrayList<Pair> vertices;

    /**
     * The original Polygon of the subPolygon
     */
    private Rectangle originalPoly;


    public Rectangle() {
        this.vertices = new ArrayList<>();
        this.originalPoly = this;


    }

    public Rectangle getOriginalPoly() {
        return originalPoly;
    }

    public void setOriginalPoly(Rectangle originalPoly) {
        this.originalPoly = originalPoly;
    }

    public double getMaxX() {
        ArrayList<Double> xs = new ArrayList<>();
        for (Pair pt : this.vertices) {
            xs.add(pt.getX());
        }
        return Collections.max(xs);
    }

    public double getMinX() {
        ArrayList<Double> xs = new ArrayList<>();
        for (Pair pt : this.vertices) {
            xs.add(pt.getX());
        }
        return Collections.min(xs);
    }

    public double getMaxY() {
        ArrayList<Double> ys = new ArrayList<>();
        for (Pair pt : this.vertices) {
            ys.add(pt.getY());
        }
        return Collections.max(ys);
    }

    public double getMinY() {
        ArrayList<Double> ys = new ArrayList<>();
        for (Pair pt : this.vertices) {
            ys.add(pt.getY());
        }
        return Collections.min(ys);
    }

    public double getHeight(){
        double height = this.getMaxY() - this.getMinY();
        return height;
    }

    public double getWidth(){
        double width = this.getMaxX() - this.getMinX();
        return width;
    }



    @Override
    public String toString() {

        return "Rectangle{" +
                "name='" + name + '\'' +
                ", vertices=" + vertices +
                ", originalPoly=" + originalPoly.name +
                '}';
    }
}

