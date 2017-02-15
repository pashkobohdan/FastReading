package com.pashkobohdan.fastreading.library.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.pashkobohdan.fastreading.R;
import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfo;
import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfoFactory;
import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfosList;
import com.pashkobohdan.fastreading.library.fileSystem.file.FileReadingAndWriting;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core.BookReadingResult;
import com.pashkobohdan.fastreading.library.ui.lists.booksList.BookEventListener;

/**
 * Created by bohdan on 15.02.17.
 */

public class BookAddDialog {

    private TextInputLayout bookName;
    private TextInputLayout bookAuthor;
    private EditText bookText;

    private AlertDialog alertDialog;

    private BookEventListener successEdit;

    public BookAddDialog(final Activity activity, BookEventListener successEdit) {
        this.successEdit = successEdit;

        LayoutInflater factory = LayoutInflater.from(activity);
        View textEntryView = factory.inflate(R.layout.dialog_edit_book, null);

        bookName = (TextInputLayout) textEntryView.findViewById(R.id.dialog_edit_book_books_name);
        bookAuthor = (TextInputLayout) textEntryView.findViewById(R.id.dialog_edit_book_books_author);
        bookText = (EditText) textEntryView.findViewById(R.id.dialog_edit_book_books_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                //.setTitle("Book cre")
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .setView(textEntryView);

        alertDialog = builder.create();

        alertDialog.setOnShowListener(dialog -> {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {

                if (tryEditBook(activity)) {
                    dialog.dismiss();
                }
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

    private boolean tryEditBook(Activity activity) {
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
            String text = bookText.getText().toString();


            if (name.length() > 0 && author.length() > 0 && text.length() > 0) {
                BookReadingResult bookReadingResult = new BookReadingResult(text, name, author);

                BookInfo bookInfo = BookInfoFactory.createNewInstance(bookReadingResult, activity);
                if(bookInfo != null){
                    successEdit.run(bookInfo);
                    return true;
                }else{
                    new AlertDialog.Builder(activity)
                            .setPositiveButton("Ok", (dialog, which) -> {
                            })
                            .setTitle("Error")
                            .setMessage("Book writing error")
                            .create()
                            .show();
                }
            }else{
                new AlertDialog.Builder(activity)
                        .setPositiveButton("Ok", (dialog, which) -> {
                        })
                        .setTitle("Information")
                        .setMessage("Please, set the valid data")
                        .create()
                        .show();
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

        return false; // nothing changed
    }


    public void show() {
        alertDialog.show();
    }

}
