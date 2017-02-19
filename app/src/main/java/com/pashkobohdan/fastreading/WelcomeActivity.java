package com.pashkobohdan.fastreading;

import com.honu.aloha.BaseWelcomeActivity;
import com.honu.aloha.PageDescriptor;

/**
 * Created by Bohdan Pashko on 18.02.17.
 */

public class WelcomeActivity extends BaseWelcomeActivity {

    @Override
    public void createPages() {
        addPage(new PageDescriptor(R.string.easy_reading, R.string.easy_reading_text, R.drawable.click_to_play, R.color.welcome_1));
        addPage(new PageDescriptor(R.string.easy_management, R.string.easy_management_text, R.drawable.manage_panel, R.color.welcome_2));
        addPage(new PageDescriptor(R.string.reading_settings, R.string.reading_settings_text, R.drawable.reading_settings, R.color.welcome_3));

        addPage(new PageDescriptor(R.string.easy_book_opening, R.string.easy_book_opening_text, R.drawable.easy_book_opening, R.color.welcome_4));
        addPage(new PageDescriptor(R.string.books_management, R.string.books_management_text, R.drawable.books_manage, R.color.welcome_5));
        addPage(new PageDescriptor(R.string.sorst_and_settings, R.string.sorst_and_settings_text, R.drawable.all_books_settings, R.color.welcome_6));
    }
}
