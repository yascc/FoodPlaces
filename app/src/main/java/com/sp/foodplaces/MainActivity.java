package com.sp.foodplaces;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sp.foodplaces.Wong.URLParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    public static final String apiKey = "AIzaSyBGjTcUuWYaBPiTqWmHJuyjqn5PYT976kk";

    private static GoogleMap map;
    private View mapView;

    private FusedLocationProviderClient locationProviderClient;
    private LocationRequest locationRequest;
    public static LatLng lastSearchLatLng;
    public static Location lastKnownLocation;

    private static GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    public static ArrayList<ModelGeoPlaces> geoPlacesList;

    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private static final String TAG = "MapsActivityOnMain";

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if(locationResult == null){
                return;
            }
            for(Location location: locationResult.getLocations()){
                lastKnownLocation = location;

                if (firstLocation == false){
                    if (lastKnownLocation != null){
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(),
                                lastKnownLocation.getLongitude()), CloseZoom));

                        runGeofence();
                    }
                }

                firstLocation = true;
            }
        }
    };

    public static final float NearbyZoom = 14;
    public static final float CloseZoom = 17;

    private Button buttonFind;

    private Boolean firstLocation = false;
    private  Boolean firstGeofence = false;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Wong
        //Set full screen with status bar on
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));

        //Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        mapView = findViewById(R.id.mapView);

        //Initialize fused location provider client and request settings
        locationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //Initialize Places API and session token
        Places.initialize(MainActivity.this, apiKey);
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        buttonFind = findViewById(R.id.bt_Find);

        //Yasmine
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
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
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.favorite:
                        startActivity(new Intent(getApplicationContext(),
                                Favorite.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(),
                                Profile.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.notification:
                        startActivity(new Intent(getApplicationContext(),
                                Notification.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });


        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);


        //Initialize autocomplete fragment for search bar
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setHint("Search for foods");
        autocompleteFragment.setCountry("SG");
        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(
                Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.OPENING_HOURS));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Place placeReturn = place;
                LatLng searchLatLng = placeReturn.getLatLng();
                String searchName = placeReturn.getName();

                OpeningHours tmpHours = placeReturn.getOpeningHours();

                Integer dayOfWeek = null;
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);

                switch (day) {
                    case Calendar.SUNDAY:
                        dayOfWeek = 6;
                        break;
                    case Calendar.MONDAY:
                        dayOfWeek = 0;
                        break;
                    case Calendar.TUESDAY:
                        dayOfWeek = 1;
                        break;
                    case Calendar.WEDNESDAY:
                        dayOfWeek = 2;
                        break;
                    case Calendar.THURSDAY:
                        dayOfWeek = 3;
                        break;
                    case Calendar.FRIDAY:
                        dayOfWeek = 4;
                        break;
                    case Calendar.SATURDAY:
                        dayOfWeek = 5;
                        break;
                    default:dayOfWeek=null;
                }

                String searchHours = null;

                if (!tmpHours.getWeekdayText().equals(null)){
                    List<String> listHours = tmpHours.getWeekdayText();
                    if(dayOfWeek!=null){
                        searchHours = listHours.get(dayOfWeek);
                    }else {
                        for(String s: listHours)
                        {
                            searchHours += s + "/n";
                        }
                    }
                }else searchHours="Opening hours: NA";

                if (searchHours == null){
                    searchHours="Opening hours: NA";
                }

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(searchLatLng, CloseZoom));

                Marker selectedMarker = map.addMarker(new MarkerOptions()
                        .position(searchLatLng)
                        .title(searchName)
                        .snippet(searchHours));

                selectedMarker.showInfoWindow();
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(getApplicationContext(), "Search error, please try again.", Toast.LENGTH_LONG).show();
            }
        });

        buttonFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng nearbyLatLng = map.getCameraPosition().target;
                lastSearchLatLng = nearbyLatLng;

                String nearbyURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                        "?location=" + nearbyLatLng.latitude + "," + nearbyLatLng.longitude +
                        "&radius=2000" +
                        "&types=restaurant" +
                        "&sensor=true" +
                        "&key=" + apiKey;

                new searchNearby().execute(nearbyURL);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            checkLocationRequest();
        } else {
            askLocationPermission();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocation();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setCompassEnabled(true);

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {

            View locationCompass = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("5"));
            RelativeLayout.LayoutParams compassLayoutParams = (RelativeLayout.LayoutParams) locationCompass.getLayoutParams();
            compassLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            compassLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            compassLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, 0);
            compassLayoutParams.topMargin = 280;
            compassLayoutParams.rightMargin = 20;

            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams locationLayoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            locationLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            locationLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            locationLayoutParams.bottomMargin = 200;
        }

        if (lastKnownLocation != null){
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(),
                    lastKnownLocation.getLongitude()), CloseZoom));
        }

        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener(){
            @Override
            public void onCameraMoveStarted(int i) {
                if (i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE){
                    buttonFind.setVisibility(View.VISIBLE);
                }
            }
        });

        map.setOnMapLongClickListener(this);

    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void checkLocationRequest() {

        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> responseTask = client.checkLocationSettings(request);
        responseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {

            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocation();
            }
        });
        responseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(MainActivity.this, 1001);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void startLocation() {
        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @SuppressLint("MissingPermission")
    private void stopLocation() {
        locationProviderClient.removeLocationUpdates(locationCallback);
    }

    private class searchNearby extends AsyncTask<String,Integer,String>{
        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                URLParser urlParser = new URLParser();
                data = urlParser.getURL(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new parseNearbyData().execute(s);
        }
    }

    private class parseNearbyData extends AsyncTask<String,Integer,List<HashMap<String,String>>>{
        @Override
        protected List<HashMap<String,String>> doInBackground(String... strings) {
            URLParser mURLParser = new URLParser();
            List<HashMap<String,String>> nearbyList = null;
            JSONObject mObject = null;
            try {
                mObject = new JSONObject(strings[0]);
                nearbyList = mURLParser.parseResult(mObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return nearbyList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String,String>> hashMaps) {
            map.clear();
            for(int i =0;i<hashMaps.size();i++){
                HashMap<String,String> mHashMap = hashMaps.get(i);
                double lat = Double.parseDouble(mHashMap.get("lat"));
                double lng = Double.parseDouble(mHashMap.get("lng"));
                String name = mHashMap.get("name");
                LatLng latlng = new LatLng(lat,lng);
                MarkerOptions options = new MarkerOptions();
                options.position(latlng);
                options.title(name);
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                map.addMarker(options);
            }

            CameraUpdate searchLocation = CameraUpdateFactory.newLatLngZoom(lastSearchLatLng, NearbyZoom);
            map.animateCamera(searchLocation, new GoogleMap.CancelableCallback(){
                @Override
                public void onFinish() {
                    buttonFind.setVisibility(View.INVISIBLE);
                }
                @Override
                public void onCancel() {

                }
            });
        }
    }

    private void runGeofence() {

        if(lastKnownLocation!=null){
            String geofenceURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                    "?location=" + lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude() +
                    "&radius=3000" +
                    "&types=restaurant" +
                    "&sensor=true" +
                    "&key=" + apiKey;

            new searchGeofence().execute(geofenceURL);
        }else{
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runGeofence();
                }
            },2000);
        }
    }

    private class searchGeofence extends AsyncTask<String,Integer,String>{
        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                URLParser urlParser = new URLParser();
                data = urlParser.getURL(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new parseGeofenceData().execute(s);
        }
    }

    private class parseGeofenceData extends AsyncTask<String,Integer,List<HashMap<String,String>>>{
        @Override
        protected List<HashMap<String,String>> doInBackground(String... strings) {
            URLParser mURLParser = new URLParser();
            List<HashMap<String,String>> geofenceList = null;
            JSONObject mObject = null;
            try {
                mObject = new JSONObject(strings[0]);
                geofenceList = mURLParser.parseResult(mObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return geofenceList;
        }

        //TODO : WONG ARRAY
        @Override
        protected void onPostExecute(List<HashMap<String,String>> hashMaps) {
            //map.clear();
            geoPlacesList = new ArrayList<>();

            for(int i =0;i<hashMaps.size();i++){
                HashMap<String,String> mHashMap = hashMaps.get(i);
                double lat = Double.parseDouble(mHashMap.get("lat"));
                double lng = Double.parseDouble(mHashMap.get("lng"));
                String name = mHashMap.get("name");
                geoPlacesList.add(new ModelGeoPlaces(name, lat, lng));
            }

            for (int b=0; b<geoPlacesList.size(); b++) {
                Log.d(TAG, "onPostExecute: geoPlaces: gList: " + geoPlacesList.get(b).getId()
                        + " " + geoPlacesList.get(b).getLatitude());
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
        map.clear();

        for (ModelGeoPlaces geoplace : geoPlacesList) {
            // Read the place information from the DB cursor
            String placeUID = geoplace.getId();
            LatLng latLng = new LatLng(geoplace.getLatitude(), geoplace.getLongitude());
            addMarker(latLng);
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

       /* Geofence geofence = geofenceHelper.getGeofence(geoPlacesList.getId(), latLng, radius, Geofence
                .GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
*/
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
        map.addMarker(markerOptions);
    }

    private void addCircle(LatLng latLng, float radius){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255,255,0,0));
        circleOptions.fillColor(Color.argb(64,255,0,0));
        circleOptions.strokeWidth(4);
        map.addCircle(circleOptions);
    }

    public static ArrayList<ModelGeoPlaces> getGeoPlacesList() {
        return geoPlacesList;
    }

    public static GeofencingClient getGeofencingClient(){
        return geofencingClient;
    }

    public static GoogleMap getMap() {
        return map;
    }

}
