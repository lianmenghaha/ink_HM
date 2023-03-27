package parser;


import shape.Polygon;
import shape.Rectangle;
import shape.RowTile;
import shape.Tile;
import simShape.SimLayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InputDoc {
    private String name;
    private int xtile;
    private int ytile;
    private double unit;
    private double sigma;
    private Rectangle chip;

    private ArrayList<Polygon> allPolys;
    private ArrayList<Rectangle> allSubRects;
    private Map<String, Polygon> mapNameToPoly;
    private ArrayList<Tile> allTiles;
    private ArrayList<RowTile> allRowTiles;

    //HM
    private ArrayList<Solution> solutions;

    //SIM
    private int rowCnt;
    private double scale;
    private ArrayList<SimLayer> simLayers;


    public InputDoc() {
        this.allPolys = new ArrayList<>();
        this.allSubRects = new ArrayList<>();
        this.mapNameToPoly = new HashMap<>();
        this.allTiles = new ArrayList<>();
        this.allRowTiles = new ArrayList<>();
        this.solutions = new ArrayList<>();
        this.simLayers = new ArrayList<>();
    }

    public InputDoc(String name, Rectangle chip, ArrayList<Polygon> polygons, ArrayList<Tile> tiles, ArrayList<RowTile> rowTiles) {
        this.name = name;
        this.chip = chip;
        this.allPolys = polygons;
        this.allTiles = tiles;
        this.allRowTiles = rowTiles;
    }

    //HM
    public ArrayList<Solution> getSolutions() {
        return solutions;
    }

    public void addToSolutions(Solution solution) {
        this.solutions.add(solution);
    }

    //SIM
    public int getRowCnt() {
        return rowCnt;
    }

    public void setRowCnt(int rowCnt) {
        this.rowCnt = rowCnt;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public ArrayList<SimLayer> getSimLayers() {
        return simLayers;
    }

    public void addToSimLayers(SimLayer simLayer) {
        this.simLayers.add(simLayer);
    }

    public void setSimLayers(ArrayList<SimLayer> simLayers) {
        this.simLayers = simLayers;
    }

    public Rectangle getChip() {
        return chip;
    }

    public void setChip(Rectangle chip) {
        this.chip = chip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getXtile() {
        return xtile;
    }

    public void setXtile(int xtile) {
        this.xtile = xtile;
    }

    public int getYtile() {
        return ytile;
    }

    public void setYtile(int ytile) {
        this.ytile = ytile;
    }

    public double getUnit() {
        return unit;
    }

    public void setUnit(double unit) {
        this.unit = unit;
    }

    public double getSigma() {
        return sigma;
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }

    public ArrayList<Polygon> getAllPolys() {
        return allPolys;
    }

    public void addToAllPolys(Polygon polyForDs) {
        this.allPolys.add(polyForDs);
    }

    public ArrayList<Rectangle> getAllSubRects() {
        return allSubRects;
    }

    public void addToAllSubRects(Rectangle subRect) {
        this.allSubRects.add(subRect);
    }

    public Map<String, Polygon> getMapNameToPoly() {
        return mapNameToPoly;
    }

    public void addToMapNameToPoly(String name, Polygon poly) {
        this.mapNameToPoly.put(name, poly);
    }

    public ArrayList<Tile> getAllTiles() {
        return allTiles;
    }

    public boolean addToAllTiles (Tile tile){
        return this.allTiles.add(tile);
    }

    public ArrayList<RowTile> getAllRowTiles() {
        return allRowTiles;
    }

    public void addToAllRowTiles(RowTile rowTile) {
        this.allRowTiles.add(rowTile);
    }

}
