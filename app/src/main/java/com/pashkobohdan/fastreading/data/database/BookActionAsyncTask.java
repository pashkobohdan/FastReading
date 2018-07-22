package com.pashkobohdan.fastreading.data.database;

import android.os.AsyncTask;

import com.pashkobohdan.fastreading.data.dto.DBBookDTO;


public class BookActionAsyncTask extends AsyncTask<Void, Void, Void> {

    private Runnable postCallback;
    private Runnable backgroundCallback;
    private DBBookDTO bookDTO;

    public BookActionAsyncTask(Runnable postCallback, Runnable backgroundCallback, DBBookDTO bookDTO) {
        this.postCallback = postCallback;
        this.backgroundCallback = backgroundCallback;
        this.bookDTO = bookDTO;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        backgroundCallback.run();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        postCallback.run();
    }
}
