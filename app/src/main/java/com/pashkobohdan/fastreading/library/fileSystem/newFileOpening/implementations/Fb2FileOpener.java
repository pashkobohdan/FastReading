package com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.implementations;

import android.support.annotation.NonNull;
import android.util.Log;

import com.pashkobohdan.fastreading.library.fileSystem.fileReading.FileReadingAndWriting;
import com.pashkobohdan.fastreading.library.fileSystem.fileReading.core.FileReadWrite;
import com.pashkobohdan.fastreading.library.fileSystem.fileReading.core.PercentSender;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core.FileOpen;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Opening FB2 files
 *
 * Created by Bohdan Pashko on 16.01.17.
 */
public class Fb2FileOpener implements FileOpen {

    @Override
    public String open(@NonNull File file, @NonNull PercentSender percentSender, @NonNull Runnable readingEndSender) {

        // reading file without encoding
        FileReadWrite readWrite = new FileReadingAndWriting();

        Log.e("FB2 reading", "start 1");

        String textWithoutEncoding = readWrite.read(file,
                (oldPercent, newPercent) -> percentSender.refreshPercents(oldPercent / 2, newPercent / 2));


        if (textWithoutEncoding == null) {
            return null;
        }

        Log.e("FB2 reading", "start 2");

        // finding encoding
        if (!textWithoutEncoding.contains("encoding=\"")) {
            return null;
        }

        Pattern pattern = Pattern.compile("encoding=\"(.*)\"");
        Matcher matcher = pattern.matcher(textWithoutEncoding);
        String currentFb2FileCharset = null;

        if (matcher.find()) {
            currentFb2FileCharset = matcher.group(1);
        }

        if (currentFb2FileCharset == null) {
            return null;
        }


        Log.e("FB2 reading", "start 3");

        // open file with encoding
        String encodedText = readWrite.read(file,
                (oldPercent, newPercent) -> percentSender.refreshPercents(oldPercent / 2 + 50, newPercent / 2 + 50),
                currentFb2FileCharset);



        Log.e("FB2 reading", "start 4");
        // find words in text
        StringBuilder results = new StringBuilder();

        pattern = Pattern.compile("<p>(.*)</p>");
        matcher = pattern.matcher(encodedText);

        while (matcher.find()) {
            String foundString = matcher.group(1).trim();

            if (foundString.endsWith("\\.") ||
                    foundString.endsWith("!") ||
                    foundString.endsWith("?") ||
                    foundString.endsWith(":")) {
                results.append(foundString);
                results.append(" ");
            } else {
                results.append(foundString);
                results.append(". ");
            }
        }


        String resultText = new String(results);
        resultText = resultText.replaceAll("<a.*>.*</a>", "");
        resultText = resultText.trim();                         // delete first and last space (if exist)
        resultText = resultText.replaceAll("\\s+", " ");        // delete all duplicate white spaces
        resultText = resultText.replaceAll("(\\.)+", "\\.");        // delete all duplicate dots

        if (resultText.length() < 1) {
            return null;
        }

        readingEndSender.run();
        return resultText;

    }

}
