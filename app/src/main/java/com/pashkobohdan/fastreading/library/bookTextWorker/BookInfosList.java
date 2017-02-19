package com.pashkobohdan.fastreading.library.bookTextWorker;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Bohdan Pashko on 02.02.17.
 */

public class BookInfosList {
    private static final List<BookInfo> bookInfos = new LinkedList<>();

    public static boolean add(BookInfo bookInfo){
        if (bookInfos.contains(bookInfo)){
            return false;
        }else{
            bookInfos.add(bookInfo);
            return true;
        }
    }

    public static BookInfo get(File file){
        for(BookInfo bookInfo : bookInfos){
            if(bookInfo.getFile().getAbsolutePath().equals(file.getAbsolutePath())){
                return bookInfo;
            }
        }

        return null;
    }

    public static List<BookInfo> getAll(){
        return bookInfos;
    }
}
