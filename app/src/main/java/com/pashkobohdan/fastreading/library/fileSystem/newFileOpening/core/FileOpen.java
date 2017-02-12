package com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core;

import android.support.annotation.NonNull;

import com.pashkobohdan.fastreading.library.fileSystem.file.core.PercentSender;

import java.io.File;

/**
 * Basic interface for opening new file and write to internal path of app
 *
 * Created by Bohdan pashko on 16.01.17.
 */

public interface FileOpen {

    /**
     * Reading file and writing to the internal path. Every percent - running percentSender to set new percent value
     *
     * @param file                 file, which will be open
     * @param readingPercentSender for sending current reading position
     * @param readingEndSender     for sending reading end event
     * @return null - if reading canceled or reading failure
     */
    BookReadingResult open(@NonNull File file, @NonNull PercentSender readingPercentSender, @NonNull Runnable readingEndSender);

}
