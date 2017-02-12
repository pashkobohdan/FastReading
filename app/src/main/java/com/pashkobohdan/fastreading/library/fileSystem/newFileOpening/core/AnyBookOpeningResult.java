package com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core;

import java.io.File;

/**
 * Created by Bohdan Pashko on 12.02.17.
 */

public class AnyBookOpeningResult {
    private String bookText;
    private String bookName;
    private String bookAuthor;
    private File outputFile;

    public AnyBookOpeningResult(String bookText, String bookName, String bookAuthor, File outputFile) {
        this.bookText = bookText;
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
        this.outputFile = outputFile;
    }

    public AnyBookOpeningResult(BookReadingResult bookReadingResult, File outputFile) {
        bookText = bookReadingResult.getBookText();
        bookName = bookReadingResult.getBookName();
        bookAuthor = bookReadingResult.getBookAuthor();
        this.outputFile = outputFile;
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

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }
}
