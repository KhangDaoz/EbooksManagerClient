package com.ebookmanagerclient.model;

import com.google.gson.annotations.SerializedName;

public class HighLight {
    private int highlightId;
    private int userId;
    private int bookId;
    private int pageNumber;
    private int startPos;
    private int endPos;


    @SerializedName("locationCFI")
    private String locationCFI;

    @SerializedName("highlightedContent")
    private String highlightedContent;

    @SerializedName("noteContent")
    private String noteContent;

    @SerializedName("backgroundColor")
    private String backgroundColor;

    // Constructor

    public HighLight(){}

    public HighLight(String locationCFI, String highlightContent, 
    String noteContent, String backgroundColor) {
        
        this.highlightedContent = highlightContent;
        this.backgroundColor = backgroundColor;
        this.noteContent = noteContent;
        this.locationCFI = locationCFI;
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

    public String getLocationCFI() {
        return locationCFI;
    }

    public void setLocationCFI(String locationCFI) {
        this.locationCFI = locationCFI;
    }

    public String getHighlightedContent() {
        return highlightedContent;
    }

    public void setHighlightedContent(String highlightedContent) {
        this.highlightedContent = highlightedContent;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    
}
