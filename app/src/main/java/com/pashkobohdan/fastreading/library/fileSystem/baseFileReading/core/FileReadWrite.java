package com.pashkobohdan.fastreading.library.fileSystem.baseFileReading.core;

import android.support.annotation.NonNull;

import java.io.File;

/**
 * Basic IO interface
 *
 * Created by Bohdan Pashko on 16.01.17.
 */

public interface FileReadWrite {

    /**
     *  Reading file. Every percent - running percentSender to set new percent value
     *
     * @param file file, which will be open
     * @param percentSender for sending current reading position
     * @return null - if reading canceled or reading failure
     */
    String read(@NonNull File file, @NonNull PercentSender percentSender);



    /**
     *  Reading file. Every percent - running percentSender to set new percent value
     *
     * @param file file, which will be open
     * @param percentSender for sending current reading position
     * @return null - if reading canceled or reading failure
     */
    String read(@NonNull File file, @NonNull PercentSender percentSender, @NonNull String charset);



    /**
     *  Writing file. Every percent - running percentSender to set new percent value
     *
     * @param file information will be recorded to this file
     * @param percentSender for sending current position of writing
     * @return one of file writing results (FileWriteResult enum)
     */
    FileWriteResult write (@NonNull File file, @NonNull String text, @NonNull PercentSender percentSender);
}
