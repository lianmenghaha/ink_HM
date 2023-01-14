package shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Polygon extends Rectangle{

    private ArrayList<Rectangle> subPolys;

    public Map<Tile, Double> tileDryScoreIncMap;
    public Map<RowTile, Boolean> isRelateToRowTile;


    private double height;
    private double width;

    public Polygon() {
        this.subPolys = new ArrayList<>();
        this.tileDryScoreIncMap = new HashMap<>();
        this.isRelateToRowTile = new HashMap<>();
    }


    public ArrayList<Rectangle> getSubPolys() {
        return subPolys;
    }

    public void addToSubPolys(Rectangle subPoly) {
        this.subPolys.add(subPoly);
    }

    public double getHeight() {
        height = this.getMaxY() - this.getMinY();
        return height;
    }


    public double getWidth() {
        width = this.getMaxX() - this.getMinX();
        return width;
    }

}
