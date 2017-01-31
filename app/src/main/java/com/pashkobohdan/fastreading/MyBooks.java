package com.pashkobohdan.fastreading;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfo;
import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfoFactory;
import com.pashkobohdan.fastreading.library.fileSystem.fileReading.InternalStorageFileHelper;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpeningThread.FileOpenThread;
import com.pashkobohdan.fastreading.library.lists.booksList.BooksRecyclerViewAdapter;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import ir.sohreco.androidfilechooser.ExternalStorageNotAvailableException;
import ir.sohreco.androidfilechooser.FileChooserDialog;

import static com.pashkobohdan.fastreading.library.fileSystem.fileReading.InternalStorageFileHelper.INTERNAL_FILE_EXTENSION;

public class MyBooks extends AppCompatActivity implements FileChooserDialog.ChooserListener {
    private static final int PERMISSION_REQUEST_CODE = 1;

    private RecyclerView booksRecyclerView;
    private RecyclerView.Adapter booksAdapter;

    private FloatingActionsMenu booksFloatingActionsMenu;
    private FloatingActionButton floatingActionButtonOpenFile, floatingActionButtonDownloadBook, floatingActionButtonCreateBook;

    //FloatingActionMenu floatingActionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        floatingActionButtonOpenFile = (FloatingActionButton) findViewById(R.id.book_list_open_pdf_fb2_txt_file);
        floatingActionButtonDownloadBook = (FloatingActionButton) findViewById(R.id.book_list_download_book_from_cloud);
        floatingActionButtonCreateBook = (FloatingActionButton) findViewById(R.id.book_list_create_new_book);


        floatingActionButtonOpenFile.setOnClickListener(v -> tryOpenFile());
        floatingActionButtonDownloadBook.setOnClickListener(v -> {

        });
        floatingActionButtonCreateBook.setOnClickListener(v -> {

        });


        booksRecyclerView = (RecyclerView) findViewById(R.id.books_recycler_view);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Adapter:
        List<BookInfo> booksInfo = new LinkedList<>();
        for (File file : getCacheDir().listFiles((directory, fileName) -> fileName.endsWith(INTERNAL_FILE_EXTENSION))) {
            if (file.getName().endsWith(INTERNAL_FILE_EXTENSION)) {
                booksInfo.add(BookInfoFactory.newInstance(file, this));//new BookInfo(file, this)
            }
        }
        booksAdapter = new BooksRecyclerViewAdapter(this, booksInfo, (bookInfo) -> {
            Toast.makeText(this, "editing : " + bookInfo.getName(), Toast.LENGTH_SHORT).show();
        }, (bookInfo) -> {
            Toast.makeText(this, "sharing : " + bookInfo.getName(), Toast.LENGTH_SHORT).show();
        }, (bookInfo) -> {
            Toast.makeText(this, "uploading : " + bookInfo.getName(), Toast.LENGTH_SHORT).show();
        }, (bookInfo, ifConfirmed) -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        ifConfirmed.run();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want delete this record  : " + bookInfo.getName()).setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }, (bookInfo) -> {
            Toast.makeText(this, "click : " + bookInfo.getName(), Toast.LENGTH_SHORT).show();
        }, (bookInfo) -> {
            Toast.makeText(this, "long click : " + bookInfo.getName(), Toast.LENGTH_SHORT).show();
        });
        ((BooksRecyclerViewAdapter) booksAdapter).setMode(Attributes.Mode.Single);
        booksRecyclerView.setAdapter(booksAdapter);


        //floatingActionMenu = (FloatingActionMenu)findViewById(R.id.material_design_android_floating_action_menu);
        /* Listeners */
        //booksRecyclerView.setOnScrollListener(onScrollListener);
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
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onSelect(String path) {
        File inputFile = new File(path);
        String bookName = InternalStorageFileHelper.fileNameWithoutExtension(inputFile);

        if (InternalStorageFileHelper.isFileWasOpened(this, inputFile)) {

            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        openFileWithUI(inputFile);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to rewrite this book \"" +
                    bookName +
                    "\" ?\nAll reading progress will be removed)")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show();

        } else {
            openFileWithUI(inputFile);
        }
    }

    private void openFileWithUI(File inputFile){
        new FileOpenThread(inputFile, this, (o, n) -> {
            Toast.makeText(this, "reading : " + n, Toast.LENGTH_SHORT).show();
        }, () -> {
            Toast.makeText(this, "\t...reading end", Toast.LENGTH_SHORT).show();
        }, (o, n) -> {
            Toast.makeText(this, "writing : " + n, Toast.LENGTH_SHORT).show();
        }, () -> {
            Toast.makeText(this, "\t...writing end", Toast.LENGTH_SHORT).show();
        }, (file) -> {

            Toast.makeText(this, "total end, file is null ? - " + (file == null), Toast.LENGTH_SHORT).show();

        }).start();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileChooserDialog();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_books, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // business logic functions

    private void tryOpenFile() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            openFileChooserDialog();
        }
    }

    private void openFileChooserDialog() {
        //FileChooserDialog.Builder builder = new FileChooserDialog.Builder(FileChooserDialog.ChooserType.FILE_CHOOSER, this);

        FileChooserDialog.Builder builder =
                new FileChooserDialog.Builder(FileChooserDialog.ChooserType.FILE_CHOOSER, this)
                        .setTitle("Select a file:")
                        .setFileFormats(new String[]{".txt", ".pdf", ".fb2"});
        //.setSelectDirectoryButtonText("ENTEKHAB")
        //.setSelectDirectoryButtonBackground(R.drawable.dr)
        //.setSelectDirectoryButtonTextColor(R.color.cl)
        //.setSelectDirectoryButtonTextSize(25)
        //.setFileIcon(R.drawable.ic_file)
        //.setDirectoryIcon(R.drawable.ic_directory)
        //.setPreviousDirectoryButtonIcon(R.drawable.ic_prev_dir);
        try {
            builder.build().show(getSupportFragmentManager(), null);
        } catch (ExternalStorageNotAvailableException e) {
            e.printStackTrace();
        }
    }
}
