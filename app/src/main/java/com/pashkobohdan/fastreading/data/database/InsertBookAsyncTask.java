package com.pashkobohdan.fastreading.data.database;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.pashkobohdan.fastreading.data.dto.DBBookDTO;
import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfosList;

public class InsertBookAsyncTask extends AsyncTask<Void, Void, Void>{

    private Runnable postCallback;
    private DBBookDTO bookDTO;

    public InsertBookAsyncTask(Runnable postCallback, DBBookDTO bookDTO) {
        this.postCallback = postCallback;
        this.bookDTO = bookDTO;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        BookDAOHolder.getDatabase().getBookDAO().insertAllBookDTO(bookDTO);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        new Handler(Looper.getMainLooper()).post(()-> {
            BookInfosList.add(bookDTO);
            postCallback.run();
        });
    }
}
