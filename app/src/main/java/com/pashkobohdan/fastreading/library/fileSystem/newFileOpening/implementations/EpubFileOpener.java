package com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.implementations;

import android.support.annotation.NonNull;

import com.pashkobohdan.fastreading.library.fileSystem.file.InternalStorageFileHelper;
import com.pashkobohdan.fastreading.library.fileSystem.file.core.PercentSender;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core.BookReadingResult;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core.FileOpen;

import org.jsoup.Jsoup;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

/**
 * Opening FB2 files
 * <p>
 * Created by Bohdan Pashko on 16.01.17.
 */
public class EpubFileOpener implements FileOpen {

    boolean titleWasReaded;
    String bookName;

    @Override
    public BookReadingResult open(@NonNull File file, @NonNull PercentSender percentSender, @NonNull Runnable readingEndSender) {

        // find words in text
        StringBuilder results = new StringBuilder();
        bookName = InternalStorageFileHelper.fileNameWithoutExtension(file);
        String bookAuthor = "";

        try {
            titleWasReaded = false;

            StringBuilder epubResultBuilder = new StringBuilder();
            EpubReader reader = new EpubReader();
            Book book = reader.readEpub(new FileInputStream(file));

            for (TOCReference reference : book.getTableOfContents().getTocReferences()) {
                epubResultBuilder = appendContentOfTOCReference(epubResultBuilder, reference);
            }

            String text = new String(epubResultBuilder);

            for (String block : text.split("(?=<\\?xml version=.*encoding=.*\\?>)")) {
                try {
                    appendPBlocKs(block, results);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

            results = new StringBuilder("empty book");
        }

        percentSender.refreshPercents(0, 100);


        String resultText = new String(results);
//        resultText = resultText.replaceAll("<a.*>.*</a>", "");
        resultText = resultText.trim();                         // delete first and last space (if exist)
        resultText = resultText.replaceAll("\\s+", " ");        // delete all duplicate white spaces
        resultText = resultText.replaceAll("(\\.)+", "\\.");        // delete all duplicate dots

        if (resultText.length() < 1) {
            return null;
        }

        readingEndSender.run();
        return new BookReadingResult(resultText, bookName, bookAuthor);

    }

    private void appendPBlocKs(String text, StringBuilder results) throws ParserConfigurationException, IOException, SAXException {
        org.jsoup.nodes.Document doc = Jsoup.parse(text);
        results.append(doc.select("p").text());

        if (!titleWasReaded) {
            String name = doc.select("title").text();
            if (name != null && name.length() > 0) {
                titleWasReaded = true;
                bookName = name;
            }
        }
    }

    private StringBuilder appendContentOfTOCReference(StringBuilder currentText, TOCReference reference) {
        try {
            currentText = currentText.append(new String(reference.getResource().getData()));
            for (TOCReference childReference : reference.getChildren()) {
                appendContentOfTOCReference(currentText, childReference);
            }

            return currentText;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentText;
    }

}
