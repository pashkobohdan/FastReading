package com.pashkobohdan.fastreading.library.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;

import com.pashkobohdan.fastreading.R;
import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfo;
import com.pashkobohdan.fastreading.library.fileSystem.file.FileReadingAndWriting;
import com.pashkobohdan.fastreading.library.fileSystem.file.InternalStorageFileHelper;

import java.io.File;

/**
 * Created by bohdan on 03.02.17.
 */

public class BookEditDialog {

    private LayoutInflater factory;
    private View textEntryView;

    private TextInputLayout bookName;
    private TextInputLayout bookAuthor;
    private TextInputLayout bookText;


    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;


    public BookEditDialog(final Activity activity, BookInfo bookInfo, Runnable successEdit) {
        factory = LayoutInflater.from(activity);
        textEntryView = factory.inflate(R.layout.dialog_edit_book, null);

        bookName = (TextInputLayout) textEntryView.findViewById(R.id.dialog_edit_book_books_name);
        bookAuthor = (TextInputLayout) textEntryView.findViewById(R.id.dialog_edit_book_books_author);
        bookText = (TextInputLayout) textEntryView.findViewById(R.id.dialog_edit_book_books_text);

        bookName.getEditText().setText(bookInfo.getName());
        bookAuthor.getEditText().setText(bookInfo.getAuthor());
        bookText.getEditText().setText(bookInfo.getAllText());

        builder = new AlertDialog.Builder(activity)
                .setTitle("Book's editing")
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .setView(textEntryView);

        alertDialog = builder.create();


        alertDialog.setOnShowListener(dialog -> {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {

                if (tryEditBook(activity, bookInfo)) {
                    dialog.dismiss();
                    successEdit.run();
                }

                //dialog.dismiss();
            });

            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(view -> showCancelDialog(activity, dialog));
        });
    }

    private void showCancelDialog(Activity activity, DialogInterface editingDialogInterface) {
        AlertDialog.Builder cancelBuilder = new AlertDialog.Builder(activity);
        cancelBuilder.setMessage("Do you want to cancel editing of this book ?")
                .setPositiveButton("Yes", (dialog, which) -> editingDialogInterface.dismiss())
                .setNegativeButton("No", (dialog, which) -> {
                })
                .create()
                .show();
    }

    private boolean tryEditBook(Activity activity, BookInfo bookInfo) {
        if (bookName.isErrorEnabled() || bookAuthor.isErrorEnabled()) {
            new AlertDialog.Builder(activity)
                    .setPositiveButton("Ok", (dialog, which) -> {
                    })
                    .setTitle("Information")
                    .setMessage("Please, set the valid data")
                    .create()
                    .show();

            return false;
        }

        try {
            String name = bookName.getEditText().getText().toString();
            String author = bookAuthor.getEditText().getText().toString();
            String text = bookText.getEditText().getText().toString();

            if (!name.equals(bookInfo.getName()) && name.length() > 0) {
                if (bookInfo.getFile().renameTo(new File(bookInfo.getFile().getParentFile(), name + InternalStorageFileHelper.INTERNAL_FILE_EXTENSION))) {

                    bookInfo.setName(name);

                } else {
                    new AlertDialog.Builder(activity)
                            .setPositiveButton("Ok", (dialog, which) -> {
                            })
                            .setTitle("Information")
                            .setMessage("Book with current name is already exist.\nSet another book's name")
                            .create()
                            .show();

                    return false;
                }
            }

            if (!author.equals(bookInfo.getAuthor()) && author.length() > 0) {
                bookInfo.setAuthor(author);
            }

            if (!text.equals(bookInfo.getAllText()) && text.length() > 0) {
                bookInfo.setAllText(text);
                String[] words= text.
                        trim().
                        replaceAll("\\s+", " ").
                        replaceAll("(\\.)+", "\\.").
                        split(" ");

                bookInfo.setWords(words);
                bookInfo.setWordsNumber(words.length);

                ProgressDialog progressDialog = new ProgressDialog(activity);


                new FileReadingAndWriting().write(bookInfo.getFile(), text, (o,n)->{});
            }

        } catch (Exception e) {
            e.printStackTrace();

            new AlertDialog.Builder(activity)
                    .setPositiveButton("Ok", (dialog, which) -> {
                    })
                    .setTitle("Information")
                    .setMessage("Please, set the valid data")
                    .create()
                    .show();

            return false;
        }

        return true; // nothing changed
    }


    public void show() {
        alertDialog.show();
    }

}
