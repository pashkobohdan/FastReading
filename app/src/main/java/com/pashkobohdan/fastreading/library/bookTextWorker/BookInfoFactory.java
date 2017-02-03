package com.pashkobohdan.fastreading.library.bookTextWorker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import com.pashkobohdan.fastreading.library.fileSystem.file.InternalStorageFileHelper;

import java.io.File;
import java.util.Random;

import static com.pashkobohdan.fastreading.library.bookTextWorker.BookInfo.BOOKS_AUTHOR_PREFERENCE_NAME;
import static com.pashkobohdan.fastreading.library.bookTextWorker.BookInfo.BOOKS_COLOR_PREFERENCE_NAME;
import static com.pashkobohdan.fastreading.library.bookTextWorker.BookInfo.BOOKS_POSITION_PREFERENCE_NAME;

/**
 * Factory method for construction BookInfo by File and Activity (for get SharedPreference)
 *
 * Created by Bohdan Pashko on 25.01.17.
 */

public class BookInfoFactory {

    public static BookInfo newInstance(File file, Activity activity) {
        BookInfo bookInfo = new BookInfo();

        bookInfo.setFile(file);
        bookInfo.setName(InternalStorageFileHelper.fileNameWithoutExtension(file));

        bookInfo.setBookPositionsPreferences(activity.getSharedPreferences(BOOKS_POSITION_PREFERENCE_NAME,
                Context.MODE_PRIVATE));
        bookInfo.setBookAuthorsPreferences(activity.getSharedPreferences(BOOKS_AUTHOR_PREFERENCE_NAME,
                Context.MODE_PRIVATE));
        bookInfo.setBookColorsPreferences(activity.getSharedPreferences(BOOKS_COLOR_PREFERENCE_NAME,
                Context.MODE_PRIVATE));

        bookInfo.setCurrentWordNumber(bookInfo.getBookPositionsPreferences().getInt(bookInfo.getName(), 0));
        bookInfo.setAuthor(bookInfo.getBookAuthorsPreferences().getString(bookInfo.getName(), "no author"));

        Random random = new Random(System.nanoTime());
        bookInfo.setColor(bookInfo.getBookColorsPreferences().getInt(bookInfo.getName(),
                Color.argb(255, random.nextInt(127) + 127, random.nextInt(127) + 127, random.nextInt(127) + 127)));


        return bookInfo;
    }

}
