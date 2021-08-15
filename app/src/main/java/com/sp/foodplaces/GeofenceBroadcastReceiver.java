package com.sp.foodplaces;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.LongDef;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiv";
    private String NotificationBody5SNear = "5 star restaurant is near you!";
    private String NotificationBody5SFurther = "5 star restaurant is further away now.";
    private String NotificationBody5SDwell = "5 star restaurant is around the corner.";

    private String EnterGeofenceTitle = "Nearing a good place!";
    private String ExitGeofenceTitle = "Further away now";
    private String DwellGeofenceTitle = "Very near you!";

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        PendingResult pendingResult = goAsync();
        Log.d(TAG, "onReceive: Boot Action");
        new Task(pendingResult, intent).execute();

        //Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show();

        NotificationHelper notificationHelper = new NotificationHelper(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        
        if (geofencingEvent.hasError()){
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        //Get Location at point of trigger
        //Location location = geofencingEvent.getTriggeringLocation();

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        //loop through geofence list, can also use geofence.toSring()
        for (Geofence geofence: geofenceList){
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }

        int transitionType = geofencingEvent.getGeofenceTransition();
        
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                //Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification(EnterGeofenceTitle, NotificationBody5SNear, MainActivity.class);
                //NotificationFragment.getNotificationsList();

                String pTimeEnter = time();

                NotificationFragment.setNotificationsList(R.drawable.ic_baseline_notifications, pTimeEnter, NotificationBody5SNear,EnterGeofenceTitle);
                Log.d(TAG, "onNotification: " + NotificationFragment.getNotificationsList().toString());
                //notificationsList.add(new ModelNotification(R.drawable.ic_baseline_notifications, "Iron Man1", "5 Star Restaurant is near you!","Enter Geofence"));
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                //Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification(DwellGeofenceTitle, NotificationBody5SDwell, MainActivity.class);

                String pTimeDwell = time();

                NotificationFragment.setNotificationsList(R.drawable.ic_baseline_notifications, pTimeDwell, NotificationBody5SDwell,DwellGeofenceTitle);
                Log.d(TAG, "onNotification: " + NotificationFragment.getNotificationsList().toString());
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                //Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification(ExitGeofenceTitle, NotificationBody5SFurther, MainActivity.class);

                String pTimeExit = time();

                NotificationFragment.setNotificationsList(R.drawable.ic_baseline_notifications, pTimeExit , NotificationBody5SFurther,ExitGeofenceTitle);
                Log.d(TAG, "onNotification: " + NotificationFragment.getNotificationsList().toString());
                break;
        }
    }

    private String time(){
        long timeMillis = Calendar.getInstance().getTimeInMillis();
        SimpleDateFormat DateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
        String time = DateFormat.format(timeMillis);
        return time;
    }

    //For broadcast long running tasks
    private static class Task extends AsyncTask<Void, Void, Void>{
        PendingResult pendingResult;
        Intent intent;

        public Task(PendingResult pendingResult, Intent intent) {
            this.pendingResult = pendingResult;
            this.intent = intent;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Log.d(TAG, "doInBackground: Work Started");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "onPostExecute: Work Finished");
            pendingResult.finish();
        }
    }
}