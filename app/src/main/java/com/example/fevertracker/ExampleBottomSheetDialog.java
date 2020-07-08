package com.example.fevertracker;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;


public class ExampleBottomSheetDialog extends BottomSheetDialogFragment {
    LocationActivity locationActivity;

    public void setLocationActivity(LocationActivity locationActivity) {
        this.locationActivity = locationActivity;
    }

    LinearLayout b1,b2;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout, container, false);
        b1 = v.findViewById(R.id.Cam);
        b2 = v.findViewById(R.id.Gal);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Cam(v);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gal(v);
            }
        });
        return v;
    }

    public void Cam(View view) throws IOException {
//        locationActivity.camera();
    }

    public void Gal(View view) {
//        locationActivity.gallery();
    }

}