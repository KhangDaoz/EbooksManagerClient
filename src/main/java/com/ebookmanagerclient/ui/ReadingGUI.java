package com.ebookmanagerclient.ui;

import com.ebookmanagerclient.controller.ReadingController;
import com.ebookmanagerclient.model.HighLight;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.event.*;

/**
 * Lớp Giao diện (View) cho Cửa sổ Đọc sách.
 * Sở hữu một ReadingController để xử lý tất cả logic.
 */
public class ReadingGUI extends JFrame {

    // Controller
    private final ReadingController controller;

    // Thành phần UI chính
    private JEditorPane contentPane; // Khung hiển thị HTML
    private JScrollPane scrollPane;
    private JToolBar toolBar;
    private JButton prevButton;
    private JButton nextButton;
    private JLabel statusLabel; // Hiển thị trang/chương

    // Menu chuột phải (cho Highlight/Note)
    private JPopupMenu contextMenu;
    private JMenuItem highlightMenuItem;
    private JMenuItem noteMenuItem;

    // Trạng thái tạm thời cho menu chuột phải
    private String lastSelectedText;
    private int lastSelectionStart;
    private int currentFontSize;

    /**
     * Constructor (Được gọi bởi MainController)
     * @param controller Một thể hiện (instance) của ReadingController
     */
    public ReadingGUI(ReadingController controller) {
        this.controller = controller;

        // 1. Thiết lập cửa sổ
        setTitle("Trình đọc sách"); // (Sau này có thể đổi thành tên sách)
        setSize(800, 600);
        setLocationRelativeTo(null); // Giữa màn hình
        // QUAN TRỌNG: Không thoát ứng dụng, chỉ đóng cửa sổ này
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 

        // 2. Khởi tạo thành phần
        initComponents();
        
        // 3. Sắp xếp bố cục
        initLayout();

        // 4. Gắn (attach) các trình xử lý sự kiện
        initListeners();
        
        // 5. Tải nội dung ban đầu
        loadInitialContent();
    }

    /**
     * Khởi tạo các thành phần Swing
     */
    private void initComponents() {
        // Lấy cỡ chữ mặc định từ Controller
        this.currentFontSize = controller.zoomIn(); // (Gọi 1 lần để lấy giá trị)
        this.currentFontSize = controller.zoomOut();
        
        // --- Khung hiển thị HTML ---
        contentPane = new JEditorPane();
        contentPane.setEditable(false); // Rất quan trọng!
        contentPane.setContentType("text/html");
        
        // Áp dụng cỡ chữ mặc định
        applyZoom(this.currentFontSize); 

        scrollPane = new JScrollPane(contentPane);

        // --- Thanh công cụ ---
        toolBar = new JToolBar();
        toolBar.setFloatable(false); // Không cho di chuyển
        prevButton = new JButton("Trang trước");
        nextButton = new JButton("Trang sau");
        statusLabel = new JLabel(" (Đang tải...) ");
        toolBar.add(prevButton);
        toolBar.add(Box.createHorizontalGlue()); // Đẩy statusLabel ra giữa
        toolBar.add(statusLabel);
        toolBar.add(Box.createHorizontalGlue()); // Đẩy nextButton ra phải
        toolBar.add(nextButton);

        // --- Menu Chuột phải (REQ) ---
        contextMenu = new JPopupMenu();
        highlightMenuItem = new JMenuItem("Đánh dấu (Highlight)");
        noteMenuItem = new JMenuItem("Thêm Ghi chú (Note)");
        contextMenu.add(highlightMenuItem);
        contextMenu.add(noteMenuItem);
    }

    /**
     * Sắp xếp bố cục (Layout)
     */
    private void initLayout() {
        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Gắn tất cả các Listener (Sự kiện)
     */
    private void initListeners() {
        // --- (REQ) Xử lý Nút (Lật trang) ---
        nextButton.addActionListener(e -> handleNextPage());
        prevButton.addActionListener(e -> handlePrevPage());

        // --- (REQ) Xử lý Phím (Lật trang) ---
        // Gắn listener vào JFrame
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    handleNextPage();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    handlePrevPage();
                }
            }
        });
        // Phải đặt 2 cái này để JFrame "nghe" được phím
        this.setFocusable(true);
        this.requestFocusInWindow();

        // --- (REQ) Xử lý Cuộn chuột (Lật trang VÀ Zoom) ---
        // Gắn listener vào KHUNG CUỘN
        scrollPane.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                // Nếu Ctrl ĐƯỢC nhấn -> ZOOM
                if (e.isControlDown()) {
                    if (e.getWheelRotation() < 0) {
                        handleZoomIn(); // Cuộn lên -> Zoom In
                    } else {
                        handleZoomOut(); // Cuộn xuống -> Zoom Out
                    }
                } 
                // Nếu Ctrl KHÔNG được nhấn -> Lật trang
                else {
                    if (e.getWheelRotation() > 0) {
                        handleNextPage(); // Cuộn xuống -> Trang sau
                    } else {
                        handlePrevPage(); // Cuộn lên -> Trang trước
                    }
                }
            }
        });

        // --- (REQ) Xử lý Chuột phải (Highlight/Note) ---
        contentPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Kiểm tra nếu là sự kiện pop-up (chuột phải)
                if (e.isPopupTrigger()) {
                    showContextMenu(e);
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showContextMenu(e);
                }
            }
        });
        
        // --- Xử lý sự kiện cho 2 nút trong Menu Chuột phải ---
        highlightMenuItem.addActionListener(e -> handleHighlightAction());
        noteMenuItem.addActionListener(e -> handleNoteAction());
        
        // --- (QUAN TRỌNG) Xử lý Đóng cửa sổ ---
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Báo cho Controller biết để lưu tiến độ
                controller.handleWindowClose();
                // (Sau đó cửa sổ sẽ tự dispose()
                // vì chúng ta đã set setDefaultCloseOperation)
            }
        });
    }
    
    // --- Các hàm Public (để Controller gọi) ---
    
    /**
     * (PUBLIC) Hiển thị nội dung HTML mới lên màn hình.
     * (Được gọi bởi Controller)
     */
    public void displayNewContent(String htmlContent, int page, int totalPages) {
        contentPane.setText(htmlContent);
        // Tự động cuộn lên đầu trang
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
        // Cập nhật thanh trạng thái
        statusLabel.setText("Trang " + (page + 1) + " / " + totalPages);
        applyZoom(this.currentFontSize); // Áp dụng lại zoom cho nội dung mới
    }
    
    /**
     * (PUBLIC) Áp dụng cỡ chữ mới.
     * (Được gọi bởi Controller)
     */
    public void applyZoom(int fontSize) {
        this.currentFontSize = fontSize;
        // Dùng StyleSheet (CSS) để thay đổi cỡ chữ của HTML
        StyleSheet css = ((HTMLDocument) contentPane.getDocument()).getStyleSheet();
        css.addRule("body { font-size: " + fontSize + "pt; }");
        // Ép JEditorPane vẽ lại với CSS mới
        contentPane.revalidate();
        contentPane.repaint();
    }
    
    // --- Các hàm Private (Xử lý sự kiện) ---
    
    private void loadInitialContent() {
        // Tải nội dung đầu tiên từ Controller
        // (Chúng ta sẽ sửa hàm controller để nó trả về cả HTML và thông tin trang)
        // (Tạm thời, chúng ta sẽ gọi hàm cũ)
        String html = (String)controller.getInitialContent();
        contentPane.setText(html);
        applyZoom(this.currentFontSize); // Áp dụng CSS
        
        // (Code tải highlights cũ...)
        // List<HighLight> highlights = controller.loadHighlights();
        // (Code để vẽ highlights lên JEditorPane...)
    }
    
    // --- Các hàm gọi Controller ---

    private void handleNextPage() {
        String html = (String)controller.getNextPageContent();
        if (html != null) {
            contentPane.setText(html);
            applyZoom(currentFontSize);
            // (Cập nhật statusLabel...)
        }
    }
    
    private void handlePrevPage() {
        String html = (String)controller.getPrevPageContent();
        if (html != null) {
            contentPane.setText(html);
            applyZoom(currentFontSize);
            // (Cập nhật statusLabel...)
        }
    }
    
    private void handleZoomIn() {
        int newSize = controller.zoomIn();
        applyZoom(newSize);
    }
    
    private void handleZoomOut() {
        int newSize = controller.zoomOut();
        applyZoom(newSize);
    }
    
    private void showContextMenu(MouseEvent e) {
        // Lấy text người dùng vừa bôi đen
        this.lastSelectedText = contentPane.getSelectedText();
        this.lastSelectionStart = contentPane.getSelectionStart();
        
        // Chỉ bật menu nếu có bôi đen
        boolean hasSelection = (lastSelectedText != null && !lastSelectedText.isEmpty());
        highlightMenuItem.setEnabled(hasSelection);
        noteMenuItem.setEnabled(hasSelection); // (Note cũng cần bối cảnh)

        // Hiển thị menu tại vị trí chuột
        contextMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    private void handleHighlightAction() {
        
        HighLight newHighlight = controller.handleCreateHighlight(
                lastSelectionStart, 
                lastSelectedText, 
                "yellow" // (Sau này có thể cho người dùng chọn màu)
        );
        
        if (newHighlight != null) {
            System.out.println("Đã lưu highlight, ID: " + newHighlight.getHighlightId());
            // (Code để vẽ highlight lên JEditorPane...)
        } else {
            JOptionPane.showMessageDialog(this, "Không thể lưu highlight.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleNoteAction() {
        // Lấy ghi chú từ người dùng
        String note = JOptionPane.showInputDialog(this, "Nhập ghi chú của bạn:", "Thêm Ghi chú", JOptionPane.PLAIN_MESSAGE);
        
        if (note != null && !note.trim().isEmpty()) {
            
            HighLight newNote = controller.handleCreateNote(
                    lastSelectionStart, 
                    lastSelectedText, 
                    note
            );
            
            if (newNote != null) {
                System.out.println("Đã lưu note, ID: " + newNote.getHighlightId());
                // (Code để vẽ "biểu tượng note" lên JEditorPane...)
            } else {
                JOptionPane.showMessageDialog(this, "Không thể lưu note.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}