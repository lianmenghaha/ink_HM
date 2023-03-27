package processor;

import org.apache.commons.math3.special.Erf;
import parser.*;
import shape.*;

import java.util.ArrayList;
import java.util.Map;

public class ProcessorOldSim {
    private DocParser parser;

    public ProcessorOldSim() {
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

        double scale = 0.35;
        double rowCnt = 1000.0;

        /*
        rescale the chip
         */
        rescaleChip(scale, input.getChip(), input.getAllPolys(), input.getAllSubRects());
        double unit = input.getUnit() * scale;
        input.setUnit(unit);

        /*
        tileGenerator
         */
        tileGenerator(rowCnt, input.getChip().getMinX(), input.getChip().getMinY(), input.getUnit(), input.getChip().getHeight(), input);


        /*
        Update polygons in each layer in each solution
         */
        double sigma = 0.5;
        input.setSigma(sigma);
        double cons_coefficient = Math.sqrt(Math.PI) * sigma / Math.sqrt(8);
        double coefficient = 1.0/(Math.sqrt(2) * sigma);
        printAndDryScoreIncPolyToTile(cons_coefficient, coefficient, input.getAllPolys(), input.getAllTiles(), input.getAllRowTiles());

        //todo: according to the rowTile we need different a and b
        double print_a = 1.513;
        double print_b = 19.21;
        double dry_a = 512;
        double dry_b = 0;
        ArrayList<Solution> solutions = input.getSolutions();
        int optsol_cnt = 0;
        int findOptSumPDScore = Integer.MAX_VALUE;

        for (int sol_cnt = 0; sol_cnt < solutions.size(); ++sol_cnt) {
            System.out.println("sol_cnt= " + sol_cnt);
            Solution sol = solutions.get(sol_cnt);

            int solutionPrintScore = 0;
            int solutionDryScore = 0;

            for (Layer layer : sol.getLayers()) {
                for (String polyName : layer.getLayerPolyNames()) {
                    layer.addToLayerPolys(mapNameToPoly.get(polyName));
                }

                /*
                Layer PrintScore Calculation
                 */
                int layerPrintScore = 0;
                for (RowTile rowTile : input.getAllRowTiles()) {
                    for (Polygon poly : layer.getLayerPolys()) {
                        if (rowTile.getIsRelateToPolygon().get(poly)) {
                            layerPrintScore = layerPrintScore + 1;
                            layer.addToPrintRowTiles(rowTile);
                            break;
                        }
                    }
                }
                //System.out.println(layerPrintScore);
                layerPrintScore = (int) Math.round(print_a * layerPrintScore + print_b);
                layer.setLayerPrintScore(layerPrintScore);
                solutionPrintScore += layerPrintScore;


                /*
                Layer DryScore Calculation
                 */
                //ArrayList<Double> allTileDs = new ArrayList<>();
                for (Tile tile : input.getAllTiles()){
                    /*
                    DryScore of each Tile
                     */
                    double tileDs = 0.0;
                    for (Polygon poly : layer.getLayerPolys()){
                        tileDs += tile.getDryScoreInc().get(poly);
                    }
                    //allTileDs.add(tileDs);
                    tileDs = (int) Math.round(tileDs * dry_a + dry_b);

                    /*
                    PrintScore of the same Tile
                     */
                    int tilePs = 0;
                    for (RowTile rowTile : layer.getPrintRowTiles()){
                        if (rowTile.getMinY() > tile.getMaxY()){
                            tilePs = tilePs + 1;
                        }
                    }
                    tilePs = (int) Math.round(tilePs * print_a + print_b);
                    /*
                    Rest DryTime of the same Tile
                     */
                    int deltaT = (int) Math.round(tileDs - tilePs);
                    layer.addToMapTileToDryScore(tile, deltaT);
                }
                //double oriDs = Collections.max(allTileDs);
                //int tileIndex = allTileDs.indexOf(oriDs);
                //layer.setOriDryTile(input.getAllTiles().get(tileIndex));

                /*
                Find the tile with maximum Rest DryTime
                 */
                int layerDryScore = 0;
                for (Tile tile : input.getAllTiles()){
                    if (layer.getMapTileToDryScore().get(tile) > layerDryScore){
                        layerDryScore = layer.getMapTileToDryScore().get(tile);
                        layer.setDryTile(tile);
                    }
                }

                layer.setLayerDryScore(layerDryScore);
                solutionDryScore += layerDryScore;





            }

            sol.setTotDryScore(solutionDryScore);
            sol.setTotPrintScore(solutionPrintScore);

            if (solutionDryScore + solutionPrintScore < findOptSumPDScore){
                optsol_cnt = sol_cnt;
            }
        }

        Solution optimalSolution = solutions.get(optsol_cnt);
        for (Layer layer : optimalSolution.getLayers()){
            System.out.println("Layer" + layer.getLayerIndex());
            System.out.println("LayerPrintScore = " + layer.getLayerPrintScore());
            System.out.println("LayerDryScore = " + layer.getLayerDryScore());
            /*for (Polygon polygon : layer.getLayerPolys()){
                System.out.print(polygon.name + " ");
            }
            System.out.println();*/
        }
        System.out.println("LayerPrintScore:");
        for (Layer layer : optimalSolution.getLayers()){
            System.out.println(layer.getLayerPrintScore());
        }
        System.out.println("LayerDryScore:");
        for (Layer layer : optimalSolution.getLayers()){
            System.out.println(layer.getLayerDryScore());
        }


        OutputDoc output = new OutputDoc(input.getName(), input.getChip(), input.getAllPolys(), input.getAllTiles(), input.getAllRowTiles(), optimalSolution);
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

    private void tileGenerator(double rowCnt, double chipMinx, double chipMiny, double chipUnit, double chipWidth, InputDoc input) {

        double rowTileUnit = chipWidth / rowCnt;
        /*
        Tile
         */
        for (int ycnt = 1; ycnt <= input.getYtile(); ++ycnt) {
            double tileMiny = chipMiny + (ycnt - 1) * chipUnit;
            for (int xcnt = 1; xcnt <= input.getXtile(); ++xcnt) {
                Tile tile = new Tile(xcnt, ycnt);
                double tileMinx = chipMinx + (xcnt - 1) * chipUnit;
                tile.setCx(tileMinx + chipUnit * 0.5);
                tile.setCy(tileMiny + chipUnit * 0.5);
                tile.setMinX(tileMinx);
                tile.setMinY(tileMiny);
                tile.setMaxX(tileMinx + chipUnit);
                tile.setMaxY(tileMiny + chipUnit);
                input.addToAllTiles(tile);

            }
        }
        /*
        RowTile
         */
        for (int ycnt = 0; ycnt < rowCnt; ++ ycnt){
            double rowTileMiny = chipMiny + ycnt * rowTileUnit;
            RowTile rowTile_ds = new RowTile((ycnt + 1));
            rowTile_ds.setMinY(rowTileMiny);
            rowTile_ds.setMaxY(rowTileMiny + rowTileUnit);
            input.addToAllRowTiles(rowTile_ds);
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
                    double preDSI = cons_coefficient * Erf.erf(_pvMinx, _pvMaxx) * Erf.erf(_pvMiny, _pvMaxy);
                    double dSI = preDSI;
                    tile.addToDryScoreInc(poly, dSI);
                    poly.tileDryScoreIncMap.put(tile, dSI);

                } else {
                    // pv is not a rectangle
                    double dSI = 0;
                    for (Rectangle subpv : poly.getSubPolys()) {
                        _pvMinx = coefficient * (subpv.getMinX() - cx);
                        _pvMaxx = coefficient * (subpv.getMaxX() - cx);
                        _pvMiny = coefficient * (subpv.getMinY() - cy);
                        _pvMaxy = coefficient * (subpv.getMaxY() - cy);
                        double preDSI = cons_coefficient * Erf.erf(_pvMinx, _pvMaxx) * Erf.erf(_pvMiny, _pvMaxy);
                        dSI += preDSI;
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
