package com.example.fevertracker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Toast;

public class EditTextC extends androidx.appcompat.widget.AppCompatEditText {

    announcement anouncement;

    public void setAnouncement(announcement anouncement) {
        this.anouncement = anouncement;
    }

    public EditTextC(Context context, AttributeSet attrs,
                     int defStyle) {
        super(context, attrs, defStyle);

    }

    public EditTextC(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public EditTextC(Context context) {
        super(context);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        if (anouncement != null) {
            anouncement.setButtons(selStart, selEnd);
        }
    }

}
