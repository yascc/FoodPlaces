package com.sp.foodplaces;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.slider.Slider;
import com.sp.foodplaces.databinding.ActivityMapsBinding;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    //@SuppressLint("NewApi")
    private static final String TAG = "MapsActivity";

    private static GoogleMap mMap;
    //private ActivityMapsBinding binding;

    private UiSettings mUiSettings;

    private static GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    public static ArrayList<ModelGeoPlaces> geoPlacesList;

    //emulator location settings on diff api: https://github.com/android/location-samples/issues/161


    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);


        //TODO: UPDATE WITH WONG's ARRAY
        geoPlacesList = new ArrayList<>();
        geoPlacesList.add(new ModelGeoPlaces("appleOrchard1", 1.3029, 103.8364));
        geoPlacesList.add(new ModelGeoPlaces("vicSecrets2", 1.3018, 103.8370));
        geoPlacesList.add(new ModelGeoPlaces("BusStop3", 1.3025, 103.83243));

        for (int i=0; i<geoPlacesList.size(); i++) {
            Log.d(TAG, "onCreate: geoPlaces: gList: " + geoPlacesList.get(i).getId()
                    + " " + geoPlacesList.get(i).getLatitude());
        }

        //Initialize and Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.discovery);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.discovery:
                        return true;
                    case R.id.directory:
                        startActivity(new Intent(getApplicationContext(),
                                Directory.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.favorite:
                        startActivity(new Intent(getApplicationContext(),
                                Favorite.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(),
                                Profile.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.notification:
                        startActivity(new Intent(getApplicationContext(),
                                Notification.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();

        // On zoom controls
        mUiSettings.setZoomControlsEnabled(true);

/*      // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */

        enableUserLocation();
        zoomToUserLocation();

        mMap.setOnMapLongClickListener(this);
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .ACCESS_FINE_LOCATION)) {
                //Show user a dialog for displaying why permission is needed
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                        .ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                        .ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);

            }
        }
    }

    private void zoomToUserLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
       /* locationTask.addOnSuccessListener(location -> {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());*/

        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                //set default zoom level
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                //mMap.addMarker(new MarkerOptions().position(latLng));
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                //!---maybe check this
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
                zoomToUserLocation();
            } else {
                //We do not have the permission
            }
        }


        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(this, "You can add geofences...", Toast.LENGTH_SHORT).show();

            } else {
                //We do not have the permission
                Toast.makeText(this, "Background location is necessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (Build.VERSION.SDK_INT >= 29) {
            //we need BG permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {

                tryAddingGeofence();

            } else {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //we show dialog and ask for permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                            .ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                            .ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }
        } else {
            tryAddingGeofence();
        }
    }

    private void tryAddingGeofence() {
        mMap.clear();

        for (ModelGeoPlaces geoplace : geoPlacesList) {
            // Read the place information from the DB cursor
            String placeUID = geoplace.getId();
            LatLng latLng = new LatLng(geoplace.getLatitude(), geoplace.getLongitude());
            addMarker(latLng);
            //!--TODO
            //radius from user setting
            addCircle(latLng, GeofenceHelper.getGeofenceRadius());
            addGeofence(latLng, GeofenceHelper.getGeofenceRadius());
            Log.d(TAG, "tryAddingGeofence: testList " + placeUID);
        }

        for (int i=0; i<geoPlacesList.size(); i++) {
            Log.d(TAG, "tryAddingGeofence: geoPlaces: gList: " + geoPlacesList.get(i).getId()
                    + " " + geoPlacesList.get(i).getLatitude());
        }

    }

    private void addGeofence(LatLng latLng, float radius) {
        //pass in GEOFENCE request ID

        /*Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence
                .GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);*/

        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest();

        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });

    }

    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
    }

    private void addCircle(LatLng latLng, float radius){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255,255,0,0));
        circleOptions.fillColor(Color.argb(64,255,0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }

    public static ArrayList<ModelGeoPlaces> getGeoPlacesList() {
        return geoPlacesList;
    }

    public static GeofencingClient getGeofencingClient(){
        return geofencingClient;
    }

    public static GoogleMap getmMap() {
        return mMap;
    }

}