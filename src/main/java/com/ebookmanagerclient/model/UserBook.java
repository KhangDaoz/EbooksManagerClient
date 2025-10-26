package com.ebookmanagerclient.model;

import com.google.gson.annotations.SerializedName;

public class UserBook {
    private int id;

    //private Book book;

    @SerializedName("user_id") 
    private int userId;

    @SerializedName("book_id")
    private int bookId;
    
    @SerializedName("reading_progress")
    private double progress;
    
    @SerializedName("date_added")
    private String dateAdded;
    
    public UserBook() {
    }

    public UserBook(int id, int userId, int bookId, float readingProgress) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.progress = readingProgress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    

    // public Book getBook() {
    //     return book;
    // }

    // public void setBook(Book book) {
    //     this.book = book;
    // }
}
