package com.pashkobohdan.fastreading.library.fileSystem.newFileOpening;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.pashkobohdan.fastreading.library.fileSystem.file.FileReadingAndWriting;
import com.pashkobohdan.fastreading.library.fileSystem.file.core.FileReadWrite;
import com.pashkobohdan.fastreading.library.fileSystem.file.core.FileWriteResult;
import com.pashkobohdan.fastreading.library.fileSystem.file.core.PercentSender;
import com.pashkobohdan.fastreading.library.fileSystem.file.InternalStorageFileHelper;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.implementations.Fb2FileOpener;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.implementations.PdfFileOpener;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.implementations.TxtFileOpener;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core.FileType;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core.FileOpen;

import java.io.File;

/**
 * Opening any file, which set in FileType enum
 *
 * Created by Bohdan Pashko on 16.01.17.
 */

public class AnyFileOpening {
    private static final TxtFileOpener txtFileReaderAndWriter = new TxtFileOpener();
    private static final PdfFileOpener pdfFileReaderAndWriter = new PdfFileOpener();
    private static final Fb2FileOpener fb2FileReaderAndWriter = new Fb2FileOpener();

    private static final FileReadWrite fileReadWrite = new FileReadingAndWriting();


    private static FileType getFileType(File file) {
        FileType fileType = null;

        if (file.getName().endsWith(".pdf")) {
            fileType = FileType.PDF_FILE;
        } else if (file.getName().endsWith(".txt")) {
            fileType = FileType.TXT_FILE;
        } else if (file.getName().endsWith(".fb2")) {
            fileType = FileType.FB2_FILE;
        }

        return fileType;
    }

    /**
     * Reading any file with extension from FileType enum.
     *
     * Don't forget, check existing of this file in internal path (method InternalStorageFileHelper.isFileWasOpened)
     *
     * @param file input file
     * @return text of input file (without excess white spaces)
     */
    public static File open(@NonNull File file, final @NonNull Activity activity,
                            @NonNull PercentSender readingPercentSender, @NonNull Runnable readingEndSender,
                            @NonNull PercentSender writingPercentSender, @NonNull Runnable writingEndSender) {
        if (!file.canRead() || file.length() < 1) {
            return null;
        }

        FileType fileType = getFileType(file);
        FileOpen fileOpen;

        switch (fileType) {
            case TXT_FILE:
                fileOpen = txtFileReaderAndWriter;
                break;
            case PDF_FILE:
                fileOpen = pdfFileReaderAndWriter;
                break;
            case FB2_FILE:
                fileOpen = fb2FileReaderAndWriter;
                break;
            default:
                fileOpen = null;
        }


        // checking output text
        String fileText = fileOpen.open(file, readingPercentSender, readingEndSender);
        if (fileText == null || fileText.length() < 1) {
            return null;
        }

        // creating output file (in internal path)
        File outputFile = InternalStorageFileHelper.createNewFile(activity, file);
        if (outputFile == null) {
            return null;
        }

        // writing text to output file
        FileWriteResult fileWriteResult = fileReadWrite.write(outputFile, fileText, writingPercentSender);
        if (fileWriteResult != FileWriteResult.SUCCESS) {
            return null;
        }
        writingEndSender.run();

        return outputFile;
    }
}
