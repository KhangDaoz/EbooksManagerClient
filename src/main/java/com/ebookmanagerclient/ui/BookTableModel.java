package com.ebookmanagerclient.ui;

import com.ebookmanagerclient.model.Book;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Model (Adapter) để "dạy" cho JTable cách hiển thị List<Book>.
 */
public class BookTableModel extends AbstractTableModel {

    // Tên cột (sẽ hiển thị trên header của bảng)
    private final String[] columnNames = {"Ảnh bìa", "Tên sách", "Tác giả", "Thao tác"};
    
    // Nguồn dữ liệu
    private List<Book> bookList;

    public BookTableModel() {
        this.bookList = new ArrayList<>();
    }

    /**
     * Cập nhật dữ liệu cho bảng và thông báo cho JTable vẽ lại.
     */
    public void setBooks(List<Book> bookList) {
        this.bookList = bookList;
        // Báo cho JTable biết dữ liệu đã thay đổi để nó vẽ lại
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
     * Hàm quan trọng nhất: Lấy giá trị cho từng ô (cell).
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        // Lấy cuốn sách ở hàng (row) tương ứng
        Book book = bookList.get(rowIndex);

        // Trả về dữ liệu cho cột (column) tương ứng
        switch (columnIndex) {
            case 0:
                return "Icon"; // (Tạm thời, sau này sẽ là ImageIcon)
            case 1:
                return book.getTitle(); // Tên sách
            case 2:
                return book.getAuthor(); // Tác giả
            case 3:
                return "Tải về"; // Chữ trên nút
            default:
                return null;
        }
    }
    
    /**
     * Lấy đối tượng Book tại một hàng cụ thể.
     * (Rất quan trọng để biết sách nào được click)
     */
    public Book getBookAt(int rowIndex) {
        return bookList.get(rowIndex);
    }
}