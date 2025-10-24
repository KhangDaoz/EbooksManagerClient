package com.ebookmanager.model.InteractClasses;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.Loader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class pdfManager {
    private PDDocument doc;
    private PDFRenderer renderer;
    
    // load file which was added from GUI.
    public pdfManager(File file) throws IOException
    {   
        this.doc = Loader.loadPDF(file);
        this.renderer = new PDFRenderer(doc);

    }
    // CLASS which desribe image file.
    public BufferedImage renderPage(int pageIndex) throws IOException
    {
        return renderer.renderImageWithDPI(pageIndex, 200);
    }

    public int getPageCount()
    {
        return doc.getNumberOfPages();
    }

    public void close()throws IOException
    {
        if(doc != null)
        {
            doc.close();
        }
    }
}
