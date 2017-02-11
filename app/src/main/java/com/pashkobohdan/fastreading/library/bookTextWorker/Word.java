package com.pashkobohdan.fastreading.library.bookTextWorker;

/**
 * Created by Bohdan Pashko on 19.06.2016.
 */
public class Word {
    private String leftPart;
    private String centerLetter;
    private String rightPart;

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
