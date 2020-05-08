package com.example.fevertracker;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import static com.example.fevertracker.RegisterActivity.TAG;

public class GeoFenceFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {
    admin admin = new admin();
    GoogleMap map;
    GoogleApiClient googleApiClient;
    ImageView GeoCircle;
    int Width = 150;
    private SeekBar seekBar;
    private Marker geoFenceMarker;
    private static final long GEO_DURATION = 1000;
    private static final String GEOFENCE_REQ_ID = "My Geofence";
    private static float GEOFENCE_RADIUS; // in meters

    public void addGeo(View view) {
//        map.getCameraPosition().zoom
        markerForGeofence(map.getCameraPosition().target);
    }

    public void setAdmin(admin admin) {
        this.admin = admin;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.geo_fencing_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() != null) {
            seekBar = getView().findViewById(R.id.seekBar);
            GeoCircle = getView().findViewById(R.id.geoCircle);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                  map.moveCamera(CameraUpdateFactory.newLatLngZoom(map.getCameraPosition().target, ((((float) progress)/100f)*21f)));
                    if(progress==0){
                        progress+=1;
                    }
                    int width = progress * 3, height = progress * 3;
                    Width = width;
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int)dpTOpx(width),(int) dpTOpx(height));
                    GeoCircle.setLayoutParams(lp);
                    GEOFENCE_RADIUS =(float) getMeterPerPixels(map.getCameraPosition().target.latitude, map.getCameraPosition().zoom)*(dpTOpx(width)/2f);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            if (admin != null) {
                admin.setViewForGeo(getView());
            }
        }
        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map2);
        if (fragment != null) {
            fragment.getMapAsync(this);
        }
        createGoogleApi();
    }

    public double getMeterPerPixels(double lat, double zoom) {
        double pixelsPerTile = 256 * ((double) getContext().getResources().getDisplayMetrics().densityDpi / 160);
        double numTiles = Math.pow(2, zoom);
        double metersPerTile = Math.cos(Math.toRadians(lat)) * EARTH_CIRCUMFERENCE_METERS / numTiles;
        return metersPerTile / pixelsPerTile;
    }

    double EARTH_CIRCUMFERENCE_METERS = 40075004;

    public int getBoundsZoomLevel(LatLng northeast, LatLng southwest,
                                  int width, int height) {
        final int GLOBE_WIDTH = 256; // a constant in Google's map projection
        final int ZOOM_MAX = 21;
        double latFraction = (latRad(northeast.latitude) - latRad(southwest.latitude)) / Math.PI;
        double lngDiff = northeast.longitude - southwest.longitude;
        double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;
        double latZoom = zoom(height, GLOBE_WIDTH, latFraction);
        double lngZoom = zoom(width, GLOBE_WIDTH, lngFraction);
        double zoom = Math.min(Math.min(latZoom, lngZoom), ZOOM_MAX);
        return (int) (zoom);
    }

    private double latRad(double lat) {
        double sin = Math.sin(lat * Math.PI / 180);
        double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
        return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
    }

    private double zoom(double mapPx, double worldPx, double fraction) {
        final double LN2 = .693147180559945309417;
        return (Math.log(mapPx / worldPx / fraction) / LN2);
    }

    @Override
    public void onMapClick(LatLng latLng) {
//        Log.d(TAG, "onMapClick("+latLng +")");
//        markerForGeofence(latLng);
    }

    private void createGoogleApi() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }
    }

    private void markerForGeofence(LatLng latLng) {
        Log.i(TAG, "markerForGeofence(" + latLng + ")");
        String title = latLng.latitude + ", " + latLng.longitude;
        // Define marker options
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .title(title);
        if (map != null) {
            // Remove last geoFenceMarker
            if (geoFenceMarker != null)
                geoFenceMarker.remove();

            geoFenceMarker = map.addMarker(markerOptions);
        }
        startGeofence();
    }

    private void startGeofence() {
        Log.i(TAG, "startGeofence()");
        if (geoFenceMarker != null) {
            Geofence geofence = createGeofence(geoFenceMarker.getPosition(), GEOFENCE_RADIUS);
            GeofencingRequest geofenceRequest = createGeofenceRequest(geofence);
            addGeofence(geofenceRequest);
            drawGeofence();
        }
    }

    private Geofence createGeofence(LatLng latLng, float radius) {
        Log.d(TAG, "createGeofence");
        return new Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID)
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration(GEO_DURATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    private GeofencingRequest createGeofenceRequest(Geofence geofence) {
        Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();
    }

    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;

    private PendingIntent createGeofencePendingIntent() {
        Log.d(TAG, "createGeofencePendingIntent");
        if (geoFencePendingIntent != null)
            return geoFencePendingIntent;

        Intent intent = new Intent(getContext(), GeofenceTransitionService.class);
        return PendingIntent.getService(
                getContext(), GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @SuppressLint("MissingPermission")
    private void addGeofence(GeofencingRequest request) {
        Log.d(TAG, "addGeofence");
        LocationServices.GeofencingApi.addGeofences(
                googleApiClient,
                request,
                createGeofencePendingIntent()
        );
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(getContext(), "onMarkerClicked: " + marker.getPosition(), Toast.LENGTH_LONG).show();
        marker.remove();
//        Log.d(TAG, "onMarkerClickListener: " + marker.getPosition() );
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap2) {
//        admin.onMapReady2(googleMap2);

        googleMap2.setOnMapClickListener(this);
        googleMap2.setOnMarkerClickListener(this);
        map = googleMap2;
        GEOFENCE_RADIUS =(float) getMeterPerPixels(map.getCameraPosition().target.latitude, map.getCameraPosition().zoom)*(dpTOpx(Width)/2f);
        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                GEOFENCE_RADIUS =(float) getMeterPerPixels(map.getCameraPosition().target.latitude, map.getCameraPosition().zoom)*(dpTOpx(Width)/2f);
            }
        });
    }

    private Circle geoFenceLimits;

    private void drawGeofence() {
        Log.d(TAG, "drawGeofence()");

        if (geoFenceLimits != null) {
//            geoFenceLimits.remove();
        }
//        Toast.makeText(getContext(), "drawGeofence" ,Toast.LENGTH_LONG).show();

        CircleOptions circleOptions = new CircleOptions()
                .center(geoFenceMarker.getPosition())
                .strokeColor(Color.argb(0, 70, 70, 70))
                .fillColor(Color.argb(50, 150, 150, 150))
                .radius(GEOFENCE_RADIUS);
        map.addCircle(circleOptions);
    }

    float dpTOpx(int dp) {
        float dip = (float) dp;
        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        return px;
    }
//    private final String KEY_GEOFENCE_LAT = "GEOFENCE LATITUDE";
//    private final String KEY_GEOFENCE_LON = "GEOFENCE LONGITUDE";

    // Saving GeoFence marker with prefs mng
//    private void saveGeofence() {
//        Log.d(TAG, "saveGeofence()");
//        SharedPreferences sharedPref = getPreferences( Context.MODE_PRIVATE );
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putLong( KEY_GEOFENCE_LAT, Double.doubleToRawLongBits( geoFenceMarker.getPosition().latitude ));
//        editor.putLong( KEY_GEOFENCE_LON, Double.doubleToRawLongBits( geoFenceMarker.getPosition().longitude ));
//        editor.apply();
//    }
}
