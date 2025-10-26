package com.ebookmanagerclient.service;

import com.ebookmanagerclient.api.ApiClient;
import com.ebookmanagerclient.api.ApiClient.ServiceType;
import com.ebookmanagerclient.model.UserBook;  
import java.io.IOException;
import java.util.Map; 

/*
 * Contact with UserBookHandler
 */

public class UserBookService {
    private ApiClient apiClient;

    public UserBookService()
    {
        this.apiClient = ApiClient.getInstance();
    }

    public UserBook[] getPersonalLibrary() throws IOException
    {
        return apiClient.get(
            ServiceType.USERBOOK,
            "/api/users/books",
            UserBook[].class 
        );
    }

    public UserBook updateReadingProgress(int bookId, float progress)
    throws IOException
    {
        Map<String, Float> requestBody = Map.of(
            "readingProgress", progress
        );

        return apiClient.put(
            ServiceType.USERBOOK,
            "/api/users/books/" + bookId + "/progress",
            requestBody,
            UserBook.class
        );
    }

    public void removeBookFromLibrary(int bookId) throws IOException
    {
        apiClient.delete(
            ServiceType.USERBOOK,
            "api/users/books/" + bookId,
            Object.class
        );
    }

}
