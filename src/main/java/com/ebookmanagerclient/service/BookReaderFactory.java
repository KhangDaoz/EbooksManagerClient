package com.ebookmanagerclient.service;

import java.io.IOError;
import java.io.IOException;
import java.io.File;
/*
 * Create BookInterfaceService based on type of file
 */
public class BookReaderFactory {
    public static BookInterfaceService getReader(String filePath)
    throws IOException
    {
        if(filePath == null || filePath.isEmpty())
        {
            throw new IOException("FilePath is blank");
        }

        String extension = "";
        int i = filePath.lastIndexOf('.');
        if (i > 0) {
            extension = filePath.substring(i + 1).toLowerCase();
        }

        // 2. Quyết định (Factory Logic)
        switch (extension) {
            case "epub":
                return  new EpubService();
                
            case "pdf":
                // (TƯƠNG LAI)
                return new PdfService(); 
                //throw new IllegalArgumentException("Định dạng PDF chưa được hỗ trợ.");

            default:
                throw new IllegalArgumentException("Định dạng file không được hỗ trợ: " + extension);
        }
    }
}   
