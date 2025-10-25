package com.ebookmanagerclient.model;

import com.google.gson.annotations.SerializedName;

public class UserBook {
    private int id;
    private int userId;
    private int bookId;
    private Book book;
    @SerializedName("readingProgress")
    private float readingProgress;

    public UserBook() {
    }

    public UserBook(int id, int userId, int bookId, float readingProgress) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.readingProgress = readingProgress;
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

    public float getReadingProgress() {
        return readingProgress;
    }

    public void setReadingProgress(float readingProgress) {
        this.readingProgress = readingProgress;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
