package com.pashkobohdan.fastreading.library.fileSystem.fileReading.core;

/**
 * Created by Bohdan Pashko on 16.01.17.
 */
@FunctionalInterface
public interface PercentSender {

    /**
     * Just sending new percent value.
     *      percent = current word position / all words count
     *
     * @param oldValue - old percents value
     * @param newValue - new percents value
     */
    void refreshPercents(int oldValue, int newValue);
}