package com.pashkobohdan.fastreading.library.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.pashkobohdan.fastreading.R;
import com.pashkobohdan.fastreading.data.database.InsertBookAsyncTask;
import com.pashkobohdan.fastreading.data.dto.DBBookDTO;


/**
 * Shows dialog when you want change ant book.
 * Catches exceptions.
 * <p>
 * Created by Bohdan Pashko on 03.02.17.
 */

public class BookEditDialog {

    private TextInputLayout bookName;
    private TextInputLayout bookAuthor;
    private EditText bookText;

    private AlertDialog alertDialog;

    private DBBookDTO bookInfo;


    public BookEditDialog(final Activity activity, DBBookDTO bookInfo, Runnable successEdit) {
        this.bookInfo = bookInfo;

        LayoutInflater factory = LayoutInflater.from(activity);
        View textEntryView = factory.inflate(R.layout.dialog_edit_book, null);

        bookName = (TextInputLayout) textEntryView.findViewById(R.id.dialog_edit_book_books_name);
        bookAuthor = (TextInputLayout) textEntryView.findViewById(R.id.dialog_edit_book_books_author);
        bookText = (EditText) textEntryView.findViewById(R.id.dialog_edit_book_books_text);

        bookName.getEditText().setText(bookInfo.getName());
        bookAuthor.getEditText().setText(bookInfo.getAuthor());


        bookText.setText(bookInfo.getText());

        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                //.setTitle("Book's editing")
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, null)
                .setView(textEntryView);

        alertDialog = builder.create();

        alertDialog.setOnShowListener(dialog -> {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {

                if (tryEditBook(activity, bookInfo)) {
                    dialog.dismiss();
                    successEdit.run();
                }
            });

            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(view -> showCancelDialog(activity, dialog));
        });
    }

    private void showCancelDialog(Activity activity, DialogInterface editingDialogInterface) {
        AlertDialog.Builder cancelBuilder = new AlertDialog.Builder(activity);
        cancelBuilder.setMessage(R.string.editing_cancel_text)
                .setPositiveButton(R.string.yes, (dialog, which) -> editingDialogInterface.dismiss())
                .setNegativeButton(R.string.no, (dialog, which) -> {
                })
                .create()
                .show();
    }

    private boolean tryEditBook(Activity activity, DBBookDTO bookInfo) {
        if (bookName.isErrorEnabled() || bookAuthor.isErrorEnabled()) {
            new AlertDialog.Builder(activity)
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                    })
                    .setTitle(R.string.information)
                    .setMessage(R.string.set_valid_data)
                    .create()
                    .show();

            return false;
        }

        try {
            String name = bookName.getEditText().getText().toString();
            String author = bookAuthor.getEditText().getText().toString();
            String text = bookText.getText().toString();

            if (!name.equals(bookInfo.getName()) && name.length() > 0) {
                bookInfo.setName(name);
            }

            if (!author.equals(bookInfo.getAuthor()) && author.length() > 0) {
                bookInfo.setAuthor(author);
            }


            if (!text.equals(bookInfo.getText()) && text.length() > 0) {
                bookInfo.setText(text);
//                String[] words = text.
//                        trim().
//                        replaceAll("\\s+", " ").
//                        replaceAll("(\\.)+", "\\.").
//                        split(" ");
//
//                bookInfo.setWords(words);

                // add progressDialog !!!


            }
            new InsertBookAsyncTask(() -> {
            }, bookInfo).execute();
//                new FileReadingAndWriting().write(bookInfo.getFile(), text, (o, n) -> {
//                });

        } catch (Exception e) {
            e.printStackTrace();

            new AlertDialog.Builder(activity)
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                    })
                    .setTitle(R.string.information)
                    .setMessage(R.string.set_valid_data)
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
