package com.pashkobohdan.fastreading.library.fileSystem.newFileOpening;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.pashkobohdan.fastreading.library.fileSystem.file.FileReadingAndWriting;
import com.pashkobohdan.fastreading.library.fileSystem.file.core.FileReadWrite;
import com.pashkobohdan.fastreading.library.fileSystem.file.core.PercentSender;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core.BookReadingResult;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core.FileOpen;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core.FileType;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.implementations.EpubFileOpener;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.implementations.Fb2FileOpener;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.implementations.PdfFileOpener;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.implementations.TxtFileOpener;

import java.io.File;

/**
 * Opening any file, which set in FileType enum
 * <p>
 * Created by Bohdan Pashko on 16.01.17.
 */

public class AnyFileOpening {
    private static final TxtFileOpener txtFileReaderAndWriter = new TxtFileOpener();
    private static final PdfFileOpener pdfFileReaderAndWriter = new PdfFileOpener();
    private static final Fb2FileOpener fb2FileReaderAndWriter = new Fb2FileOpener();
    private static final EpubFileOpener epubFileReaderAndWriter = new EpubFileOpener();

    private static final FileReadWrite fileReadWrite = new FileReadingAndWriting();


    private static FileType getFileType(File file) {
        FileType fileType = null;

        if (file.getName().endsWith(".pdf")) {
            fileType = FileType.PDF_FILE;
        } else if (file.getName().endsWith(".txt")) {
            fileType = FileType.TXT_FILE;
        } else if (file.getName().endsWith(".fb2")) {
            fileType = FileType.FB2_FILE;
        } else if (file.getName().endsWith(".epub")) {
            fileType = FileType.EPUB_FILE;
        }

        return fileType;
    }

    /**
     * Reading any file with extension from FileType enum.
     * <p>
     * Don't forget, check existing of this file in internal path (method InternalStorageFileHelper.isFileWasOpened)
     *
     * @param file input file
     * @return text of input file (without excess white spaces)
     */
    public static BookReadingResult open(@NonNull File file, final @NonNull Activity activity,
                                            @NonNull PercentSender readingPercentSender, @NonNull Runnable readingEndSender) {
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
            case EPUB_FILE:
                fileOpen = epubFileReaderAndWriter;
                break;
            default:
                fileOpen = null;
        }


        // checking output text
        BookReadingResult bookOpeningResult = fileOpen.open(file, readingPercentSender, readingEndSender);
        if (bookOpeningResult == null || bookOpeningResult.getBookText().length() < 1) {
            //return null;
            return null;
        }
        return bookOpeningResult;

//        // creating output file (in internal path)
//        File outputFile = InternalStorageFileHelper.createNewFile(activity, file);
//        if (outputFile == null) {
//            return null;
//        }
//
//        // writing text to output file
//        FileWriteResult fileWriteResult = fileReadWrite.write(outputFile, bookOpeningResult.getBookText(), writingPercentSender);
//        if (fileWriteResult != FileWriteResult.SUCCESS) {
//            return null;
//        }
//        writingEndSender.run();
//
//        return new AnyBookOpeningResult(bookOpeningResult, outputFile);
    }
}
