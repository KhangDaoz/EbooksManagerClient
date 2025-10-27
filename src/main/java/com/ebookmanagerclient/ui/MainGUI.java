package com.ebookmanagerclient.ui;

import com.ebookmanagerclient.controller.MainController;
import com.ebookmanagerclient.model.Book;

// Thêm import
// Thêm import
// Thêm import
// Thêm import
// Thêm import
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.io.File;         // Thêm import
import java.io.IOException;

import com.ebookmanagerclient.service.BookInterfaceService; // Thêm import
import com.ebookmanagerclient.service.BookReaderFactory; // Thêm import
import com.ebookmanagerclient.service.EpubService;
import com.ebookmanagerclient.service.PdfService;
import com.ebookmanagerclient.controller.ReadingController; // Thêm import
/**
 * Lớp Giao diện (View) cho Màn hình chính (Dashboard).
 * Sở hữu một MainController để xử lý logic.
 */
public class MainGUI extends JFrame {

    // Controller
    private final MainController controller;

    // Thành phần UI chính
    private JSplitPane splitPane;
    private JPanel sidebarPanel;
    private JPanel mainContentPanel;
    private CardLayout mainCardLayout;

    // Các "View" con
    private JPanel communityView;
    private JPanel myLibraryView;
    private JPanel readingView;
    // (Các view khác...)

    // Thành phần của "Cộng đồng"
    private JTable communityTable;
    private BookTableModel communityTableModel;
    private JTextField communitySearchField;
    private JButton communitySearchButton;
    
    // Thành phần của cá nhân
    private JTable libraryTable;
    private LibraryTableModel libraryTableModel;
    private JTextField librarySearchField;
    private JButton librarySearchButton;

    // Thành phần của Reading 
    private JTable readingTable;
    private LibraryTableModel readingTableModel;
    private JTextField readingSearchField;
    private JButton readingSearchButton;

    // Thành phần Header
    private JLabel welcomeLabel;
    private JButton logoutButton; // (Sẽ được thêm vào menu "Kien")

    /**
     * Constructor
     */
    public MainGUI() {
        // 1. Khởi tạo Controller
        this.controller = new MainController();

        // 2. Thiết lập cửa sổ (JFrame)
        setTitle("Ebook Manager");
        setSize(1280, 720); // Kích thước lớn
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Giữa màn hình
        
        // 3. Tạo Sidebar (Bảng điều khiển bên trái)
        sidebarPanel = createSidebar();

        // 4. Tạo Main Content (Nội dung chính)
        mainContentPanel = createMainContentPanel();

        // 5. Tạo Split Pane (Chia 2 khung)
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebarPanel, mainContentPanel);
        splitPane.setDividerLocation(200); // Độ rộng của sidebar
        splitPane.setEnabled(false); // Không cho di chuyển thanh chia
        
        // 6. Thêm Header (Phần "Xin chào, Kien!")
        JPanel headerPanel = createHeaderPanel();

        // 7. Sắp xếp bố cục tổng
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        // THÊM MỚI: Tạo Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Tệp");
        JMenuItem openLocalFileItem = new JMenuItem("Mở sách từ máy tính...");
        fileMenu.add(openLocalFileItem);
        menuBar.add(fileMenu);
        this.setJMenuBar(menuBar); // Gắn menu vào JFrame

        // Gắn sự kiện
        openLocalFileItem.addActionListener(e -> handleOpenLocalFile());

        // 8. Tải dữ liệu ban đầu
        loadInitialData();
    }



    // THÊM HÀM MỚI NÀY
    private void handleOpenLocalFile() 
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file sách để đọc");
        // (Tùy chọn: Thêm bộ lọc file .epub, .pdf)
        // Example filter:
        // FileNameExtensionFilter filter = new FileNameExtensionFilter("Ebook Files (.epub, .pdf)", "epub", "pdf");
        // fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String localPath = selectedFile.getAbsolutePath();
            BookInterfaceService reader = null; // Khai báo ở đây để dùng trong catch/finally

            try {
                // 1. Lấy trình đọc (Factory)
                reader = BookReaderFactory.getReader(localPath);

                // 2. Mở sách (Nạp dữ liệu)
                reader.openBook(localPath);

                // 3. Tạo Controller (dùng Constructor MỚI cho sách local)
                // Constructor này tự tạo một "Book ảo" với ID=0
                ReadingController readingController = new ReadingController(reader);

                // --- BẮT ĐẦU PHẦN KIỂM TRA VÀ MỞ GUI ---
                // 4. Chọn và hiển thị GUI phù hợp
                final BookInterfaceService finalReader = reader; // Cần biến final cho lambda
                SwingUtilities.invokeLater(() -> {
                    if (finalReader instanceof EpubService) {
                        // Mở GUI cho EPUB
                        ReadingGUI readingFrame = new ReadingGUI(readingController);
                        readingFrame.setVisible(true);
                    } else if (finalReader instanceof PdfService) {
                        // Mở GUI cho PDF
                        PdfGUI pdfFrame = new PdfGUI(readingController);
                        pdfFrame.setVisible(true);
                    } else {
                        // Trường hợp Factory trả về loại không xác định
                        JOptionPane.showMessageDialog(this, // Dùng 'this' vì đang ở trong MainGUI
                                "Loại trình đọc không xác định.",
                                "Lỗi Mở Sách",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
                // --- KẾT THÚC PHẦN KIỂM TRA VÀ MỞ GUI ---

            } catch (IOException e) { // Lỗi mở sách (openBook) hoặc I/O khác
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Lỗi I/O khi mở sách:\n" + e.getMessage(),
                        "Lỗi Đọc Sách",
                        JOptionPane.ERROR_MESSAGE);
                if (reader != null) reader.closeBook(); // Đảm bảo đóng nếu đã mở dở
            } catch (IllegalArgumentException e) { // Lỗi từ Factory (định dạng không hỗ trợ)
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Lỗi định dạng file:\n" + e.getMessage(),
                        "Lỗi Định Dạng",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) { // Bắt các lỗi không mong muốn khác
                e.printStackTrace();
                 JOptionPane.showMessageDialog(this,
                        "Lỗi không xác định khi mở sách cục bộ:\n" + e.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                if (reader != null) reader.closeBook();
            }
        }
    }
    /**
     * Tạo phần Header
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        welcomeLabel = new JLabel("Xin chào, " + controller.getUserName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        // (Phần tìm kiếm chung và nút Profile/Logout sẽ ở đây)
        // (Tạm thời đơn giản hóa)
        logoutButton = new JButton("Đăng xuất");
        
        header.add(welcomeLabel, BorderLayout.CENTER);
        header.add(logoutButton, BorderLayout.EAST);
        
        logoutButton.addActionListener(e -> handleLogout());
        
        return header;
    }

    /**
     * Tạo Sidebar (Menu trái)
     */
    private JPanel createSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Các nút điều hướng
        JButton communityButton = new JButton("Cộng đồng");
        JButton libraryButton = new JButton("Thư viện của tôi");
        JButton readingButton = new JButton("Đang đọc");

        // (Căn chỉnh các nút)
        Dimension buttonSize = new Dimension(180, 40);
        communityButton.setMaximumSize(buttonSize);
        libraryButton.setMaximumSize(buttonSize);
        readingButton.setMaximumSize(buttonSize);
        
        panel.add(communityButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Khoảng cách
        panel.add(libraryButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(readingButton);
        // (Thêm các nút khác...)

        // Gắn sự kiện cho các nút
        communityButton.addActionListener(e -> showView("COMMUNITY"));
        libraryButton.addActionListener(e -> showView("LIBRARY"));
        readingButton.addActionListener(e -> showView("READING"));

        return panel;
    }

    /**
     * Tạo Main Content Panel (sử dụng CardLayout)
     */
    private JPanel createMainContentPanel() {
        mainCardLayout = new CardLayout();
        JPanel panel = new JPanel(mainCardLayout);

        // Tạo các View con và thêm vào CardLayout
        communityView = createCommunityView();
        myLibraryView = createMyLibraryView(); 
        readingView = createReadingView();
        
        
        panel.add(communityView, "COMMUNITY");
        panel.add(myLibraryView, "LIBRARY");
        panel.add(readingView, "READING");

        return panel;
    }

    /**
     * (Quan trọng) Tạo View cho tab "Cộng đồng"
     */
    private JPanel createCommunityView() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Panel tìm kiếm (ở trên)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        communitySearchField = new JTextField(30);
        communitySearchButton = new JButton("Tìm");
        searchPanel.add(new JLabel("Tìm kiếm toàn bộ sách:"));
        searchPanel.add(communitySearchField);
        searchPanel.add(communitySearchButton);

        // 2. Bảng (JTable) ở giữa
        communityTableModel = new BookTableModel(); // Dùng Model đã tạo
        communityTable = new JTable(communityTableModel);
        
        // 2a. Tạo hành động (Action) cho nút "Tải về"
        Runnable communityAction = () -> {
            // Đây là code sẽ chạy khi nút "Tải về" được nhấn
            handleCommunityTableButtonClick();
        };

        // 2b. Lấy cột "Thao tác" (Cột thứ 3, index là 3)
        TableColumnModel columnModel = communityTable.getColumnModel();
        int actionColumnIndex = 3; 
        
        // 2c. Gắn Renderer (vẽ nút) và Editor (xử lý click)
        columnModel.getColumn(actionColumnIndex).setCellRenderer(new JTableButtonRenderer());
        columnModel.getColumn(actionColumnIndex).setCellEditor(new JTableButtonEditor(communityAction));

        // --- PHẦN CẬP NHẬT KẾT THÚC ---

        // Cho phép cuộn
        JScrollPane scrollPane = new JScrollPane(communityTable);
        

        // 3. Thêm vào panel chính
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Gắn sự kiện cho nút tìm kiếm
        communitySearchButton.addActionListener(e -> 
            loadCommunityBooks(communitySearchField.getText())
        );
        communitySearchField.addActionListener(e -> 
            loadCommunityBooks(communitySearchField.getText())
        );
        return panel;
    }
    

    private void handleCommunityTableButtonClick() {
        // Lấy hàng (row) mà người dùng đã click
        int selectedRow = communityTable.convertRowIndexToModel(communityTable.getEditingRow());
        
        // Lấy sách tại hàng đó
        Book selectedBook = communityTableModel.getBookAt(selectedRow);

        if (selectedBook != null) {
            System.out.println("Đang thêm sách: " + selectedBook.getTitle());
            
            // 3. Ủy quyền cho Controller (REQ 2 của bạn)
            boolean success = controller.handleAddBookToLibrary(selectedBook.getId());

            // 4. Phản hồi cho người dùng
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Đã thêm '" + selectedBook.getTitle() + "' vào thư viện.",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không thể thêm sách. Sách có thể đã có trong thư viện.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // --- Các hàm Tải Dữ liệu và Xử lý Sự kiện ---

    /**
     * Hàm nội bộ để chuyển View trong CardLayout
     */
    private void showView(String viewName) {
        if (viewName.equals("LIBRARY")) {
            // (Khi nhấn "Thư viện", cần tải dữ liệu N+1)
            loadMyLibraryBooks(null);
        } else if (viewName.equals("READING")) {
            // (Khi nhấn "Đang đọc", cần tải và lọc)
            // loadReadingData();
            loadReadingBooks(null);
        }
        mainCardLayout.show(mainContentPanel, viewName);
    }
    
    /**
     * Tải dữ liệu ban đầu khi mở MainGUI
     */
    private void loadInitialData() {
        // Tải dữ liệu cho tab "Cộng đồng"
        loadCommunityBooks(null);
        
        // Tải dữ liệu Thư viện cá nhân vào cache
        // Chạy trên một luồng (thread) riêng để không làm treo UI
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                controller.loadPersonalLibrary(); // Đây là hàm N+1
                return null;
            }
            @Override
            protected void done() {
                // Sau khi tải xong, cập nhật thống kê
                Map<String, Integer> stats = controller.getLibraryStats();
                System.out.println("Tải thư viện xong. Tổng số: " + stats.get("total"));
                // (Cập nhật các label thống kê...)
            }
        }.execute();
    }

    /**
     * Gọi Controller để lấy sách "Cộng đồng" và cập nhật bảng
     */
    private void loadCommunityBooks(String query) {
        // Lấy dữ liệu (View gọi Controller)
        List<Book> books = controller.getCommunityBooks(query);
        // Cập nhật Model của Bảng (Table Model)
        communityTableModel.setBooks(books);
    }
    
    /**
     * Xử lý Đăng xuất
     */
    private void handleLogout() {
        controller.handleLogout(); // Gọi Controller
        
        // Mở lại LoginGUI
        new LoginGUI().setVisible(true);
        // Đóng MainGUI
        this.dispose();
    }

    private JPanel createMyLibraryView() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(new EmptyBorder(10, 10, 10, 10));

    // 1. Panel tìm kiếm
    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    librarySearchField = new JTextField(30);
    librarySearchButton = new JButton("Tìm");
    searchPanel.add(new JLabel("Tìm trong thư viện của bạn:"));
    searchPanel.add(librarySearchField);
    searchPanel.add(librarySearchButton);

    // 2. Bảng (JTable)
    libraryTableModel = new LibraryTableModel(); // Dùng Model mới
    libraryTable = new JTable(libraryTableModel);
    
    // 2a. Tạo hành động (Action) cho nút "Đọc"
    Runnable libraryAction = () -> {
        // Đây là code sẽ chạy khi nút "Đọc" được nhấn
        handleLibraryTableButtonClick();
    };
    
    // 2b. Gắn Renderer và Editor (Dùng lại 2 lớp Helper)
    int actionColumnIndex = 3; 
    libraryTable.getColumnModel().getColumn(actionColumnIndex).setCellRenderer(new JTableButtonRenderer());
    libraryTable.getColumnModel().getColumn(actionColumnIndex).setCellEditor(new JTableButtonEditor(libraryAction));

    JScrollPane scrollPane = new JScrollPane(libraryTable);

    // 3. Thêm vào panel chính
    panel.add(searchPanel, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);
    
    // Gắn sự kiện cho nút tìm kiếm
    ActionListener searchAction = e -> 
        loadMyLibraryBooks(librarySearchField.getText());
    librarySearchButton.addActionListener(searchAction);
    librarySearchField.addActionListener(searchAction);

    return panel;
}

    /**
     * (MỚI) Tạo View cho tab "Đang đọc"
     */
    private JPanel createReadingView() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        readingSearchField = new JTextField(30);
        readingSearchButton = new JButton("Tìm");
        searchPanel.add(new JLabel("Tìm trong mục đang đọc:"));
        searchPanel.add(readingSearchField);
        searchPanel.add(readingSearchButton);

        // 2. Bảng (JTable)
        // Tái sử dụng LibraryTableModel vì nó cũng có cột "Đọc"
        readingTableModel = new LibraryTableModel(); 
        readingTable = new JTable(readingTableModel);
        
        // 2a. Tạo hành động (Action) cho nút "Đọc"
        Runnable readingAction = () -> {
            // Đây là code sẽ chạy khi nút "Đọc" được nhấn
            handleReadingTableButtonClick();
        };
        
        // 2b. Gắn Renderer và Editor (Dùng lại 2 lớp Helper)
        int actionColumnIndex = 3; 
        readingTable.getColumnModel().getColumn(actionColumnIndex).setCellRenderer(new JTableButtonRenderer());
        readingTable.getColumnModel().getColumn(actionColumnIndex).setCellEditor(new JTableButtonEditor(readingAction));

        JScrollPane scrollPane = new JScrollPane(readingTable);

        // 3. Thêm vào panel chính
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Gắn sự kiện cho nút tìm kiếm
        ActionListener searchAction = e -> 
            loadReadingBooks(readingSearchField.getText());
        readingSearchButton.addActionListener(searchAction);
        readingSearchField.addActionListener(searchAction);

        return panel;
    }

    /**
     * (MỚI) Xử lý sự kiện khi nút "Đọc" trong bảng "Đang đọc" được nhấn.
     */
    private void handleReadingTableButtonClick() {
        int selectedRow = readingTable.convertRowIndexToModel(readingTable.getEditingRow());
        
        // Lấy sách từ Model của bảng NÀY
        Book selectedBook = readingTableModel.getBookAt(selectedRow);

        if (selectedBook != null) {
            System.out.println("Đang mở sách: " + selectedBook.getTitle());
            
            // Ủy quyền cho Controller (REQ 2 của bạn)
            // Gọi cùng một hàm với tab "Thư viện"
            controller.handleReadBook(selectedBook);
            
            // (Tạm thời thông báo)
            JOptionPane.showMessageDialog(this,
                    "Đang mở trình đọc sách cho: " + selectedBook.getTitle(),
                    "Đang mở sách...",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * (MỚI) Gọi Controller để lấy sách "Đang đọc" và cập nhật bảng
     */
    private void loadReadingBooks(String query) {
        // Lấy dữ liệu (View gọi Controller)
        // Controller sẽ lọc từ cache (client-side)
        List<Book> books = controller.getReadingBooks(query); // <-- Gọi hàm getReadingBooks
        // Cập nhật Model của Bảng (Table Model)
        readingTableModel.setBooks(books);
    }
    /**
     * (MỚI) Xử lý sự kiện khi nút "Đọc" trong bảng Thư viện được nhấn.
     */
    private void handleLibraryTableButtonClick() {
        int selectedRow = libraryTable.convertRowIndexToModel(libraryTable.getEditingRow());
        
        // Lấy sách từ Model của bảng này
        Book selectedBook = libraryTableModel.getBookAt(selectedRow);

        if (selectedBook != null) {
            System.out.println("Đang mở sách: " + selectedBook.getTitle());
            
            // Ủy quyền cho Controller (REQ 2 của bạn)
            // (Đây là hàm chúng ta sẽ code ReadingGUI để xử lý)
            controller.handleReadBook(selectedBook);
            
            // (Tạm thời thông báo)
            JOptionPane.showMessageDialog(this,
                    "Đang mở trình đọc sách cho: " + selectedBook.getTitle(),
                    "Đang mở sách...",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * (MỚI) Gọi Controller để lấy sách "Thư viện" và cập nhật bảng
     */
    private void loadMyLibraryBooks(String query) {
        // Lấy dữ liệu (View gọi Controller)
        // Controller sẽ lọc từ cache (client-side)
        List<Book> books = controller.getMyLibraryBooks(query);
        // Cập nhật Model của Bảng (Table Model)
        libraryTableModel.setBooks(books);
    }
}