package com.ebookmanagerclient.ui;

import com.ebookmanagerclient.controller.ReadingController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform; // Cho zoom
import java.awt.image.BufferedImage;

/**
 * Lớp Giao diện (View) cho Cửa sổ Đọc sách PDF.
 * Hiển thị trang PDF dưới dạng ảnh.
 */
public class PdfGUI extends JFrame {

    private final ReadingController controller;

    // Thành phần UI
    private ImagePanel imagePanel; // Panel tùy chỉnh để vẽ ảnh
    private JScrollPane scrollPane;
    private JToolBar toolBar;
    private JButton prevButton;
    private JButton nextButton;
    private JLabel statusLabel;

    // Trạng thái zoom
    private double currentZoom = 1.0; // 1.0 = 100%
    private boolean initialZoomCalculated = false;
    /**
     * Constructor
     * @param controller Thể hiện ReadingController
     */
    public PdfGUI(ReadingController controller) {
        this.controller = controller;

        setTitle("Trình đọc PDF"); // Có thể cập nhật sau
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        initLayout();
        initListeners();

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e)
            {
                if(!initialZoomCalculated && imagePanel.currentImage!=null)
                {
                    calculateAndApplyFitHeightZoom();
                    initialZoomCalculated = true;
                }
            }
            @Override
            public void componentShown(ComponentEvent e) {
                // Đảm bảo tính toán khi cửa sổ hiện ra lần đầu
                if (!initialZoomCalculated && imagePanel.currentImage != null) {
                    calculateAndApplyFitHeightZoom();
                    initialZoomCalculated = true;
                }
            }
        });

        loadInitialContent();


    }
    public void calculateAndApplyFitHeightZoom()
    {
        if (imagePanel.currentImage == null) return;

        // Lấy kích thước vùng hiển thị của JScrollPane
        Dimension viewSize = scrollPane.getViewport().getViewSize();
        int availableHeight = viewSize.height;
        if (availableHeight <= 0) return; // Chưa có kích thước

        // Lấy chiều cao gốc của ảnh
        int imageHeight = imagePanel.currentImage.getHeight();
        if (imageHeight <= 0) return;

        // Tính tỉ lệ zoom cần thiết
        // Trừ đi một chút lề (padding) nếu muốn
        double zoomFactor = (double) availableHeight / imageHeight;

        // Đặt mức zoom mới và vẽ lại
        this.currentZoom = zoomFactor;
        imagePanel.setImage(imagePanel.currentImage); // Gọi lại để cập nhật preferredSize và repaint
        System.out.println("Applied Fit Height Zoom: " + currentZoom); // Log để kiểm tra
    }

    private void initComponents() {
        // --- Panel hiển thị ảnh ---
        imagePanel = new ImagePanel();
        scrollPane = new JScrollPane(imagePanel);

        // --- Thanh công cụ ---
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        prevButton = new JButton("Trang trước");
        nextButton = new JButton("Trang sau");
        statusLabel = new JLabel(" (Đang tải...) ");
        toolBar.add(prevButton);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(statusLabel);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(nextButton);
    }

    private void initLayout() {
        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initListeners() {
        // --- Nút lật trang ---
        nextButton.addActionListener(e -> handleNextPage());
        prevButton.addActionListener(e -> handlePrevPage());

        // --- Phím (Lật trang) ---
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
        this.setFocusable(true);
        this.requestFocusInWindow();

        // --- Cuộn chuột (Lật trang VÀ Zoom) ---
        scrollPane.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown()) { // Zoom
                    if (e.getWheelRotation() < 0) handleZoomIn();
                    else handleZoomOut();
                } else { // Lật trang
                    // Cho phép cuộn dọc trong trang trước khi lật
                    JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                    if (e.getWheelRotation() > 0) { // Cuộn xuống
                        // Nếu đã ở cuối trang thì mới lật trang
                        if (verticalScrollBar.getValue() == verticalScrollBar.getMaximum() - verticalScrollBar.getVisibleAmount()) {
                            handleNextPage();
                        }
                    } else { // Cuộn lên
                        // Nếu đã ở đầu trang thì mới lật trang
                        if (verticalScrollBar.getValue() == verticalScrollBar.getMinimum()) {
                            handlePrevPage();
                        }
                    }
                }
            }
        });

        // --- Đóng cửa sổ ---
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.handleWindowClose();
            }
        });
    }

    // --- Tải nội dung ---
    private void loadInitialContent() {
        Object content = controller.getInitialContent();
        if (content instanceof BufferedImage) {
            //displayPageImage((BufferedImage) content, controller.getCurrentPageIndex(), controller.getTotalPages());
        
            BufferedImage initialImage = (BufferedImage) content;
            imagePanel.setImage(initialImage);
            statusLabel.setText("Trang "
            + (controller.getCurrentPageIndex()+1) + "/"
            + (controller.getTotalPages()));
        
        } else if (content instanceof String) {
            // Hiển thị lỗi nếu Controller trả về String
            showError((String) content);
        }
        // (Tải highlight PDF nếu có - logic phức tạp hơn)
    }

    // --- Các hàm gọi Controller ---
    private void handleNextPage() {
        Object content = controller.getNextPageContent();
        if (content instanceof BufferedImage) {
            displayPageImage((BufferedImage) content, controller.getCurrentPageIndex(), controller.getTotalPages());
        } else if (content instanceof String) {
            showError((String) content);
        }
        // Nếu content là null (đã ở trang cuối), không làm gì cả
    }

    private void handlePrevPage() {
        Object content = controller.getPreviousPageContent();
        if (content instanceof BufferedImage) {
            displayPageImage((BufferedImage) content, controller.getCurrentPageIndex(), controller.getTotalPages());
        } else if (content instanceof String) {
            showError((String) content);
        }
        // Nếu content là null (đã ở trang đầu), không làm gì cả
    }

    private void handleZoomIn() {
        currentZoom += 0.1; // Tăng zoom 10%
        imagePanel.repaint(); // Yêu cầu ImagePanel vẽ lại với zoom mới
    }

    private void handleZoomOut() {
        if (currentZoom > 0.2) { // Giới hạn zoom tối thiểu
            currentZoom -= 0.1;
            imagePanel.repaint();
        }
    }

    // --- Hàm cập nhật UI ---

    /**
     * (PUBLIC) Hiển thị ảnh trang PDF mới.
     * Được gọi bởi Controller hoặc các hàm xử lý sự kiện.
     */
    public void displayPageImage(BufferedImage image, int pageIndex, int totalPages) {
        imagePanel.setImage(image); // Đặt ảnh mới cho ImagePanel
        statusLabel.setText("Trang " + (pageIndex + 1) + " / " + totalPages);
        // Cuộn lên đầu trang mới
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }

    /**
     * Hiển thị thông báo lỗi (ví dụ: khi không tải được trang)
     */
    private void showError(String errorMessage) {
         JOptionPane.showMessageDialog(this, errorMessage, "Lỗi đọc PDF", JOptionPane.ERROR_MESSAGE);
         // Có thể hiển thị một ảnh báo lỗi trên imagePanel
         imagePanel.setImage(null); // Xóa ảnh cũ
         statusLabel.setText("Lỗi");
    }

    // --- Lớp Nội bộ (Inner Class) để vẽ ảnh ---
    // --- Lớp Nội bộ (Inner Class) để vẽ ảnh (ĐÃ CẬP NHẬT) ---
    private class ImagePanel extends JPanel {
        private BufferedImage currentImage;

        public ImagePanel() {
            // Đặt màu nền để dễ nhìn thấy lề (tùy chọn)
            setBackground(Color.LIGHT_GRAY);
        }

        public void setImage(BufferedImage img) {
            this.currentImage = img;
            revalidate(); // Cập nhật layout khi ảnh thay đổi
            repaint();    // Vẽ lại
        }

        /**
         * Tính toán kích thước panel mong muốn dựa trên ảnh và zoom.
         */
        @Override
        public Dimension getPreferredSize() {
            if (currentImage == null) {
                return new Dimension(400, 400); // Kích thước mặc định khi lỗi
            }
            // Kích thước mong muốn là kích thước ảnh đã được zoom
            int zoomedWidth = (int) (currentImage.getWidth() * currentZoom);
            int zoomedHeight = (int) (currentImage.getHeight() * currentZoom);
            return new Dimension(zoomedWidth, zoomedHeight);
        }

        /**
         * Vẽ ảnh vào TRUNG TÂM panel và áp dụng zoom.
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Vẽ nền JPanel trước
            if (currentImage != null) {
                Graphics2D g2d = (Graphics2D) g.create();

                // Kích thước của panel (vùng vẽ có thể nhìn thấy)
                int panelWidth = getWidth();
                int panelHeight = getHeight();

                // Kích thước của ảnh sau khi zoom
                int imageWidth = currentImage.getWidth();
                int imageHeight = currentImage.getHeight();
                int zoomedWidth = (int) (imageWidth * currentZoom);
                int zoomedHeight = (int) (imageHeight * currentZoom);

                // Tính toán tọa độ (x, y) để vẽ ảnh vào TRUNG TÂM
                int x = (panelWidth - zoomedWidth) / 2;
                int y = (panelHeight - zoomedHeight) / 2;

                // Đảm bảo x, y không âm (nếu ảnh zoom lớn hơn panel)
                x = Math.max(0, x);
                y = Math.max(0, y);

                // Tạo phép biến đổi: Di chuyển đến vị trí (x, y) VÀ scale (zoom)
                AffineTransform at = AffineTransform.getTranslateInstance(x, y);
                at.scale(currentZoom, currentZoom);

                // Vẽ ảnh đã biến đổi (di chuyển và zoom)
                g2d.drawRenderedImage(currentImage, at);

                g2d.dispose();
            } else {
                g.setColor(Color.RED);
                g.drawString("Không thể hiển thị trang PDF", 50, 50);
            }
        }
    } // --- Kết thúc lớp ImagePanel ---
}