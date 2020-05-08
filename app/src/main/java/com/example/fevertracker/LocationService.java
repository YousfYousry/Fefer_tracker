package com.example.fevertracker;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import static android.app.Notification.DEFAULT_VIBRATE;
import static com.example.fevertracker.App.CHANNEL_1_ID;
import static com.example.fevertracker.App.CHANNEL_ID;

public class LocationService extends Service {

    Context context2 = this;
    private static final String TAG = "LocationActivity";
    public long INTERVAL = 5000; //5 seconds
    public long FASTEST_INTERVAL = 5000; // 5 seconds
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;
    String NewLoc = "";
    public static final String SHARED_PREFS = "sharedPrefs";
    String Status = "Green", oldStatus = "Green";
    boolean firstTime = false;
    boolean timeIn = false, pushed = false;
    private NotificationManagerCompat notificationManager;
    boolean firstTimeNot = true;

    @Override
    public void onCreate(){super.onCreate();}


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startTimer();
        StartStatusAndTimerListener();
        notificationManager = NotificationManagerCompat.from(this);
//        Intent notificationIntent = new Intent(this, LocationActivity.class);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0, notificationIntent, 0);
//
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("Fever Tracker")
//                .setContentText("Keep the app running for any announcement")
//                .setSmallIcon(R.drawable.ic_launcher_round)
//                .setContentIntent(pendingIntent)
//                .build();
//
//        startForeground(1, notification);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Intent notificationIntent = new Intent(this, LocationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, TAG, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableVibration(true);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
        } else {
            notificationBuilder = new NotificationCompat.Builder(this);
        }
        notificationBuilder
//              .setContentTitle(notification.getTitle())
                .setContentText(String.format("R.string.workfield_driver_refuse"))
              //.setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setLargeIcon(icon)
                .setColor(Color.RED)
                .setSmallIcon(R.mipmap.ic_launcher);

        notificationBuilder.setDefaults(DEFAULT_VIBRATE);
        notificationBuilder.setLights(Color.YELLOW, 1000, 300);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, TAG,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Fever Tracker")
                    .setContentText("Keep the app running for any announcement")
                    .setSmallIcon(R.drawable.ic_launcher_round)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(1, notification);
        } else {


            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Fever Tracker")
                    .setContentText("Keep the app running for any announcement")
                    .setSmallIcon(R.drawable.ic_launcher_round)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1, notification);

            // startForeground(1, notification);
        }
        return START_NOT_STICKY;
    }



    public void push(String loc) {
        String Id=loadData("Id");
        if (!Id.isEmpty()) {
            if (!firstTime) {
                FirebaseDatabase.getInstance().getReference().child("CurrLocation").child("Green" ).child(Id).removeValue();
                FirebaseDatabase.getInstance().getReference().child("CurrLocation").child("Yellow").child(Id).removeValue();
                FirebaseDatabase.getInstance().getReference().child("CurrLocation").child( "Red"  ).child(Id).removeValue();
                oldStatus = Status;
                firstTime = true;
            }

            String key = FirebaseDatabase.getInstance().getReference().child("Location").child(Id).child("tracker").push().getKey();
            FirebaseDatabase.getInstance().getReference().child("Location").child(Id).child("tracker").child(key).setValue(loc);
            if (Status.compareTo(oldStatus) != 0) {
//              FirebaseDatabase.getInstance().getReference().child("CurrLocation").child(oldStatus).child(Id).removeValue();
                FirebaseDatabase.getInstance().getReference().child("CurrLocation").child("Green").child(Id).removeValue();
                FirebaseDatabase.getInstance().getReference().child("CurrLocation").child("Yellow").child(Id).removeValue();
                FirebaseDatabase.getInstance().getReference().child("CurrLocation").child("Red").child(Id).removeValue();
            }
            FirebaseDatabase.getInstance().getReference().child("CurrLocation").child(Status).child(Id).setValue(loc);
            oldStatus = Status;
        }
    }

    public void StartStatusAndTimerListener() {
        DatabaseReference reff2 = FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id"));
        reff2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("state").getValue() != null) {
                    status(dataSnapshot.child("state").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        DatabaseReference reff3 = FirebaseDatabase.getInstance().getReference().child("adminInfo");
        reff3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("TimeInterval").getValue() != null) {
                    if (isNumric(dataSnapshot.child("TimeInterval").getValue().toString())) {
                        timer.cancel();
                        timeIn = true;
                        timer = new Timer();
                        timerTask = new TimerTask() {
                            public void run() {
                                pushed = false;
                                loc = new Loc();
                                loc.create();
                            }
                        };
                        timer.schedule(timerTask, 0, Long.parseLong(dataSnapshot.child("TimeInterval").getValue().toString()) * 1000); //
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        DatabaseReference reff4 = FirebaseDatabase.getInstance().getReference().child("adminInfo").child("announcement");
        reff4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!firstTimeNot) {
                    Intent notificationIntent = new Intent(context2, LocationActivity.class);
                    saveData("n", "not");
                    PendingIntent pendingIntent = PendingIntent.getActivity(context2,
                            0, notificationIntent, 0);


                    if (dataSnapshot.getValue() != null) {
                        NotificationCompat.Builder notificationBuilder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            notificationBuilder = new NotificationCompat.Builder(context2, CHANNEL_1_ID);
                            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_1_ID, TAG, NotificationManager.IMPORTANCE_DEFAULT);
                            notificationChannel.enableVibration(true);
                            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
                        } else {
                            notificationBuilder = new NotificationCompat.Builder(context2);
                        }
                        notificationBuilder
                                .setSmallIcon(R.drawable.ic_launcher_round)
                                .setContentTitle("Fever Tracker")
                                .setContentText(dataSnapshot.getValue().toString())
                                .setContentIntent(pendingIntent)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .build();


                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(2, notificationBuilder.build());
                    }
                }
                firstTimeNot = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    public void push() {
        if (!pushed) {
            if (mCurrentLocation != null) {
              //show(context2, mCurrentLocation.getLatitude() + "      " + mCurrentLocation.getLongitude());
                Log.d(TAG, mCurrentLocation.getLatitude() + "      " + mCurrentLocation.getLongitude());
                NewLoc = "*" + mCurrentLocation.getLatitude() + "**" + mCurrentLocation.getLongitude() + "**"+ Calendar.getInstance().getTime()+"*";
                push(NewLoc);
                pushed = true;
            }
        }
    }

//    static Toast toast = null;
//
//    public static void show(Context context, String text) {
//        try {
//            if (toast != null) {
//                toast.setText(text);
//            } else {
//                toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
//            }
//            toast.show();
//        } catch (Exception e) {
//            Looper.prepare();
//            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
//            Looper.loop();
//        }
//    }
    public boolean isNumric(String string) {
        boolean numeric = true;
        try {
            Long num = Long.parseLong(string);
        } catch (NumberFormatException e) {
            numeric = false;
        }
        return numeric;
    }
    public void status(String status) {
        if (Integer.parseInt(status) == 1) {
            Status = "Green";
        }
        if (Integer.parseInt(status) == 2) {
            Status = "Yellow";
        }
        if (Integer.parseInt(status) == 3) {
            Status = "Red";
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////
    public Timer timer;
    public TimerTask timerTask;
    long period = 600000;
    Loc loc;
    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                pushed = false;
                loc = new Loc();
                loc.create();
            }
        };
        timer.schedule(timerTask, 0, period); //
    }

//    public void destroy() {
//        loc = null;
//    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

///////////////////////////////////////////////////////////////////////////////////////////////

    public class Loc implements LocationListener, GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {
        Loc context = this;

        public void create() {
            if (!isGooglePlayServicesAvailable()) {
                stopForeground(true);
                stopSelf();
            }

            createLocationRequest();
            mGoogleApiClient = new GoogleApiClient.Builder(context2)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) context)
                    .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) context)
                    .build();
            mGoogleApiClient.connect();
        }

//
//        protected void stopLocationUpdates() {
//            if (mGoogleApiClient.isConnected()) {
//                LocationServices.FusedLocationApi.removeLocationUpdates(
//                        mGoogleApiClient, this);
//                Log.d(TAG, "Location update stopped .......................");
//            }
//        }

        private boolean isGooglePlayServicesAvailable() {
            int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context2);
            if (ConnectionResult.SUCCESS == status) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onConnected(Bundle bundle) {
            Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
            startLocationUpdates();
        }

        @SuppressLint("MissingPermission")
        public void startLocationUpdates() {
            if (mGoogleApiClient.isConnected()) {
                PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
        }

        @Override
        public void onLocationChanged(Location location) {
            mCurrentLocation = location;
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//            destroy();
            mGoogleApiClient.disconnect();
            push();
        }

        protected void createLocationRequest() {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(INTERVAL);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }
    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }
    public void saveData(String data, String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, data);
        editor.apply();
    }
}