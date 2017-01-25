package com.pashkobohdan.fastreading.library.fileSystem.newFileOpeningThread;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.pashkobohdan.fastreading.library.fileSystem.fileReading.core.PercentSender;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.AnyFileOpening;

import java.io.File;

/**
 * Created by Bohdan Pashko on 23.01.17.
 */

public class FileOpenThread extends Thread {

    public FileOpenThread(@NonNull final File file, @NonNull final Activity activity,
                          @NonNull PercentSender readingPercentSender, @NonNull Runnable readingEnd,
                          @NonNull PercentSender writingPercentSender, @NonNull Runnable writingEnd,
                          @NonNull FileOpenResultSender openResultSender) {
        super(() -> {
            File outputFile = AnyFileOpening.open(file, activity,
                    (oldValue, neValue) -> activity.runOnUiThread(() -> readingPercentSender.refreshPercents(oldValue, neValue)),
                    () -> activity.runOnUiThread(readingEnd),
                    (oldValue, neValue) -> activity.runOnUiThread(() -> writingPercentSender.refreshPercents(oldValue, neValue)),
                    () -> activity.runOnUiThread(writingEnd));

            activity.runOnUiThread(()->openResultSender.send(outputFile));
        });
    }

}
