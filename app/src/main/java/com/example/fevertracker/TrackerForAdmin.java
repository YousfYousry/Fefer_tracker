package com.example.fevertracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.fevertracker.LocationActivity.SHARED_PREFS;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class TrackerForAdmin extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap googleMap;
    boolean CameraMoved = false;
    boolean dataUpdated = false;
    int hours = 0, minutes = 0, seconds = 0, loc = 0;
    ProgressBar progressBar;
    double speed = 1;
    String data, hoursString = "", minutesString = "", secondsString = "";
    TextView speedText;
    ImageView playImage, arrowFloat, reversePic, increaseSpeed;
    LinearLayout linearLayout;
    SeekBar seekBar;
    boolean play = false, moreOprion = false, loop = false, reverse = false;
    EditText editText;
    ArrayList<LatLng> TempLatLngA = new ArrayList<>();
    int choosenIndex = 0;
    Switch aSwitch;

    public void update(View view) {
        progressBar.setVisibility(View.VISIBLE);
        dataUpdated = false;
        getData();
    }

    public void showAllLoc(View view) {
        progressBar.setVisibility(View.VISIBLE);
        showAllLocations(0.0001);
    }

    public void IncreaseSpeed(View view) {
        if (speed < 2000) {
            speed *= 2;
            delay = (int) (2048f / speed);
            speedText.setText(("x" + Math.round(speed * 100.0) / 100.0 * mode));
//            if (speed > 500) {
//                increaseSpeed.setImageResource(R.drawable.ic_media_ffr);
//            }
        }
    }

    public void decreaseSpeed(View view) {
        speed /= 2;
        delay = (int) (2048f / speed);
        speedText.setText("x" + (Math.round(speed * 100.0) / 100.0 * mode));
//        if (speed < 500) {
//            increaseSpeed.setImageResource(R.drawable.ic_media_ff);
//        }
    }

    public void mode(View view) {
        if (reverse) {
            reverse = false;
            mode = 1;
            speedText.setText(("x" + Math.round(speed * 100.0) / 100.0 * mode));
            reversePic.setImageResource(R.drawable.ic_media);
        } else {
            reverse = true;
            mode = -1;
            speedText.setText(("x" + Math.round(speed * 100.0) / 100.0 * mode));
            reversePic.setImageResource(R.drawable.ic_media_rew);
        }
    }

    public void moreOption(View view) {
        if (moreOprion) {
            moreOprion = false;
            arrowFloat.setImageResource(R.drawable.arrow_up_float);
            linearLayout.setVisibility(View.GONE);
        } else {
            moreOprion = true;
            arrowFloat.setImageResource(R.drawable.arrow_down_float);
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    public void play(View view) {


//        if (marker == null) {
//            showMarker(TempLatLngA2.get(loc));
//            googleMap.addPolyline(new PolylineOptions().add(TempLatLngA2.get(loc)));
//            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(TempLatLngA2.get(loc))
//                    .zoom(googleMap.getCameraPosition().zoom).build()));
//        } else {
//            googleMap.addPolyline(new PolylineOptions().add(TempLatLngA2.get(loc)));
//            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(TempLatLngA2.get(loc))
//                    .zoom(googleMap.getCameraPosition().zoom).build()));
//            TrackerForAdmin.MarkerAnimation.animateMarkerToGB(marker, TempLatLngA2.get(loc), new LocationActivity.LatLngInterpolator.Spherical());
////            System.out.println(TempLatLngA2.get(i));
//        }
//        googleMap.clear();
//        try {
//            showMarker(TempLatLngA3.get(loc));
//        } catch (Exception e) {
//
//        }
        if (play) {
            play = false;
            running = false;
            thread = false;
//            choosenIndex = choosenIndexForThread;
            playImage.setImageResource(R.drawable.ic_media_play);
        } else {
            play = true;
            running = true;
            playImage.setImageResource(R.drawable.ic_media_pause);
            loopInAnotherThread();
        }
    }

    boolean running = false;
    int threadProgress;
    //    int choosenIndexForThread = 0;
    boolean thread = false;
    int delay = 2048;
    int mode = 1;

    private void loopInAnotherThread() {
        new Thread(new Runnable() {
            public void run() {
                if (choosenIndex >= timeSeconds.size()) {
                    choosenIndex = timeSeconds.size() - 1;
                } else if (choosenIndex < 0) {
                    choosenIndex = 0;
                }
                for (; choosenIndex >= 0 && choosenIndex < timeSeconds.size() && running; choosenIndex += mode) {
                    thread = true;
                    threadProgress = (int) ((((float) timeSeconds.get(choosenIndex)) / (86400f)) * 100f);
                    if (threadProgress > 100) {
                        threadProgress = 100;
                    } else if (threadProgress < 0) {
                        threadProgress = 0;
                    }
                    seekBar.setProgress(threadProgress + mode);
                    seekBar.setProgress(threadProgress);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                play = false;
                thread = false;
                running = false;
//                choosenIndex = choosenIndexForThread;
                playImage.setImageResource(R.drawable.ic_media_play);
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_for_admin);
        progressBar = findViewById(R.id.progressBar);
        playImage = findViewById(R.id.imageView2);
        arrowFloat = findViewById(R.id.imageView3);
        reversePic = findViewById(R.id.imageView4);
        aSwitch =findViewById(R.id.switch1);
        increaseSpeed = findViewById(R.id.increaseSpeed);
        linearLayout = findViewById(R.id.linearLayout);
        speedText = findViewById(R.id.speed);
        seekBar = findViewById(R.id.seekBar2);
        editText = findViewById(R.id.editText);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                loc = (int) ((((double) (progress)) / 100f) * ((double) (TempLatLngA3.size() - 1)));
                if (thread) {
//                    if (choosenIndexForThread >= timeSeconds.size()) {
//                        choosenIndexForThread = timeSeconds.size() - 1;
//                    } else if (choosenIndexForThread < 0) {
//                        choosenIndexForThread = 0;
//                    }
                    seconds = timeSeconds.get(choosenIndex);
                } else {
                    seconds = get((int) ((((double) (progress)) / 100f) * 86400f));
                }
                minutes = 0;
                hours = 0;
                while (seconds > 59) {
                    minutes += 1;
                    seconds -= 60;
                }
                while (minutes > 59) {
                    hours += 1;
                    minutes -= 60;
                }
                if (Integer.toString(seconds).length() < 2) {
                    secondsString = "0" + seconds;
                } else {
                    secondsString = Integer.toString(seconds);
                }

                if (Integer.toString(minutes).length() < 2) {
                    minutesString = "0" + minutes;
                } else {
                    minutesString = Integer.toString(minutes);
                }

                if (Integer.toString(hours).length() < 2) {
                    hoursString = "0" + hours;
                } else {
                    hoursString = Integer.toString(hours);
                }

                editText.setText(hoursString + ":" + minutesString + ":" + secondsString);

//                if (!loop) {
//                    loop = true;
//                    startTimer();
//                }

//                if (thread) {
//                    try {
//                        showMarker(TempLatLngA.get(choosenIndexForThread));
//                    } catch (Exception e) {
//
//                    }
//                } else {
                try {
                    showMarker(TempLatLngA.get(choosenIndex));
                } catch (Exception e) {

                }
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (play) {
                    play = false;
                    running = false;
//                    choosenIndex = choosenIndexForThread;
                    playImage.setImageResource(R.drawable.ic_media_play);
                    thread = false;
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapTracker);
        supportMapFragment.getMapAsync(this);
    }

    Marker marker;

    private void showMarker(LatLng currentLatLng) {
//        googleMap.clear();

//        int height = 150;
//        int width = 150;
//        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.person);
//        Bitmap b = bitmapdraw.getBitmap();
//        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
//        MarkerOptions options = new MarkerOptions().position(new LatLng(currentLatLng.latitude, currentLatLng.longitude)).title("Hello Maps");
//        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.self_icon_green));
//        try {
//            googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).position(currentLatLng));

        if (aSwitch.isChecked()) {
            if (marker == null) {
                googleMap.clear();
                marker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(currentLatLng));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, googleMap.getCameraPosition().zoom));
                googleMap.addPolyline(new PolylineOptions().add(currentLatLng));
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(currentLatLng)
                        .zoom(googleMap.getCameraPosition().zoom).build()));
            } else {
                googleMap.addPolyline(new PolylineOptions().add(currentLatLng));
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(currentLatLng)
                        .zoom(googleMap.getCameraPosition().zoom).build()));
                TrackerForAdmin.MarkerAnimation.animateMarkerToGB(marker, currentLatLng, new LocationActivity.LatLngInterpolator.Spherical());
//            System.out.println(TempLatLngA2.get(i));
            }
        } else {
            marker = null;
            googleMap.clear();
            marker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(currentLatLng));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, googleMap.getCameraPosition().zoom));
        }

        //            googleMap.addMarker();
//
//        } catch (Exception e) {
//
//        }
//        if (!CameraMoved) {
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,
//                    13));
//            CameraMoved = true;
//        }
    }

    public void getData() {
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Location").child(loadData("Id"));
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("tracker").getValue() != null) {
                    data = dataSnapshot.child("tracker").getValue().toString();
                    if (!dataUpdated) {
                        update();
                        dataUpdated = true;
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "No data is saved.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public Timer timer;
    public TimerTask timerTask;
    long period = 50;
    int i = 0;

    public void startTimer() {
        marker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(TempLatLngA2.get(i)));
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
//                try {

//                    if (marker==null) {
//                        marker =googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(TempLatLngA2.get(i)));
//                    } else {

//                    }

//                    showMarker(TempLatLngA2.get(i));
//                } catch (Exception e) {
//                    System.out.println(e);
//
//                }
                i++;
                googleMap.addPolyline(new PolylineOptions().add(TempLatLngA2.get(i)));
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(TempLatLngA2.get(i))
                        .zoom(13).build()));
//                TrackerForAdmin.MarkerAnimation.animateMarkerToGB(marker, TempLatLngA2.get(i), new LocationActivity.LatLngInterpolator.Spherical());
                System.out.println(TempLatLngA2.get(i));
                if (i >= TempLatLngA2.size() - 1) {
                    i = 0;
                    loop = false;
                    timer.cancel();
                }
            }
        };
        timer.schedule(timerTask, 0, period); //
    }

    public void update() {
        googleMap.clear();
        StringToCoord(data);
    }

    int x;
    ArrayList<Integer> timeSeconds = new ArrayList<>();

    public int get(int num) {
        int myNumber = num;
        int distance = Math.abs(timeSeconds.get(0) - myNumber);
        int idx = 0;
        for (int c = 1; c < timeSeconds.size(); c++) {
            int cdistance = Math.abs(timeSeconds.get(c) - myNumber);
            if (cdistance < distance) {
                idx = c;
                distance = cdistance;
            }
        }
        choosenIndex = idx;
        return timeSeconds.get(idx);
    }

    ArrayList<String> date = new ArrayList<>();

    public void StringToCoord(String data) {
        String temp = "", temp2 = "", month = "", day = "", Year = "", choosenDate = "2020  May  08";
        boolean add, arrange = false;
        i = 0;
        x = 0;
        Matcher m = Pattern.compile("\\*([^\\*]+)\\*").matcher(data);
        while (m.find()) {
            if (temp.isEmpty()) {
                temp = m.group(1);
            } else if (!temp.isEmpty() && temp2.isEmpty()) {
                temp2 = m.group(1);
            } else if (!temp.isEmpty() && !temp2.isEmpty()) {
                int i = 0, seconds = 0;
                String newStr = "*" + m.group(1).replace(" ", "**") + "*";
                Matcher l = Pattern.compile("\\*([^\\*]+)\\*").matcher(newStr);
                while (l.find()) {
                    i++;
                    if (i == 2) {
                        month = l.group(1);
                    } else if (i == 3) {
                        day = l.group(1);
                    } else if (i == 4) {
                        String timeHour = "*" + l.group(1).replace(":", "**") + "*";
                        Matcher t = Pattern.compile("\\*([^\\*]+)\\*").matcher(timeHour);
                        int y = 0;
                        while (t.find()) {
                            if (y == 0) {
                                seconds = Integer.parseInt(t.group(1)) * 60 * 60;
                            } else if (y == 1) {
                                seconds += Integer.parseInt(t.group(1)) * 60;
                            } else if (y == 2) {
                                seconds += Integer.parseInt(t.group(1));
                            }
                            y++;
                        }
                    } else if (i == 6) {
                        Year = l.group(1);
                    }
                }

                if (!date.contains(Year + "  " + month + "  " + day)) {
                    date.add(Year + "  " + month + "  " + day);
                }
                if (choosenDate.compareTo(Year + "  " + month + "  " + day) == 0) {
                    arrange = true;
                    timeSeconds.add(seconds);
                    TempLatLngA.add(new LatLng(Double.parseDouble(temp), Double.parseDouble(temp2)));
                }
                temp = "";
                temp2 = "";
            }
        }
        if (arrange) {
            int secTem;
            LatLng latLngTem;
            int n = timeSeconds.size();
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (timeSeconds.get(i) > timeSeconds.get(j)) {
                        secTem = timeSeconds.get(i);
                        timeSeconds.set(i, timeSeconds.get(j));
                        timeSeconds.set(j, secTem);

                        latLngTem = TempLatLngA.get(i);
                        TempLatLngA.set(i, TempLatLngA.get(j));
                        TempLatLngA.set(j, latLngTem);
                    }
                }
            }
            seekBar.setProgress((int) ((((float) timeSeconds.get(0)) / (86400f)) * 100f));
        }
        progressBar.setVisibility(View.GONE);
    }

    ArrayList<LatLng> TempLatLngA2 = new ArrayList<>();
    ArrayList<LatLng> TempLatLngA3 = new ArrayList<>();

    public void showAllLocations(double area) {
        progressBar.setVisibility(View.VISIBLE);
        boolean add;
        //modify area
        if (TempLatLngA.size() > 0) {
            for (int i = 0; i < TempLatLngA.size() - 1; i += 1) {
                LatLng TempLatLng = TempLatLngA.get(i);
                add = true;
                for (int x = 0; x < TempLatLngA3.size(); x++) {
//                    System.out.println(distance(TempLatLngA.get(i),TempLatLngA.get(i-1)));
                    if (distance(TempLatLngA.get(i), TempLatLngA3.get(x)) < area) {
                        add = false;
                        break;
                    }
                }

                if (add) {
                    TempLatLngA3.add(TempLatLng);
                }
            }
        }
        progressBar.setVisibility(View.GONE);
    }

    public double distance(LatLng latLng1, LatLng latLng2) {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.

        double lat1 = latLng1.latitude, lat2 = latLng2.latitude, lon1 = latLng1.longitude, lon2 = latLng2.longitude;


        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return (c * r * 1000);
    }

    public boolean isNumric(String string) {
        boolean numeric = true;
        try {
            Double num = Double.parseDouble(string);
        } catch (NumberFormatException e) {
            numeric = false;
        }
        return numeric;
    }

    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }

    @Override
    public void onMapReady(GoogleMap googleMap2) {
        googleMap = googleMap2;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(2.926219, 101.642033), 17));
        progressBar.setVisibility(View.VISIBLE);
        getData();
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

    public interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class Spherical implements LocationActivity.LatLngInterpolator {
            /* From github.com/googlemaps/android-maps-utils */
            @Override
            public LatLng interpolate(float fraction, LatLng from, LatLng to) {
                // http://en.wikipedia.org/wiki/Slerp
                double fromLat = toRadians(from.latitude);
                double fromLng = toRadians(from.longitude);
                double toLat = toRadians(to.latitude);
                double toLng = toRadians(to.longitude);
                double cosFromLat = cos(fromLat);
                double cosToLat = cos(toLat);
                // Computes Spherical interpolation coefficients.
                double angle = computeAngleBetween(fromLat, fromLng, toLat, toLng);
                double sinAngle = sin(angle);
                if (sinAngle < 1E-6) {
                    return from;
                }
                double a = sin((1 - fraction) * angle) / sinAngle;
                double b = sin(fraction * angle) / sinAngle;
                // Converts from polar to vector and interpolate.
                double x = a * cosFromLat * cos(fromLng) + b * cosToLat * cos(toLng);
                double y = a * cosFromLat * sin(fromLng) + b * cosToLat * sin(toLng);
                double z = a * sin(fromLat) + b * sin(toLat);
                // Converts interpolated vector back to polar.
                double lat = atan2(z, sqrt(x * x + y * y));
                double lng = atan2(y, x);
                return new LatLng(toDegrees(lat), toDegrees(lng));
            }

            private double computeAngleBetween(double fromLat, double fromLng, double toLat, double toLng) {
                // Haversine's formula
                double dLat = fromLat - toLat;
                double dLng = fromLng - toLng;
                return 2 * asin(sqrt(pow(sin(dLat / 2), 2) +
                        cos(fromLat) * cos(toLat) * pow(sin(dLng / 2), 2)));
            }
        }

    }
}
