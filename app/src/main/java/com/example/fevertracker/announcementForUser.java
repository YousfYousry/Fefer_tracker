package com.example.fevertracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import maes.tech.intentanim.CustomIntent;

public class announcementForUser extends AppCompatActivity {
    ListView announcementList;
    ArrayList<Announce> announce = new ArrayList<>();
    long max;
    Context context = this;

    public void back(View view){
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_for_user);

        TypedValue tv = new TypedValue();
        FrameLayout frameLayout = findViewById(R.id.dummy);
        LinearLayout toolbar = findViewById(R.id.select);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
        RelativeLayout.LayoutParams param2 = (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            params.height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics()) + dpToPx(4);
            param2.height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        frameLayout.setLayoutParams(param2);
        toolbar.setLayoutParams(params);

        announcementList = findViewById(R.id.announcementList);
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("adminInfo");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (announce != null) {
                    announce.clear();
                }
                max = 0;
                for (DataSnapshot ds : dataSnapshot.child("announcement").getChildren()) {
                    if (ds.getKey() != null) {
                        if (Long.parseLong(ds.getKey()) > max) {
                            max = Long.parseLong(ds.getKey());
                        }
                    }
                }

                for (long i = 0; i <= max; i++) {
                    if (dataSnapshot.child("announcement").child(Long.toString(i)).getValue() != null) {
                        announce.add(new Announce(Html.fromHtml(dataSnapshot.child("announcement").child(Long.toString(i)).getValue().toString()), i));
                    }
                }
                announceAdapter arrayAdapter = new announceAdapter(context, R.layout.announce_user, announce);
                announcementList.setAdapter(arrayAdapter);
//                Toast.makeText(context,"Announcement is edited",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public int dpToPx(int dip) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        return (int) px;
    }
    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "right-to-left");
    }
}
