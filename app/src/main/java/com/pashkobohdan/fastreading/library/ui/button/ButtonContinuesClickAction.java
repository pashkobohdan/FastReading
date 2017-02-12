package com.pashkobohdan.fastreading.library.ui.button;

import android.view.View;
import android.widget.Button;

/**
 * Created by bohdan on 12.02.17.
 */

public class ButtonContinuesClickAction {

    public final void setContinuesClickAction(Button button, Runnable action, int delay){
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

}
