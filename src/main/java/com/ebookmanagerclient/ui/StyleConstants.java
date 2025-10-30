package com.ebookmanagerclient.ui;

import java.awt.Color;
import java.awt.Font;

// Lớp chứa các hằng số màu sắc và font chữ chung cho giao diện hiện đại
public class StyleConstants {
    // Màu sắc chủ đạo (Tông xanh dương, chuyên nghiệp)
    public static final Color PRIMARY_ACCENT_COLOR = new Color(30, 70, 150);     // Xanh dương đậm chủ đạo
    public static final Color SECONDARY_ACCENT_COLOR = new Color(0, 150, 255);   // Xanh dương sáng (cho nút)
    
    // Màu nền và văn bản
    public static final Color ACTIVE_BG_COLOR = new Color(230, 240, 255);        // Nền active/hover nhẹ
    public static final Color TEXT_COLOR = new Color(30, 30, 30);
    public static final Color SUBTLE_COLOR = new Color(100, 100, 100);          // Màu chữ mờ/chú thích
    public static final Color BORDER_COLOR = new Color(220, 220, 220); 
    public static final Color BG_WHITE = Color.WHITE;
    public static final Color SECONDARY_COLOR = new Color(250, 250, 250);      // Màu nền form nhạt
    
    // Font chữ
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 28);  
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 36);   
    public static final Font DEFAULT_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font AUTHOR_BOOK_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font TITLE_BOOK_FONT = new Font("Segoe UI", Font.BOLD, 14);
    
    // Hữu ích cho việc sử dụng màu trong HTML (JLabel)
    public static String toHtmlColor(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}