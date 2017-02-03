package com.pashkobohdan.fastreading;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfo;
import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfosList;

import java.io.File;

public class CurrentBook extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_book);

        Intent i = getIntent();
        File bookFile = (File) i.getSerializableExtra("serializable_book_file");

        //bookFile.renameTo()

        BookInfo currentBookInfo = BookInfosList.get(bookFile);
        if(currentBookInfo==null){
            Toast.makeText(this, "current book is null !!!", Toast.LENGTH_SHORT).show();
        }

    }
}
