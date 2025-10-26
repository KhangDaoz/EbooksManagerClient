package com.ebookmanagerclient.controller;

import com.ebookmanagerclient.model.Book;
import com.ebookmanagerclient.model.UserBook;
import com.ebookmanagerclient.service.*; // Import tất cả service
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller (Bộ điều khiển) cho Màn hình chính (MainGUI).
 * Chịu trách nhiệm:
 * 1. Download book into personal bibrary 
 * 2. Finding
 * 3. Filter in Reading tab
 * 4. Actions: Read, Add, Exist
 */
public class MainController {

    // 1. Dependencies 
    private final AuthService authService;
    private final BookService bookService;
    private final UserBookService userBookService;
    private final FileService fileService;
    // BookReaderFactory 

    // 2. Data Cache 
    // Save personal library temporaly
    private List<UserBook> myUserBookCache; 
    private List<Book> myLibraryDetailsCache; 

    /**
     * Constructor
     */
    public MainController() {
        
        this.authService = AuthService.getInstance();
        this.bookService = new BookService();
        this.userBookService = new UserBookService();
        this.fileService = new FileService();

        
        this.myUserBookCache = new ArrayList<>();
        this.myLibraryDetailsCache = new ArrayList<>();
    }

    // Community tab

   
    public List<Book> getCommunityBooks(String query) {
        try {
            return List.of(bookService.searchCommunityBooks(query));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>(); 
        }
    }

    // My library and Reading tab

    public void loadPersonalLibrary() {
        // Reload cachse
        myUserBookCache.clear();
        myLibraryDetailsCache.clear();
        
        try {
            // Get list of details: bookId and progress
            UserBook[] userBooks = userBookService.getPersonalLibrary();

            // Query
            //              !!!Need to OPTIMIZE 
            for (UserBook ub : userBooks) {
                try {
                    
                    Book bookDetail = bookService.getBookDetails(ub.getBookId());
                    
                    myUserBookCache.add(ub);
                    myLibraryDetailsCache.add(bookDetail);
                    
                } catch (IOException e) {
                    System.err.println("Không thể tải chi tiết cho bookId: " + 
                    ub.getBookId());
                   
                }
            }
        } catch (IOException e) {
            e.printStackTrace();    
        }
    }

    /**
     * Lấy sách cho tab "Thư viện của tôi", hỗ trợ tìm kiếm (Client-side).
     * @param query Từ khóa tìm kiếm
     * @return Danh sách sách đã lọc
     */
    public List<Book> getMyLibraryBooks(String query) {
        // Chỉ lọc từ cache
        return filterBookList(myLibraryDetailsCache, query);
    }

    /**
     * (REQ 1 & 4) Lấy sách cho tab "Đang đọc", hỗ trợ tìm kiếm (Client-side).
     * @param query Từ khóa tìm kiếm
     * @return Danh sách sách "đang đọc" đã lọc
     */
    public List<Book> getReadingBooks(String query) {
        // 1. Lọc theo tiến độ (progress > 0)
        List<Book> readingList = myLibraryDetailsCache.stream()
            .filter(book -> {
                double progress = getProgressForBook(book.getId());
                return progress > 0; // REQ 1: Progress > 0%
            })
            .collect(Collectors.toList());
            
        // 2. Lọc theo từ khóa tìm kiếm
        return filterBookList(readingList, query);
    }
    
    /**
     * (REQ 4 - Helper) Hàm lọc client-side
     */
    private List<Book> filterBookList(List<Book> list, String query) {
        if (query == null || query.trim().isEmpty()) {
            return list; // Trả về tất cả nếu không tìm kiếm
        }
        
        String lowerQuery = query.toLowerCase().trim();
        return list.stream()
            .filter(book -> 
                book.getTitle().toLowerCase().contains(lowerQuery) ||
                book.getAuthor().toLowerCase().contains(lowerQuery)
            )
            .collect(Collectors.toList());
    }

    // --- C. Các Hành động (Actions) từ UI ---

    /**
     * (REQ 2) Xử lý khi nhấn nút "Tải về" (Thêm vào thư viện).
     * @param bookId ID sách từ tab "Cộng đồng"
     * @return true nếu thành công
     */
    public boolean handleAddBookToLibrary(int bookId) {
        try {
            // (Gọi UserBookHandler - Cổng 8083)
            userBookService.addBookToLibrary(bookId);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * (REQ 2) Xử lý khi nhấn nút "Đọc".
     * @param book Đối tượng Book (từ "Thư viện của tôi" hoặc "Đang đọc")
     */
    public void handleReadBook(Book book) {
        try {
            // 1. Tải file (FileService sẽ tự kiểm tra nếu file đã tồn tại)
            String localPath = fileService.downloadBook(book);

            // 2. Lấy trình đọc (Factory)
            BookInterfaceService reader = BookReaderFactory.getReader(localPath);
            
            // 3. Mở sách (Code này sẽ gọi Giai đoạn tiếp theo)
            // (Chúng ta sẽ khởi tạo ReadingController và ReadingGUI ở đây)
            System.out.println("Sẵn sàng mở sách: " + localPath);
            // ReadingController readingController = new ReadingController(reader, book, ...);
            // new ReadingGUI(readingController).setVisible(true);
            
        } catch (Exception e) {
            e.printStackTrace();
            // UI cần hiển thị lỗi (ví dụ: "Không thể mở sách" hoặc "Định dạng không hỗ trợ")
        }
    }
    
    /**
     * Xử lý khi nhấn nút "Đăng xuất".
     */
    public void handleLogout() {
        authService.logout();
        // UI (MainGUI) sẽ chịu trách nhiệm đóng chính nó
        // và mở lại LoginGUI
    }

    // --- D. Các Hàm hỗ trợ cho UI ---

    /**
     * Lấy tên người dùng (ví dụ: "Kien")
     */
    public String getUserName() {
        return authService.getCurrentUser().getUsername();
    }
    
    /**
     * Lấy tiến độ đọc cho một cuốn sách trong thư viện.
     * @param bookId ID sách
     * @return Tiến độ (ví dụ: 0.45 cho 45%)
     */
    public double getProgressForBook(int bookId) {
        // Tìm trong cache
        return myUserBookCache.stream()
            .filter(ub -> ub.getBookId() == bookId)
            .map(UserBook::getProgress)
            .findFirst()
            .orElse(0.0); // Mặc định là 0
    }
    
    /**
     * Lấy thống kê cho câu chào: "32 sách", "5 đang đọc"
     * @return Map chứa "total" và "reading"
     */
    public Map<String, Integer> getLibraryStats() {
        int total = myUserBookCache.size();
        
        int reading = (int) myUserBookCache.stream()
            .filter(ub -> ub.getProgress() > 0)
            .count();
            
        return Map.of("total", total, "reading", reading);
    }
}