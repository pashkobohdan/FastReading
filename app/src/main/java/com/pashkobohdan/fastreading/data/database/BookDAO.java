package com.pashkobohdan.fastreading.data.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pashkobohdan.fastreading.data.dto.DBBookDTO;

import java.util.List;

@Dao
public interface BookDAO {

    @Query("SELECT * FROM DBBookDTO")
    List<DBBookDTO> getAllBooks();

    @Query("SELECT * FROM DBBookDTO WHERE id LIKE :bookId")
    List<DBBookDTO> bookByIdList(long bookId) ;

    @Delete
    void deleteBook(DBBookDTO books);

    @Query("UPDATE DBBookDTO SET currentWordNumber=:currentWord WHERE id = :bookId")
    void updateBookPosition(int currentWord, long bookId);

    @Query("UPDATE DBBookDTO SET author=:newAuthor WHERE id = :bookId")
    void updateBookAuthor(int newAuthor, long bookId);

    @Query("UPDATE DBBookDTO SET color=:newColor WHERE id = :bookId")
    void updateBookColor(int newColor, long bookId);

    @Query("UPDATE DBBookDTO SET currentSpeed=:newSpeed WHERE id = :bookId")
    void updateBookSpeed(int newSpeed, long bookId);

    @Query("UPDATE DBBookDTO SET lastOpeningDate=:newDate WHERE id = :bookId")
    void updateBookLasOpenDate(int newDate, long bookId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllBookDTO(DBBookDTO... books);
}
