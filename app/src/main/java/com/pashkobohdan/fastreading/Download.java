package com.pashkobohdan.fastreading;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfo;
import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfoFactory;
import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfosList;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core.AnyBookOpeningResult;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core.BookReadingResult;
import com.pashkobohdan.fastreading.library.firebase.downloadBooks.FirebaseBook;
import com.pashkobohdan.fastreading.library.ui.lists.downloadFirebaseBooks.FirebaseBooksListAdapter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.pashkobohdan.fastreading.library.fileSystem.file.InternalStorageFileHelper.INTERNAL_FILE_EXTENSION;

public class Download extends AppCompatActivity {

    private ListView listView;
    private EditText searchText;

    private String textToSearching;
    private FirebaseBooksListAdapter firebaseBooksListAdapter;

    private List<FirebaseBook> books;
    private List<FirebaseBook> booksToListView = new LinkedList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);


        listView = (ListView) findViewById(R.id.download_firebase_books_list);
        searchText = (EditText) findViewById(R.id.firebase_books_search_text);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        /**
         * Data check
         */
        if (BookInfosList.getAll().size() == 0) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Loading local books info");
            progressDialog.show();

            for (File file : getCacheDir().listFiles((directory, fileName) -> fileName.endsWith(INTERNAL_FILE_EXTENSION))) {
                BookInfosList.add(BookInfoFactory.newInstance(file, this));
            }

            progressDialog.dismiss();
        }


        firebaseBooksListAdapter = new FirebaseBooksListAdapter(Download.this, booksToListView);
        listView.setAdapter(firebaseBooksListAdapter);


        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textToSearching = searchText.getText().toString().trim().toLowerCase();
                refreshBooksList();
            }
        });
        listView.setOnItemClickListener((parent, view, position, id) -> {
            FirebaseBook book = booksToListView.get(position);

            BookInfo newBookInfo = BookInfoFactory.createNewInstance(new BookReadingResult(book.getBookText(), book.getBookName(), book.getBookAuthor()), this);
            if (newBookInfo == null) {
                Toast.makeText(this, "File writing error", Toast.LENGTH_SHORT).show();
                return;
            }

            BookInfosList.getAll().add(newBookInfo);
            Toast.makeText(this, "Book write successfully", Toast.LENGTH_SHORT).show();

            books.remove(book);
            refreshBooksList();
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("books");
        myRef.limitToFirst(1000).orderByChild("bookName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                books = new LinkedList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    FirebaseBook book = postSnapshot.getValue(FirebaseBook.class);

                    if (!books.contains(book)) {

                        boolean isExist = false;
                        for (BookInfo bookInfo : BookInfosList.getAll()) {
                            if (book.getBookName().equals(bookInfo.getName())
                                    && book.getBookAuthor().equals(bookInfo.getAuthor())
                                    && book.getBookText().equals(bookInfo.getAllText())) {
                                isExist = true;
                                break;
                            }
                        }

                        if (!isExist) {
                            books.add(book);

                            refreshBooksList();
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Download.this, "Database error. Try later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshBooksList() {
        booksToListView.clear();

        if (textToSearching == null || textToSearching.equals("")) {
            booksToListView.addAll(books);
        } else {
            for (FirebaseBook book : books) {
                /**
                 * need fix (better algorithm)
                 */
                if (book.getBookName().toLowerCase().contains(textToSearching)
                        || book.getBookAuthor().toLowerCase().contains(textToSearching)) {
                    booksToListView.add(book);
                }
            }
        }

        firebaseBooksListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
