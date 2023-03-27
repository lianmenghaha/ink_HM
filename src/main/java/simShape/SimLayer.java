package simShape;

import shape.Polygon;
import shape.RowTile;
import shape.Tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther lianmeng
 * @create 02.02.23
 */
public class SimLayer {

    private int layerIndex;
    private double layerProcessPeriod;

    private double layerPrintScore;


    private ArrayList<String> layerPolygonNames;
    private ArrayList<Polygon> layerPolygons;

    private ArrayList<RowTile> printRowTiles;

    private Map<Tile, Double> tileProcessMap;
    private ArrayList<Tile> criticalTiles;

    public SimLayer(int layerIndex) {
        this.layerIndex = layerIndex;
        this.layerPolygons = new ArrayList<>();
        this.layerPolygonNames = new ArrayList<>();
        this.printRowTiles = new ArrayList<>();
        this.tileProcessMap = new HashMap<>();
        this.criticalTiles = new ArrayList<>();
    }

    public ArrayList<Tile> getCriticalTiles() {
        return criticalTiles;
    }

    public void addToCriticalTiles(Tile tile) {
        this.criticalTiles.add(tile);
    }

    public Map<Tile, Double> getTileProcessMap() {
        return tileProcessMap;
    }

    public void addToTileProcessMap(Tile tile, double tileProcessPeriod) {
        this.tileProcessMap.put(tile, tileProcessPeriod);
    }

    public double getLayerPrintScore() {
        return layerPrintScore;
    }

    public void setLayerPrintScore(double layerPrintScore) {
        this.layerPrintScore = layerPrintScore;
    }

    public ArrayList<RowTile> getPrintRowTiles() {
        return printRowTiles;
    }

    public void addToPrintRowTiles(RowTile printRowTile) {
        this.printRowTiles.add(printRowTile);
    }

    public int getLayerIndex() {
        return layerIndex;
    }

    public void setLayerIndex(int layerIndex) {
        this.layerIndex = layerIndex;
    }

    public double getLayerProcessPeriod() {
        return layerProcessPeriod;
    }

    public void setLayerProcessPeriod(double layerProcessPeriod) {
        this.layerProcessPeriod = layerProcessPeriod;
    }

    public ArrayList<String> getLayerPolygonNames() {
        return layerPolygonNames;
    }

    public void addToLayerPolygonNames(String polygonName) {
        this.layerPolygonNames.add(polygonName);
    }

    public ArrayList<Polygon> getLayerPolygons() {
        return layerPolygons;
    }

    public void addToLayerPolygons(Polygon polygon) {
        this.layerPolygons.add(polygon);
    }



}
