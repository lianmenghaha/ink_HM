package processor;

import org.apache.commons.math3.special.Erf;
import parser.DocParser;
import parser.InputDoc;
import parser.OutputDoc;
import shape.*;
import simShape.SimLayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class ProcessorSIM {
    private DocParser parser;

    public ProcessorSIM() {
        this.parser = new DocParser();
    }

    public OutputDoc process(String inputPath) {
        parser.parseInputToDoc(inputPath);
        InputDoc input = parser.getParser();

        /*
        Process mapNameToPoly
         */
        for (int i = 0; i < input.getAllPolys().size(); ++i) {
            Polygon poly = input.getAllPolys().get(i);
            input.addToMapNameToPoly(poly.name, poly);
        }
        Map<String, Polygon> mapNameToPoly = input.getMapNameToPoly();

        /*
        Identify SubPolygons
         */
        identifySubPolygons(input.getAllPolys(), input.getAllSubRects());

        /*
        rescale the chip
         */
        rescaleChip(input.getScale(), input.getChip(), input.getAllPolys(), input.getAllSubRects());


        /*
        tileGenerator
         */
        tileGenerator(input);


        /*
        Update polygons in each layer in each solution
         */
        double sigma = 0.5;
        input.setSigma(sigma);
        double cons_coefficient = Math.sqrt(Math.PI) * sigma / Math.sqrt(8);
        double coefficient = 1.0 / (Math.sqrt(2) * sigma);
        printAndDryScoreIncPolyToTile(cons_coefficient, coefficient, input.getAllPolys(), input.getAllTiles(), input.getAllRowTiles());

        //todo: according to the rowTile we need different a and b
        //double print_a = 1.513;
        //double print_b = 19.21;

        double print_a = 17;
        double print_b = 29;
        double dry_a = 512;
        double dry_b = 0;


        for (SimLayer simLayer : input.getSimLayers()) {
            for (String polyName : simLayer.getLayerPolygonNames()) {
                simLayer.addToLayerPolygons(mapNameToPoly.get(polyName));
            }

            /*
            Layer PrintScore Calculation also
             */
            int layerPrintScore = 0;
            for (RowTile rowTile : input.getAllRowTiles()) {
                for (Polygon poly : simLayer.getLayerPolygons()) {
                    if (rowTile.getIsRelateToPolygon().get(poly)) {
                        layerPrintScore = layerPrintScore + 1;
                        simLayer.addToPrintRowTiles(rowTile);
                        break;
                    }
                }
            }
            System.out.println(layerPrintScore);
            layerPrintScore = (int) Math.round(print_a * layerPrintScore + print_b);
            simLayer.setLayerPrintScore(layerPrintScore);


            /*
            Layer DryScore Calculation
             */
            for (Tile tile : input.getAllTiles()) {
                /*
                DryScore of each Tile
                 */
                double tileDs = 0.0;
                for (Polygon poly : simLayer.getLayerPolygons()) {
                    tileDs += tile.getDryScoreInc().get(poly);
                }
                //allTileDs.add(tileDs);
                tileDs = tileDs * dry_a + dry_b;

                /*
                PrintScore of the same Tile
                 */
                double tilePs = 0.0;
                for (RowTile rowTile : simLayer.getPrintRowTiles()) {
                    if (tile.getBelowRowTiles().contains(rowTile)) {
                        tilePs = tilePs + 1;
                    }
                }
                tilePs = tilePs * print_a + print_b;

                /*
                Process Period of the same Tile
                 */
                double tileProcessPeriod = tileDs + tilePs;
                tile.setTileProcessPeriod(tileProcessPeriod);
                simLayer.addToTileProcessMap(tile, tileProcessPeriod);
            }
            double layerProcessPeriod = Collections.max(simLayer.getTileProcessMap().values());
            simLayer.setLayerProcessPeriod(layerProcessPeriod);
            for (Tile tile : input.getAllTiles()){
                if (tile.getTileProcessPeriod() == simLayer.getLayerProcessPeriod()){
                 simLayer.addToCriticalTiles(tile);
                }
            }
            System.out.println("#ct= " + simLayer.getCriticalTiles().size());


        }




        for (SimLayer simLayer : input.getSimLayers()) {
            System.out.println("Layer" + simLayer.getLayerIndex());
            System.out.println("LayerPrintScore = " + simLayer.getLayerPrintScore());
            System.out.println("LayerProcessPeriod = " + simLayer.getLayerProcessPeriod());

        }
        System.out.println("LayerProcessPeriod:");
        for (SimLayer simLayer : input.getSimLayers()) {
            System.out.println(simLayer.getLayerProcessPeriod());
        }



        OutputDoc output = new OutputDoc(input.getName(), input.getChip(), input.getAllPolys(), input.getAllTiles(), input.getAllRowTiles(), input.getSimLayers());
        return output;

    }


    private void identifySubPolygons(ArrayList<Polygon> polygons, ArrayList<Rectangle> subRects) {
        for (Rectangle sub : subRects) {
            ArrayList<Pair> subVertices = sub.vertices;
            for (Polygon ori : polygons) {
                if (ori.vertices.size() == 4) continue;
                int cnt = 0;
                for (Pair subVx : subVertices) {
                    if (isInside(ori, subVx)) cnt++;
                }
                if (cnt == subVertices.size()) {
                    ori.getSubPolys().add(sub);
                    sub.setOriginalPoly(ori);
                }
            }
        }
    }

    private void rescaleChip(double scale, Rectangle chip, ArrayList<Polygon> polygons, ArrayList<Rectangle> subRects) {
        double chipMinX_ori = chip.getMinX();
        double chipMinY_ori = chip.getMinY();
        for (Pair xy : chip.vertices) {
            xy.x = (xy.x - chipMinX_ori) * scale;
            xy.y = (xy.y - chipMinY_ori) * scale;
        }
        for (Polygon poly : polygons) {
            for (Pair xy : poly.vertices) {
                xy.x = (xy.x - chipMinX_ori) * scale;
                xy.y = (xy.y - chipMinY_ori) * scale;
            }
        }
        for (Rectangle rect : subRects) {
            for (Pair xy : rect.vertices) {
                xy.x = (xy.x - chipMinX_ori) * scale;
                xy.y = (xy.y - chipMinY_ori) * scale;
            }
        }
    }

    private void tileGenerator(InputDoc input) {

        /*
        Tile
         */
        int rowCnt = input.getRowCnt();
        int xTile = input.getXtile();
        int yTile = input.getYtile();
        double chipMinx = input.getChip().getMinX();
        double chipMiny = input.getChip().getMinY();
        double chipHeight = input.getChip().getHeight();
        double chipWidth = input.getChip().getWidth();


        double columnTileUnt = chipWidth / xTile;
        double rowTileUnit = chipHeight / yTile;

        for (int ycnt = 1; ycnt <= input.getYtile(); ++ycnt) {
            double tileMiny = chipMiny + (ycnt - 1) * rowTileUnit;
            for (int xcnt = 1; xcnt <= input.getXtile(); ++xcnt) {
                Tile tile = new Tile(xcnt, ycnt);
                double tileMinx = chipMinx + (xcnt - 1) * columnTileUnt;
                tile.setCx(tileMinx + columnTileUnt * 0.5);
                tile.setCy(tileMiny + rowTileUnit * 0.5);
                tile.setMinX(tileMinx);
                tile.setMinY(tileMiny);
                tile.setMaxX(tileMinx + columnTileUnt);
                tile.setMaxY(tileMiny + rowTileUnit);
                input.addToAllTiles(tile);

            }
        }
        /*
        RowTile
         */
        double rowUnit = chipHeight / rowCnt;
        for (int ycnt = 1; ycnt <= rowCnt; ++ycnt) {
            double rowTileMiny = chipMiny + (ycnt - 1) * rowUnit;
            RowTile rowTile_ds = new RowTile(ycnt);
            rowTile_ds.setMinY(rowTileMiny);
            rowTile_ds.setMaxY(rowTileMiny + rowUnit);
            input.addToAllRowTiles(rowTile_ds);
        }

        /*
        identify all BelowRowTiles for each tile
         */
        for (Tile tile : input.getAllTiles()) {
            for (RowTile rowTile : input.getAllRowTiles()) {
                if (tile.getMinY() >= rowTile.getMaxY()) {
                    tile.addToBelowRowTiles(rowTile);
                }
            }


        }


    }

    private void printAndDryScoreIncPolyToTile(double cons_coefficient, double coefficient, ArrayList<Polygon> polygons, ArrayList<Tile> tiles, ArrayList<RowTile> rowTiles) {
        double cx;
        double cy;
        double _pvMaxx;
        double _pvMiny;
        double _pvMaxy;
        double _pvMinx;
        for (Polygon poly : polygons) {
            for (Tile tile : tiles) {
                cx = tile.getCx();
                cy = tile.getCy();

                if (poly.getSubPolys().size() == 0) {
                    // pv is a rectangle
                    _pvMinx = coefficient * (poly.getMinX() - cx);
                    _pvMaxx = coefficient * (poly.getMaxX() - cx);
                    _pvMiny = coefficient * (poly.getMinY() - cy);
                    _pvMaxy = coefficient * (poly.getMaxY() - cy);
                    double realDSI = cons_coefficient * Erf.erf(_pvMinx, _pvMaxx) * Erf.erf(_pvMiny, _pvMaxy);
                    tile.addToDryScoreInc(poly, realDSI);
                    poly.tileDryScoreIncMap.put(tile, realDSI);

                } else {
                    // pv is not a rectangle
                    double dSI = 0;
                    for (Rectangle subpv : poly.getSubPolys()) {
                        _pvMinx = coefficient * (subpv.getMinX() - cx);
                        _pvMaxx = coefficient * (subpv.getMaxX() - cx);
                        _pvMiny = coefficient * (subpv.getMinY() - cy);
                        _pvMaxy = coefficient * (subpv.getMaxY() - cy);
                        double realDSI = cons_coefficient * Erf.erf(_pvMinx, _pvMaxx) * Erf.erf(_pvMiny, _pvMaxy);
                        dSI += realDSI;
                    }
                    tile.addToDryScoreInc(poly, dSI);
                    poly.tileDryScoreIncMap.put(tile, dSI);
                }

            }

            for (RowTile rowTile : rowTiles) {
                if (!(poly.getMinY() > rowTile.getMaxY() || poly.getMaxY() < rowTile.getMinY())) {
                    rowTile.addToIsRelateToPolygon(poly, true);
                    poly.isRelateToRowTile.put(rowTile, true);
                } else {
                    rowTile.addToIsRelateToPolygon(poly, false);
                    poly.isRelateToRowTile.put(rowTile, false);
                }
            }
        }
    }

    public static boolean onSegment(Pair p, Pair q, Pair r) {
        if (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x)
                && q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y))
            return true;
        return false;
    }

    public static int orientation(Pair p, Pair q, Pair r) {
        double val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);

        if (val == 0)
            return 0;
        return (val > 0) ? 1 : 2;
    }

    public static boolean doIntersect(Pair p1, Pair q1, Pair p2, Pair q2) {

        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        if (o1 != o2 && o3 != o4)
            return true;

        if (o1 == 0 && onSegment(p1, p2, q1))
            return true;

        if (o2 == 0 && onSegment(p1, q2, q1))
            return true;

        if (o3 == 0 && onSegment(p2, p1, q2))
            return true;

        if (o4 == 0 && onSegment(p2, q1, q2))
            return true;

        return false;
    }

    public static boolean isInside(Rectangle rectangle, Pair p) {
        int INF = 10000;
        int n = rectangle.vertices.size();
        if (n < 3)
            return false;

        Pair extreme = new Pair(INF, p.y);

        int count = 0, i = 0;
        do {
            int next = (i + 1) % n;
            if (doIntersect(rectangle.vertices.get(i), rectangle.vertices.get(next), p, extreme)) {
                if (orientation(rectangle.vertices.get(i), p, rectangle.vertices.get(next)) == 0)
                    return onSegment(rectangle.vertices.get(i), p, rectangle.vertices.get(next));

                count++;
            }
            i = next;
        } while (i != 0);

        return (count & 1) == 1;
    }


}
