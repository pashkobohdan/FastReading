package com.pashkobohdan.fastreading;

import com.honu.aloha.BaseWelcomeActivity;
import com.honu.aloha.PageDescriptor;

/**
 * Created by bohdan on 18.02.17.
 */

public class WelcomeActivity extends BaseWelcomeActivity {

    @Override
    public void createPages() {
        addPage(new PageDescriptor(R.string.app_name, R.string.title_activity_settings, R.drawable.test, R.color.colorAccent));
//        addPage(new PageDescriptor(R.string.welcome_header_1, R.string.welcome_content_1, R.drawable.welcome_image_1, R.color.welcome_color_1));
//        addPage(new PageDescriptor(R.string.welcome_header_2, R.string.welcome_content_2, R.drawable.welcome_image_2, R.color.welcome_color_2));
    }

}
