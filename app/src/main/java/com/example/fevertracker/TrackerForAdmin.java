package com.example.fevertracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.content.ContextCompat;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    FrameLayout dragger;
    private static final String TAG = "TrackerForAdmin";
    GoogleMap googleMap, googleMapTemp;
    boolean CameraMoved = false;
    boolean dataUpdated = false;
    int hours = 0, minutes = 0, seconds = 0, loc = 0;
    ProgressBar progressBar;
    double speed = 1;
    String data, hoursString = "", minutesString = "", secondsString = "";
    TextView speedText, dateText, chooseDateText;
    ImageView playImage, arrowFloat, reversePic, increaseSpeed, decreaseSpeed, previous, next, all, imageSelectDate;
    LinearLayout linearLayout, select, selectDatesArrow, selectDates;
    SeekBar seekBar;
    boolean play = false, moreOprion = false, loop = false, reverse = false;
    ArrayList<LatLng> TempLatLngA = new ArrayList<>();
    int choosenIndex = 0;
    Switch aSwitch, CamSwitch;
    private AppCompatAutoCompleteTextView autoTextView;
    ArrayList<Dates> ToPrint = new ArrayList<>();
    ArrayList<PlacesClass> toPrintPlaces = new ArrayList<>();
    RelativeLayout relativeLayout, relativeLayoutMap, layout2;
    boolean days = true, manual = false;
    String text = "";
    Toast dateLodingToast, doneLoading;
    TextView TimeTextView;
    int i = 0, x;
    ArrayList<Integer> timeSeconds = new ArrayList<>();
    ArrayList<String> timeSecondsForAll = new ArrayList<>();
    ArrayList<Integer> timeIntervalForAll = new ArrayList<>();
    ArrayList<LatLng> printedLoc = new ArrayList<>();
    ArrayList<Integer> convertor = new ArrayList<>();
    ArrayList<Polyline> road = new ArrayList<>();
    ArrayList<Marker> locationStops = new ArrayList<>();
    DataSnapshot locData;
    ArrayList<String> date = new ArrayList<>();
    ArrayList<String> States = new ArrayList<>();
    ArrayList<String> Countries = new ArrayList<>();
    FrameLayout FrameLayout, exit;
    boolean popupSelectDates = false, searchBar = false, show = false, colappesed = false;
    ImageView search_bar;
    ListView chl, Placesl;
    Marker HistoryMarker;
    String coordenates;
    double panelHeight;
    SlidingUpPanelLayout layout1;
    RelativeLayout.LayoutParams layout2Param;
    int height = 0;
    double offset = 0;
    Marker newMarker;
    Polyline selectedRoad;
    int[] colour;
    PlacesAdapter adapter;
    int maxInSec = 300, circleRange = 500;

    public void update(View view) {
        if (controls) {
            progressBar.setVisibility(View.VISIBLE);
            dataUpdated = false;
            getData();
        }
    }

    ArrayList<Integer> heights = new ArrayList<>();

    public void setTotalHeight(int Individualheight, int position) {
//        totalHeight = 0;
//        heights.set(position, Individualheight);
//        for (int c = 0; c < heights.size(); c++) {
//            totalHeight += heights.get(c);
//        }
//        double LocalpanelHeight;
//        System.out.println("Height:------------------------" + totalHeight);
//        System.out.println("panelHeight:------------------------" + panelHeight);
//        if (totalHeight < panelHeight) {
//            LocalpanelHeight = ((double) height) - totalHeight;
//            System.out.println("LocalpanelHeight:------------------------" + LocalpanelHeight);
//            layout2Param.setMargins(0, (int) LocalpanelHeight - dpToPx(20), 0, 0);
//            layout2.setLayoutParams(layout2Param);
//        } else {
//            LocalpanelHeight = ((double) height) - (0.5d * (double) height);
//            System.out.println("LocalpanelHeight:------------------------" + LocalpanelHeight);
//            layout2Param.setMargins(0, (int) LocalpanelHeight - dpToPx(20), 0, 0);
//            layout2.setLayoutParams(layout2Param);
//        }
        if (!colappesed) {
            layout1.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            colappesed = true;
        }
    }

    public void show(View view) {
        if (!show) {
            locationfinalAnalizer();
        } else {
            googleMap.clear();
            for (int i = 0; i < TempLatLngA.size(); i++) {
                PutAMarker(TempLatLngA.get(i), null, "", 0);
            }
            show = false;
        }
    }

    int lastPos = -1, lastLoc = -1, last = -1;
    View oldView;

    public void changeColor(View view, int position) {
        if (last != -1) {
            adapter.setViewColor(last);
            oldView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            colour[last] = 0;
            last = -1;
        }
        view.setBackgroundColor(Color.LTGRAY);
        colour[position] = 1;
        last = position;
        oldView = view;
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public String roundTime(double value) {
        String secondsString = "", minutesString = "", hoursString = "";
        int minutes = 0;
        int hours = 0;
        while (value > 59) {
            minutes += 1;
            value -= 60;
        }
        while (minutes > 59) {
            hours += 1;
            minutes -= 60;
        }
        if (Integer.toString((int) value).length() < 2) {
            secondsString = "0" + value;
        } else {
            secondsString = Integer.toString((int) value);
        }

        if (Integer.toString(minutes).length() < 2) {
            minutesString = "0" + minutes;
        } else {
            minutesString = Integer.toString(minutes);
        }

        if (Integer.toString(hours).length() < 2) {
            if (hours == 0) {
                hoursString = "12";
            } else {
                hoursString = "0" + hours;
            }
        } else {
            hoursString = Integer.toString(hours);
        }
        return (hoursString + ":" + minutesString + ":" + secondsString);
    }

    public void selectRoad(int pos) {
        lastPos = pos;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        int i;
        for (i = convertor.get(pos - 1); i < convertor.get(pos + 1); i++) {
            road.get(i).remove();
            road.set(i, googleMap.addPolyline(new PolylineOptions().add(TempLatLngAForAllLocations.get(i)).add(TempLatLngAForAllLocations.get(i + 1)).width(10f).color(Color.RED)));
            builder.include(TempLatLngAForAllLocations.get(i));

            if (i > convertor.get(pos - 1)) {
                Location prevLoc = new Location("service Provider");
                prevLoc.setLatitude(TempLatLngAForAllLocations.get(i).latitude);
                prevLoc.setLongitude(TempLatLngAForAllLocations.get(i).longitude);

                Location newLoc = new Location("service Provider");
                newLoc.setLatitude(TempLatLngAForAllLocations.get(i + 1).latitude);
                newLoc.setLongitude(TempLatLngAForAllLocations.get(i + 1).longitude);

                float bearing = prevLoc.bearingTo(newLoc);

                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.playred);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, dpToPx(15), dpToPx(15), false);
                locationStops.get(i).remove();
                locationStops.set(i, googleMap.addMarker(new MarkerOptions()
                        .position(TempLatLngAForAllLocations.get(i))
                        .title(timeSecondsForAll.get(i))
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).anchor(0.5f, 0.5f).rotation(bearing)));
            }
        }
        builder.include(TempLatLngAForAllLocations.get(i));
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        googleMap.animateCamera(cu);
    }

    public void selectPos(int pos) {
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.stop);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap LocationStop = Bitmap.createScaledBitmap(b, dpToPx(20), dpToPx(20), false);
        locationStops.get(convertor.get(pos)).remove();
        locationStops.set(convertor.get(pos), googleMap.addMarker(new MarkerOptions()
                .position(printedLoc.get(pos))
                .icon(BitmapDescriptorFactory.fromBitmap(LocationStop)).anchor(0.5f, 0.5f)));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(printedLoc.get(pos), 15));
        lastLoc = pos;
    }

    public void deselectRoad() {
        if (lastPos != -1) {
            for (int i = convertor.get(lastPos - 1); i < convertor.get(lastPos + 1); i++) {
                road.get(i).remove();
                road.set(i, googleMap.addPolyline(new PolylineOptions().add(TempLatLngAForAllLocations.get(i)).add(TempLatLngAForAllLocations.get(i + 1)).width(8f).color(Color.BLUE)));
                if (i > convertor.get(lastPos - 1)) {
                    Location prevLoc = new Location("service Provider");
                    prevLoc.setLatitude(TempLatLngAForAllLocations.get(i).latitude);
                    prevLoc.setLongitude(TempLatLngAForAllLocations.get(i).longitude);

                    Location newLoc = new Location("service Provider");
                    newLoc.setLatitude(TempLatLngAForAllLocations.get(i + 1).latitude);
                    newLoc.setLongitude(TempLatLngAForAllLocations.get(i + 1).longitude);

                    float bearing = prevLoc.bearingTo(newLoc);

                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.play);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, dpToPx(15), dpToPx(15), false);
                    locationStops.get(i).remove();
                    locationStops.set(i, googleMap.addMarker(new MarkerOptions()
                            .position(TempLatLngAForAllLocations.get(i))
                            .title(timeSecondsForAll.get(i))
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).anchor(0.5f, 0.5f).rotation(bearing)));
                }

            }
            lastPos = -1;
        }
    }

    public void deselectPos() {
        if (lastLoc != -1) {
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.stopblue);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap LocationStop = Bitmap.createScaledBitmap(b, dpToPx(20), dpToPx(20), false);
            locationStops.get(convertor.get(lastLoc)).remove();
            locationStops.set(convertor.get(lastLoc), googleMap.addMarker(new MarkerOptions()
                    .position(printedLoc.get(lastLoc))
                    .icon(BitmapDescriptorFactory.fromBitmap(LocationStop)).anchor(0.5f, 0.5f)));
            lastLoc = -1;
        }
    }

    public void setCamera() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int x = 0; x < TempLatLngAForAllLocations.size(); x++) {
            builder.include(TempLatLngAForAllLocations.get(x));
        }
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        googleMap.animateCamera(cu);
    }

    private static final double LN2 = 0.6931471805599453;
    private static final int WORLD_PX_HEIGHT = 256;
    private static final int WORLD_PX_WIDTH = 256;
    private static final int ZOOM_MAX = 21;

    public int getBoundsZoomLevel(LatLngBounds bounds, int mapWidthPx, int mapHeightPx) {

        LatLng ne = bounds.northeast;
        LatLng sw = bounds.southwest;

        double latFraction = (latRad(ne.latitude) - latRad(sw.latitude)) / Math.PI;

        double lngDiff = ne.longitude - sw.longitude;
        double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;

        double latZoom = zoom(mapHeightPx, WORLD_PX_HEIGHT, latFraction);
        double lngZoom = zoom(mapWidthPx, WORLD_PX_WIDTH, lngFraction);

        int result = Math.min((int) latZoom, (int) lngZoom);
        return Math.min(result, ZOOM_MAX);
    }

    private double latRad(double lat) {
        double sin = Math.sin(lat * Math.PI / 180);
        double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
        return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
    }

    private double zoom(int mapPx, int worldPx, double fraction) {
        return Math.floor(Math.log(mapPx / worldPx / fraction) / LN2);
    }

    class GetPlaces extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;
        private Context context;
        private String[] placeName;
        private String[] imageUrl;
        private ListView listView;

        public GetPlaces(Context context, ListView listView) {
            // TODO Auto-generated constructor stub
            this.context = context;
            this.listView = listView;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            dialog.dismiss();

            listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_expandable_list_item_1, placeName));
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(true);
            dialog.setMessage("Loading..");
            dialog.isIndeterminate();
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            PlacesService service = new PlacesService("AIzaSyCCvayX5xNZCxW_J4hyX4v6GFo6pQaH1gI");
            List<PlaceClass> findPlaceClasses = service.findPlaces(2.922064, 101.651581, "");  // hq for hospital
            // atm for ATM

            placeName = new String[findPlaceClasses.size()];
            imageUrl = new String[findPlaceClasses.size()];

            for (int i = 0; i < findPlaceClasses.size(); i++) {

                PlaceClass placeClassDetail = findPlaceClasses.get(i);
                placeClassDetail.getIcon();

                System.out.println(placeClassDetail.getName());
                placeName[i] = placeClassDetail.getName();

                imageUrl[i] = placeClassDetail.getIcon();

            }
            return null;
        }

    }

    String PlaceName = "", Address = "";

    public void getAddress(LatLng Marker) {
        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(Marker.latitude, Marker.longitude, 1);
            if (addresses.size() > 0) {
                PlaceName = addresses.get(0).getLocality();
                if (PlaceName == null) {
                    PlaceName = "";
                }
                Address = CheckNullString(addresses.get(0).getPostalCode()).trim() + " " + PlaceName + " " + CheckNullString(addresses.get(0).getSubThoroughfare()).trim() + ","
                        + CheckNullString(addresses.get(0).getAdminArea()).trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void locationfinalAnalizer() {
        googleMap.clear();
        toPrintPlaces.clear();
        printedLoc.clear();
        convertor.clear();
        road.clear();
        locationStops.clear();
        double time = 0;
        double distance = 0;
        boolean firstlocation = false;
        for (int i = 0; i < TempLatLngAForAllLocations.size(); i++) {
            if (i == TempLatLngAForAllLocations.size() - 1) {
                PutAMarker(TempLatLngAForAllLocations.get(i), null, timeSecondsForAll.get(i), timeIntervalForAll.get(i));
            } else {
                PutAMarker(TempLatLngAForAllLocations.get(i), TempLatLngAForAllLocations.get(i + 1), timeSecondsForAll.get(i), timeIntervalForAll.get(i));
            }
            if (i > 0) {
                road.add(googleMap.addPolyline(new PolylineOptions().add(TempLatLngAForAllLocations.get(i - 1)).add(TempLatLngAForAllLocations.get(i)).width(8f).color(Color.BLUE)));
            }
            if (timeIntervalForAll.get(i) >= maxInSec) {
                getAddress(TempLatLngAForAllLocations.get(i));
                coordenates = Double.toString(TempLatLngAForAllLocations.get(i).latitude).substring(0, 10) + "," + Double.toString(TempLatLngAForAllLocations.get(i).longitude).substring(0, 10);
                for (int c = 0; c < 2; c++) {
                    if (c == 0 && firstlocation) {
                        toPrintPlaces.add(new PlacesClass("", "", "", roundTime(time), (round(distance / 1000d, 1)) + " km", ""));
                        time = 0;
                        distance = 0;
                        heights.add(0);
                        convertor.add(i);
                        printedLoc.add(null);
                    } else if (c == 1) {
                        toPrintPlaces.add(new PlacesClass(PlaceName, timeSecondsForAll.get(i), Address, "", "", coordenates));
                        time = 0;
                        heights.add(0);
                        convertor.add(i);
                        printedLoc.add(TempLatLngAForAllLocations.get(i));
                        firstlocation = true;
                    }
                }
                CircleOptions circleOptions = new CircleOptions()
                        .center(TempLatLngAForAllLocations.get(i))
                        .strokeColor(Color.argb(0, 70, 70, 70))
                        .fillColor(Color.argb(50, 150, 150, 150))
                        .radius(circleRange);
                googleMap.addCircle(circleOptions);
            } else {
                time += timeIntervalForAll.get(i);
            }
            if (i < TempLatLngAForAllLocations.size() - 1) {
                distance += distance(new LatLng(TempLatLngAForAllLocations.get(i).latitude, TempLatLngAForAllLocations.get(i).longitude), new LatLng(TempLatLngAForAllLocations.get(i + 1).latitude, TempLatLngAForAllLocations.get(i + 1).longitude));
            }
        }
//            new GetPlaces(this, Placesl).execute();
        colappesed = false;
        int index = printedLoc.size();
//            if (printedLoc.get(index) == null) {
//                printedLoc.remove(index);
//                convertor.remove(index);
//                ToPrintPlaces.remove(index);
//            }
        colour = new int[index];
        for (int i = 0; i < index; i++) {
            colour[i] = 0;
        }
        adapter = new PlacesAdapter(getApplicationContext(), R.layout.location_stops_layout, toPrintPlaces);
        adapter.setColor(colour);
        adapter.setTrackerForAdmin(this);
        Placesl.setAdapter(adapter);
        setCamera();
        lastPos = -1;
        lastLoc = -1;
        last = -1;
        HistoryMarker = null;
        Placesl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeColor(view, position);
                if (printedLoc.get(position) != null) {
                    deselectRoad();
                    deselectPos();
                    selectPos(position);
                } else {
                    deselectPos();
                    deselectRoad();
                    selectRoad(position);
                }
            }
        });
        show = true;
        layout1.setEnabled(true);
        if (layout1.getPanelState() != SlidingUpPanelLayout.PanelState.ANCHORED) {
            layout1.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        }
    }

    public String CheckNullString(String string) {
        if (string == null) {
            return "";
        } else {
            return string;
        }
    }

    public void back(View view) {
        onBackPressed();
    }

    public void exit(View view) {
        exit.setVisibility(View.GONE);
        selectDatesArrow.setVisibility(View.VISIBLE);
        autoTextView.setVisibility(View.GONE);
        autoTextView.setText("");
        imageSelectDate.setImageResource(R.drawable.arrow_down_float);
        slideView(FrameLayout, FrameLayout.getLayoutParams().height, 0, 100L);
        popupSelectDates = false;
        search_bar.setImageResource(R.drawable.ic_search_black_24dp);
        searchBar = false;
    }

    public void search_bar(View view) {
        if (!searchBar) {
            selectDatesArrow.setVisibility(View.GONE);
            autoTextView.setVisibility(View.VISIBLE);
            exit.setVisibility(View.VISIBLE);
            search_bar.setImageResource(R.drawable.ic_close_black_24dp);
            searchBar = true;
        } else {
            selectDatesArrow.setVisibility(View.VISIBLE);
            autoTextView.setVisibility(View.GONE);
            autoTextView.setText("");
            imageSelectDate.setImageResource(R.drawable.arrow_down_float);
            slideView(FrameLayout, FrameLayout.getLayoutParams().height, 0, 100L);
            popupSelectDates = false;
            search_bar.setImageResource(R.drawable.ic_search_black_24dp);
            searchBar = false;
        }
    }

    public void popup(View view) {
        if (!popupSelectDates) {
            imageSelectDate.setImageResource(R.drawable.arrow_up_float);
            exit.setVisibility(View.VISIBLE);
            int size = ToPrint.size();
            if (size > 5) {
                size = 5;
            }
            int height = size * dpToPx(41);
            slideView(FrameLayout, FrameLayout.getLayoutParams().height, height, 300L);
//            FrameLayout.setVisibility(View.VISIBLE);
//            FrameLayout.animate()
//                    .alpha(1.0f)
//                    .setDuration(300)
//                    .setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    super.onAnimationEnd(animation);
//                    FrameLayout.setVisibility(View.VISIBLE);
//                }
//            });
            popupSelectDates = true;
        } else {
            imageSelectDate.setImageResource(R.drawable.arrow_down_float);
            slideView(FrameLayout, FrameLayout.getLayoutParams().height, 0, 300L);
            exit.setVisibility(View.GONE);

//            FrameLayout.animate()
//                    .alpha(0.0f)
//                    .setDuration(300)
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            super.onAnimationEnd(animation);
//                            FrameLayout.setVisibility(View.GONE);
//                        }
//                    });
            popupSelectDates = false;
        }
    }

    public void toggleState(View view) {
        if (aSwitch.isChecked()) {
            aSwitch.setText("Animation On   ");

        } else {
            aSwitch.setText("Animation Off   ");
        }
    }

    public void CameraFocus(View view) {
        if (CamSwitch.isChecked()) {
            CamSwitch.setText("Camera Focus On   ");

        } else {
            CamSwitch.setText("Camera Focus Off   ");
        }
    }


//    public void showAllLoc(View view) {
//        if (controls) {
//            marker = null;
//            showAllLocations(0.0001);
//        }
//    }

    public void nextLocation(View view) {
        if (controls && !play) {
            choosenIndex++;
            if (choosenIndex >= timeSeconds.size()) {
                choosenIndex = timeSeconds.size() - 1;
            } else if (choosenIndex < 0) {
                choosenIndex = 0;
            }
            if (choosenIndex >= 0 && choosenIndex < timeSeconds.size()) {
                manual = true;
                threadProgress = (int) ((((float) timeSeconds.get(choosenIndex)) / (86400f)) * 100f);
                if (threadProgress > 100) {
                    threadProgress = 100;
                } else if (threadProgress < 0) {
                    threadProgress = 0;
                }
                seekBar.setProgress(threadProgress + mode);
                seekBar.setProgress(threadProgress);
            }
            manual = false;
        }
    }

    public void previousLocation(View view) {
        if (controls && !play) {
            choosenIndex--;
            if (choosenIndex >= timeSeconds.size()) {
                choosenIndex = timeSeconds.size() - 1;
            } else if (choosenIndex < 0) {
                choosenIndex = 0;
            }
            if (choosenIndex >= 0 && choosenIndex < timeSeconds.size()) {
                manual = true;
                threadProgress = (int) ((((float) timeSeconds.get(choosenIndex)) / (86400f)) * 100f);
                if (threadProgress > 100) {
                    threadProgress = 100;
                } else if (threadProgress < 0) {
                    threadProgress = 0;
                }
                seekBar.setProgress(threadProgress + mode);
                seekBar.setProgress(threadProgress);
            }
            manual = false;
        }
    }

    public void IncreaseSpeed(View view) {
        if (controls) {
            if (speed < 2000) {
                speed *= 2;
                delay = (int) (2048f / speed);
                speedText.setText(("x" + Math.round(speed * 100.0) / 100.0 * mode));
                if (speed > 2000) {
                    increaseSpeed.setAlpha(100);
                }
                if (speed > 0.4) {
                    decreaseSpeed.setAlpha(255);
                }
            }
        }
    }

    public void decreaseSpeed(View view) {
        if (controls) {
            if (speed > 0.4) {
                speed /= 2;
                delay = (int) (2048f / speed);
                speedText.setText("x" + (Math.round(speed * 100.0) / 100.0 * mode));
                if (speed < 2000) {
                    increaseSpeed.setAlpha(255);
                }
                if (speed < 0.4) {
                    decreaseSpeed.setAlpha(100);
                }
            }
        }
    }

    public void mode(View view) {
        if (controls) {
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
        if (controls) {
            if (play) {
                play = false;
                playImage.setImageResource(R.drawable.ic_media_play);
            } else {
                play = true;
                playImage.setImageResource(R.drawable.ic_media_pause);
                playLocationAnimation();
            }
        }
    }

    int threadProgress;
    int delay = 2048;
    int mode = 1;
    Boolean drag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_for_admin);

        Placesl = (ListView) findViewById(R.id.Places);
        selectDates = findViewById(R.id.selectDates);
        imageSelectDate = findViewById(R.id.imageSelectDate);
        layout1 = findViewById(R.id.slidingPanel);
        layout1.setAnchorPoint(0.3f);
        dragger = findViewById(R.id.dragger);
        dragger.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                layout1.setEnabled(true);
                return false;
            }
        });
        Placesl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                layout1.setEnabled(false);
                return false;
            }
        });

        layout2 = findViewById(R.id.layout2);
        drag = false;
        layout1.post(new Runnable() {

            @Override
            public void run() {
                height = layout1.getHeight() - dpToPx(100);
                layout1.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                    @Override
                    public void onPanelSlide(View panel, float slideOffset) {
                        if (slideOffset > 0.18) {
                            layout2Param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (slideOffset * (double) height));
                            Placesl.setLayoutParams(layout2Param);
                        }
                    }

                    @Override
                    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                    }
                });
            }
        });

        panelHeight = 0;
        select = findViewById(R.id.select);
        FrameLayout frameLayout = findViewById(R.id.dummy);


        TypedValue tv = new TypedValue();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) select.getLayoutParams();
        LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) selectDates.getLayoutParams();
        RelativeLayout.LayoutParams param2 = (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();

        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            params.height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics()) + dpToPx(4);
            params2.height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics()) + dpToPx(4);
            param2.height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        frameLayout.setLayoutParams(param2);

        chooseDateText = findViewById(R.id.chooseDateText);
        exit = findViewById(R.id.exit);
        selectDatesArrow = findViewById(R.id.selectDatesArrow);
        search_bar = findViewById(R.id.search_bar);
        dateText = findViewById(R.id.dates);
        selectDates.setLayoutParams(params2);
        FrameLayout = findViewById(R.id.FrameLayout);
        select.setLayoutParams(params);
        progressBar = findViewById(R.id.progressBar);
        playImage = findViewById(R.id.imageView2);
        decreaseSpeed = findViewById(R.id.decreaseSpeed);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        all = findViewById(R.id.all);
        reversePic = findViewById(R.id.imageView4);
        arrowFloat = findViewById(R.id.imageView3);
        aSwitch = findViewById(R.id.switch1);
        CamSwitch = findViewById(R.id.CameraSwitch);
        increaseSpeed = findViewById(R.id.increaseSpeed);
        linearLayout = findViewById(R.id.linearLayout);
        speedText = findViewById(R.id.speed);
        seekBar = findViewById(R.id.seekBar2);
        TimeTextView = findViewById(R.id.editText);
        autoTextView = (AppCompatAutoCompleteTextView) findViewById(R.id.searchBar);
        relativeLayout = findViewById(R.id.Relative);
        relativeLayoutMap = findViewById(R.id.mapFragment);
        chl = (ListView) findViewById(R.id.Dates);
        doneLoading = Toast.makeText(getApplicationContext(), "Done loading", Toast.LENGTH_LONG);
        dateLodingToast = Toast.makeText(getApplicationContext(), text + " is loading", Toast.LENGTH_LONG);
        getData();
        initMap();
    }

    public void ShowControls() {
        playImage.setAlpha(255);
        decreaseSpeed.setAlpha(255);
        previous.setAlpha(255);
        next.setAlpha(255);
        increaseSpeed.setAlpha(255);
        reversePic.setAlpha(255);
        all.setAlpha(255);
    }

    public void hideControls() {
        playImage.setAlpha(100);
        decreaseSpeed.setAlpha(100);
        previous.setAlpha(100);
        next.setAlpha(100);
        increaseSpeed.setAlpha(100);
        reversePic.setAlpha(100);
        all.setAlpha(100);
    }

    boolean mapInit = false;
    Marker marker;

    public void reset() {
        doneLoading.cancel();
        dateLodingToast.cancel();
        speed = 1;
        delay = (int) (2048f / speed);
        speedText.setText("x" + (Math.round(speed * 100.0) / 100.0 * mode));
        reverse = false;
        mode = 1;
        speedText.setText(("x" + Math.round(speed * 100.0) / 100.0 * mode));
        reversePic.setImageResource(R.drawable.ic_media);
        moreOprion = false;
        arrowFloat.setImageResource(R.drawable.arrow_up_float);
        linearLayout.setVisibility(View.GONE);
        play = false;
        playImage.setImageResource(R.drawable.ic_media_play);
        days = true;
        DisableControls();
        relativeLayout.setVisibility(View.VISIBLE);
        relativeLayoutMap.setVisibility(View.GONE);
    }

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
                if (CamSwitch.isChecked()) {
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(currentLatLng)
                            .zoom(googleMap.getCameraPosition().zoom).build()));
                }
            } else {
                googleMap.addPolyline(new PolylineOptions().add(currentLatLng));
                if (CamSwitch.isChecked()) {
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(currentLatLng)
                            .zoom(googleMap.getCameraPosition().zoom).build()));
                    TrackerForAdmin.MarkerAnimation.animateMarkerToGB(marker, currentLatLng, new LocationActivity.LatLngInterpolator.Spherical());
                }
            }
        } else {
            marker = null;
            googleMap.clear();
            marker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(currentLatLng));
            if (CamSwitch.isChecked()) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, googleMap.getCameraPosition().zoom));
            }
        }
    }

    public void getData() {
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Location").child(loadData("Id")).child("Location History");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locData = dataSnapshot;
                if (locData.getValue() != null) {
                    if (!dataUpdated) {
                        dataUpdated = true;
                        ToPrint.clear();
                        date.clear();
                        States.clear();
                        Countries.clear();
                        timeSeconds.clear();
                        for (DataSnapshot ds : locData.getChildren()) {
                            if (ds.getKey() != null) {
                                date.add(ds.getKey());
                            }
                        }
//                        arrangeDate();
                        for (int i = 0; i < date.size(); i++) {
                            ToPrint.add(new Dates(date.get(i)));
                        }
                        DatesAdapter adapter = new DatesAdapter(getApplicationContext(), R.layout.dates_layout, ToPrint);
                        chl.setAdapter(adapter);
                        autoTextView.setAdapter(adapter);
                        SearchInit();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "There is no data saved!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void initMap() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapTracker);
        supportMapFragment.getMapAsync(this);
    }

    public void SearchInit() {
        autoTextView.setThreshold(1); //will start working from first character
        chl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                text = (String) ToPrint.get(position).getDate();
                chooseDateText.setText(text);
                days = false;
//                doneLoading.cancel();
                dateLodingToast = Toast.makeText(getApplicationContext(), text + " is loading", Toast.LENGTH_LONG);
                dateLodingToast.show();
                Looding = true;
                progressBar.setVisibility(View.VISIBLE);
                layout1.setEnabled(true);
                if (layout1.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    layout1.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
//                relativeLayout.setVisibility(View.GONE);
//                relativeLayoutMap.setVisibility(View.VISIBLE);
//                hideControls();

                if (googleMap != null) {
                    googleMap.clear();
                }
                exit(null);
                dateChoosed();

            }
        });
        autoTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                autoTextView.dismissDropDown();
                slideView(FrameLayout, FrameLayout.getLayoutParams().height, 0, 100L);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                autoTextView.dismissDropDown();
                ToPrint.clear();
                if (!TextUtils.isEmpty(autoTextView.getText())) {
                    boolean found = false;
                    for (int x = 0; x < date.size(); x++) {
                        if (isFound(autoTextView.getText().toString().toLowerCase(), date.get(x).toLowerCase())) {
                            ToPrint.add(new Dates(date.get(x)));
                            found = true;
                        }
                    }
                    int size = ToPrint.size();
                    if (size > 5) {
                        size = 5;
                    }
                    int height = size * dpToPx(41);
                    slideView(FrameLayout, FrameLayout.getLayoutParams().height, height, 300L);
                    if (!found) {
                        slideView(FrameLayout, FrameLayout.getLayoutParams().height, 0, 100L);
                    }
                    DatesAdapter adapter = new DatesAdapter(getApplicationContext(), R.layout.dates_layout, ToPrint);
                    chl.setAdapter(adapter);
                } else {
                    slideView(FrameLayout, FrameLayout.getLayoutParams().height, 0, 100L);
                    for (int s = 0; s < date.size(); s++) {
                        ToPrint.add(new Dates(date.get(s)));
                    }
                    DatesAdapter adapter = new DatesAdapter(getApplicationContext(), R.layout.dates_layout, ToPrint);
                    chl.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                autoTextView.dismissDropDown();
                ToPrint.clear();
                if (!TextUtils.isEmpty(autoTextView.getText())) {
                    autoTextView.dismissDropDown();
                    boolean found = false;
                    if (!TextUtils.isEmpty(autoTextView.getText())) {
                        for (int x = 0; x < date.size(); x++) {
                            if (isFound(autoTextView.getText().toString().toLowerCase(), date.get(x).toLowerCase())) {
                                ToPrint.add(new Dates(date.get(x)));
                                found = true;
                            }
                        }
                        int size = ToPrint.size();
                        if (size > 5) {
                            size = 5;
                        }
                        int height = size * dpToPx(41);
                        slideView(FrameLayout, FrameLayout.getLayoutParams().height, height, 300L);
                        if (!found) {
                            slideView(FrameLayout, FrameLayout.getLayoutParams().height, 0, 100L);
                        }
                        DatesAdapter adapter = new DatesAdapter(getApplicationContext(), R.layout.dates_layout, ToPrint);
                        chl.setAdapter(adapter);
                    }
                } else {
                    slideView(FrameLayout, FrameLayout.getLayoutParams().height, 0, 100L);
                    for (int s = 0; s < date.size(); s++) {
                        ToPrint.add(new Dates(date.get(s)));
                    }
                    DatesAdapter adapter = new DatesAdapter(getApplicationContext(), R.layout.dates_layout, ToPrint);
                    chl.setAdapter(adapter);
                }
            }
        });
    }

    private void playLocationAnimation() {
        new Thread(new Runnable() {
            public void run() {
                if (choosenIndex >= timeSeconds.size()) {
                    choosenIndex = timeSeconds.size() - 1;
                } else if (choosenIndex < 0) {
                    choosenIndex = 0;
                }
                for (; choosenIndex >= 0 && choosenIndex < timeSeconds.size() && play; choosenIndex += mode) {
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
                playImage.setImageResource(R.drawable.ic_media_play);
                updateControls();
            }
        }).start();
    }

    public void updateControls() {
        if (choosenIndex <= 0) {
            previous.setAlpha(100);
        } else {
            previous.setAlpha(255);
        }
        if (choosenIndex >= (timeSeconds.size() - 1)) {
            next.setAlpha(100);
        } else {
            next.setAlpha(255);
        }
    }

//    private void PrepareTheData() {
//        new Thread(new Runnable() {
//            public void run() {
//                locationAnalizer(data);
//                if (ToPrint != null) {
//                    ToPrint.clear();
//                }
//                for (int i = 0; i < date.size(); i++) {
//                    ToPrint.add(new Dates(date.get(i)));
//                }
//                runOnMainThread();
//            }
//        }).start();
//    }
//    public void runOnMainThread() {
//        TrackerForAdmin.this.runOnUiThread(new Runnable() {
//            public void run() {
//                DatesAdapter adapter = new DatesAdapter(getApplicationContext(), R.layout.dates_layout, ToPrint);
//                chl.setAdapter(adapter);
//                autoTextView.setAdapter(adapter);
//                if (!init2) {
//                    init2 = true;
//                    SearchInit();
//                }
//                if (dateLodingToast != null) {
//                    dateLodingToast.cancel();
//                }
//                progressBar.setVisibility(View.GONE);
//            }
//        });
//    }


    private void dateChoosed() {
        new Thread(new Runnable() {
            public void run() {
                locationAnalizer();
//                if (!titleFormed) {
//                    TimeTitle.formTimeTitle();
//                    titleFormed = true;
//                }
                mainThreadDatechoosen();
            }
        }).start();
    }


    public void mainThreadDatechoosen() {
        TrackerForAdmin.this.runOnUiThread(new Runnable() {
            public void run() {
                if (dateLodingToast != null) {
                    dateLodingToast.cancel();
                }
                doneLoading.show();
                Looding = false;
//                updateControls();
//                ShowControls();
                locationfinalAnalizer();
                progressBar.setVisibility(View.GONE);
//                EnableControls();
//                if (timeSeconds != null) {
//                    if (timeSeconds.size() > 0) {
//                        seekBar.setProgress((int) ((((float) timeSeconds.get(0)) / (86400f)) * 100f));
//                    }
//                }
//                if (googleMap != null) {
//                    googleMap.clear();
//                    if (TempLatLngA != null) {
//                        if (TempLatLngA.size() > 0) {
//                            showMarker(TempLatLngA.get(0));
//                        }
//                    }
//                }
//                if (backWhileLoading) {
//                    backWhileLoading = false;
//                    reset();
//                }
            }
        });
    }

    public void DisableControls() {
        controls = false;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void EnableControls() {
        controls = true;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (play || manual) {
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

                String ampm = "AM";
                if (hours >= 12) {
                    ampm = "PM";
                }
                if (hours > 12) {
                    hours -= 12;
                }

                if (Integer.toString(hours).length() < 2) {
                    if (hours == 0) {
                        hoursString = "12";
                    } else {
                        hoursString = "0" + hours;
                    }
                } else {
                    hoursString = Integer.toString(hours);
                }

                TimeTextView.setText(hoursString + ":" + minutesString + ":" + secondsString + " " + ampm);
//                updateControls();

                try {
                    showMarker(TempLatLngA.get(choosenIndex));
                } catch (Exception e) {

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (play) {
                    play = false;
                    playImage.setImageResource(R.drawable.ic_media_play);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    boolean init2 = false, controls = false;


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

    boolean backWhileLoading = false, Looding = false;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (!Looding) {
//            if (!days) {
//                reset();
//            } else {
            finish();
            //            }
        } else {
            if (doubleBackToExitPressedOnce) {
                finish();
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Application is Loading! click BACK again to exit.", Toast.LENGTH_LONG).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    String choosenDate = "";
    boolean doTaskAgain = true, firstTime = false;
    ArrayList<Integer> frequent = new ArrayList<>();
    ArrayListForTime TimeTitle = new ArrayListForTime();

    public void locationAnalizer() {
        if (!text.isEmpty() && !choosenDate.isEmpty()) {
            if (choosenDate.compareTo(text) == 0) {
                doTaskAgain = false;
            } else {
                doTaskAgain = true;
            }
        } else {
            doTaskAgain = true;
        }
        boolean add = true, arrange = false;
        choosenDate = text;
        if (doTaskAgain) {
            firstTime = false;
            timeSeconds.clear();
            TempLatLngA.clear();
            TempLatLngAForAllLocations.clear();
            timeSecondsForAll.clear();
            timeIntervalForAll.clear();
            titleFormed = false;
//            String temp = "", temp2 = "", month = "", day = "", Year = "";
            i = 0;
            x = 0;
//            int smallestDisIndex = 0;
//            double oldDistance = 0;

            for (DataSnapshot ds2 : locData.child(choosenDate).getChildren()) {
                if (ds2.getKey() != null) {
                    for (DataSnapshot ds3 : locData.child(choosenDate).child(ds2.getKey()).getChildren()) {
                        if (ds3.getKey() != null) {
                            for (DataSnapshot ds4 : locData.child(choosenDate).child(ds2.getKey()).child(ds3.getKey()).getChildren()) {
                                if (ds4.getKey() != null) {
                                    timeSeconds.add(TimeConverter(Long.parseLong(ds4.getKey())));
                                    LocationConverter(locData.child(choosenDate).child(ds2.getKey()).child(ds3.getKey()).child(ds4.getKey()).getValue().toString());
                                }
                            }
                            Countries.add(ds3.getKey());
                        }
                    }
                    States.add(ds2.getKey());
                }
            }
            arrange();
        }
    }

    public int TimeConverter(long unixSeconds) {
        Date date = new java.util.Date(unixSeconds * 1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
        String formattedDate = sdf.format(date);
        String timeHour = "*" + formattedDate.replace(":", "**") + "*";
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
        return seconds;
    }

    double counter = 0;
    double lat = 0, lon = 0;

    public void arrange() {
        int secTem;
        LatLng latLngTem;
        int n = timeSeconds.size();
        if (n > 0 && TempLatLngA.size() > 0) {
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
        }

        int start = 0;
        counter = 0;
        lat = 0;
        lon = 0;
        for (int i = 0; i < TempLatLngA.size(); i++) {
            if (counter > 0) {
                if (distance(new LatLng(TempLatLngA.get(i).latitude, TempLatLngA.get(i).longitude), new LatLng((lat / counter), (lon / counter))) > circleRange) {
                    TempLatLngAForAllLocations.add(new LatLng(lat / counter, lon / counter));
                    timeSecondsForAll.add(SecondsToTime(start) + "-" + SecondsToTime(timeSeconds.get(i)));
                    timeIntervalForAll.add(timeSeconds.get(i) - start);
                    lat = 0;
                    lon = 0;
                    counter = 0;
                }
            }
            if (counter == 0) {
                start = timeSeconds.get(i);
            }
            lat += TempLatLngA.get(i).latitude;
            lon += TempLatLngA.get(i).longitude;
            counter++;
        }
        TempLatLngAForAllLocations.add(new LatLng(lat / counter, lon / counter));
        timeSecondsForAll.add(SecondsToTime(start) + "-" + SecondsToTime(timeSeconds.get(timeSeconds.size() - 1)));
        timeIntervalForAll.add(timeSeconds.get(timeSeconds.size() - 1) - start);
    }

    public void LocationConverter(String location) {

        String temp = "";
        Matcher m = Pattern.compile("\\*([^\\*]+)\\*").matcher(location);
        while (m.find()) {
            if (m.group(1) != null) {
                if (temp.isEmpty()) {
                    temp = m.group(1);
                } else {
                    TempLatLngA.add(new LatLng(Double.parseDouble(temp), Double.parseDouble(m.group(1))));
                    temp = "";
                }
            }
        }
    }

    Boolean dateArranged = false;

    public void arrangeDate() {
        String Temp = "";
        int n = date.size();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (Converter(date.get(i)) > Converter(date.get(j))) {
                    Temp = date.get(i);
                    date.set(i, date.get(j));
                    date.set(j, Temp);
                }
            }
        }
    }

    public String SecondsToTime(int seconds) {
        String secondsString = "", minutesString = "", hoursString = "";
        int minutes = 0;
        int hours = 0;
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

        String ampm = "AM";
        if (hours >= 12) {
            ampm = "PM";
        }
        if (hours > 12) {
            hours -= 12;
        }

        if (Integer.toString(hours).length() < 2) {
            if (hours == 0) {
                hoursString = "12";
            } else {
                hoursString = "0" + hours;
            }
        } else {
            hoursString = Integer.toString(hours);
        }
        return (hoursString + ":" + minutesString + ":" + secondsString + " " + ampm);
    }

    public int TimeToSeconds(String time) {
        int seconds = 0;
        String timeHour = "*" + time.replace(":", "**") + "*";
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
        return seconds;
    }

    public int Converter(String dateString) {
        int value = 0;
        if (isFound("Jan", dateString)) {
            value += 100;
        } else if (isFound("Feb", dateString)) {
            value += 200;
        } else if (isFound("Mar", dateString)) {
            value += 300;
        } else if (isFound("Apr", dateString)) {
            value += 400;
        } else if (isFound("May", dateString)) {
            value += 500;
        } else if (isFound("Jun", dateString)) {
            value += 600;
        } else if (isFound("Jul", dateString)) {
            value += 700;
        } else if (isFound("Aug", dateString)) {
            value += 800;
        } else if (isFound("Sep", dateString)) {
            value += 900;
        } else if (isFound("Oct", dateString)) {
            value += 1000;
        } else if (isFound("Nov", dateString)) {
            value += 1100;
        } else if (isFound("Dec", dateString)) {
            value += 1200;
        }
        for (int s = 0; s < 3000; s++) {
            if (isFound(Integer.toString(s), dateString)) {
                if (s > 2000) {
                    value += s * 10000;
                } else {
                    value += s;
                }
            }
        }
        return value;
    }

    ArrayList<LatLng> TempLatLngAForAllLocations = new ArrayList<>();
    boolean titleFormed = false;

//    public void showAllLocations(double area) {
//        if (googleMap != null) {
//            googleMap.clear();
//        }
//        if (!titleFormed) {
//            TimeTitle.formTimeTitle();
//            titleFormed = true;
//        }
//        int max = Collections.max(frequent);
//        for (int r = 0; r < TempLatLngAForAllLocations.size(); r++) {
//            showMarkerForAll(TempLatLngAForAllLocations.get(r), getColorForMarker(frequent.get(r), max), r);
//        }
//        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(Marker marker) {
//                String title = marker.getTitle(); // Retrieve the title
//                dateText.setText(title);
//            }
//        });
//    }

    public void showMarkerForAll(LatLng currentLatLng, int color, int index) {
        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(color)).title(TimeTitle.get(index)).position(currentLatLng)).showInfoWindow();
    }

    public void PutAMarker(LatLng currentLatLng, LatLng newLocation, String title, int time) {
        if (time >= maxInSec) {
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.stopblue);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, dpToPx(20), dpToPx(20), false);
            locationStops.add(googleMap.addMarker(new MarkerOptions()
                    .position(currentLatLng)
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).anchor(0.5f, 0.5f)));
        } else {
            float bearing = -1;
            if (newLocation != null) {
                Location prevLoc = new Location("service Provider");
                prevLoc.setLatitude(currentLatLng.latitude);
                prevLoc.setLongitude(currentLatLng.longitude);

                Location newLoc = new Location("service Provider");
                newLoc.setLatitude(newLocation.latitude);
                newLoc.setLongitude(newLocation.longitude);

                bearing = prevLoc.bearingTo(newLoc);
            }
            if (bearing != -1) {
                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.play);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, dpToPx(15), dpToPx(15), false);
                locationStops.add(googleMap.addMarker(new MarkerOptions()
                        .position(currentLatLng)
                        .title(title)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).anchor(0.5f, 0.5f).rotation(bearing)));
            } else {
                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.rec);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, dpToPx(15), dpToPx(15), false);
                locationStops.add(googleMap.addMarker(new MarkerOptions()
                        .position(currentLatLng)
                        .title(title)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).anchor(0.5f, 0.5f)));
            }
        }
    }

    public int getColorForMarker(int frequentInt) {
        if (frequentInt > maxInSec) {
            frequentInt = maxInSec;
        }
        return (int) (120 - ((((double) frequentInt) / ((double) maxInSec)) * 120));
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
        googleMapTemp = googleMap2;
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                layout1.setEnabled(true);
                if (layout1.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    layout1.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else {
                    layout1.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                }
            }
        });

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(2.926219, 101.642033), 17));
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
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
