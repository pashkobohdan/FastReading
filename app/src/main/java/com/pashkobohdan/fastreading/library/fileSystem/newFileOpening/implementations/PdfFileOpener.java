package com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.implementations;

import android.support.annotation.NonNull;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.pashkobohdan.fastreading.library.fileSystem.fileReading.core.PercentSender;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core.FileOpen;

import java.io.File;
import java.io.IOException;

/**
 * Opening PDF files
 * <p>
 * Created by Bohdan Pashko on 16.01.17.
 */

public class PdfFileOpener implements FileOpen {
    @Override
    public String open(@NonNull File file, @NonNull PercentSender percentSender, @NonNull Runnable readingEndSender) {

        try {
            PdfReader pdfReader = new PdfReader(file.getAbsolutePath());
            PdfReaderContentParser parser = new PdfReaderContentParser(pdfReader);
            int numberOfPages = pdfReader.getNumberOfPages();
            int oldPercent = 0, newPercent;

            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
                TextExtractionStrategy strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
                String pageText = strategy.getResultantText();

                stringBuffer.append(pageText);

                if (pageText.endsWith("\\.") ||
                        pageText.endsWith("!") ||
                        pageText.endsWith("?") ||
                        pageText.endsWith(":")) {
                    stringBuffer.append(" ");
                } else {
                    stringBuffer.append(". ");
                }

                newPercent = 100 * i / numberOfPages;
                if (newPercent != oldPercent) {
                    percentSender.refreshPercents(oldPercent, newPercent);
                    oldPercent = newPercent;
                }
            }

            pdfReader.close();

            String resultText = new String(stringBuffer);
            resultText = resultText.trim();                         // delete first and last space (if exist)
            resultText = resultText.replaceAll("\\s+", " ");        // delete all duplicate white spaces
            resultText = resultText.replaceAll("(\\.)+", "\\.");        // delete all duplicate dots

            if (resultText.length() < 1) {
                return null;
            }

            readingEndSender.run();
            return resultText;

        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }

    }

}
