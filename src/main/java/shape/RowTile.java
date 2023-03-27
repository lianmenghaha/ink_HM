package shape;

import java.util.HashMap;
import java.util.Map;

public class RowTile {

    private int yInd;
    private double minY, maxY;

    private Map<Polygon, Boolean> isRelateToPolygon;


    public RowTile(int yInd){
       this.yInd = yInd;
       this.isRelateToPolygon = new HashMap<>();
    }


    public int getyInd() {
        return yInd;
    }

    public double getMinY() {
        return minY;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public double getMaxY() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public Map<Polygon, Boolean> getIsRelateToPolygon() {
        return isRelateToPolygon;
    }

    public void addToIsRelateToPolygon(Polygon polyForDs, Boolean tf) {
        this.isRelateToPolygon.put(polyForDs, tf);
    }



}
