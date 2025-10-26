package com.ebookmanagerclient.service;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/*
 * openbook()
 * isBookOpen()
 * getTitle()
 * getAuthors()
 * getTableOfContent()
 * getContent()
 * getContentBySpineIndex()
 * closeBook()
 * resourceToString()
 */
public class EpubService implements BookInterfaceService {
    // Class Book comes from epublib, not Book class of user.
    private Book currentBook;

    //Constructor
    public EpubService()
    {
        this.currentBook = null;
    }

    // Open a book from local filePath
    @Override
    public void openBook(String filePath) throws IOException
    {
        EpubReader reader = new EpubReader();
        try(FileInputStream in = new FileInputStream(filePath))
        {
            this.currentBook = reader.readEpub(in);
        }
    }

    // Check if book   opened
    public boolean isBookOpen()
    {
        return this.currentBook==null;
    }

    @Override
    public String getTitle()
    {
        if(!isBookOpen())
        {
            return "No book open";
        }
        return currentBook.getTitle().toString();
    }

    @Override
    public List<String> getAuthors()
    {
        if(!isBookOpen())
        {
            return new ArrayList<>();
        }

        // return a list of authors' s name
        return this.currentBook.getMetadata().getAuthors().stream()
        .map(author -> author.getFirstname() + " " + author.getLastname())
        .toList();
    }

    // Take table of contents

    @Override
    public List<TOCReference> getTableOfContents()
    {
        if(!isBookOpen())
        {
            return new ArrayList<>();
        }

        return this.currentBook.getTableOfContents().getTocReferences();
    }

    // Take HTML content of certain chapter

    @Override
    public String getContent(TOCReference tocReference) throws IOException
    {
        if(!isBookOpen() || tocReference==null)
        {
            throw new IOException("This chapter is Blank!!!");
        }

        Resource resource = tocReference.getResource();
        return resourceToString(resource);
    }

    @Override
    public int getSpineSize() {
        if (!isBookOpen()) {
            return 0;
        }
        // Spine là thứ tự đọc tuyến tính
        return this.currentBook.getSpine().getSpineReferences().size();
    }

    // Take content of a Chapter based on order
    @Override
    public String getContentBySpineIndex(int spineIndex) throws IOException
    {
        if(!isBookOpen())
        {
            throw new IOException("Book is not open.");
        }

        List<SpineReference> spine = this.currentBook.getSpine().getSpineReferences();

        if(spineIndex<0 || spineIndex >= spine.size())
        {
            throw new IndexOutOfBoundsException("Invalid Index: "+ spineIndex);
        }

        SpineReference res = spine.get(spineIndex);
        return res.toString();
    }


    // Convert resource to String(HTML)

    private String resourceToString(Resource res) throws IOException
    {
        if(res == null)
        {
            return "<p> Cannot find content of chapter.</p>";
        }

        try(InputStream in = res.getInputStream())
        {
            byte[] data = in.readAllBytes();

            String encoding = res.getInputEncoding();

            if(encoding==null)
            {
                return new String(data, StandardCharsets.UTF_8);
            }

            return new String(data, encoding);
        }
    }

    @Override
    public void closeBook()
    {
        this.currentBook = null;
    }
    
}
