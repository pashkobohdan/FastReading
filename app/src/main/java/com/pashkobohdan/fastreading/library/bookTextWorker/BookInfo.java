package com.pashkobohdan.fastreading.library.bookTextWorker;

import android.content.SharedPreferences;

import com.pashkobohdan.fastreading.library.fileSystem.fileReading.FileReadingAndWriting;
import com.pashkobohdan.fastreading.library.fileSystem.fileReading.core.FileReadWrite;

import java.io.File;
import java.util.Random;

/**
 * Created by bohdan on 24.01.17.
 */

public class BookInfo {

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


    private SharedPreferences bookPositionsPreferences;
    private SharedPreferences.Editor positionEditor;

    private SharedPreferences bookAuthorsPreferences;
    private SharedPreferences.Editor authorEditor;

    private SharedPreferences bookColorsPreferences;
    private SharedPreferences.Editor colorEditor;


    public void readWords(final Runnable readingSuccess, final Runnable readingFailure) {
        new Thread(() -> {
            FileReadWrite fileReadWrite = new FileReadingAndWriting();
            String bookText = fileReadWrite.read(file, (o, n) -> {});

            if (bookText == null) {
                readingFailure.run();
            } else {
                words = bookText.
                        trim().
                        replaceAll("\\s+", " ").
                        replaceAll("(\\.)+", "\\.").
                        split(" ");

                wordsNumber = words.length;

                readingSuccess.run();
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

    public void setName(String name) {
        this.name = name;
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


    /**
     * Setters and getters (when changes - write to SharedPreference)
     */

    public void setAuthor(String author) {
        if (this.author != null && !this.author.equals(author)) {
            bookAuthorsPreferences.edit().putString(name, author).apply();
        }

        this.author = author;
    }

    public void setColor(int color) {
        if (this.color != color) {
            getBookColorsPreferences().edit().putInt(name, color).apply();
        }

        this.color = color;
    }

    public void setCurrentWordNumber(int currentWordNumber) {
        if (this.currentWordNumber != currentWordNumber) {
            bookPositionsPreferences.edit().putInt(name, currentWordNumber).apply();
        }

        this.currentWordNumber = currentWordNumber;
    }
}
