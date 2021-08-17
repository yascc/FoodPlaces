package com.sp.foodplaces;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.List;




public class GeofenceHelper extends ContextWrapper{

    private static final String TAG = "GeofenceHelper";
    private static long GEOFENCE_TIMEOUT = 24 * 60 * 60 * 1000; // 24 hours
    private static float GEOFENCE_RADIUS = 80; // in metres

    public static List<Geofence> mGeofenceList;
    private static PendingIntent geofencePendingIntent;


    public GeofenceHelper(Context base){
        super(base);
    }

    public static float getGeofenceRadius() {
        return GEOFENCE_RADIUS;
    }

    public static void setGeofenceRadius(float geofenceRadius) {
        GEOFENCE_RADIUS = geofenceRadius;
    }

    public static void registerAllGeofences() {
        //TODO: check list here
        mGeofenceList = new ArrayList<>();

        for (ModelGeoPlaces geoplace : MainActivity.getGeoPlacesList()) {
            // Read the place information from the DB cursor
            String placeUID = geoplace.getId();
            double placeLat = geoplace.getLatitude();
            double placeLng = geoplace.getLongitude();

            // Build a Geofence object
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(placeUID)
                    .setExpirationDuration(GEOFENCE_TIMEOUT)
                    .setCircularRegion(placeLat, placeLng, GEOFENCE_RADIUS)
                    .setLoiteringDelay(5000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                            | Geofence.GEOFENCE_TRANSITION_DWELL
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            // Add it to the list
            mGeofenceList.add(geofence);
        }

        for (int i=0; i<mGeofenceList.size(); i++) {
            Log.d(TAG, "registerAllGeofences: gList: " + mGeofenceList.get(i).getRequestId()
                    + " " + mGeofenceList.get(i).toString());
        }
    }

    public static void unRegisterAllGeofences(){
        try {
            MainActivity.getMap().clear();
            mGeofenceList.clear();
            MainActivity.getGeofencingClient().removeGeofences(geofencePendingIntent);

            Log.d(TAG, "unRegisterAllGeofences success");
            for (int i=0; i<mGeofenceList.size(); i++) {
                Log.d(TAG, "unRegisterAllGeofences: gList: " + mGeofenceList.get(i).getRequestId()
                        + " " + mGeofenceList.get(i).toString());
            }

        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            Log.e(TAG, securityException.getMessage());
        }
    }



   public GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.addGeofences(mGeofenceList);
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        return builder.build();
    }

    //!=====
    public Geofence getGeofence(String ID, LatLng latLng, float radius, int transitionTypes) {
        return new Geofence.Builder()
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setRequestId(ID)
                .setTransitionTypes(transitionTypes)
                .setLoiteringDelay(5000)
                .setExpirationDuration(GEOFENCE_TIMEOUT)
                .build();
    }

    public PendingIntent getPendingIntent(){
        if (geofencePendingIntent != null){
        return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(this, 2607, intent, PendingIntent
                .FLAG_UPDATE_CURRENT);

        return geofencePendingIntent;
    }



    public String getErrorString(Exception e){
        if (e instanceof ApiException){
            ApiException apiException = (ApiException) e;
            switch (apiException.getStatusCode()){
                case GeofenceStatusCodes
                        .GEOFENCE_NOT_AVAILABLE:
                    return "GEOFENCE_NOT_AVAILABLE";
                //app has a limit of 100 geofences
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_GEOFENCES:
                    return "GEOFENCE_TOO_MANY_GEOFENCES";

                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "GEOFENCE_TOO_MANY_PENDING_INTENTS";
            }
        }
        return e.getLocalizedMessage();
    }

    public static List<Geofence> getmGeofenceList() {
        return mGeofenceList;
    }

}


