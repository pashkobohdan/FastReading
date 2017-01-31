package com.pashkobohdan.fastreading.library.fileSystem.newFileOpeningThread;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.pashkobohdan.fastreading.library.fileSystem.fileReading.core.PercentSender;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.AnyFileOpening;

import java.io.File;

/**
 * Opens file by thread.
 * Use runOnUiThread !- it's not implemented
 *
 * Created by Bohdan Pashko on 23.01.17.
 */
public class FileOpenThread extends Thread {

    public FileOpenThread(@NonNull final File file, @NonNull final Activity activity,
                          @NonNull PercentSender readingPercentSender, @NonNull Runnable readingEnd,
                          @NonNull PercentSender writingPercentSender, @NonNull Runnable writingEnd,
                          @NonNull FileOpenResultSender openResultSender) {
        super(() -> {
            File outputFile = AnyFileOpening.open(file, activity,
                    readingPercentSender,
                    readingEnd,
                    writingPercentSender,
                    writingEnd);

            openResultSender.send(outputFile);
        });
    }

}
