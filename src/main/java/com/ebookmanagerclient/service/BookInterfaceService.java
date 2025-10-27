package com.ebookmanagerclient.service;

import java.io.IOException;
import java.util.List;

import nl.siegmann.epublib.domain.TOCReference;

public interface BookInterfaceService {

    // Open a book from local filePath
    void openBook(String filePath) throws IOException;

    String getTitle();

    List<String> getAuthors();

    List<?> getTableOfContents();

    Object getContent(Object chapterRef) throws IOException;

    void closeBook();

    int getSpineSize();

    Object getContentBySpineIndex(int spineIndex) throws IOException;

}