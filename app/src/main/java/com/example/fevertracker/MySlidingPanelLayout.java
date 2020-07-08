package com.example.fevertracker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MySlidingPanelLayout extends SlidingUpPanelLayout {
    public MySlidingPanelLayout(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("drrrrrrrrrrrrrrrrrrrraaaaaaaaaaaaaaaaaaaaaaaaaaaaggggggggggiiiiiiiinnnnnnnnggggggggg");
        return false;
    }
}