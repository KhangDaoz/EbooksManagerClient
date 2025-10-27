package com.ebookmanagerclient.controller;

import com.ebookmanagerclient.model.Book;
import com.ebookmanagerclient.model.HighLight;
import com.ebookmanagerclient.service.AuthService;
import com.ebookmanagerclient.service.HighlightService;
import com.ebookmanagerclient.service.BookInterfaceService; // "Hợp đồng" đọc
import com.ebookmanagerclient.service.UserBookService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller (Bộ điều khiển) cho Cửa sổ Đọc sách (ReadingGUI).
 * Chịu trách nhiệm điều phối:
 * - Nội dung sách (từ IBookReaderService)
 * - Tiến độ đọc (từ UserBookService)
 * - Highlight/Note (từ HighlightService)
 * - Trạng thái UI (Zoom, Trang hiện tại)
 */
public class ReadingController {

    // --- Services (Logic) ---
    private final BookInterfaceService readerService;
    private final UserBookService userBookService;
    private final HighlightService highlightService;
    private final AuthService authService;

    // --- State (Trạng thái) ---
    private final Book book; // Sách đang đọc
    private int currentSpineIndex; // Vị trí chương/trang hiện tại
    private int totalSpineSize; // Tổng số chương/trang
    private int currentFontSize; // Cỡ chữ (cho zoom)

    /**
     * Constructor được gọi bởi MainController.
     * @param readerService Thể hiện (instance) của trình đọc (ví dụ: EpubService)
     * @param book Sách đang được mở
     */
    public ReadingController(BookInterfaceService readerService, Book book) {
        this.readerService = readerService;
        this.book = book;

        // Khởi tạo các service khác
        this.userBookService = new UserBookService();
        this.highlightService = new HighlightService();
        this.authService = AuthService.getInstance(); // Để lấy userId

        // Khởi tạo trạng thái
        this.currentFontSize = 14; // Cỡ chữ mặc định
        this.currentSpineIndex = 0; // Luôn bắt đầu từ chương 0
        //this.totalSpineSize = readerService.getSpineSize();
    }

    public ReadingController(BookInterfaceService readerService)
    {
        this(readerService, createLocalBook());    
    }

    public static Book createLocalBook()
    {
        Book localBook = new Book();
        localBook.setId(-1);
        localBook.setTitle("LocalBook");
        localBook.setAuthor("N/A");
        return localBook;
    }

    // --- 1. Tải Dữ liệu Ban đầu (Cho UI gọi) ---

    /**
     * Get content of Book
     * Type of class is Object because each type of files 
     * has a certain type of content.
     * epub: html
     * pdf: bufferedimage
     */
    public Object getInitialContent() {
        try {

            this.totalSpineSize = readerService.getSpineSize();
            return readerService.getContentBySpineIndex(currentSpineIndex);

        } catch (IOException e) {
            e.printStackTrace();
            return "<html><body><h1>Không thể tải sách</h1></body></html>";
        }
    }

    /*
     * Get next page content
     */

    public Object getNextPageContent()
    {
        if(currentSpineIndex< totalSpineSize-1)
        {
            currentSpineIndex ++;
            try
            {
                saveCurrentProgress();
                return readerService.getContentBySpineIndex(currentSpineIndex);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                currentSpineIndex--;
                return "Cannot load next page";
            }
        }
        return null;
    }

    /*
     * get previous page content
     */
    public Object getPrevPageContent()
    {
        if(currentSpineIndex > 0)
        {
            currentSpineIndex --;
            try
            {
                saveCurrentProgress();
                return readerService.getContentBySpineIndex(currentSpineIndex);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                currentSpineIndex++;
                return "Cannot load next page";
            }
        }
        return null;
    }
    /**
     * Lấy tất cả highlight/note cũ của sách này.
     */
    public List<HighLight> loadHighlights() {
        if(book.getId()==-1) return new ArrayList<>();
        
        try {
            return List.of(highlightService.getHighlightsForBook(book.getId()));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>(); // Trả về danh sách rỗng nếu lỗi
        }
    }


    // --- 3. Điều khiển Zoom (UI gọi) ---

    // Do not use zoom function for pdf file. (image)

    /**
     * Xử lý logic khi người dùng "Zoom In" (Ctrl + Cuộn lên).
     * @return Cỡ chữ mới.
     */
    public int zoomIn() {
        if (currentFontSize < 72) { // Đặt giới hạn max
            currentFontSize += 2;
        }
        return currentFontSize;
    }

    /**
     * Xử lý logic khi người dùng "Zoom Out" (Ctrl + Cuộn xuống).
     * @return Cỡ chữ mới.
     */
    public int zoomOut() {
        if (currentFontSize > 8) { // Đặt giới hạn min
            currentFontSize -= 2;
        }
        return currentFontSize;
    }

    /*
    * Xử lý khi người dùng tạo một HIGHLIGHT mới.
    * (ĐÃ CẬP NHẬT: Nhận selectionStart thay vì cfi)
    * @param selectionStart Vị trí con trỏ (int)
    * @param selectedText Đoạn text (do UI cung cấp)
    * @param color Màu sắc (ví dụ: "yellow")
    */
    public HighLight handleCreateHighlight(int selectionStart, String selectedText, String color) {
        if(book.getId()==-1) return null;

        // TẠM THỜI: Chúng ta sẽ dùng offset làm CFI.
        // (Trong tương lai, bạn sẽ dùng EpubService để biến
        // currentSpineIndex + selectionStart thành một CFI thật)
        String locationCFI = "temp-cfi-spine" + currentSpineIndex + "/offset" + selectionStart;

        try {
            HighLight newHighlight = new HighLight(locationCFI, selectedText, null, color);
            return highlightService.createHighlight(book.getId(), newHighlight);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Trả về null nếu thất bại
        }
    }

    public HighLight handleCreateNote(int selectionStart, String selectedText, String userNote) {
        if(book.getId()==-1) return null;
        
        // TẠM THỜI: Dùng offset làm CFI
        String locationCFI = "temp-cfi-spine" + currentSpineIndex + "/offset" + selectionStart;

        try {
            HighLight newNote = new HighLight(locationCFI, selectedText, userNote, null);
            return highlightService.createHighlight(book.getId(), newNote);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Trả về null nếu thất bại
        }
    }

    /**
     * Xử lý khi người dùng xóa một highlight/note.
     * @param highlightId ID của highlight cần xóa
     */
    public boolean handleDeleteHighlight(int highlightId) {
        if(book.getId()==-1) return false;
        
        try {
            highlightService.deleteHighlight(book.getId(), highlightId);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // --- 5. Lưu Trạng thái (Nội bộ & UI gọi) ---

    /**
     * Xử lý khi cửa sổ Đọc sách (ReadingGUI) BỊ ĐÓNG.
     * UI phải gọi hàm này.
     */
    public void handleWindowClose() {
        System.out.println("Đang đóng sách, lưu tiến độ cuối cùng...");
        saveCurrentProgress();
        readerService.closeBook(); // Giải phóng tài nguyên file
    }

    /**
     * Hàm nội bộ (private) để tính toán và lưu tiến độ.
     */
    private void saveCurrentProgress() {
        if(book.getId()==-1) return;
        if (totalSpineSize == 0) {
            return; // Tránh lỗi chia cho 0
        }

        // Tính % (ví dụ: đang ở chương 5/10 -> 50%)
        float progressPercent = ((float) currentSpineIndex / (float) totalSpineSize) * 100.0f;
        
        // Làm tròn đến 2 chữ số
        progressPercent = Math.round(progressPercent * 100.0f) / 100.0f;

        try {
            // (Gọi UserBookHandler - Cổng 8083)
            userBookService.updateReadingProgress(book.getId(), progressPercent);
        } catch (IOException e) {
            e.printStackTrace();
            // Lỗi không lưu được tiến độ (ví dụ: mất mạng)
        }
    }
}