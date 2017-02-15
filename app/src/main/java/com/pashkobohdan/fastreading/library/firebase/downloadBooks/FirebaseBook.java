package com.pashkobohdan.fastreading.library.firebase.downloadBooks;

/**
 * Created by Bohdan Pashko on 15.02.17.
 */

public class FirebaseBook {
    private String bookName;
    private String bookAuthor;
    private String bookText;

    public FirebaseBook() {
    }

    public FirebaseBook(String bookName, String bookAuthor, String bookText) {
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
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

    public String getBookText() {
        return bookText;
    }

    public void setBookText(String bookText) {
        this.bookText = bookText;
    }

    @Override
    public String toString() {
        return "FirebaseBook{" +
                "bookName='" + bookName + '\'' +
                ", bookAuthor='" + bookAuthor + '\'' +
                ", bookText='" + bookText + '\'' +
                '}';
    }
}
