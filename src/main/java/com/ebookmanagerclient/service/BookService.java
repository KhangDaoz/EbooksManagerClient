package com.ebookmanagerclient.service;

import com.ebookmanagerclient.api.ApiClient;
import com.ebookmanagerclient.api.ApiClient.ServiceType;
import com.ebookmanagerclient.model.Book;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.File;
import java.util.Map;

/*
 * Contact with BookHandler of Server
 */
public class BookService {
    private ApiClient apiClient;

    public BookService()
    {
        this.apiClient = ApiClient.getInstance();
    }

    // Take all Books in common library

    public Book[] getAllBooks() throws IOException
    {

        return apiClient.get(
            ServiceType.BOOK, 
            "/api/books",
            Book[].class);
    };

    // Take specific information of certain book

    public Book getBookDetails(int bookId) throws IOException
    {
        return apiClient.get(
            ServiceType.BOOK,
            "/api/books"  + bookId,
            Book.class
        );
    }

    public Book updateBookDetails(int bookId, Book updatedBook) 
    throws IOException
    {
        return apiClient.put(
            ServiceType.BOOK,
            "/api/books/" + bookId,
            updatedBook,
            Book.class
        );
    }

    public void deleteBook(int bookId) throws IOException
    {
        apiClient.delete(
            ServiceType.BOOK,
            "api/books/" + bookId, null,
            Object.class
        );
    }

    public Book uploadBook(File file, String title, String authorName, 
    String publishDate) throws IOException
    {
        Map<String, String> metadata = Map.of(
            "bookTitle", title,
            "authorName", authorName,
            "publishDate", publishDate
        );

        return apiClient.upload(
            ServiceType.BOOK,
            "/api/books",
            file,
            metadata,
            Book.class
        );
    }

    // Update: Finding books
    public Book[] searchCommunityBooks(String query) throws IOException {
        String endpoint = "/api/books"; // Endpoint cơ bản

        // Nếu có từ khóa tìm kiếm, thêm nó vào URL
        if(query != null && !query.trim().isEmpty()) 
        {
            // Mã hóa từ khóa để đảm bảo URL hợp lệ (ví dụ: "Lập Trình" -> "L%E1%BA%ADp+Tr%C3%ACnh")
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            endpoint += "?q=" + encodedQuery;
        } 

        return apiClient.get(
            ServiceType.BOOK, // Gọi đến BookHandler (Cổng 8081)
            endpoint,
            Book[].class
        );
    }
}
