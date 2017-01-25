package com.pashkobohdan.fastreading.library.fileSystem.fileReading;

import android.content.Context;

import java.io.File;

/**
 * Created by Bohdan Pashko on 22.01.17.
 */

public class InternalStorageFileHelper {
    public static final String INTERNAL_FILE_EXTENSION = ".tmp";

    public static String fileNameWithoutExtension(File file) {
        String inputFileName = file.getName();

        return inputFileName.lastIndexOf(".") > 0 ?
                inputFileName.substring(0, inputFileName.lastIndexOf(".")) :
                inputFileName;
    }

    public static boolean isFileWasOpened(Context context, File inputFile) {
        for (File file : context.getCacheDir().listFiles((directory, fileName) -> fileName.endsWith(INTERNAL_FILE_EXTENSION))) {
            if (fileNameWithoutExtension(inputFile).equals(fileNameWithoutExtension(file))) {
                return true;
            }
        }

        return false;
    }

    public static File createNewFile(Context context, File inputFile) {
        File file;
        try {
            file = new File(context.getCacheDir(), fileNameWithoutExtension(inputFile) + INTERNAL_FILE_EXTENSION);
            if (!file.exists() && !file.createNewFile()) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
        return file;
    }

}
