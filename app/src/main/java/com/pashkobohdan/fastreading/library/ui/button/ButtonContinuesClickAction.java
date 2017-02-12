package com.pashkobohdan.fastreading.library.ui.button;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Bohdan Pashko on 12.02.17.
 */

public class ButtonContinuesClickAction {

    public static void setContinuesClickAction(View button, Runnable action, int delay) {
        Handler handler = new Handler();
        AtomicBoolean isClicked = new AtomicBoolean(false);
        AtomicBoolean isLongClick = new AtomicBoolean(false);


        button.setOnLongClickListener(new View.OnLongClickListener() {
            Runnable runnable;

            @Override
            public boolean onLongClick(View v) {
                isLongClick.set(true);
                runnable = () -> {
                    if (isClicked.get()) {
                        action.run();
                        handler.postDelayed(runnable, delay);
                    }
                };
                handler.postDelayed(runnable, delay);
                return false;
            }
        });

        button.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isClicked.set(false);

                if (!isLongClick.get()) {
                    action.run();
                }
                isLongClick.set(false);
            } else {
                isClicked.set(true);
            }
            return false;
        });

//        button.setOnClickListener(v -> {
//            if (!isLongClick.get()) {
//                action.run();
//            }
//            isLongClick.set(false);
//        });
    }


}
