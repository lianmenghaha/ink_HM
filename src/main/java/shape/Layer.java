package shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Layer {

    private int layerIndex;

    private int layerOriPrintScore;
    private double layerDryScore;
    private int layerPrintScore;


    ArrayList<String> layerPolyNames;
    ArrayList<Polygon> layerPolys;

    private Tile dryTile;
    private Tile oriDryTile;
    private ArrayList<RowTile> printRowTiles;

    private Map<Tile, Integer> mapTileToDryScore;


    public Layer(int layerIndex, int layerOriPrintScore) {
        this.layerIndex = layerIndex;
        this.layerOriPrintScore = layerOriPrintScore;
        this.layerPolyNames = new ArrayList<>();
        this.layerPolys = new ArrayList<>();
        this.printRowTiles = new ArrayList<>();
        this.mapTileToDryScore = new HashMap<>();
    }

    public Tile getOriDryTile() {
        return oriDryTile;
    }

    public void setOriDryTile(Tile oriDryTile) {
        this.oriDryTile = oriDryTile;
    }

    public Map<Tile, Integer> getMapTileToDryScore() {
        return mapTileToDryScore;
    }

    public void addToMapTileToDryScore(Tile tile, int dryScore) {
        this.mapTileToDryScore.put(tile, dryScore);
    }

    public ArrayList<RowTile> getPrintRowTiles() {
        return printRowTiles;
    }

    public void addToPrintRowTiles(RowTile printRowTile) {
        this.printRowTiles.add(printRowTile);
    }

    public double getLayerPrintScore() {
        return layerPrintScore;
    }

    public void setLayerPrintScore(int layerPrintScore) {
        this.layerPrintScore = layerPrintScore;
    }

    public int getLayerIndex() {
        return layerIndex;
    }

    public int getLayerOriPrintScore() {
        return layerOriPrintScore;
    }

    public double getLayerDryScore() {
        return layerDryScore;
    }

    public void setLayerDryScore(double layerDryScore) {
        this.layerDryScore = layerDryScore;
    }

    public Tile getDryTile() {
        return dryTile;
    }

    public void setDryTile(Tile dryTile) {
        this.dryTile = dryTile;
    }

    public ArrayList<String> getLayerPolyNames() {
        return layerPolyNames;
    }

    public void addToLayerPolyNames(String layerPolyName) {
        this.layerPolyNames.add(layerPolyName);
    }

    public ArrayList<Polygon> getLayerPolys() {
        return layerPolys;
    }

    public void addToLayerPolys(Polygon layerPoly) {
        this.layerPolys.add(layerPoly);
    }


}
