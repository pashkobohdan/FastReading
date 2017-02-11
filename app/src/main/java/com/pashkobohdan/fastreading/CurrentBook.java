package com.pashkobohdan.fastreading;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfo;
import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfosList;

import java.io.File;

public class CurrentBook extends AppCompatActivity {
    enum ReadingStatus{
        STATUS_PLAYING,
        STATUS_PAUSE
    }

    private LinearLayout topManagePanel, bottomManagePanel;
    private RelativeLayout readingPanel;

    private BookInfo bookInfo;

    private ReadingStatus currentReadingStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_book);

        topManagePanel = (LinearLayout)findViewById(R.id.current_book_top_manage_panel);
        bottomManagePanel = (LinearLayout)findViewById(R.id.current_book_bottom_manage_panel);
        readingPanel = (RelativeLayout) findViewById(R.id.current_book_reading_space);

        readingPanel.setOnClickListener(v -> {
            Toast.makeText(this, "reading pane touch !", Toast.LENGTH_SHORT).show();

            if(currentReadingStatus == ReadingStatus.STATUS_PAUSE){
                refreshStatus(ReadingStatus.STATUS_PLAYING);
            }else{
                refreshStatus(ReadingStatus.STATUS_PAUSE);
            }
        });

        setSupportActionBar((Toolbar) findViewById(R.id.current_book_toolbar));


        if (!getBookInfo()) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setMessage("Book loading error. Try later")
                    .setPositiveButton("Ok", (dialog, which) -> finish())
                    .show();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(bookInfo.getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        refreshStatus(ReadingStatus.STATUS_PAUSE);
    }

    private boolean getBookInfo() {
        Intent i = getIntent();
        File bookFile = (File) i.getSerializableExtra("serializable_book_file");

        bookInfo = BookInfosList.get(bookFile);
        return bookInfo != null;
    }

    private void refreshStatus(ReadingStatus newRefreshStatus){
        currentReadingStatus = newRefreshStatus;

        switch (currentReadingStatus){
            case STATUS_PAUSE:
                topManagePanel.setVisibility(View.INVISIBLE);
                bottomManagePanel.setVisibility(View.INVISIBLE);
                getSupportActionBar().hide();

                break;
            case STATUS_PLAYING:
                topManagePanel.setVisibility(View.VISIBLE);
                bottomManagePanel.setVisibility(View.VISIBLE);
                getSupportActionBar().show();

                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            new AlertDialog.Builder(this)
                    .setMessage("Do you to go back ?")
                    .setPositiveButton("Yes", (dialog, which) -> finish())
                    .setNegativeButton("No", (dialog, which) -> {
                    })
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }
}
