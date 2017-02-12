package com.pashkobohdan.fastreading.library.fileSystem.newFileOpeningThread;

import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core.AnyBookOpeningResult;


/**
 * Created by Bohdan Pashko on 24.01.17.
 */
@FunctionalInterface
public interface FileOpenResultSender {

    void send(AnyBookOpeningResult file);

}
