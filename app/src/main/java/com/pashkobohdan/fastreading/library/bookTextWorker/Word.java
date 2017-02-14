package com.pashkobohdan.fastreading.library.bookTextWorker;

/**
 * Created by Bohdan Pashko on 19.06.2016.
 */
public class Word {
    private String leftPart;
    private String centerLetter;
    private String rightPart;


    public static Word newInstance(String word) {
        if (word.length() == 0) {
            return new Word("", "", "");
        } else if (word.length() == 1) {
            return new Word("", word, "");
        } else if (word.length() > 1 && word.length() < 6) {
            return new Word(word.substring(0, 1), word.substring(1, 2), word.length() > 2 ? word.substring(2) : "");
        } else if (word.length() > 5 && word.length() < 10) {
            return new Word(word.substring(0, 2), word.substring(2, 3), word.substring(3));
        } else {
            return new Word(word.substring(0, 3), word.substring(3, 4), word.substring(4));
        }
    }

    public Word(String leftPart, String centerLetter, String rightPart) {
        this.leftPart = leftPart;
        this.centerLetter = centerLetter;
        this.rightPart = rightPart;
    }

    public String getLeftPart() {
        return leftPart;
    }

    public void setLeftPart(String leftPart) {
        this.leftPart = leftPart;
    }

    public String getCenterLetter() {
        return centerLetter;
    }

    public void setCenterLetter(String centerLetter) {
        this.centerLetter = centerLetter;
    }

    public String getRightPart() {
        return rightPart;
    }

    public void setRightPart(String rightPart) {
        this.rightPart = rightPart;
    }

    @Override
    public String toString() {
        return leftPart + centerLetter + rightPart;
    }
}
