package com.sp.foodplaces;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sp.foodplaces.Wong.DirectoryLoading;
import com.sp.foodplaces.Wong.PlaceAdapter;
import com.sp.foodplaces.Wong.PlaceModel;
import com.sp.foodplaces.MainActivity;
import com.sp.foodplaces.Wong.URLParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Directory extends AppCompatActivity {

    ArrayList<PlaceModel> placeModelList;
    RecyclerView recyclerView;

    final DirectoryLoading loading = new DirectoryLoading(Directory.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);

        //Wong
        placeModelList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);

        loading.startLoading();
        /*
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loading.endLoading();
            }
        },1000);

         */

        GetData getData = new GetData();
        getData.execute();

        //Initialize and Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.directory);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.discovery:
                        startActivity(new Intent(getApplicationContext(),
                                MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.directory:
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

    }

    public class GetData extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            Double tmpLat = 0.0;
            Double tmpLng = 0.0;

            if(MainActivity.lastSearchLatLng != null){
                tmpLat = MainActivity.lastSearchLatLng.latitude;
                tmpLng = MainActivity.lastSearchLatLng.longitude;
            }else if (MainActivity.lastKnownLocation != null){
                tmpLat = MainActivity.lastKnownLocation.getLatitude();
                tmpLng = MainActivity.lastKnownLocation.getLongitude();
            }else {
                tmpLat = 1.3098;
                tmpLng = 103.7775;
            }

            String data=null;
            String directoryURL =   "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                                    "?location=" +
                                    tmpLat +
                                    "," +
                                    tmpLng +
                                    "&radius=2000" +
                                    "&types=restaurant" +
                                    "&sensor=true" +
                                    "&key=" + MainActivity.apiKey;

            URLParser urlParser = new URLParser();
            try {
                data = urlParser.getURL(directoryURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new Directory.parseDirectoryData().execute(s);
        }
    }

    private class parseDirectoryData extends AsyncTask<String,Integer,List<HashMap<String,String>>> {
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            URLParser urlParser = new URLParser();
            List<HashMap<String, String>> directoryList = null;
            JSONObject object = null;
            try {
                object = new JSONObject(strings[0]);
                directoryList = urlParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return directoryList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String,String>> hashMaps) {
            for(int i =0;i<hashMaps.size();i++){
                HashMap<String,String> hashMap = hashMaps.get(i);

                PlaceModel placeModel = new PlaceModel();
                placeModel.setName(hashMap.get("name"));
                placeModel.setRating(hashMap.get("rating"));
                placeModel.setPrice(hashMap.get("price"));
                placeModel.setPhoto(hashMap.get("photoID"));
                placeModel.setAddress(hashMap.get("address"));
                placeModel.setPlaceID(hashMap.get("placeID"));
                placeModel.setFav("0");

                placeModelList.add(placeModel);

            //PutDataToRecycleView(placeModelList);
            loading.endLoading();
            }

            PutDataToRecycleView(placeModelList);
        }
    }

    private void PutDataToRecycleView(ArrayList<PlaceModel> placeModelList){
        PlaceAdapter placeAdapter = new PlaceAdapter(this, placeModelList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(placeAdapter);
    }
}