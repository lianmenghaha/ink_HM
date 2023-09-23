package parser;

import shape.Polygon;
import shape.Rectangle;
import shape.RowTile;
import shape.Tile;
import simShape.SimLayer;

import java.util.ArrayList;

public class OutputDoc extends InputDoc{

    //HM
    private Solution optSolution;

    private ArrayList<Rectangle> subRectangles;

    //SIM


    public OutputDoc(String name, Rectangle chip, ArrayList<Polygon> polygons, ArrayList<Tile> tiles, ArrayList<RowTile> rowTiles, ArrayList<SimLayer> simLayers) {
        super(name, chip, polygons, tiles, rowTiles);
        this.setSimLayers(simLayers);
        this.subRectangles = new ArrayList<>();
    }

    //HM
    public OutputDoc(String name, Rectangle chip, ArrayList<Polygon> polygons, ArrayList<Tile> tiles, ArrayList<RowTile> rowTiles, Solution optSolution) {
        super(name, chip, polygons, tiles, rowTiles);
        this.optSolution = optSolution;
        this.subRectangles = new ArrayList<>();
    }


    public ArrayList<Rectangle> getSubRectangles() {
        return subRectangles;
    }

    public void setSubRectangles(ArrayList<Rectangle> subRectangles) {
        this.subRectangles = subRectangles;
    }

    public Solution getOptSolution() {
        return optSolution;
    }



}
