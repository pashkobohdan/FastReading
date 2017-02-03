package com.pashkobohdan.fastreading.library.fileSystem.file;

import android.app.Activity;
import android.content.Context;

import java.io.File;
import java.io.IOException;

/**
 * Created by Bohdan Pashko on 22.01.17.
 */

public class InternalStorageFileHelper {
    public static final String INTERNAL_FILE_EXTENSION = ".tmp";

    /**
     * ONLY first 20 symbols !!!
     *
     * @param file
     * @return
     */
    public static String fileNameWithoutExtension(File file) {
        String inputFileName = file.getName();

        String prevName = inputFileName.lastIndexOf(".") > 0 ?
                inputFileName.substring(0, inputFileName.lastIndexOf(".")) :
                inputFileName;

        return prevName.length() > 19 ? prevName.substring(0, 20) : prevName;
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

    public static boolean tryRenameFile(Activity activity, File inputFile, String outputName) {
        try {

            for (File file : activity.getCacheDir().listFiles((directory, fileName) -> fileName.endsWith(INTERNAL_FILE_EXTENSION))) {
                if (fileNameWithoutExtension(inputFile).equals(fileNameWithoutExtension(file))) {
                    return false;
                }
            }

            return inputFile.renameTo(new File(inputFile.getAbsolutePath(), outputName + INTERNAL_FILE_EXTENSION));

        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}
