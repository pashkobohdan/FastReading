package com.pashkobohdan.fastreading;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfo;
import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfosList;

import java.io.File;

public class CurrentBook extends AppCompatActivity {
    private BookInfo bookInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_book);

        Toolbar toolbar = (Toolbar) findViewById(R.id.current_book_toolbar);
        setSupportActionBar(toolbar);


        if(!getBookInfo()){
            return;
        }

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(bookInfo.getName());
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private boolean getBookInfo() {
        Intent i = getIntent();
        File bookFile = (File) i.getSerializableExtra("serializable_book_file");

        bookInfo = BookInfosList.get(bookFile);
        if(bookInfo==null){
            return false;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
