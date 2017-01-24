package com.pashkobohdan.fastreading.library.fileSystem.newFileOpeningThread;

import java.io.File;

/**
 * Created by Bohdan Pashko on 24.01.17.
 */
@FunctionalInterface
public interface FileOpenResultSender {

    void send(File file);

}
