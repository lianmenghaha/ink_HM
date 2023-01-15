package parser;

import org.apache.commons.math3.special.Erf;
import shape.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

public class DocParser {

    private InputDocHM parser;

    public DocParser() {
        this.parser = new InputDocHM();
    }

    public void processInput(String[] input){
        /*
        Read files
         */
        for (int i = 0; i < input.length; ++i) {
            if (input[i].equals("")) continue;
            else if (input[i].contains("xtile")) {
                this.parser.setXtile(Integer.parseInt(input[i].substring(6)));
                //System.err.println(this.parseDoc.getXtile());
            } else if (input[i].contains("ytile")) {
                this.parser.setYtile(Integer.parseInt(input[i].substring(6)));
            } else if (input[i].contains("unit")) {
                this.parser.setUnit(Double.parseDouble(input[i].substring(5)));
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
                this.parser.setChip(chip);
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
                this.parser.addToAllPolys(poly);
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
                this.parser.addToAllSubRects(poly);
                i--;
            } else if (input[i].contains("Solution")) {
                String[] tmpStr = input[i].split(" ");
                int totOriPrintScore = Integer.parseInt(tmpStr[tmpStr.length - 1]);
                Solution sol = new Solution(totOriPrintScore);
                this.parser.addToSolutions(sol);
                i++;
                while (!input[i].equals("FIN")) {
                    if (input[i].contains("Layer")) {
                        String str = input[i].substring(5,6);
                        int layer_cnt = Integer.parseInt(str);
                        tmpStr = input[i].split(" ");
                        int layerIntPs = Integer.parseInt(tmpStr[tmpStr.length - 1]);
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

    public void parseInputToDoc(String path) {
        this.processInput(this.readFiles(path));
    }

    public InputDocHM getParser() {
        return parser;
    }




}
