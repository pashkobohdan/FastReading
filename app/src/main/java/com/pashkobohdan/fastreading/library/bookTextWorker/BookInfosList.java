package com.pashkobohdan.fastreading.library.bookTextWorker;

import com.pashkobohdan.fastreading.data.dto.DBBookDTO;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Bohdan Pashko on 02.02.17.
 */

public class BookInfosList {
    private static final List<DBBookDTO> bookInfos = new LinkedList<>();

    public static boolean add(DBBookDTO bookInfo){
        if (bookInfos.contains(bookInfo)){
            return false;
        }else{
            bookInfos.add(bookInfo);
            return true;
        }
    }

//    public static BookInfo get(File file){
//        for(BookInfo bookInfo : bookInfos){
//            if(bookInfo.getFile().getAbsolutePath().equals(file.getAbsolutePath())){
//                return bookInfo;
//            }
//        }
//
//        return null;
//    }

    public static List<DBBookDTO> getAll(){
        return bookInfos;
    }
}
