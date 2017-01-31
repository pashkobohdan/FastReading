package com.pashkobohdan.fastreading.library.fileSystem.fileReading;

import android.support.annotation.NonNull;

import com.pashkobohdan.fastreading.library.fileSystem.fileReading.core.FileReadWrite;
import com.pashkobohdan.fastreading.library.fileSystem.fileReading.core.FileWriteResult;
import com.pashkobohdan.fastreading.library.fileSystem.fileReading.core.PercentSender;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Basic reading and writing files.
 * Just for simple files (not PDF or something similar)
 * <p>
 * Created by Bohdan Pashko on 18.01.17.
 */

public class FileReadingAndWriting implements FileReadWrite {
    private static final int READING_BUFFER_SIZE = 1024 * 1024;

    @Override
    public String read(@NonNull File file, @NonNull PercentSender percentSender) {
        if (!file.canRead() || !file.canRead() || file.length() < 1) {
            return null;
        }

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file), (int) file.length() / 5);

            String line;
            int oldPercent = 0, newPercent;

            while ((line = bufferedReader.readLine()) != null) {
                text.append(line);
                text.append('\n');

                newPercent = (int) (100.0 * (text.length() * 2) / file.length()); // bytes count (8 bit) = chars count * 2 (16 bit)
                if (newPercent != oldPercent) {
                    percentSender.refreshPercents(oldPercent, newPercent);
                    oldPercent = newPercent;
                }
            }

            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }

        return text.length() < 1 ? null : new String(text);

    }

    @Override
    public String read(@NonNull File file, @NonNull PercentSender percentSender, @NonNull String charset) {
        if (!file.exists() || !file.canRead() || file.length() < 1) {
            return null;
        }

        StringBuilder text = new StringBuilder();

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            InputStreamReader inputStreamReader = new InputStreamReader(dataInputStream, charset);


            BufferedReader bufferedReader = new BufferedReader(inputStreamReader, READING_BUFFER_SIZE);

            String line;
            int currentPercent = 0;
            int newPercent;

            while ((line = bufferedReader.readLine()) != null) {
                text.append(line);
                text.append('\n');

                newPercent = (int) (100.0 * (text.length() * 2) / file.length()); // bytes count (8 bit) = chars count * 2 (16 bit)
                percentSender.refreshPercents(currentPercent, newPercent);
                currentPercent = newPercent;
            }

            bufferedReader.close();
            inputStreamReader.close();
            dataInputStream.close();
            fileInputStream.close();

        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }

        return text.length() < 1 ? null : new String(text);
    }

    @Override
    public FileWriteResult write(@NonNull File file, @NonNull String text, @NonNull PercentSender percentSender) {
        try {
            if (!file.exists() || !file.canWrite()) {
                return FileWriteResult.FAILURE;
            }

            percentSender.refreshPercents(0, 0);

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(text);

            percentSender.refreshPercents(0, 100);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();

            return FileWriteResult.FAILURE;
        }

        return FileWriteResult.SUCCESS;
    }
}
