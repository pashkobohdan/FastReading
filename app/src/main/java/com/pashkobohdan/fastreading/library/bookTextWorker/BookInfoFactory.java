package com.pashkobohdan.fastreading.library.bookTextWorker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import com.pashkobohdan.fastreading.library.fileSystem.file.InternalStorageFileHelper;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core.AnyBookOpeningResult;

import java.io.File;
import java.util.Date;
import java.util.Random;

import static com.pashkobohdan.fastreading.library.bookTextWorker.BookInfo.BOOKS_AUTHOR_PREFERENCE_NAME;
import static com.pashkobohdan.fastreading.library.bookTextWorker.BookInfo.BOOKS_COLOR_PREFERENCE_NAME;
import static com.pashkobohdan.fastreading.library.bookTextWorker.BookInfo.BOOKS_CURRENT_SPEED_PREFERENCE_NAME;
import static com.pashkobohdan.fastreading.library.bookTextWorker.BookInfo.BOOKS_LAST_OPEN_DATE_PREFERENCE_NAME;
import static com.pashkobohdan.fastreading.library.bookTextWorker.BookInfo.BOOKS_NAME_PREFERENCE_NAME;
import static com.pashkobohdan.fastreading.library.bookTextWorker.BookInfo.BOOKS_POSITION_PREFERENCE_NAME;

/**
 * Factory method for construction BookInfo by File and Activity (for get SharedPreference)
 * <p>
 * Created by Bohdan Pashko on 25.01.17.
 */

public class BookInfoFactory {

    public static BookInfo newInstance(File file, Activity activity) {
        BookInfo bookInfo = new BookInfo();

        bookInfo.setFile(file);
        bookInfo.setFileName(InternalStorageFileHelper.fileNameWithoutExtension(file));

        bookInfo.setBookNamesPreferences(activity.getSharedPreferences(BOOKS_NAME_PREFERENCE_NAME,
                Context.MODE_PRIVATE));
        bookInfo.setBookPositionsPreferences(activity.getSharedPreferences(BOOKS_POSITION_PREFERENCE_NAME,
                Context.MODE_PRIVATE));
        bookInfo.setBookAuthorsPreferences(activity.getSharedPreferences(BOOKS_AUTHOR_PREFERENCE_NAME,
                Context.MODE_PRIVATE));
        bookInfo.setBookColorsPreferences(activity.getSharedPreferences(BOOKS_COLOR_PREFERENCE_NAME,
                Context.MODE_PRIVATE));
        bookInfo.setBookSpeedsPreferences(activity.getSharedPreferences(BOOKS_CURRENT_SPEED_PREFERENCE_NAME,
                Context.MODE_PRIVATE));
        bookInfo.setBookLastOpenDatePreferences(activity.getSharedPreferences(BOOKS_LAST_OPEN_DATE_PREFERENCE_NAME,
                Context.MODE_PRIVATE));


        bookInfo.setName(bookInfo.getBookNamesPreferences().getString(bookInfo.getFileName(),bookInfo.getFileName()));
        bookInfo.setCurrentWordNumber(bookInfo.getBookPositionsPreferences().getInt(bookInfo.getFileName(), 0));
        bookInfo.setAuthor(bookInfo.getBookAuthorsPreferences().getString(bookInfo.getFileName(), "no author"));

        Random random = new Random(System.nanoTime());
        bookInfo.setColor(bookInfo.getBookColorsPreferences().getInt(bookInfo.getFileName(),
                Color.argb(255, random.nextInt(127) + 127, random.nextInt(127) + 127, random.nextInt(127) + 127)));

        bookInfo.setCurrentSpeed(bookInfo.getBookSpeedsPreferences().getInt(bookInfo.getFileName(), 80));
        bookInfo.setLastOpeningDate(bookInfo.getBookLastOpenDatePreferences().getInt(bookInfo.getFileName(), 0));


        return bookInfo;
    }

    public static BookInfo createNewInstance(AnyBookOpeningResult bookOpeningResult, Activity activity) {
        BookInfo bookInfo = new BookInfo();

        bookInfo.setFile(bookOpeningResult.getOutputFile());
        bookInfo.setFileName(InternalStorageFileHelper.fileNameWithoutExtension(bookOpeningResult.getOutputFile()));

        bookInfo.setBookNamesPreferences(activity.getSharedPreferences(BOOKS_NAME_PREFERENCE_NAME,
                Context.MODE_PRIVATE));
        bookInfo.setBookPositionsPreferences(activity.getSharedPreferences(BOOKS_POSITION_PREFERENCE_NAME,
                Context.MODE_PRIVATE));
        bookInfo.setBookAuthorsPreferences(activity.getSharedPreferences(BOOKS_AUTHOR_PREFERENCE_NAME,
                Context.MODE_PRIVATE));
        bookInfo.setBookColorsPreferences(activity.getSharedPreferences(BOOKS_COLOR_PREFERENCE_NAME,
                Context.MODE_PRIVATE));
        bookInfo.setBookSpeedsPreferences(activity.getSharedPreferences(BOOKS_CURRENT_SPEED_PREFERENCE_NAME,
                Context.MODE_PRIVATE));
        bookInfo.setBookLastOpenDatePreferences(activity.getSharedPreferences(BOOKS_LAST_OPEN_DATE_PREFERENCE_NAME,
                Context.MODE_PRIVATE));


        bookInfo.setName(bookOpeningResult.getBookName());
        bookInfo.setCurrentWordNumber(0);
        bookInfo.setAuthor(bookOpeningResult.getBookAuthor());

        Random random = new Random(System.nanoTime());
        bookInfo.setColor(Color.argb(255, random.nextInt(127) + 127, random.nextInt(127) + 127, random.nextInt(127) + 127));

        bookInfo.setCurrentSpeed(80);
        bookInfo.setLastOpeningDate(0);


        return bookInfo;
    }

}
