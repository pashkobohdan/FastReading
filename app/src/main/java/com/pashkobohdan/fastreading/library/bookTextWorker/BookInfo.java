package com.pashkobohdan.fastreading.library.bookTextWorker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;

import com.pashkobohdan.fastreading.library.fileSystem.fileReading.FileReadingAndWriting;
import com.pashkobohdan.fastreading.library.fileSystem.fileReading.InternalStorageFileHelper;
import com.pashkobohdan.fastreading.library.fileSystem.fileReading.core.FileReadWrite;

import java.io.File;
import java.util.Random;

/**
 * Created by bohdan on 24.01.17.
 */

public class BookInfo {
    public static final String BOOKS_POSITION_PREFERENCE_NAME = "books_positions";
    public static final String BOOKS_AUTHOR_PREFERENCE_NAME = "books_authors";
    public static final String BOOKS_COLOR_PREFERENCE_NAME = "book_colors";

    private File file;

    private String name;
    private String author;
    private int color;
    private int wordsNumber;
    private int currentWordNumber;


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

    /**
     * Recommended constructor.
     * Just set file of book and BookInfo is ready !
     *
     * @param file
     * @param context
     */
    public BookInfo(@NonNull final File file, @NonNull final Activity context) {
        this.setFile(file);
        setName(InternalStorageFileHelper.fileNameWithoutExtension(file));

        bookPositionsPreferences = context.getSharedPreferences(BOOKS_POSITION_PREFERENCE_NAME, Context.MODE_PRIVATE);
        bookAuthorsPreferences = context.getSharedPreferences(BOOKS_AUTHOR_PREFERENCE_NAME, Context.MODE_PRIVATE);
        bookColorsPreferences = context.getSharedPreferences(BOOKS_COLOR_PREFERENCE_NAME, Context.MODE_PRIVATE);

        currentWordNumber = bookPositionsPreferences.getInt(name, 0);
        author = bookAuthorsPreferences.getString(name, "no author");

        Random random = new Random(System.nanoTime());
        color = bookColorsPreferences.getInt(name, Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255)));

    }

    public void readWords(final Runnable readingSuccess, final Runnable readingFailure) {
        new Thread(() -> {
            FileReadWrite fileReadWrite = new FileReadingAndWriting();
            String bookText = fileReadWrite.read(file, (o, n) -> {
            });
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

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
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

    public void setCurrentWordNumber(int currentWordNumber) {
        this.currentWordNumber = currentWordNumber;
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
}
