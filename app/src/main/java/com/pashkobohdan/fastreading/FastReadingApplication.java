package com.pashkobohdan.fastreading;

import android.app.Application;

import com.pashkobohdan.fastreading.data.database.BookDAOHolder;

public class FastReadingApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BookDAOHolder.init(getApplicationContext());
    }
}
