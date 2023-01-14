package shape;

import java.util.HashMap;
import java.util.Map;

public class Tile {

    private int xInd, yInd;
    private double minX, minY, maxX, maxY, cx, cy;

    private Map<Polygon, Double> dryScoreInc;


    public Tile(int xInd, int yInd){
        this.dryScoreInc = new HashMap<>();

    }

    public int getxInd() {
        return xInd;
    }

    public void setxInd(int xInd) {
        this.xInd = xInd;
    }

    public int getyInd() {
        return yInd;
    }

    public void setyInd(int yInd) {
        this.yInd = yInd;
    }

    public double getMinX() {
        return minX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public double getMinY() {
        return minY;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public double getCx() {
        return 0.5 * (this.minX + this.maxX);
    }

    public void setCx(double cx) {
        this.cx = cx;
    }

    public double getCy() {
        return 0.5 * (this.minY + this.maxY);
    }

    public void setCy(double cy) {
        this.cy = cy;
    }

    public Map<Polygon, Double> getDryScoreInc() {
        return dryScoreInc;
    }

    public void addToDryScoreInc (Polygon poly, Double dryScoreIncrease){
        this.dryScoreInc.put(poly, dryScoreIncrease);
    }


}
