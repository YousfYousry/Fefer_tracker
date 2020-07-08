package com.example.fevertracker;

import android.graphics.Outline;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;
import androidx.annotation.RequiresApi;

public class ZoftinoCustomOutlineProvider extends ViewOutlineProvider {

    int roundCorner;

    public ZoftinoCustomOutlineProvider(int round) {
        roundCorner = round;
    }

    @Override
    public void getOutline(View view, Outline outline) {
            outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), roundCorner);
    }
}
