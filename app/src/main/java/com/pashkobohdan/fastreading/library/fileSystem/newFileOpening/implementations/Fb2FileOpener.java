package com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.implementations;

import android.support.annotation.NonNull;
import android.util.Log;

import com.pashkobohdan.fastreading.library.fileSystem.file.InternalStorageFileHelper;
import com.pashkobohdan.fastreading.library.fileSystem.file.core.PercentSender;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core.BookReadingResult;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core.FileOpen;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Opening FB2 files
 * <p>
 * Created by Bohdan Pashko on 16.01.17.
 */
public class Fb2FileOpener implements FileOpen {

    @Override
    public BookReadingResult open(@NonNull File file, @NonNull PercentSender percentSender, @NonNull Runnable readingEndSender) {

        // reading file without encoding
        percentSender.refreshPercents(0, 0);
//        FileReadWrite readWrite = new FileReadingAndWriting();
//
//
//        String textWithoutEncoding = readWrite.read(file,
//                (oldPercent, newPercent) -> percentSender.refreshPercents(oldPercent / 2, newPercent / 2));
//
//
//        if (textWithoutEncoding == null) {
//            return null;
//        }
//
//
//        // finding encoding
//        if (!textWithoutEncoding.contains("encoding=\"")) {
//            return null;
//        }
//
//        Pattern pattern = Pattern.compile("encoding=\"(.*)\"");
//        Matcher matcher = pattern.matcher(textWithoutEncoding);
//        String currentFb2FileCharset = null;
//
//        if (matcher.find()) {
//            currentFb2FileCharset = matcher.group(1);
//        }
//
//        if (currentFb2FileCharset == null) {
//            return null;
//        }
//
//        if (currentFb2FileCharset.toLowerCase().contains("utf-8")) {
//            currentFb2FileCharset = "utf-8";
//        } else if (currentFb2FileCharset.toLowerCase().contains("windows-1251")) {
//            currentFb2FileCharset = "windows-1251";
//        }
//
//
//        // open file with encoding
//        String encodedText = readWrite.read(file,
//                (oldPercent, newPercent) -> percentSender.refreshPercents(oldPercent / 2 + 50, newPercent / 2 + 50),
//                currentFb2FileCharset);


        // find words in text
        StringBuilder results = new StringBuilder();
        String bookName = InternalStorageFileHelper.fileNameWithoutExtension(file);
        String bookAuthor = "";

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

            NodeList name = doc.getElementsByTagName("book-title");
            if (name != null && name.getLength() > 0) {
                bookName = ((Element) name.item(0)).getTextContent();
            }

            bookAuthor = getAuthor(doc);

        } catch (Exception e) {
            e.printStackTrace();

            results = new StringBuilder("empty book");
        }

        percentSender.refreshPercents(0, 100);


        String resultText = new String(results);
        resultText = resultText.replaceAll("<a.*>.*</a>", "");
        resultText = resultText.trim();                         // delete first and last space (if exist)
        resultText = resultText.replaceAll("\\s+", " ");        // delete all duplicate white spaces
        resultText = resultText.replaceAll("(\\.)+", "\\.");        // delete all duplicate dots

        if (resultText.length() < 1) {
            return null;
        }

        readingEndSender.run();
        return new BookReadingResult(resultText, bookName, bookAuthor);

    }

    private String getAuthor(Document document) {
        String resultAuthor = "";

        Node allTitleInfo = document.getElementsByTagName("title-info").item(0);

        NodeList titleInfo = allTitleInfo.getChildNodes();
        for (int a = 0; a < titleInfo.getLength(); a++) {
            if (titleInfo.item(a).getNodeName().equals("author")) {

                Node author = titleInfo.item(a);

                NodeList authorValues = author.getChildNodes();
                if (authorValues.getLength() == 0) {
                    resultAuthor += author.getTextContent() + " ";
                } else {
                    for (int j = 0; j < authorValues.getLength(); j++) {
                        Node element = authorValues.item(j);

                        if (element.getNodeName().equals("first-name") ||
                                element.getNodeName().equals("last-name") ||
                                element.getNodeName().equals("middle-name")) {
                            resultAuthor += element.getTextContent() + " ";
                        }
                    }
                }
            }

        }


        return resultAuthor;
    }

}
