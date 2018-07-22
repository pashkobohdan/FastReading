package com.pashkobohdan.fastreading.data.database;

import android.arch.persistence.room.Room;
import android.content.Context;

public class BookDAOHolder {

    private static AppDatabase database;
    private static Context context;

    public static void init(Context initContext) {
        context = initContext;
    }

    public static AppDatabase getDatabase() {
        if(database == null) {
            database = Room.databaseBuilder(context, AppDatabase.class,
                    "books_database").build();
        }
        return database;
    }
}
