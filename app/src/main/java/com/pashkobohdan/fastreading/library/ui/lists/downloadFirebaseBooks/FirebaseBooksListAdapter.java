package com.pashkobohdan.fastreading.library.ui.lists.downloadFirebaseBooks;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pashkobohdan.fastreading.R;
import com.pashkobohdan.fastreading.library.firebase.downloadBooks.FirebaseBook;

import java.util.List;

/**
 * Created by Bohdan Pashko on 15.02.17.
 */

public class FirebaseBooksListAdapter extends ArrayAdapter<FirebaseBook> {
    private List<FirebaseBook> values;

    private static class FirebaseBookHolder {
        TextView bookName;
        TextView bookAuthor;
        TextView bookText;

        public FirebaseBookHolder(View view){
            bookName = (TextView)view.findViewById(R.id.firebase_book_name);
            bookAuthor = (TextView)view.findViewById(R.id.firebase_book_author);
            bookText = (TextView)view.findViewById(R.id.firebase_book_text);
        }
    }

    public FirebaseBooksListAdapter(Activity context, List<FirebaseBook> eventList) {
        super(context, R.layout.download_firebase_book_row, eventList);

        values = eventList;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public FirebaseBook getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(final int position, final View convertView, @NonNull ViewGroup parent) {
        FirebaseBook item = values.get(position);

        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.download_firebase_book_row, null);
        }

        FirebaseBookHolder viewHolder = new FirebaseBookHolder(view);
        viewHolder.bookName.setText(item.getBookName());
        viewHolder.bookAuthor.setText(item.getBookAuthor());
        viewHolder.bookText.setText(getSubstring(item.getBookText(), 500));

        return view;
    }


    private String getSubstring(String string, int maxLength) {
        return string.length() > maxLength ? string.substring(0, maxLength - 1) : string;
    }
}
