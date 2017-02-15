package com.pashkobohdan.fastreading;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pashkobohdan.fastreading.library.firebase.downloadBooks.FirebaseBook;
import com.pashkobohdan.fastreading.library.ui.lists.downloadFirebaseBooks.FirebaseBooksListAdapter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Download extends AppCompatActivity {

    private List<FirebaseBook> books;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        listView = (ListView)findViewById(R.id.download_firebase_books_list) ;


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("books");
        myRef.limitToFirst(1000).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast.makeText(Download.this, "Data changing", Toast.LENGTH_SHORT).show();

                books = new LinkedList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    books.add(postSnapshot.getValue(FirebaseBook.class));

                    Toast.makeText(Download.this, "added : "+postSnapshot.getValue(FirebaseBook.class).toString(), Toast.LENGTH_SHORT).show();
                }

                FirebaseBooksListAdapter firebaseBooksListAdapter = new FirebaseBooksListAdapter(Download.this, books);
                listView.setAdapter(firebaseBooksListAdapter);

                Toast.makeText(Download.this, "Data changed !", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.AC);

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
