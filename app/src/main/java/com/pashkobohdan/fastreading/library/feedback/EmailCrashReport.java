package com.pashkobohdan.fastreading.library.feedback;

import android.app.Activity;
import android.app.AlertDialog;

import com.pashkobohdan.fastreading.R;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Bohdan Pashko on 19.02.17.
 */

public class EmailCrashReport {

    public static void sendCrashReport(Activity activity, Throwable exception) {

        new AlertDialog.Builder(activity).setTitle(R.string.send_crash_report_title)
                .setMessage(R.string.send_crash_report_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    exception.printStackTrace(pw);

                    EmailFeedback.sendEmailToDeveloper(activity, "", sw.toString());
                })
                .setNegativeButton(R.string.no, (dialog, which) -> {
                }).create().show();

        exception.printStackTrace();

    }

}
