package com.pashkobohdan.fastreading.data.dto;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "DBBookDTO")
public class DBBookDTO {

    @PrimaryKey()
    private long id;

    private String text;

    private String name;
    private String author;
    private int color;
    private int currentWordNumber;
    private int currentSpeed;
    private int lastOpeningDate;
    private boolean wasRead;

    @Ignore
    private String[] words;

    public DBBookDTO() {
        //Default constructor for Room
        id = System.currentTimeMillis();
    }

    @Ignore
    public DBBookDTO(String text, String name, String author, int color, int currentWordNumber, int currentSpeed, int lastOpeningDate, boolean wasRead) {
        id = System.currentTimeMillis();
        this.text = text;
        this.name = name;
        this.author = author;
        this.color = color;
        this.currentWordNumber = currentWordNumber;
        this.currentSpeed = currentSpeed;
        this.lastOpeningDate = lastOpeningDate;
        this.wasRead = wasRead;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getCurrentWordNumber() {
        return currentWordNumber;
    }

    public void setCurrentWordNumber(int currentWordNumber) {
        this.currentWordNumber = currentWordNumber;
    }

    public int getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(int currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public int getLastOpeningDate() {
        return lastOpeningDate;
    }

    public void setLastOpeningDate(int lastOpeningDate) {
        this.lastOpeningDate = lastOpeningDate;
    }

    public boolean isWasRead() {
        return true;
    }

    public void setWasRead(boolean wasRead) {
        this.wasRead = wasRead;
    }

    public String[] getWords(){
        if(words == null) {
             words = text.
                    trim().
                    replaceAll("\\s+", " ").
                    replaceAll("(\\.)+", "\\.").
                    split(" ");
        }
        return words;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DBBookDTO bookDTO = (DBBookDTO) o;
        return Objects.equals(text, bookDTO.text) &&
                Objects.equals(name, bookDTO.name) &&
                Objects.equals(author, bookDTO.author);
    }

    @Override
    public int hashCode() {

        return Objects.hash(text, name, author);
    }
}
