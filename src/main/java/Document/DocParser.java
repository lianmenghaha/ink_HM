package Document;

import shape.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

public class DocParser {

    private InputDocHM parse;

    public DocParser() {
        this.parse = new InputDocHM();
    }

    public void processInput(String[] input){
        /*
        Read files
         */
        for (int i = 1; i < input.length; ++i) {
            if (input[i].equals("")) continue;
            else if (input[i].contains("xtile")) {
                this.parse.setXtile(Integer.parseInt(input[i].substring(6)));
                //System.err.println(this.parseDoc.getXtile());
            } else if (input[i].contains("ytile")) {
                this.parse.setYtile(Integer.parseInt(input[i].substring(6)));
            } else if (input[i].contains("unit")) {
                this.parse.setUnit(Double.parseDouble(input[i].substring(5)));
                //System.out.println(this.parseDoc.getUnit());
            } else if (input[i].contains("Chip")) {
                Rectangle chip = new Rectangle();
                chip.name = "chip";
                i++;
                while (input[i].contains("line")) {
                    String[] paths = input[i].split(" ");
                    String[] pathCoordinates = paths[1].split(",");
                    String[] pathXY = pathCoordinates[0].split("\\+");
                    String[] pathY = pathXY[1].split("i");
                    Pair xy = new Pair(Double.parseDouble(pathXY[0]), Double.parseDouble(pathY[0]));
                    chip.vertices.add(xy);
                    i++;
                }
                this.parse.setChip(chip);
                i--;
            } else if (input[i].contains("chipo")) {
                Polygon poly = new Polygon();
                String[] namestr = input[i].split(" ");
                poly.name = namestr[0].replace("chip", "");
                i++;
                while (input[i].contains("line")) {
                    String[] paths = input[i].split(" ");
                    String[] pathCoordinates = paths[1].split(",");
                    String[] pathXY = pathCoordinates[0].split("\\+");
                    String[] pathY = pathXY[1].split("i");
                    Pair xy = new Pair(Double.parseDouble(pathXY[0]), Double.parseDouble(pathY[0]));
                    poly.vertices.add(xy);
                    i++;
                }
                this.parse.addToAllPolys(poly);
                i--;
            } else if (input[i].contains("chipd")) {
                Rectangle poly = new Rectangle();
                String[] namestr = input[i].split(" ");
                poly.name = namestr[0].replace("chip", "");
                i++;
                while (i < input.length && input[i].contains("line")) {
                    String[] paths = input[i].split(" ");
                    String[] pathCoordinates = paths[1].split(",");
                    String[] pathXY = pathCoordinates[0].split("\\+");
                    String[] pathY = pathXY[1].split("i");
                    Pair xy = new Pair(Double.parseDouble(pathXY[0]), Double.parseDouble(pathY[0]));
                    poly.vertices.add(xy);
                    i += 1;
                }
                this.parse.addToAllSubRects(poly);
                i--;
            } else if (input[i].contains("Solution")) {
                Solution sol = new Solution(Integer.parseInt(input[i].substring(9)));
                this.parse.addToSolutions(sol);
                i++;
                while (!input[i].equals("FIN")) {
                    if (input[i].contains("Layer")) {
                        int layer_cnt = Integer.parseInt(input[i].substring(5, 7));
                        int layerIntPs = Integer.parseInt(input[i].substring(7));
                        Layer layer = new Layer(layer_cnt, layerIntPs);
                        sol.addToLayers(layer);
                        i++;
                        String[] polys = input[i].split(" ");
                        for (String polyName : polys) {
                            layer.addToLayerPolyNames(polyName);
                        }
                    }
                    i++;
                }

            }

        }

        /*
        Process mapNameToPoly
         */
        for (int i = 0; i < this.parse.getAllPolys().size(); ++i) {
            Polygon poly = this.parse.getAllPolys().get(i);
            this.parse.addToMapNameToPoly(poly.name, poly);
        }
        Map<String, Polygon> mapNameToPoly = this.parse.getMapNameToPoly();

        /*
        Identify SubPolygons
         */
        identifySubPolygons(parse.getAllPolys(), parse.getAllSubRects());

        double scale = 0.35;
        double rowCnt = 1000.0;

        /*
        rescale the chip
         */
        rescaleChip(scale, parse.getChip(), parse.getAllPolys(), parse.getAllSubRects(), parse.getUnit());

        /*
        tileGenerator
         */
        tileGenerator(scale, rowCnt, parse.getChip().getMinX(), parse.getChip().getMinY(), parse.getUnit(), parse.getChip().getHeight());


        /*
        Update polygons in each layer in each solution
         */
        double sigma = 0.5;
        parse.setSigma(sigma);
        double cons_coefficient = Math.sqrt(Math.PI) * sigma / Math.sqrt(8);
        double coefficient = 1.0/(Math.sqrt(2) * sigma);
        double _pvMinx, _pvMaxx, _pvMiny, _pvMaxy, cx, cy;
        //todo: according to the rowTile we need different a and b
        double print_a = 1.513;
        double print_b = 19.21;
        ArrayList<Solution> solutions = parse.getSolutions();
        for (Solution sol : solutions) {
            int solutionPrintScore = 0;
            for (Layer layer : sol.getLayers()) {
                for (String polyName : layer.getLayerPolyNames()) {
                    layer.addToLayerPolys(mapNameToPoly.get(polyName));
                }

                /*
                Layer PrintScore Calculation
                 */
                int layerPrintScore = 0;
                for (RowTile rowTile : parse.getAllRowTiles()) {
                    for (Polygon poly : layer.getLayerPolys()) {
                        if (rowTile.getIsRelateToPolyForDs().get(poly)) {
                            layerPrintScore = layerPrintScore + 1;
                            break;
                        }
                    }
                }
                layerPrintScore = (int) Math.round(print_a * layerPrintScore + print_b);
                layer.setLayerPrintScore(layerPrintScore);
                solutionPrintScore += layerPrintScore;

                /*
                Layer DryScore Calculation
                 */



            }
        }



    }

    private void tileGenerator(double scale, double rowCnt, double chipMinx, double chipMiny, double chipUnit, double chipWidth) {

        double rowTileUnit = chipWidth / rowCnt;
        /*
        Tile
         */
        for (int ycnt = 1; ycnt <= this.parse.getYtile(); ++ycnt) {
            double tileMiny = chipMiny + (ycnt - 1) * chipUnit;
            for (int xcnt = 1; xcnt <= this.parse.getXtile(); ++xcnt) {
                Tile tile = new Tile(xcnt, ycnt);
                double tileMinx = chipMinx + (xcnt - 1) * chipUnit;
                tile.setCx(tileMinx + chipUnit * 0.5);
                tile.setCy(tileMiny + chipUnit * 0.5);
                tile.setMinX(tileMinx);
                tile.setMinY(tileMiny);
                tile.setMaxX(tileMinx + chipUnit);
                tile.setMaxY(tileMiny + chipUnit);
                this.parse.addToAllTiles(tile);

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
            this.parse.addToAllRowTiles(rowTile_ds);
        }



    }

    private void rescaleChip(double scale, Rectangle chip, ArrayList<Polygon> polygons, ArrayList<Rectangle> subRects, double chipUnit) {
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
        chipUnit = chipUnit * scale;
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


    private String[] readFiles(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles();
        ArrayList<String> allLines = new ArrayList<>();
        ArrayList<String> lines;
        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                lines = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                allLines.addAll(lines);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return allLines.toArray(new String[]{});
    }

    public void parseInputToDocDsCal(String path) {
        this.processInput(this.readFiles(path));
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
