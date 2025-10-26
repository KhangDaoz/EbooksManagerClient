package com.ebookmanagerclient.ui;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Lớp Editor để xử lý sự kiện click chuột vào JButton trong JTable.
 */
public class JTableButtonEditor extends DefaultCellEditor {

    private JButton button;
    private String label;
    private boolean isPushed;
    private int selectedRow;
    private JTable table;
    
    // Runnable là một "hành động" (Action)
    // mà chúng ta sẽ truyền vào từ MainGUI
    private Runnable action; 

    public JTableButtonEditor(Runnable action) {
        // Constructor, dùng JCheckBox rỗng là một mẹo phổ biến
        super(new JCheckBox()); 
        this.action = action;
        
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Dừng việc chỉnh sửa ô (editing)
                fireEditingStopped();
                // Thực thi hành động
                if (action != null) {
                    action.run();
                }
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        this.table = table;
        this.selectedRow = row;
        
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        // Khi nút được nhấn, "hành động" sẽ được thực thi
        isPushed = false;
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }
    
    /**
     * Lấy hàng (row) đang được chọn.
     * MainGUI sẽ gọi hàm này để biết sách nào được click.
     */
    public int getSelectedRow() {
        return selectedRow;
    }
}