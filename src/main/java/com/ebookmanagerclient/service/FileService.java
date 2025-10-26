package com.ebookmanagerclient.service;

import com.ebookmanagerclient.api.ApiClient;
import com.ebookmanagerclient.api.ApiClient.ServiceType;
import com.ebookmanagerclient.model.Book;

import java.io.File;
import java.io.IOException;


public class FileService {
    
    private ApiClient apiClient;

    // Local folder to save downloaded file
    public static final String Download_dir = "downloaded_books";

    // Constructor
    public FileService()
    {
        this.apiClient = ApiClient.getInstance();

        File dir = new File(Download_dir);
        
        if(!dir.exists())
        {
            dir.mkdirs();
        }
    }

    // Download unexisted Book

    public String downloadBook(Book book) throws IOException
    {
        if(book==null || book.getFileUrl() == null 
        || book.getFileUrl().isEmpty())
        {
            throw new IOException("Book is invalid");
        }

        String localFileName = "book_" + Integer.toString(book.getId())
        + ".epub";

        String localFilePath = Download_dir + File.separator + localFileName;

        File localFile = new File(localFilePath);

        // Check if file is already on device
        if(localFile.exists())
        {
            System.out.println("Book existed");
            return localFilePath;
        }

        // Download file

        System.out.println("Downloading ...");

        apiClient.downloadFile(
            ServiceType.BOOK,
            book.getFileUrl(),
            localFilePath
        );

        System.out.println("Download suscessfully");
        return localFilePath;
    }
}
