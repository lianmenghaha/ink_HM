package processor;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Document;
import parser.OutputDoc;
import parser.Solution;
import shape.Layer;
import shape.Pair;
import shape.Polygon;
import shape.Tile;

import java.io.FileNotFoundException;

import static com.itextpdf.kernel.colors.Color.convertRgbToCmyk;

public class DrawPdf {

    private OutputDoc output;
    private String pdfPath;

    public DrawPdf(OutputDoc output, String pdfPath) throws FileNotFoundException {
        this.output = output;
        this.pdfPath = pdfPath;
        this.outputToPdfOldModel();
        this.drawDesign();
    }

    public void outputToPdfOldModel() throws FileNotFoundException {
        pdfPath += "/result.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(pdfPath));
        Rectangle pageRect = new Rectangle((float) output.getChip().getMinX(), (float) output.getChip().getMinY(), (float) output.getChip().getWidth(), (float) output.getChip().getHeight());
        Document document = new Document(pdfDoc, new PageSize(pageRect));
        Solution optimalSolution = output.getOptSolution();

        Color LIGHTBLUE = convertRgbToCmyk(new DeviceRgb(173, 216, 230));
        Color LIME = convertRgbToCmyk(new DeviceRgb(0, 255, 0));
        Color BLACK = convertRgbToCmyk(new DeviceRgb(0, 0, 0));
        Color CRIMSON = convertRgbToCmyk(new DeviceRgb(220, 20, 60));
        Color BLUE = convertRgbToCmyk(new DeviceRgb(68, 114, 196));
        Color YELLOW = convertRgbToCmyk(new DeviceRgb(255, 255, 0));
        Color GREY = convertRgbToCmyk(new DeviceRgb(231, 230, 230));
        Color RED = convertRgbToCmyk(new DeviceRgb(255, 0, 0));


        for (Layer layer : optimalSolution.getLayers()) {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

            /*
            draw chip
             */
            Rectangle chip = new Rectangle((float) output.getChip().getMinX(), (float) output.getChip().getMinY(), (float) output.getChip().getWidth(), (float) output.getChip().getHeight());
            canvas.setLineWidth((float) 0.1)
                    .rectangle(chip)
                    .stroke();

            /*
            draw Layer Polygons
             */
            for (Polygon poly : layer.getLayerPolys()) {
                if (poly.getSubPolys().size() == 0) {
                    Rectangle polyrect = new Rectangle((float) poly.getMinX(), (float) poly.getMinY(), (float) poly.getWidth(), (float) poly.getHeight());
                    canvas.setColor(GREY, true)
                            .setLineWidth((float) 0.1)
                            .rectangle(polyrect)
                            .fill()
                            .stroke();

                    canvas.setColor(BLACK, false)
                            .setLineWidth((float) 0.1)
                            .rectangle(polyrect)
                            .stroke();
                } else {


                    for (shape.Rectangle subrect : poly.getSubPolys()){
                        Rectangle polyrect = new Rectangle((float) subrect.getMinX(), (float) subrect.getMinY(), (float) subrect.getWidth(), (float) subrect.getHeight());
                        canvas.setColor(GREY, true)
                                .setExtGState(new PdfExtGState().setFillOpacity(0.5f))
                                .setLineWidth((float) 0.1)
                                .rectangle(polyrect)
                                .fill()
                                .stroke();
                    }
                    canvas.setColor(BLACK, false)
                            .setLineWidth((float) 0.1)
                            .moveTo(poly.vertices.get(0).x, poly.vertices.get(0).y);
                    for (int i = 1; i < poly.vertices.size(); i++){
                        canvas.lineTo(poly.vertices.get(i).x, poly.vertices.get(i).y);
                    }
                    canvas.closePathStroke();

                }
            }

            /*
            draw Tiles
             */
            Tile dsTile = layer.getDryTile();
            Rectangle rect = new Rectangle((float) dsTile.getMinX(), (float) dsTile.getMinY(), (float) (dsTile.getMaxX() - dsTile.getMinX()), (float) (dsTile.getMaxY() - dsTile.getMinY()));
            canvas.setColor(RED, true)
                    .setLineWidth((float) 0.5)
                    .rectangle(rect)
                    .fill()
                    .stroke();
            /*dsTile = layer.getOriDryTile();
            Rectangle oriRect = new Rectangle((float) dsTile.getMinX(), (float) dsTile.getMinY(), (float) (dsTile.getMaxX() - dsTile.getMinX()), (float) (dsTile.getMaxY() - dsTile.getMinY()));
            canvas.setColor(CRIMSON, true)
                    .setLineWidth((float) 0.1)
                    .rectangle(oriRect)
                    .fill()
                    .stroke();*/
//            for (Tile tile : output.getAllTiles()) {
//                Rectangle rectTile = new Rectangle((float) tile.getMinX(), (float) tile.getMinY(), (float) (tile.getMaxX() - tile.getMinX()), (float) (tile.getMaxY() - tile.getMinY()));
//                canvas.setColor(LIME, false)
//                        .rectangle(rectTile)
//                        .stroke();
//            }


        }

        pdfDoc.close();


    }

    public void drawDesign() throws FileNotFoundException {

        pdfPath += "_tot.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(pdfPath));
        Rectangle pageRect = new Rectangle((float) output.getChip().getMinX(), (float) output.getChip().getMinY(), (float) output.getChip().getWidth(), (float) output.getChip().getHeight());
        Document document = new Document(pdfDoc, new PageSize(pageRect));
        Solution optimalSolution = output.getOptSolution();

        Color LIGHTBLUE = convertRgbToCmyk(new DeviceRgb(173, 216, 230));
        Color LIME = convertRgbToCmyk(new DeviceRgb(0, 255, 0));
        Color BLACK = convertRgbToCmyk(new DeviceRgb(0, 0, 0));
        Color CRIMSON = convertRgbToCmyk(new DeviceRgb(220, 20, 60));
        Color BLUE = convertRgbToCmyk(new DeviceRgb(0, 0, 255));
        Color YELLOW = convertRgbToCmyk(new DeviceRgb(255, 255, 0));
        Color GREY = convertRgbToCmyk(new DeviceRgb(231, 230, 230));

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        /*
            draw chip
             */
//        Rectangle chip = new Rectangle((float) output.getChip().getMinX(), (float) output.getChip().getMinY(), (float) output.getChip().getWidth(), (float) output.getChip().getHeight());
//        canvas.setLineWidth((float) 0.1)
//                .rectangle(chip)
//                .stroke();

        for (Layer layer : optimalSolution.getLayers()) {



            /*
            draw Layer Polygons
             */

            for (Polygon poly : layer.getLayerPolys()) {
                if (poly.getSubPolys().size() == 0) {
                    Rectangle polyrect = new Rectangle((float) poly.getMinX(), (float) poly.getMinY(), (float) poly.getWidth(), (float) poly.getHeight());
                    canvas.setColor(GREY, true)
                            .setLineWidth((float) 0.1)
                            .rectangle(polyrect)
                            .fill()
                            .stroke();

                    canvas.setColor(BLACK, false)
                            .setLineWidth((float) 0.1)
                            .rectangle(polyrect)
                            .stroke();
                } else {


                    for (shape.Rectangle subrect : poly.getSubPolys()){
                        Rectangle polyrect = new Rectangle((float) subrect.getMinX(), (float) subrect.getMinY(), (float) subrect.getWidth(), (float) subrect.getHeight());
                        canvas.setColor(GREY, true)
                                .setExtGState(new PdfExtGState().setFillOpacity(0.5f))
                                .setLineWidth((float) 0.1)
                                .rectangle(polyrect)
                                .fill()
                                .stroke();
                    }
                    canvas.setColor(BLACK, false)
                            .setLineWidth((float) 0.1)
                            .moveTo(poly.vertices.get(0).x, poly.vertices.get(0).y);
                    for (int i = 1; i < poly.vertices.size(); i++){
                        canvas.lineTo(poly.vertices.get(i).x, poly.vertices.get(i).y);
                    }
                    canvas.closePathStroke();

                }
            }


        }

        pdfDoc.close();

    }
}
