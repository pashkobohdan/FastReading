package com.pashkobohdan.fastreading.library.fileSystem.newFileOpeningThread;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.pashkobohdan.fastreading.library.fileSystem.file.core.PercentSender;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.AnyFileOpening;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core.AnyBookOpeningResult;

import java.io.File;

/**
 * Opens file by thread.
 * Don't use runOnUiThread !- it's already implemented
 * <p>
 * Created by Bohdan Pashko on 23.01.17.
 */
public class FileOpenThread extends Thread {

    public FileOpenThread(@NonNull final File file, @NonNull final Activity activity,
                          @NonNull PercentSender readingPercentSender, @NonNull Runnable readingEnd,
                          @NonNull PercentSender writingPercentSender, @NonNull Runnable writingEnd,
                          @NonNull FileOpenResultSender fileOpeningEnd) {
        super(() -> {
            AnyBookOpeningResult outputFile = AnyFileOpening.open(file, activity,
                    (oldValue, neValue) -> activity.runOnUiThread(() -> readingPercentSender.refreshPercents(oldValue, neValue)),
                    () -> activity.runOnUiThread(readingEnd),
                    (oldValue, neValue) -> activity.runOnUiThread(() -> writingPercentSender.refreshPercents(oldValue, neValue)),
                    () -> activity.runOnUiThread(writingEnd));

            activity.runOnUiThread(() -> fileOpeningEnd.send(outputFile));
        });
    }

}
