package com.example.group1_projectwork;

public class Book {
    private String id;
    private String title;
    private String author;
    private String pdfUri;
    private String userId; // Added field to link the book to a specific user

    public Book(String id, String title, String author, String pdfUri, String userId) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.pdfUri = pdfUri;
        this.userId = userId;
    }

    // Getter methods
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPdfUri() {
        return pdfUri;
    }

    public String getUserId() {
        return userId;
    }

    // Setter methods
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPdfUri(String pdfUri) {
        this.pdfUri = pdfUri;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
