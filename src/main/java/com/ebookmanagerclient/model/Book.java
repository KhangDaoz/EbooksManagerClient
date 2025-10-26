package com.ebookmanagerclient.model;

import com.google.gson.annotations.SerializedName;

public class Book {
    private int id;
    private String title;
    private String author;
    
    // Map java atributes with JSON keys
    @SerializedName("fileUrl")
    private String fileUrl;
    
    @SerializedName("publishDate") // name of attribute in JSON file
    private String publishDate;
    

    // Setters
    
    public void setId(int id) {
        this.id = id;
    }
    public void setTitle(String tittle) {
        this.title = tittle;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }
    
    // Getters
    
    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getAuthor() {
        return author;
    }
    public String getFileUrl() {
        return fileUrl;
    }
    public String getPublishDate() {
        return publishDate;
    }

}
