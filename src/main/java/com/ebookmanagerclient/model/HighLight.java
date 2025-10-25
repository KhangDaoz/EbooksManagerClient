package com.ebookmanagerclient.model;

import com.google.gson.annotations.SerializedName;

public class HighLight {
    private int highlightId;
    private int userId;
    private int bookId;
    private int pageNumber;
    private int startPos;
    private int endPos;
    private String backgroundColor;
    private String noteContent;

    @SerializedName("locationCFI")
    private String location;

    // Constructor

    public HighLight(){}

    public HighLight(int highlightId, int userId, int bookId, int startPos, int endPos, String backgroundColor,
            String noteContent, String location) {
        this.highlightId = highlightId;
        this.userId = userId;
        this.bookId = bookId;
        this.startPos = startPos;
        this.endPos = endPos;
        this.backgroundColor = backgroundColor;
        this.noteContent = noteContent;
        this.location = location;
    }

    public int getHighlightId() {
        return highlightId;
    }

    public void setHighlightId(int highlightId) {
        this.highlightId = highlightId;
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

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    
}
