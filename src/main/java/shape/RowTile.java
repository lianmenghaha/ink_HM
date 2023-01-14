package shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RowTile {

    private int yInd;
    private double minY, maxY;

    private Map<Polygon, Boolean> isRelateToPolyForDs;
    private ArrayList<Polygon> relatePolys;

    public RowTile(int yInd){
       this.relatePolys = new ArrayList<>();
       this.isRelateToPolyForDs = new HashMap<>();
    }


    public int getyInd() {
        return yInd;
    }

    public void setyInd(int yInd) {
        this.yInd = yInd;
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

    public Map<Polygon, Boolean> getIsRelateToPoly() {
        return isRelateToPolyForDs;
    }
    public void addToIsRelateToPoly(Polygon polygonVar, Boolean tf) {
        this.isRelateToPolyForDs.put(polygonVar, tf);
    }

    public Map<Polygon, Boolean> getIsRelateToPolyForDs() {
        return isRelateToPolyForDs;
    }

    public void addToIsRelateToPolyForDs(Polygon polyForDs, Boolean tf) {
        this.isRelateToPolyForDs.put(polyForDs, tf);
    }

    public ArrayList<Polygon> getRelatePolys() {
        return relatePolys;
    }

    public void addToRelatePolys(Polygon polygonVar) {
        this.relatePolys.add(polygonVar);
    }
}
