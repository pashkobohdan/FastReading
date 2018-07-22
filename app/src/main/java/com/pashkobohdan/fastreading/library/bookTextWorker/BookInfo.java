package com.pashkobohdan.fastreading.library.bookTextWorker;

import android.content.SharedPreferences;

import com.pashkobohdan.fastreading.library.fileSystem.file.FileReadingAndWriting;
import com.pashkobohdan.fastreading.library.fileSystem.file.core.FileReadWrite;

import java.io.File;

/**
 * Created by Bohdan Pashko on 24.01.17.
 */

public class BookInfo {
    public static final String BOOKS_NAME_PREFERENCE_NAME = "books_names";
    public static final String BOOKS_POSITION_PREFERENCE_NAME = "books_positions";
    public static final String BOOKS_AUTHOR_PREFERENCE_NAME = "books_authors";
    public static final String BOOKS_COLOR_PREFERENCE_NAME = "book_colors";
    public static final String BOOKS_CURRENT_SPEED_PREFERENCE_NAME = "book_speeds";
    public static final String BOOKS_LAST_OPEN_DATE_PREFERENCE_NAME = "book_last_open_date";

    private File file;
    private String fileName;

    private String name;
    private String author;
    private int color;
    private int currentWordNumber;
    private int currentSpeed;

    private int lastOpeningDate;

    /**
     * Sets dynamically (Thread helps).
     */
    private String[] words;
    private String allText;


    private SharedPreferences bookNamesPreferences;
    private SharedPreferences bookPositionsPreferences;
    private SharedPreferences bookAuthorsPreferences;
    private SharedPreferences bookColorsPreferences;
    private SharedPreferences bookSpeedsPreferences;
    private SharedPreferences bookLastOpenDatePreferences;

    private boolean wasRead;

    public BookInfo() {
    }

    @Deprecated
    public void readWords(final Runnable readingSuccess, final Runnable readingFailure) {
        new Thread(() -> {
            try {
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

                    readingSuccess.run();

                    setWasRead(true);
                }
            }catch (Exception e){
                e.printStackTrace();

                readingFailure.run();
            }
        }).start();
    }


    /**
     * Setters and getters
     */

    @Deprecated
    public File getFile() {
        return file;
    }

    @Deprecated
    public void setFile(File file) {
        this.file = file;
    }

    @Deprecated
    public String getName() {
        return name;
    }

    @Deprecated
    public String getAuthor() {
        return author;
    }

    @Deprecated
    public int getCurrentWordNumber() {
        return currentWordNumber;
    }

    @Deprecated
    public String[] getWords() {
        return words;
    }

    @Deprecated
    public void setWords(String[] words) {
        this.words = words;
    }

    @Deprecated
    public SharedPreferences getBookPositionsPreferences() {
        return bookPositionsPreferences;
    }

    @Deprecated
    public void setBookPositionsPreferences(SharedPreferences bookPositionsPreferences) {
        this.bookPositionsPreferences = bookPositionsPreferences;
    }


    @Deprecated
    public SharedPreferences getBookAuthorsPreferences() {
        return bookAuthorsPreferences;
    }

    @Deprecated
    public void setBookAuthorsPreferences(SharedPreferences bookAuthorsPreferences) {
        this.bookAuthorsPreferences = bookAuthorsPreferences;
    }

    @Deprecated
    public int getColor() {
        return color;
    }

    @Deprecated
    public SharedPreferences getBookColorsPreferences() {
        return bookColorsPreferences;
    }

    @Deprecated
    public void setBookColorsPreferences(SharedPreferences bookColorsPreferences) {
        this.bookColorsPreferences = bookColorsPreferences;
    }

    @Deprecated
    public boolean isWasRead() {
        return wasRead;
    }

    @Deprecated
    public void setWasRead(boolean wasRead) {
        this.wasRead = wasRead;
    }


    @Deprecated
    public String getFileName() {
        return fileName;
    }

    @Deprecated
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    @Deprecated
    public String getAllText() {
        return allText;
    }

    @Deprecated
    public void setAllText(String allText) {
        this.allText = allText;
    }

    @Deprecated
    public SharedPreferences getBookNamesPreferences() {
        return bookNamesPreferences;
    }

    @Deprecated
    public void setBookNamesPreferences(SharedPreferences bookNamesPreferences) {
        this.bookNamesPreferences = bookNamesPreferences;
    }

    @Deprecated
    public SharedPreferences getBookSpeedsPreferences() {
        return bookSpeedsPreferences;
    }

    @Deprecated
    public void setBookSpeedsPreferences(SharedPreferences bookSpeedsPreferences) {
        this.bookSpeedsPreferences = bookSpeedsPreferences;
    }

    @Deprecated
    public int getCurrentSpeed() {
        return currentSpeed;
    }


    @Deprecated
    public int getLastOpeningDate() {
        return lastOpeningDate;
    }


    @Deprecated
    public SharedPreferences getBookLastOpenDatePreferences() {
        return bookLastOpenDatePreferences;
    }

    @Deprecated
    public void setBookLastOpenDatePreferences(SharedPreferences bookLastOpenDatePreferences) {
        this.bookLastOpenDatePreferences = bookLastOpenDatePreferences;
    }
    /**
     * Setters and getters (when changes - write to SharedPreference)
     */

    @Deprecated
    public void setName(String name) {
        if (this.name == null || (name != null && !name.equals(this.name))) {
            bookNamesPreferences.edit().putString(fileName, name).apply();
        }

        this.name = name;
    }


    @Deprecated
    public void setAuthor(String author) {
        bookAuthorsPreferences.edit().putString(fileName, author).apply();

        this.author = author;
    }

    @Deprecated
    public void setColor(int color) {
        bookColorsPreferences.edit().putInt(fileName, color).apply();

        this.color = color;
    }

    @Deprecated
    public void setCurrentWordNumber(int currentWordNumber) {
        bookPositionsPreferences.edit().putInt(fileName, currentWordNumber).apply();

        this.currentWordNumber = currentWordNumber;
    }

    @Deprecated
    public void setCurrentSpeed(int currentSpeed) {
        bookSpeedsPreferences.edit().putInt(fileName, currentSpeed).apply();

        this.currentSpeed = currentSpeed;
    }

    @Deprecated
    public void setLastOpeningDate(int lastOpeningDate) {
        bookLastOpenDatePreferences.edit().putInt(fileName, lastOpeningDate).apply();

        this.lastOpeningDate = lastOpeningDate;
    }

}
