package com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core;

/**
 * Created by Bohdan Pashko on 12.02.17.
 */

public class BookReadingResult {
    private String bookText;
    private String bookName;
    private String bookAuthor;

    public BookReadingResult(String bookText, String bookName, String bookAuthor) {
        this.bookText = bookText;
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
    }

    public String getBookText() {
        return bookText;
    }

    public void setBookText(String bookText) {
        this.bookText = bookText;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }
}
