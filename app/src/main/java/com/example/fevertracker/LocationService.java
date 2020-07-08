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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Notification.DEFAULT_VIBRATE;
import static com.example.fevertracker.App.CHANNEL_1_ID;
import static com.example.fevertracker.App.CHANNEL_ID;

public class LocationService extends Service {

    Context context2 = this;
    private static final String TAG = "LocationActivity";
    public long INTERVAL = 500000; //5 seconds
    public long FASTEST_INTERVAL = 500000; // 5 seconds
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    //    String mLastUpdateTime;
    String NewLoc = "";
    public static final String SHARED_PREFS = "sharedPrefs";
    String Status = "Green", oldStatus = "Green", oldCity = "";
    boolean firstTime = false;
    boolean timeIn = false, pushed = true, dataCleared = false;
    private NotificationManagerCompat notificationManager;
    boolean firstTimeNot = true;
    String State = "", City = "", Address = "";
    Intent notificationIntent;
    public Timer timer;
    public TimerTask timerTask;
    long period = 10000;
    Loc loc = new Loc();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startTimer();
        StartStatusAndTimerListener();
//        notificationManager = NotificationManagerCompat.from(this);
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
//
//        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel serviceChannel = new NotificationChannel(
                    "serviceNotificationChannelId",
                    "Hidden Notification Service",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notifManager.createNotificationChannel(serviceChannel);

            Intent hidingIntent = new Intent(this, NotificationBroadcastReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    1,
                    hidingIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            Notification notification = new Notification.Builder(this, "serviceNotificationChannelId")
                    .setContentTitle("Hide Notification Example")
                    .setContentText("To hide me, click and uncheck \"Hidden Notification Service\"")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .getNotification();
            startForeground(1, notification);
        } else {
            Notification.Builder builder = new Notification.Builder(this);
            this.startForeground(-1, builder.getNotification());
            stopSelf();
        }
//        notificationIntent = new Intent(this, LocationActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0, notificationIntent, 0);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//        NotificationCompat.Builder notificationBuilder;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
//            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, TAG, NotificationManager.IMPORTANCE_DEFAULT);
//            notificationChannel.enableVibration(true);
//            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
//        } else {
//            notificationBuilder = new NotificationCompat.Builder(this);
//        }
//        notificationBuilder
////              .setContentTitle(notification.getTitle())
//                .setContentText(String.format("R.string.workfield_driver_refuse"))
//                //.setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
//                .setAutoCancel(true)
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                .setContentIntent(pendingIntent)
//                .setLargeIcon(icon)
//                .setColor(Color.RED)
//                .setSmallIcon(R.mipmap.ic_launcher);
//
//        notificationBuilder.setDefaults(DEFAULT_VIBRATE);
//        notificationBuilder.setLights(Color.YELLOW, 1000, 300);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(1, notificationBuilder.build());
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, TAG,
//                    NotificationManager.IMPORTANCE_HIGH);
//            notificationManager.createNotificationChannel(channel);
//
//            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                    .setContentTitle("Fever Tracker")
//                    .setContentText("Keep the app running for any announcement")
//                    .setSmallIcon(R.drawable.ic_launcher_round)
//                    .setContentIntent(pendingIntent)
//                    .build();
//            startForeground(1, notification);
//        } else {
//
//
//            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                    .setContentTitle("Fever Tracker")
//                    .setContentText("Keep the app running for any announcement")
//                    .setSmallIcon(R.drawable.ic_launcher_round)
//                    .setContentIntent(pendingIntent)
//                    .build();
//
//            startForeground(1, notification);
//
//            // startForeground(1, notification);
//        }
//        notificationManager.cancelAll();

        return START_NOT_STICKY;
    }

    public void push() {
        if (!pushed && mCurrentLocation != null) {
            NewLoc = "*" + mCurrentLocation.getLatitude() + "**" + mCurrentLocation.getLongitude() + "*";
            final String Id = loadData("Id");
            if (!Id.isEmpty() && !Status.isEmpty()) {
                if (!firstTime || Status.compareTo(oldStatus) != 0 || City.compareTo(oldCity) != 0) {
                    DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("CurrLocation");
                    reff.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() != null) {

                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    if (ds.getKey() != null) {
                                        FirebaseDatabase.getInstance().getReference().child("CurrLocation").child(ds.getKey()).child("Green").child(Id).removeValue();
                                        FirebaseDatabase.getInstance().getReference().child("CurrLocation").child(ds.getKey()).child("Yellow").child(Id).removeValue();
                                        FirebaseDatabase.getInstance().getReference().child("CurrLocation").child(ds.getKey()).child("Red").child(Id).removeValue();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    oldCity = City;
                    oldStatus = Status;
                    firstTime = true;
                }
                long milliseconds = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd");
                Date resultdate = new Date(milliseconds);

                getAddress();
//                if (!State.isEmpty() && !City.isEmpty()) {
                FirebaseDatabase.getInstance().getReference().child("Location/" + Id + "/Location History/" + sdf.format(resultdate) + "/" + State + "/" + City + "/" + (milliseconds / 1000L)).setValue(NewLoc);
//                }
//                if (!City.isEmpty()) {
                FirebaseDatabase.getInstance().getReference().child("CurrLocation").child(City).child(Status).child(Id).removeValue();
                FirebaseDatabase.getInstance().getReference().child("CurrLocation").child(City).child(Status).child(Id).child(Long.toString(milliseconds / 1000L)).setValue(NewLoc);
//                }
                pushed = true;
            }
        }
    }

    public void getAddress() {
        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);
            if (addresses.size() > 0) {
                State = addresses.get(0).getAdminArea().trim();
                City = addresses.get(0).getLocality().trim();
//                Address = addresses.get(0).getSubThoroughfare().trim();
                if (State == null||State.isEmpty()) {
                    State = "unKnown";
                }
                if (City == null||City.isEmpty()) {
                    City = "unKnown";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void StartStatusAndTimerListener() {
        DatabaseReference reff2 = FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("state");
        reff2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    status(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        DatabaseReference reff3 = FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("Notification");
        reff3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null && !dataSnapshot.getValue().toString().isEmpty()) {
                    Intent notificationIntent = new Intent(context2, LocationActivity.class);
                    saveData("n", "not");
                    PendingIntent pendingIntent = PendingIntent.getActivity(context2,
                            0, notificationIntent, 0);


                    if (dataSnapshot.getValue() != null) {
                        NotificationCompat.Builder notificationBuilder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            notificationBuilder = new NotificationCompat.Builder(context2, "channel5");
                            NotificationChannel notificationChannel = new NotificationChannel("channel5", TAG, NotificationManager.IMPORTANCE_DEFAULT);
                            notificationChannel.enableVibration(true);
                            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
                        } else {
                            notificationBuilder = new NotificationCompat.Builder(context2);
                        }
                        notificationBuilder
                                .setSmallIcon(R.drawable.ic_launcher_round)
                                .setContentTitle("Fever Tracker")
                                .setContentText(dataSnapshot.child("Notification").getValue().toString())
                                .setContentIntent(pendingIntent)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .build();

                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(2, notificationBuilder.build());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        DatabaseReference reff4 = FirebaseDatabase.getInstance().getReference().child("adminInfo").child("TimeInterval");
        reff4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    if (isNumric(dataSnapshot.getValue().toString())) {
                        timer.cancel();
                        timer = new Timer();
                        timerTask = new TimerTask() {
                            public void run() {
                                loc.create();
                            }
                        };
                        timer.schedule(timerTask, 0, Long.parseLong(dataSnapshot.getValue().toString()) * 1000); //
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        DatabaseReference reff5 = FirebaseDatabase.getInstance().getReference().child("adminInfo").child("announcement");
        reff5.addValueEventListener(new ValueEventListener() {
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

    public void startTimer() {
        createLocationRequest();
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                loc.create();
            }
        };
        timer.schedule(timerTask, 0, period);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mGoogleApiClient = new GoogleApiClient.Builder(context2)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(loc)
                .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class Loc implements LocationListener, GoogleApiClient.ConnectionCallbacks {
        public void create() {

            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
            pushed = false;
        }

        @Override
        public void onConnected(Bundle bundle) {
            startLocationUpdates();
        }

        @SuppressLint("MissingPermission")
        public void startLocationUpdates() {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this, Looper.getMainLooper());
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
        }

        @Override
        public void onLocationChanged(Location location) {
            mCurrentLocation = location;
            mGoogleApiClient.disconnect();
            push();
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