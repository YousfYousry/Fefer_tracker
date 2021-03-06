package com.example.fevertracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import maes.tech.intentanim.CustomIntent;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class findUserAdmin extends AppCompatActivity implements ZBarScannerView.ResultHandler {
    //Vars
    boolean search_opened = false, listenningStarted = false, cameraStarted = false, buttOpened = true, userFoundBool = false, userFoundOnMap = false;
    int width, height;
    final int RequestCameraPermissionID = 1001;
    long max;
    ArrayList<userSearch> toPrint = new ArrayList<>();
    ArrayList<Long> Users = new ArrayList<>();
    String search_input, Qr_result;
    ZBarScannerView mScannerView;
    findUserAdmin findUserAdmin = this;
    Context context = this;
    profileForAdminFragment profileForAdminFragment;
    public static final String SHARED_PREFS = "sharedPrefs";

    //Views
    EditText input_search;
    ProgressBar progressBar;
    ListView userFound;
    Activity activity = this;
    FrameLayout frame_container;
    RelativeLayout butt;

    public void realTimeMap(View view) {
        if (!loadData("Id").isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            findUserOnMap();
        }
    }

    public void Geofence(View view) {
        startActivity(new Intent(getApplicationContext(), GeoFenceForAdmin.class));
        CustomIntent.customType(context, "left-to-right");
    }

    public void LocationHistory(View view) {
        startActivity(new Intent(getApplicationContext(), TrackerForAdmin.class));
        CustomIntent.customType(context, "left-to-right");
    }

    public void back(View view) {
        onBackPressed();
    }

    public void search_bar(View view) {
        if (!search_opened) {
            if (cameraStarted) {
                mScannerView.stopCamera();
                closeCam();
            }
            openSearch();
        } else {
            hideKeyBoard(input_search);
        }
    }

    public void qrScanner(View view) {
        if (search_opened) {
            closeSearch();
        }
        openQrScannerCam();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user_admin);

        initViews();
        if (userFoundBool) {
            userFoundFunc(loadData("Id"));
        } else {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    openSearch();
                }
            }, 1500);
        }
    }

    public void initViews() {
        if (!loadData("Id").isEmpty()) {
            userFoundBool = true;
        }
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        frame_container = findViewById(R.id.frame_container);
        butt = findViewById(R.id.butt);


        TypedValue tv = new TypedValue();
        FrameLayout frameLayout = findViewById(R.id.dummy);
        LinearLayout toolbar = findViewById(R.id.toolbarr);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
        RelativeLayout.LayoutParams param2 = (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            params.height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics()) + dpToPx(4);
            param2.height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        frameLayout.setLayoutParams(param2);
        toolbar.setLayoutParams(params);


        input_search = findViewById(R.id.input_search);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        userFound = findViewById(R.id.userFound);
        userFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                userFoundFunc(Long.toString(Users.get(position)));
            }
        });
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Member");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                max = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey() != null) {
                        if (Long.parseLong(ds.getKey()) > max) {
                            max = Long.parseLong(ds.getKey());
                        }
                    }
                }
                if (!listenningStarted) {
                    startListenning();
                    progressBar.setVisibility(View.GONE);
                    listenningStarted = true;
                } else {
                    if (!input_search.getText().toString().trim().isEmpty()) {
                        getUsers();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void startListenning() {
        input_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!input_search.getText().toString().trim().isEmpty()) {
                    if (userFoundBool) {
                        frame_container.setVisibility(View.GONE);
                        userFound.setVisibility(View.VISIBLE);
                    }
                    getUsers();
                } else {
                    if (userFoundBool) {
                        frame_container.setVisibility(View.VISIBLE);
                        userFound.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!input_search.getText().toString().isEmpty()) {
                    if (userFoundBool) {
                        frame_container.setVisibility(View.GONE);
                        userFound.setVisibility(View.VISIBLE);
                    }
                    getUsers();
                } else {
                    if (userFoundBool) {
                        frame_container.setVisibility(View.VISIBLE);
                        userFound.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    public void getUsers() {
        toPrint.clear();
        Users.clear();
        search_input = input_search.getText().toString();
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Member");
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.getKey() != null) {
                            if (isFound(search_input, ds.getKey())) {
                                if (!Users.contains(Long.parseLong(ds.getKey()))) {
                                    Users.add(Long.parseLong(ds.getKey()));
                                    String id = ds.getKey();
                                    String name = dataSnapshot.child(ds.getKey()).child("name").getValue().toString();
                                    String passport = dataSnapshot.child(ds.getKey()).child("passport").getValue().toString();
                                    int Status = 1;
                                    if (dataSnapshot.child(ds.getKey()).child("state").getValue() != null) {
                                        Status = Integer.parseInt(dataSnapshot.child(ds.getKey()).child("state").getValue().toString());
                                    }
                                    toPrint.add(new userSearch(name, passport, id, Status));
                                }
                            } else if (dataSnapshot.child(ds.getKey()).child("name").getValue() != null && isFound(search_input.toLowerCase().trim(), dataSnapshot.child(ds.getKey()).child("name").getValue().toString().toLowerCase().trim())) {
                                if (!Users.contains(Long.parseLong(ds.getKey()))) {
                                    Users.add(Long.parseLong(ds.getKey()));
                                    String id = ds.getKey();
                                    String name = dataSnapshot.child(ds.getKey()).child("name").getValue().toString();
                                    String passport = "";
                                    if (dataSnapshot.child(ds.getKey()).child("passport").getValue() != null) {
                                        passport = dataSnapshot.child(ds.getKey()).child("passport").getValue().toString();
                                    }
                                    int Status = 1;
                                    if (dataSnapshot.child(ds.getKey()).child("state").getValue() != null) {
                                        Status = Integer.parseInt(dataSnapshot.child(ds.getKey()).child("state").getValue().toString());
                                    }
                                    toPrint.add(new userSearch(name, passport, id, Status));
                                }
                            } else if (dataSnapshot.child(ds.getKey()).child("passport").getValue() != null && isFound(search_input, dataSnapshot.child(ds.getKey()).child("passport").getValue().toString().toLowerCase().trim())) {
                                if (!Users.contains(Long.parseLong(ds.getKey()))) {
                                    Users.add(Long.parseLong(ds.getKey()));
                                    String id = ds.getKey();
                                    String name = "";
                                    if (dataSnapshot.child(ds.getKey()).child("name").getValue() != null) {
                                        name = dataSnapshot.child(ds.getKey()).child("name").getValue().toString();
                                    }
                                    String passport = dataSnapshot.child(ds.getKey()).child("passport").getValue().toString();
                                    int Status = 1;
                                    if (dataSnapshot.child(ds.getKey()).child("state").getValue() != null) {
                                        Status = Integer.parseInt(dataSnapshot.child(ds.getKey()).child("state").getValue().toString());
                                    }
                                    toPrint.add(new userSearch(name, passport, id, Status));
                                }
                            }
                        }
                    }
                } catch (Exception e) {

                }

                userSearchAdapter adapter = new userSearchAdapter(getApplicationContext(), R.layout.search_users_adapter, toPrint);
                userFound.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void findUserOnMap() {
        userFoundOnMap = false;
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("CurrLocation");
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey() != null) {
                        for (DataSnapshot ds2 : dataSnapshot.child(ds.getKey()).getChildren()) {
                            if (ds2.getKey() != null) {
                                if (ds2.getKey().compareTo("Green") == 0) {
                                    for (DataSnapshot ds3 : dataSnapshot.child(ds.getKey()).child(ds2.getKey()).getChildren()) {
                                        if (ds3.getKey() != null) {
                                            if (ds3.getKey().compareTo(loadData("Id")) == 0) {
                                                userFoundOnMap = true;
                                            }
                                        }
                                        if (userFoundOnMap) {
                                            break;
                                        }
                                    }
                                }

                                if (ds2.getKey().compareTo("Yellow") == 0) {
                                    for (DataSnapshot ds3 : dataSnapshot.child(ds.getKey()).child(ds2.getKey()).getChildren()) {
                                        if (ds3.getKey() != null) {
                                            if (ds3.getKey().compareTo(loadData("Id")) == 0) {
                                                userFoundOnMap = true;
                                            }
                                        }
                                        if (userFoundOnMap) {
                                            break;
                                        }
                                    }
                                }

                                if (ds2.getKey().compareTo("Red") == 0) {
                                    for (DataSnapshot ds3 : dataSnapshot.child(ds.getKey()).child(ds2.getKey()).getChildren()) {
                                        if (ds3.getKey() != null) {
                                            if (ds3.getKey().compareTo(loadData("Id")) == 0) {
                                                userFoundOnMap = true;
                                            }
                                        }
                                        if (userFoundOnMap) {
                                            break;
                                        }
                                    }
                                }

                            }
                            if (userFoundOnMap) {
                                break;
                            }
                        }
                    }
                    if (userFoundOnMap) {
                        break;
                    }
                }

                progressBar.setVisibility(View.GONE);
                if (userFoundOnMap) {
                    Intent i = new Intent(getApplicationContext(), RealTimeTracker.class);
                    String UserId = loadData("Id");
                    i.putExtra("UserId", UserId);
                    startActivity(i);
                    CustomIntent.customType(context, "left-to-right");
                } else {
                    Toast.makeText(context, "User was not found on the map.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(context, "Something wrong happened.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void openQrScannerCam() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCam();
        } else {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.CAMERA},
                    RequestCameraPermissionID
            );
        }
    }

    public void startCam() {
        frame_container.setVisibility(View.VISIBLE);
        userFound.setVisibility(View.GONE);
        qrScannerAdmin qrScanner = new qrScannerAdmin();
        qrScanner.setAdmin(findUserAdmin);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                qrScanner).commit();
        cameraStarted = true;
    }

    public void qrCam(ZBarScannerView LocalmScannerView) {
        mScannerView = LocalmScannerView;
        LocalmScannerView.setResultHandler(this);
        LocalmScannerView.startCamera();
    }

    @Override
    public void handleResult(me.dm7.barcodescanner.zbar.Result result) {
        Qr_result = result.getContents().trim();
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Member");
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean found = false;
                try {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.getKey() != null) {
                            if (isFound(Qr_result, ds.getKey().toLowerCase().trim())) {
                                Toast.makeText(getApplicationContext(), "User is found in the server.", Toast.LENGTH_LONG).show();
                                userFoundFunc(ds.getKey());
                                found = true;
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "User was not found.", Toast.LENGTH_LONG).show();
                }
                if (!found) {
                    Toast.makeText(getApplicationContext(), "User was not found.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "permission denied.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    startCam();
                }
            }
            break;
        }
    }

    public void userFoundFunc(String id) {
        userFoundBool = true;
        saveData(id, "Id");

        Intent intent = new Intent();
        setResult(2, intent);

        if (search_opened) {
            closeSearch();
        }
        if (cameraStarted) {
            closeCam();
        }
        if (buttOpened) {
            slideViewHeight(butt, dpToPx(45), 0, 300);
            buttOpened = false;
        }
        frame_container.setVisibility(View.VISIBLE);
        userFound.setVisibility(View.GONE);

        profileForAdminFragment = new profileForAdminFragment();
        profileForAdminFragment.setContext(context);
        profileForAdminFragment.setfindUserAdmin(findUserAdmin);
        profileForAdminFragment.setLocalFile(Uri.parse(loadData("pic")));
        profileForAdminFragment.setId(id);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                profileForAdminFragment).commit();
    }

    public void LoadPic(final ImageView selectedImage) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("uploads").child(loadData("Id"));
        try {
            final File localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    saveData(Uri.fromFile(localFile).toString(), "pic");
                    selectedImage.setImageURI(Uri.fromFile(localFile));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(), "Error while downloading profile picture", Toast.LENGTH_SHORT).show();
                    saveData("", "pic");
                    selectedImage.setImageResource(R.drawable.avatar);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveData(String data, String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, data);
        editor.apply();
    }

    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }

    public void openSearch() {
        if (!search_opened) {
            if (userFoundBool) {
                slideViewHeight(butt, 0, dpToPx(45), 300);
                buttOpened = true;
            }
            slideViewWidth(input_search, dpToPx(40), width - dpToPx(100), 500);
            input_search.setHint("Enter User's name, passport or Id.");
            showSoftKeyboard(input_search);
            search_opened = true;
        }
    }

    public void closeSearch() {
        if (search_opened) {
            if (userFoundBool) {
                slideViewHeight(butt, dpToPx(45), 0, 300);
                buttOpened = false;
            }
            hideKeyBoard(input_search);
            slideViewWidth(input_search, width - dpToPx(100), dpToPx(40), 500);
            input_search.setHint("");
            input_search.setText("");
            input_search.clearFocus();
            search_opened = false;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        if (search_opened) {
            closeSearch();
            if (userFoundBool) {
                userFoundFunc(loadData("Id"));
            }
        } else if (cameraStarted) {
            closeCam();
            if (userFoundBool) {
                userFoundFunc(loadData("Id"));
            }
        } else {
            super.onBackPressed();
        }
    }

    public void closeCam() {
        frame_container.setVisibility(View.GONE);
        userFound.setVisibility(View.VISIBLE);
        cameraStarted = false;
    }

    public void slideViewWidth(final View view, int currentWidth, int newWidth, long duration) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentWidth, newWidth)
                .setDuration(duration);

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */

        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation1) {
                Integer value = (Integer) animation1.getAnimatedValue();
                view.getLayoutParams().width = value.intValue();
                view.requestLayout();
            }
        });

        /*  We use an animationSet to play the animation  */

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
    }

    public void slideViewHeight(final View view, int currentHeight, int newHeight, long duration) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentHeight, newHeight)
                .setDuration(duration);

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */

        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation1) {
                Integer value = (Integer) animation1.getAnimatedValue();
                view.getLayoutParams().height = value.intValue();
                view.requestLayout();
            }
        });

        /*  We use an animationSet to play the animation  */

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void hideKeyBoard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public boolean isFound(String p, String hph) {
        boolean Found = hph.indexOf(p) != -1 ? true : false;
        return Found;
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
}
