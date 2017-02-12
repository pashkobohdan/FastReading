package com.pashkobohdan.fastreading;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfo;
import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfosList;
import com.pashkobohdan.fastreading.library.bookTextWorker.Word;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;


import static android.view.KeyEvent.KEYCODE_BACK;
import static android.view.KeyEvent.KEYCODE_VOLUME_DOWN;
import static android.view.KeyEvent.KEYCODE_VOLUME_UP;

public class CurrentBook extends AppCompatActivity {
    enum ReadingStatus {
        STATUS_PLAYING,
        STATUS_PAUSE
    }

    private BookInfo bookInfo;

    private AppBarLayout appBarLayout;
    private LinearLayout topManagePanel, bottomManagePanel;
    private RelativeLayout readingPanel;
    private SeekBar currentPositionSeekBar;
    private TextView currentWordLeftPart, currentWordCenterPart, currentWordRightPart, currentSpeed;
    private ImageButton positionForwardBack, positionBack, positionUp, positionForwardUp;

    private volatile ReadingStatus currentReadingStatus;
    private ArrayList<Word> words;
    private int readingPosition;


    private Timer timer;
    private TimerTask timerTask;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_book);

        // ui elements setting
        appBarLayout = (AppBarLayout) findViewById(R.id.current_book_app_bar_layout);

        topManagePanel = (LinearLayout) findViewById(R.id.current_book_top_manage_panel);
        bottomManagePanel = (LinearLayout) findViewById(R.id.current_book_bottom_manage_panel);
        readingPanel = (RelativeLayout) findViewById(R.id.current_book_reading_space);

        currentPositionSeekBar = (SeekBar) findViewById(R.id.current_book_current_position_seek_bar);
        currentWordLeftPart = (TextView) findViewById(R.id.current_book_left_part);
        currentWordCenterPart = (TextView) findViewById(R.id.current_book_center_part);
        currentWordRightPart = (TextView) findViewById(R.id.current_book_right_part);
        currentSpeed = (TextView) findViewById(R.id.current_book_current_speed);
        positionForwardBack = (ImageButton) findViewById(R.id.current_book_speed_forward_back);
        positionBack = (ImageButton) findViewById(R.id.current_book_speed_back);
        positionUp = (ImageButton) findViewById(R.id.current_book_speed_up);
        positionForwardUp = (ImageButton) findViewById(R.id.current_book_speed_forward_up);


        // set actionBar
        setSupportActionBar((Toolbar) findViewById(R.id.current_book_toolbar));

        // check bookInfo for cracks
        if (!getBookInfo()) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setMessage("Book loading error. Try later")
                    .setPositiveButton("Ok", (dialog, which) -> finish())
                    .show();
        }

        // actionBar changing
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(bookInfo.getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        /**
         *  Here's all right !
         */

        //refresh book's last opening date (time)
        bookInfo.setLastOpeningDate((int) (new Date().getTime() / 1000));

        parseWords();

        initializeStartReadingVaules();

        initializeListeners();

        /**
         *  current status - PAUSE
         */
        refreshStatus(ReadingStatus.STATUS_PAUSE);
    }

    private boolean getBookInfo() {
        Intent i = getIntent();
        File bookFile = (File) i.getSerializableExtra("serializable_book_file");

        bookInfo = BookInfosList.get(bookFile);
        return bookInfo != null;
    }

    private void parseWords() {
        words = new ArrayList<>();

        for (String word : bookInfo.getWords()) {
            words.add(parseWordByString(word));
        }
    }

    private Word parseWordByString(String word) {
        if (word.length() == 0) {
            return new Word("", "", "");
        } else if (word.length() == 1) {
            return new Word("", word, "");
        } else if (word.length() > 1 && word.length() < 6) {
            return new Word(word.substring(0, 1), word.substring(1, 2), word.length() > 2 ? word.substring(2) : "");
        } else if (word.length() > 5 && word.length() < 10) {
            return new Word(word.substring(0, 2), word.substring(2, 3), word.substring(3));
        } else {
            return new Word(word.substring(0, 3), word.substring(3, 4), word.substring(4));
        }
    }

    private void initializeStartReadingVaules() {
        currentPositionSeekBar.setMax(words.size() - 1);

        setReadingPosition(bookInfo.getCurrentWordNumber());

        currentSpeed.setText(bookInfo.getCurrentSpeed() + "");
    }

    private void initializeListeners() {
        readingPanel.setOnClickListener(v -> {
            if (currentReadingStatus == ReadingStatus.STATUS_PAUSE) {
                refreshStatus(ReadingStatus.STATUS_PLAYING);
            } else {
                refreshStatus(ReadingStatus.STATUS_PAUSE);
            }
        });

        positionForwardBack.setOnClickListener(v -> {
            int position;
            for (position = readingPosition - 1; position >= 0; position--) {
                if (bookInfo.getWords()[position].endsWith(".") ||
                        bookInfo.getWords()[position].endsWith("?") ||
                        bookInfo.getWords()[position].endsWith("!") ||
                        bookInfo.getWords()[position].endsWith(":")) {
                    break;
                }
            }

            setReadingPosition(position == 0 ? 0 : position + 1);
        });
        positionBack.setOnClickListener(v ->
                setReadingPosition(getReadingPosition() == 0 ? 0 : getReadingPosition() - 1));
        positionUp.setOnClickListener(v ->
                setReadingPosition(getReadingPosition() ==
                        bookInfo.getWords().length - 1 ? bookInfo.getWords().length - 1 : getReadingPosition() + 1));
        positionForwardUp.setOnClickListener(v -> {
            int position;
            for (position = readingPosition + 1; position < bookInfo.getWords().length; position++) {
                if (bookInfo.getWords()[position].endsWith(".") ||
                        bookInfo.getWords()[position].endsWith("?") ||
                        bookInfo.getWords()[position].endsWith("!") ||
                        bookInfo.getWords()[position].endsWith(":")) {
                    break;
                }
            }

            setReadingPosition(position == bookInfo.getWords().length - 1 ? bookInfo.getWords().length - 1 : position + 1);
        });

        currentPositionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setReadingPosition(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopPlaying();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    if (currentReadingStatus == ReadingStatus.STATUS_PAUSE) {
                        stopPlaying();
                    }

                    if (getReadingPosition() < words.size() - 1) {
                        setReadingPosition(getReadingPosition() + 1);
                    } else {
                        showBookEndDialog();
                        // book is already end !
                        // restart book (dialog) !
                    }

                    if (getReadingPosition() == words.size() - 1) {
                        refreshStatus(ReadingStatus.STATUS_PAUSE);
                    } else {
                        if (words.get(getReadingPosition()).toString().length() > 10) {
                            startOfRestartPlaying((int) (1500.0 / bookInfo.getCurrentSpeed()));
                        }
                    }

                });
            }
        };
    }

    private void refreshStatus(ReadingStatus newRefreshStatus) {
        if (newRefreshStatus == currentReadingStatus) {
            return;
        }
        currentReadingStatus = newRefreshStatus;

        switch (currentReadingStatus) {
            case STATUS_PAUSE:

                appBarLayout.setVisibility(View.VISIBLE);
                topManagePanel.setVisibility(View.VISIBLE);
                bottomManagePanel.setVisibility(View.VISIBLE);

                stopPlaying();
                break;
            case STATUS_PLAYING:

                appBarLayout.setVisibility(View.GONE);
                topManagePanel.setVisibility(View.GONE);
                bottomManagePanel.setVisibility(View.GONE);

                startOfRestartPlaying(1000);
                break;
        }
    }

    private void startOfRestartPlaying(int initialDelay) {
        stopPlaying();
        initializeTimerTask();
        timer = new Timer();

        timer.schedule(timerTask, initialDelay, (int) (60000.0 / bookInfo.getCurrentSpeed()));
    }

    private void stopPlaying() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void showBookEndDialog() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("Book end. Do you want to start again ?")
                .setPositiveButton("Ok", (dialog, which) -> setReadingPosition(0))
                .setNegativeButton("No", (dialog, which) -> {
                })
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        bookInfo.setCurrentWordNumber(readingPosition);
        refreshStatus(ReadingStatus.STATUS_PAUSE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        refreshStatus(ReadingStatus.STATUS_PAUSE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        refreshStatus(ReadingStatus.STATUS_PAUSE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_current_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                //tryExitToBookList();
                finish();
                break;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KEYCODE_VOLUME_DOWN:
                bookInfo.setCurrentSpeed(bookInfo.getCurrentSpeed() >= 40 ? bookInfo.getCurrentSpeed() - 20 : bookInfo.getCurrentSpeed());
                //Toast.makeText(this, "volume down", Toast.LENGTH_SHORT).show();

                currentSpeed.setText(bookInfo.getCurrentSpeed() + "");

                return true;

            case KEYCODE_VOLUME_UP:
                bookInfo.setCurrentSpeed(bookInfo.getCurrentSpeed() <= 1580 ? bookInfo.getCurrentSpeed() + 20 : bookInfo.getCurrentSpeed());

                currentSpeed.setText(bookInfo.getCurrentSpeed() + "");

                //Toast.makeText(this, "volume up : ", Toast.LENGTH_SHORT).show();
                return true;


            case KEYCODE_BACK:
                //tryExitToBookList();
                finish();
                break;

        }

        return super.onKeyDown(keyCode, event);
    }


//    private void tryExitToBookList() {
//        new AlertDialog.Builder(this)
//                .setMessage("Do you want to go back ?")
//                .setPositiveButton("Yes", (dialog, which) -> finish())
//                .setNegativeButton("No", (dialog, which) -> {
//                })
//                .show();
//    }

    public int getReadingPosition() {
        return readingPosition;
    }

    public void setReadingPosition(int readingPosition) {
        this.readingPosition = readingPosition;

        currentPositionSeekBar.setProgress(readingPosition);

        Word currentWord = words.get(readingPosition);
        currentWordLeftPart.setText(currentWord.getLeftPart());
        currentWordCenterPart.setText(currentWord.getCenterLetter());
        currentWordRightPart.setText(currentWord.getRightPart());
    }
}