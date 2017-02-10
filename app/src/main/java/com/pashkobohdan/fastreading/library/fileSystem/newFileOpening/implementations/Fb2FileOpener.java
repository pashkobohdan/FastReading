package com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.implementations;

import android.support.annotation.NonNull;

import com.pashkobohdan.fastreading.library.fileSystem.file.FileReadingAndWriting;
import com.pashkobohdan.fastreading.library.fileSystem.file.core.FileReadWrite;
import com.pashkobohdan.fastreading.library.fileSystem.file.core.PercentSender;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core.FileOpen;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Opening FB2 files
 * <p>
 * Created by Bohdan Pashko on 16.01.17.
 */
public class Fb2FileOpener implements FileOpen {

    @Override
    public String open(@NonNull File file, @NonNull PercentSender percentSender, @NonNull Runnable readingEndSender) {

        // reading file without encoding
        FileReadWrite readWrite = new FileReadingAndWriting();


        String textWithoutEncoding = readWrite.read(file,
                (oldPercent, newPercent) -> percentSender.refreshPercents(oldPercent / 2, newPercent / 2));


        if (textWithoutEncoding == null) {
            return null;
        }


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

        if (currentFb2FileCharset.toLowerCase().contains("utf-8")) {
            currentFb2FileCharset = "utf-8";
        } else if (currentFb2FileCharset.toLowerCase().contains("windows-1251")) {
            currentFb2FileCharset = "windows-1251";
        }


        // open file with encoding
        String encodedText = readWrite.read(file,
                (oldPercent, newPercent) -> percentSender.refreshPercents(oldPercent / 2 + 50, newPercent / 2 + 50),
                currentFb2FileCharset);


//        try {
//
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document document = builder.parse(new InputSource(new StringReader(encodedText)));
//            Element rootElement = document.getDocumentElement();
//
//
//            readingEndSender.run();
//
//            return getTexts("p", rootElement).replaceAll("<a.*>.*</a>", "")
//                    .trim()
//                    .replaceAll("\\s+", " ")
//                    .replaceAll("(\\.)+", "\\.");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        // find words in text
        StringBuilder results = new StringBuilder();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            Document doc = dbf.newDocumentBuilder().parse(file);
            doc.getDocumentElement().normalize(); // Not Mandatory

            NodeList elements = doc.getElementsByTagName("p");
            for (int i = 0; i < elements.getLength(); i++) {
                Element elem = (Element) elements.item(i);

                String curText = elem.getTextContent().trim();

                results.append(elem.getTextContent());
                if (curText.endsWith(".") || curText.endsWith(":") || curText.endsWith("!") || curText.endsWith("?")) {
                    results.append(" ");
                } else {
                    results.append(". ");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            results = new StringBuilder("empty book");
        }

//        pattern = Pattern.compile("<p>(.+?)</p>");
//        matcher = pattern.matcher(encodedText);
//
//        while (matcher.find()) {
//            String foundString = matcher.group(1).trim();
//
//            if (foundString.endsWith("\\.") ||
//                    foundString.endsWith("!") ||
//                    foundString.endsWith("?") ||
//                    foundString.endsWith(":")) {
//                results.append(foundString);
//                results.append(" ");
//            } else {
//                results.append(foundString);
//                results.append(". ");
//            }
//        }


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

    protected String getTexts(String tagName, Element element) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < list.getLength(); i++) {
                NodeList subList = list.item(i).getChildNodes();

                if (subList != null && subList.getLength() > 0) {
                    result.append(subList.item(i).getNodeValue());
                }
            }

            return new String(result);

        }

        return null;
    }

}
