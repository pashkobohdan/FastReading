package com.pashkobohdan.fastreading;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.AnyFileOpening;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core.InternalStorageFileHelper;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpeningThread.FileOpenThread;

import java.io.File;

import ir.sohreco.androidfilechooser.ExternalStorageNotAvailableException;
import ir.sohreco.androidfilechooser.FileChooserDialog;

public class MyBooks extends AppCompatActivity implements FileChooserDialog.ChooserListener {
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                openFileChooserDialog();
            }
        });
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

    @Override
    public void onSelect(String path) {
        Toast.makeText(this, "the file you chose : " + path, Toast.LENGTH_SHORT).show();

        if (InternalStorageFileHelper.isFileWasOpened(this, new File(path))) {
            Toast.makeText(this, "output file already exist", Toast.LENGTH_SHORT).show();
        } else {
            File output = InternalStorageFileHelper.createNewFile(this, new File(path));

            if (output != null) {
                Toast.makeText(this, "output file : " + output.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "output file didn't created", Toast.LENGTH_SHORT).show();
            }


            Toast.makeText(this, "trying to open this file ... ", Toast.LENGTH_SHORT).show();

//            AnyFileOpening.open(new File(path),this,(o,n)->{
//                Toast.makeText(this, "reading : "+n, Toast.LENGTH_SHORT).show();
//            },()->{
//                Toast.makeText(this, "\t...reading end", Toast.LENGTH_SHORT).show();
//            },(o,n)->{
//                Toast.makeText(this, "writing : "+n, Toast.LENGTH_SHORT).show();
//            },()->{
//                Toast.makeText(this, "\t...writing end", Toast.LENGTH_SHORT).show();
//            });

            new FileOpenThread(new File(path), this, (o, n) -> {
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

}
