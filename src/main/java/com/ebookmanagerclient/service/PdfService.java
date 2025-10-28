package com.ebookmanagerclient.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.Loader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class PdfService implements BookInterfaceService {
    
    private PDDocument document;
    private PDFRenderer renderer;

    // COnstructor
    public PdfService()
    {
        document = null;
        renderer = null;
    }

    
    @Override
    public void openBook(String filePath) throws IOException
    {
        try
        {
            document = Loader.loadPDF(new File(filePath));
            if(document != null)
            {
                System.out.println("DOCUMENT EXIST!!!!");
            }
            try
            {
                renderer = new PDFRenderer(document);
                if(renderer !=  null)
                {
                    System.out.println("RENDERER LOADED!!");
                }
            }
            catch(Exception e)
            {
                if(document!=null) document.close();
                throw new IOException("Cannot create Renderer", e);
            }

        }

        catch(IOException e)
        {
            document = null;
            renderer = null;
            throw new IOException("Cannot open File");
        }
    }

    @Override

    public List<String> getAuthors()
    {
        return new ArrayList<>(); 
    }

    @Override
    public String getTitle() {
        if (document == null) return "No PDF Open";
        PDDocumentInformation info = document.getDocumentInformation();
        return info.getTitle() != null ? info.getTitle() : "Untitled PDF";
    }
    /**
     * Lấy Mục lục (Bookmarks) của PDF.
     * @return Danh sách các mục (PDOutlineItem)
     */
    
    @Override
    public List<PDOutlineItem> getTableOfContents() {
        if (document == null) return Collections.emptyList();
        PDDocumentOutline outline = document.getDocumentCatalog().getDocumentOutline();
        if (outline == null) return Collections.emptyList();

        List<PDOutlineItem> items = new ArrayList<>();
        PDOutlineItem current = outline.getFirstChild();
        while (current != null) {
            items.add(current);
            // (Code này chỉ lấy cấp 1, có thể cần đệ quy để lấy cấp con)
            current = current.getNextSibling();
        }
        return items;
    }
/**
     * (Ít dùng cho PDF) Lấy trang dựa trên mục lục.
     * Cần code thêm để tìm trang tương ứng với PDOutlineItem.
     */
    
    @Override
    public Object getContent(Object chapterRef) throws IOException {
        if (document == null) throw new IOException("PDF is not open.");
        if (!(chapterRef instanceof PDOutlineItem)) {
             throw new IOException("Tham chiếu không phải PDOutlineItem.");
        }
        // TODO: Cần tìm số trang (page index) tương ứng với PDOutlineItem
        // PDOutlineItem outlineItem = (PDOutlineItem) chapterRef;
        // int pageIndex = findPageIndexForOutline(outlineItem);
        // return getContentBySpineIndex(pageIndex);
        throw new UnsupportedOperationException("Lấy nội dung từ mục lục PDF chưa được hỗ trợ.");
    }
    /**
     * Lấy ảnh (BufferedImage) của một trang cụ thể.
     * @param pageIndex Chỉ số trang (bắt đầu từ 0)
     * @return BufferedImage của trang đó
     */
    
    @Override
    public Object getContentBySpineIndex(int pageIndex) throws IOException {
        if (document == null || renderer == null) {
            throw new IOException("PDF is not open or renderer not initialized.");
        }
        if (pageIndex < 0 || pageIndex >= getSpineSize()) {
            throw new IndexOutOfBoundsException("Chỉ số trang PDF không hợp lệ: " + pageIndex);
        }
        // Render trang tại pageIndex thành ảnh
        return renderer.renderImageWithDPI(pageIndex, 300);
    }
    /**
     * Lấy tổng số trang của PDF.
     */
    
    @Override
    public int getSpineSize() {
        return (document == null) ? 0 : document.getNumberOfPages();
    }

    
    @Override
    public void closeBook() {
        if (document != null) {
            try {
                document.close();
            } catch (IOException e) {
                e.printStackTrace(); // Ghi log lỗi khi đóng
            } finally {
                document = null;
                renderer = null;
            }
        }
    }
}
