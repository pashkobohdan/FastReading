package com.pashkobohdan.fastreading.library.feedback;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by Bohdan Pashko on 18.02.17.
 */

public class EmailFeedback {
    private static final String DEVELOPER_EMAIL = "b.s.apps.company@gmail.com";


    public static boolean sendEmailToDeveloper(Activity activity, String theme, String text){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{DEVELOPER_EMAIL});
        i.putExtra(Intent.EXTRA_SUBJECT, theme);
        i.putExtra(Intent.EXTRA_TEXT   , text);
        try {
            activity.startActivity(Intent.createChooser(i, "Send mail..."));

            return true;
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();

            return false;
        }
    }
}
