package com.ebookmanagerclient.ui;

import com.ebookmanagerclient.model.Book;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Model (Adapter) để "dạy" cho JTable (Thư viện của tôi) 
 * cách hiển thị List<Book>.
 */
public class LibraryTableModel extends AbstractTableModel {

    // Tên cột (giống hệt BookTableModel)
    private final String[] columnNames = {"Ảnh bìa", "Tên sách", "Tác giả", "Thao tác"};
    
    private List<Book> bookList;

    public LibraryTableModel() {
        this.bookList = new ArrayList<>();
    }

    /**
     * Cập nhật dữ liệu cho bảng.
     */
    public void setBooks(List<Book> bookList) {
        this.bookList = bookList;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return bookList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    /**
     * Lấy giá trị cho từng ô (cell).
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Book book = bookList.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return "Icon"; // (Tạm thời)
            case 1:
                return book.getTitle();
            case 2:
                return book.getAuthor();
            case 3:
                return "Đọc"; // <-- THAY ĐỔI CHÍNH (Nút "Đọc")
            default:
                return null;
        }
    }
    
    /**
     * Lấy đối tượng Book tại một hàng cụ thể.
     */
    public Book getBookAt(int rowIndex) {
        return bookList.get(rowIndex);
    }
}