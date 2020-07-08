package com.example.fevertracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RealTimeTracker extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap googleMap;
    ProgressBar progressBar;
    GoogleApiClient mGoogleApiClient;
    Timer timer;
    RelativeLayout show;
    FrameLayout backgroundScreen;
    TextView UserName, UserID, lastSeen, mode;
    ImageButton Button;
    int[] Time;
    LatLng[] Loc;
    int[] status;
    int idChoosen;
    ArrayList<Marker> allUsers = new ArrayList<>();
    int[] counter;
    boolean[] isOnline;
    boolean cameraMoved = false, initA = false, slide = false, following = false;
    int max = 0, oldMax = 0, panelHeight = 500;
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    ImageView selectedImage;
    String profileId;

    public void gps(View view) {
        if (!following) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Loc[idChoosen], googleMap.getCameraPosition().zoom));
            Button.setImageResource(R.drawable.ic_gps_fixed);
            following = true;
        } else {
            Button.setImageResource(R.drawable.ic_gps_not_fixed_black_24dp);
            following = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_tracker);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                profileId = null;
            } else {
                profileId = extras.getString("UserId");
            }
        } else {
            profileId = (String) savedInstanceState.getSerializable("UserId");
        }
        initVar();
        initMap();
    }

    public void startLiveListenning() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                GetNumberOfUsers();
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    public void GetNumberOfUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Member");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                max = (int) dataSnapshot.getChildrenCount();
                getUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getUsers() {

        if (!initA || oldMax != max) {
            Time = new int[max + 1];
            Loc = new LatLng[max + 1];
            status = new int[max + 1];
            isOnline = new boolean[max + 1];
            counter = new int[max + 1];
            allUsers.clear();
            for (int i = 0; i < max + 1; i++) {
                allUsers.add(null);
            }
            initA = true;
            oldMax = max;
        }


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
                                            for (DataSnapshot ds4 : dataSnapshot.child(ds.getKey()).child(ds2.getKey()).child(ds3.getKey()).getChildren()) {
                                                if (ds4.getKey() != null) {
                                                    int time = Integer.parseInt(ds4.getKey());
                                                    LatLng loc = GetLocFromString(dataSnapshot.child(ds.getKey()).child(ds2.getKey()).child(ds3.getKey()).child(ds4.getKey()).getValue().toString());
                                                    addMarker(1, Integer.parseInt(ds3.getKey()), loc, time);
                                                }
                                            }
                                        }
                                    }
                                }

                                if (ds2.getKey().compareTo("Yellow") == 0) {
                                    for (DataSnapshot ds3 : dataSnapshot.child(ds.getKey()).child(ds2.getKey()).getChildren()) {
                                        if (ds3.getKey() != null) {
                                            for (DataSnapshot ds4 : dataSnapshot.child(ds.getKey()).child(ds2.getKey()).child(ds3.getKey()).getChildren()) {
                                                if (ds4.getKey() != null) {
                                                    int time = Integer.parseInt(ds4.getKey());
                                                    LatLng loc = GetLocFromString(dataSnapshot.child(ds.getKey()).child(ds2.getKey()).child(ds3.getKey()).child(ds4.getKey()).getValue().toString());
                                                    addMarker(2, Integer.parseInt(ds3.getKey()), loc, time);
                                                }
                                            }
                                        }
                                    }
                                }

                                if (ds2.getKey().compareTo("Red") == 0) {
                                    for (DataSnapshot ds3 : dataSnapshot.child(ds.getKey()).child(ds2.getKey()).getChildren()) {
                                        if (ds3.getKey() != null) {
                                            for (DataSnapshot ds4 : dataSnapshot.child(ds.getKey()).child(ds2.getKey()).child(ds3.getKey()).getChildren()) {
                                                if (ds4.getKey() != null) {
                                                    int time = Integer.parseInt(ds4.getKey());
                                                    LatLng loc = GetLocFromString(dataSnapshot.child(ds.getKey()).child(ds2.getKey()).child(ds3.getKey()).child(ds4.getKey()).getValue().toString());
                                                    addMarker(3, Integer.parseInt(ds3.getKey()), loc, time);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (!cameraMoved) {
                    LatLngBounds bounds = builder.build();
                    int width = getResources().getDisplayMetrics().widthPixels;
                    int height = getResources().getDisplayMetrics().heightPixels;
                    int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                    googleMap.animateCamera(cu);
                    cameraMoved = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void addMarker(int i, int id, LatLng loc, int time) {
        long currTime = System.currentTimeMillis() / 1000L;
        boolean putMarker = false;
        if (Loc[id] != null) {
            if (time > Time[id]) {
                putMarker = true;
                Time[id] = time;
                Loc[id] = loc;
                counter[id] = 0;
            } else {
                counter[id]++;
            }
        } else {
            Time[id] = time;
            Loc[id] = loc;
            counter[id] = 241;
            putMarker = true;
        }

        if (counter[id] > 250) {
            counter[id] = 241;
        }

        if (counter[id] < 240 || (currTime - time) < 240) {
            isOnline[id] = true;
        } else {
            isOnline[id] = false;
        }
        if (!cameraMoved) {
            builder.include(loc);
        }
        int color = 122;

        if (putMarker) {
            if (i == 2) {
                color = 61;
            } else if (i == 3) {
                color = 0;
            }
            if (allUsers.get(id) == null) {
                allUsers.set(id, googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(color)).position(loc).title(Integer.toString(id))));
            } else {
                if (status[id] != i) {
                    allUsers.get(id).remove();
                    allUsers.set(id, googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(color)).position(loc).title(Integer.toString(id))));
                } else {
                    RealTimeTracker.MarkerAnimation.animateMarkerToGB(allUsers.get(id), loc, new LocationActivity.LatLngInterpolator.Spherical());
                }
            }
            status[id] = i;
            if (id == idChoosen) {
                if (slide) {
                    if (following) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Loc[idChoosen], googleMap.getCameraPosition().zoom));
                    }
                    updateDetails();
                }
            } else if (profileId != null && id == Integer.parseInt(profileId)) {
                idChoosen = Integer.parseInt(profileId);
                updateDetails();
                slideView(show, 0, panelHeight, 500);
                slide = true;
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Loc[idChoosen], 16));
                Button.setImageResource(R.drawable.ic_gps_fixed);
                following = true;
                cameraMoved = true;
                profileId = null;
            }
        }
    }

    public String TimeConverter(long unixSeconds) {
        Date date = new java.util.Date(unixSeconds * 1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public void slideView(final View view, int currentHeight, int newHeight, long duration) {

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

    public LatLng GetLocFromString(String location) {
        String temp = "";
        Matcher m = Pattern.compile("\\*([^\\*]+)\\*").matcher(location);
        while (m.find()) {
            if (m.group(1) != null) {
                if (temp.isEmpty()) {
                    temp = m.group(1);
                } else {
                    return new LatLng(Double.parseDouble(temp), Double.parseDouble(m.group(1)));
                }
            }
        }
        return null;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        timer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    public void initVar() {
        progressBar = findViewById(R.id.progressBar);
        show = findViewById(R.id.show);
        UserName = findViewById(R.id.UserName);
        UserID = findViewById(R.id.UserID);
        lastSeen = findViewById(R.id.Time);
        mode = findViewById(R.id.status);
        selectedImage = findViewById(R.id.profilePicture);
        Button = findViewById(R.id.Button);
        backgroundScreen = findViewById(R.id.backgroundScreen);
        backgroundScreen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Button.setImageResource(R.drawable.ic_gps_not_fixed_black_24dp);
                following = false;
                return false;
            }
        });

        show.post(new Runnable() {
            @Override
            public void run() {
                panelHeight = show.getHeight();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) show.getLayoutParams();
                params.height = 0;
                show.setLayoutParams(params);
                show.setVisibility(View.VISIBLE);
            }
        });
    }

    public void initMap() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapTracker);
        supportMapFragment.getMapAsync(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onMapReady(GoogleMap Map) {
        googleMap = Map;
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                idChoosen = Integer.parseInt(marker.getTitle());
                updateDetails();
                slideView(show, 0, panelHeight, 500);
                slide = true;
                return false;
            }
        });
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (slide) {
                    slideView(show, panelHeight, 0, 500);
                    slide = false;
                    Button.setImageResource(R.drawable.ic_gps_not_fixed_black_24dp);
                    following = false;
                }
            }
        });
        startLiveListenning();
    }

    public void updateDetails() {
        getPic(Integer.toString(idChoosen));

        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Member").child(Integer.toString(idChoosen));
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").getValue() != null) {
                    UserName.setText(dataSnapshot.child("name").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        UserID.setText(Integer.toString(idChoosen));
        lastSeen.setText(TimeConverter(Time[idChoosen]));
        if (isOnline[idChoosen]) {
            mode.setText("online");
            mode.setTextColor(Color.GREEN);
        } else {
            mode.setText("offline");
            mode.setTextColor(Color.RED);
        }
    }

    public void getPic(String id) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("uploads").child(id);
        try {
            final File localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    selectedImage.setImageURI(Uri.fromFile(localFile));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    System.out.println(exception);
                    selectedImage.setImageResource(R.drawable.avatar);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class MarkerAnimation {
        public static void animateMarkerToGB(final Marker marker, final LatLng finalPosition, final LocationActivity.LatLngInterpolator latLngInterpolator) {
            final LatLng startPosition = marker.getPosition();
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final Interpolator interpolator = new AccelerateDecelerateInterpolator();
            final float durationInMs = 2000;
            handler.post(new Runnable() {
                long elapsed;
                float t;
                float v;

                @Override
                public void run() {
                    // Calculate progress using interpolator
                    elapsed = SystemClock.uptimeMillis() - start;
                    t = elapsed / durationInMs;
                    v = interpolator.getInterpolation(t);
                    marker.setPosition(latLngInterpolator.interpolate(v, startPosition, finalPosition));
                    // Repeat till progress is complete.
                    if (t < 1) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (slide) {
            slideView(show, panelHeight, 0, 500);
            slide = false;
        } else {
            super.onBackPressed();
        }
    }
}
