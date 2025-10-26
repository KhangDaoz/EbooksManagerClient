package com.ebookmanagerclient.ui;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

/**
 * Lớp Renderer để "vẽ" một JButton bên trong một ô của JTable.
 */
public class JTableButtonRenderer extends JButton implements TableCellRenderer {

    public JTableButtonRenderer() {
        setOpaque(true); // Đảm bảo nút được vẽ đúng
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        // 'value' chính là text chúng ta trả về từ BookTableModel
        // (ví dụ: "Tải về" hoặc "Đọc")
        setText((value == null) ? "" : value.toString());
        
        return this;
    }
}