package processor;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import parser.OutputDoc;
import parser.Solution;
import shape.Polygon;
import shape.Tile;
import simShape.SimLayer;

import java.io.FileNotFoundException;

import static com.itextpdf.kernel.colors.Color.convertRgbToCmyk;

public class SimDrawPdf {

    private OutputDoc output;
    private String pdfPath;

    public SimDrawPdf(OutputDoc output, String pdfPath) throws FileNotFoundException {
        this.output = output;
        this.pdfPath = pdfPath;
        this.simOutputToPdf();
    }

    public void simOutputToPdf() throws FileNotFoundException {
        pdfPath += "/result.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(pdfPath));
        Rectangle pageRect = new Rectangle((float) output.getChip().getMinX(), (float) output.getChip().getMinY(), (float) output.getChip().getWidth(), (float) output.getChip().getHeight());
        Document document = new Document(pdfDoc, new PageSize(pageRect));
        Solution optimalSolution = output.getOptSolution();

        Color LIGHTBLUE = convertRgbToCmyk(new DeviceRgb(173,216,230));
        Color LIME = convertRgbToCmyk(new DeviceRgb(0, 255, 0));
        Color BLACK = convertRgbToCmyk(new DeviceRgb(0,0,0));
        Color CRIMSON = convertRgbToCmyk(new DeviceRgb(220, 20, 60));
        Color BLUE = convertRgbToCmyk(new DeviceRgb(0, 0, 255));
        Color YELLOW = convertRgbToCmyk(new DeviceRgb(255, 255, 0));


        for (SimLayer simLayer : output.getSimLayers()){
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

            /*
            draw chip
             */
            Rectangle chip = new Rectangle((float) output.getChip().getMinX(), (float) output.getChip().getMinY(), (float) output.getChip().getWidth(), (float) output.getChip().getHeight());
            canvas.setLineWidth((float) 0.1)
                    .rectangle(chip)
                    .stroke();

            /*
            draw Tiles
             */
            for (Tile ct : simLayer.getCriticalTiles()) {
                Rectangle rect = new Rectangle((float) ct.getMinX(), (float) ct.getMinY(), (float) ct.getWidth(), (float) ct.getHeight());
                canvas.setColor(YELLOW, true)
                        .setLineWidth((float) 0.1)
                        .rectangle(rect)
                        .fill()
                        .stroke();
            }
            /*dsTile = layer.getOriDryTile();
            Rectangle oriRect = new Rectangle((float) dsTile.getMinX(), (float) dsTile.getMinY(), (float) (dsTile.getMaxX() - dsTile.getMinX()), (float) (dsTile.getMaxY() - dsTile.getMinY()));
            canvas.setColor(CRIMSON, true)
                    .setLineWidth((float) 0.1)
                    .rectangle(oriRect)
                    .fill()
                    .stroke();*/
            for (Tile tile : output.getAllTiles()){
                Rectangle rectTile = new Rectangle((float) tile.getMinX(), (float) tile.getMinY(), (float) (tile.getMaxX() - tile.getMinX()), (float) (tile.getMaxY() - tile.getMinY()));
                canvas.setColor(LIME, false)
                        .rectangle(rectTile)
                        .stroke();
            }

            /*
            draw Layer Polygons
             */
            for (Polygon poly : simLayer.getLayerPolygons()){
                Rectangle polyrect = new Rectangle((float) poly.getMinX(), (float) poly.getMinY(), (float) poly.getWidth(), (float) poly.getHeight());
                canvas.setColor(BLACK, false)
                        .setLineWidth((float) 0.14)
                        .rectangle(polyrect)
                        .stroke();

            }


        }

        pdfDoc.close();



    }
}
