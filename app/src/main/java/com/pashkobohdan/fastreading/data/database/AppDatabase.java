package com.pashkobohdan.fastreading.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.pashkobohdan.fastreading.data.dto.DBBookDTO;


@Database(entities = {DBBookDTO.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract BookDAO getBookDAO();
}
