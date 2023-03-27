package shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tile {

    private int xInd, yInd;
    private double minX, minY, maxX, maxY, cx, cy;

    private Map<Polygon, Double> dryScoreInc;

    private ArrayList<RowTile> aboveRowTiles;
    private ArrayList<RowTile> belowRowTiles;

    private double tileProcessPeriod;




    public Tile(int xInd, int yInd){
        this.xInd = xInd;
        this.yInd = yInd;
        this.dryScoreInc = new HashMap<>();
        this.aboveRowTiles = new ArrayList<>();
        this.belowRowTiles = new ArrayList<>();

    }

    public double getTileProcessPeriod() {
        return tileProcessPeriod;
    }

    public void setTileProcessPeriod(double tileProcessPeriod) {
        this.tileProcessPeriod = tileProcessPeriod;
    }

    public ArrayList<RowTile> getBelowRowTiles() {
        return belowRowTiles;
    }

    public void addToBelowRowTiles(RowTile belowRowTile) {
        this.belowRowTiles.add(belowRowTile);
    }

    public ArrayList<RowTile> getAboveRowTiles() {
        return aboveRowTiles;
    }

    public void addToAboveRowTiles(RowTile rowTile) {
        this.aboveRowTiles.add(rowTile);
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

    public double getWidth(){
        return (this.maxX - this.minX);
    }

    public double getHeight(){
        return (this.maxY - this.minY);
    }

}
