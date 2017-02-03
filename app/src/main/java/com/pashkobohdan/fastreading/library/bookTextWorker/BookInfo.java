package com.pashkobohdan.fastreading.library.bookTextWorker;

import android.content.SharedPreferences;

import com.pashkobohdan.fastreading.library.fileSystem.file.FileReadingAndWriting;
import com.pashkobohdan.fastreading.library.fileSystem.file.core.FileReadWrite;

import java.io.File;

/**
 * Created by Bohdan Pashko on 24.01.17.
 */

public class BookInfo {
    public static final String BOOKS_NAME_PREFERENCE_NAME = "books_positions";
    public static final String BOOKS_POSITION_PREFERENCE_NAME = "books_positions";
    public static final String BOOKS_AUTHOR_PREFERENCE_NAME = "books_authors";
    public static final String BOOKS_COLOR_PREFERENCE_NAME = "book_colors";

    private File file;

    private String name;
    private String author;
    private int color;
    private int wordsNumber;
    private int currentWordNumber = -1;


    /**
     * Sets dynamically (Thread helps).
     */
    private String[] words;
    private String allText;


    private SharedPreferences bookPositionsPreferences;
    private SharedPreferences.Editor positionEditor;

    private SharedPreferences bookAuthorsPreferences;
    private SharedPreferences.Editor authorEditor;

    private SharedPreferences bookColorsPreferences;
    private SharedPreferences.Editor colorEditor;

    private boolean wasRead;

    public BookInfo() {
    }

    public void readWords(final Runnable readingSuccess, final Runnable readingFailure) {
        new Thread(() -> {
            FileReadWrite fileReadWrite = new FileReadingAndWriting();
            String bookText = fileReadWrite.read(file, (o, n) -> {
            });

            if (bookText == null) {
                readingFailure.run();
            } else {
                allText = bookText;

                words = bookText.
                        trim().
                        replaceAll("\\s+", " ").
                        replaceAll("(\\.)+", "\\.").
                        split(" ");

                wordsNumber = words.length;

                readingSuccess.run();

                setWasRead(true);
            }

        }).start();
    }


    /**
     * Setters and getters
     */

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public int getWordsNumber() {
        return wordsNumber;
    }

    public void setWordsNumber(int wordsNumber) {
        this.wordsNumber = wordsNumber;
    }

    public int getCurrentWordNumber() {
        return currentWordNumber;
    }

    public String[] getWords() {
        return words;
    }

    public void setWords(String[] words) {
        this.words = words;
    }

    public SharedPreferences getBookPositionsPreferences() {
        return bookPositionsPreferences;
    }

    public void setBookPositionsPreferences(SharedPreferences bookPositionsPreferences) {
        this.bookPositionsPreferences = bookPositionsPreferences;
    }

    public SharedPreferences.Editor getPositionEditor() {
        return positionEditor;
    }

    public void setPositionEditor(SharedPreferences.Editor positionEditor) {
        this.positionEditor = positionEditor;
    }

    public SharedPreferences getBookAuthorsPreferences() {
        return bookAuthorsPreferences;
    }

    public void setBookAuthorsPreferences(SharedPreferences bookAuthorsPreferences) {
        this.bookAuthorsPreferences = bookAuthorsPreferences;
    }

    public SharedPreferences.Editor getAuthorEditor() {
        return authorEditor;
    }

    public void setAuthorEditor(SharedPreferences.Editor authorEditor) {
        this.authorEditor = authorEditor;
    }

    public SharedPreferences.Editor getColorEditor() {
        return colorEditor;
    }

    public void setColorEditor(SharedPreferences.Editor colorEditor) {
        this.colorEditor = colorEditor;
    }

    public int getColor() {
        return color;
    }

    public SharedPreferences getBookColorsPreferences() {
        return bookColorsPreferences;
    }

    public void setBookColorsPreferences(SharedPreferences bookColorsPreferences) {
        this.bookColorsPreferences = bookColorsPreferences;
    }

    public boolean isWasRead() {
        return wasRead;
    }

    public void setWasRead(boolean wasRead) {
        this.wasRead = wasRead;
    }

    /**
     * Setters and getters (when changes - write to SharedPreference)
     */

    public void setName(String name) {
        if(this.name != null && !name.equals(this.name)){
            this.name = name;

            setAuthor(author);
            setColor(color);
            setCurrentWordNumber(currentWordNumber);
        }

        this.name = name;
    }


    public void setAuthor(String author) {
        //if (this.author != null && !this.author.equals(author)) {
            bookAuthorsPreferences.edit().putString(name, author).apply();
        //}

        this.author = author;
    }

    public void setColor(int color) {
        //if (this.color != color) {
            bookColorsPreferences.edit().putInt(name, color).apply();
        //}

        this.color = color;
    }

    public void setCurrentWordNumber(int currentWordNumber) {
        //if (this.currentWordNumber != currentWordNumber) {
            bookPositionsPreferences.edit().putInt(name, currentWordNumber).apply();
        //}

        this.currentWordNumber = currentWordNumber;
    }

    public String getAllText() {
        return allText;
    }

    public void setAllText(String allText) {
        this.allText = allText;
    }
}
