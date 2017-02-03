package com.pashkobohdan.fastreading.library.ui.lists.booksList;

import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfo;

/**
 * Interface (can be used as Lambda expression) definition for a callback
 * to be invoked when makes moething with BookInfo
 *
 * Created by Bohdan Pashko on 24.01.17.
 */

public interface BookEventListener {

    void run(BookInfo bookInfo);

}
