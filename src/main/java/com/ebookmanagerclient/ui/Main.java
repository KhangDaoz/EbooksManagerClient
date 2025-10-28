package com.ebookmanagerclient.ui;

import javax.swing.SwingUtilities;

/**
 * Lớp Main chứa điểm khởi đầu (entry point) của toàn bộ ứng dụng Client.
 * Nhiệm vụ duy nhất của nó là khởi chạy Giao diện Đăng nhập (LoginGUI)
 * trên luồng (Thread) an toàn của Swing.
 */
public class Main {

    /**
     * Phương thức main, được JVM (Máy ảo Java) gọi khi chạy.
     * @param args Các tham số dòng lệnh (không dùng ở đây)
     */
    public static void main(String[] args) {
        // Swing (thư viện UI) không an toàn về luồng (thread-safe).
        // Cách tốt nhất là luôn khởi chạy và cập nhật UI 
        // trên Luồng Xử lý Sự kiện (Event Dispatch Thread - EDT).
        // SwingUtilities.invokeLater() sẽ làm việc này cho chúng ta.
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Tạo một thể hiện (instance) của LoginGUI
                LoginGUI loginFrame = new LoginGUI();
                
                // Hiển thị cửa sổ LoginGUI
                loginFrame.setVisible(true);
            }
        });
    }
}