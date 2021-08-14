package com.sp.foodplaces.Wong;

import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sp.foodplaces.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class URLParser {

    private String invalidPhotoURL  = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&"
                                    + "photoreference=CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU"
                                    + "&key=" + MainActivity.apiKey;

    private HashMap<String,String> parseJsonObject (JSONObject jsonObject){
        HashMap<String,String> hashMap = new HashMap<>();
        try{
            //name
            String json_name = jsonObject.getString("name");

            //place_id
            String json_placeID = jsonObject.getString("place_id");

            //address
            String json_address = jsonObject.getString("vicinity");

            //location
            String json_lat = jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lat");
            String json_lng = jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lng");

            //rating
            if (jsonObject.has("rating")) {
                String json_rating;
                String tmpRating = jsonObject.getString("rating");
                Double douRating = Double.parseDouble(tmpRating);
                String finalRating = String.format("%.1f", douRating);
                if (jsonObject.has("user_ratings_total")) {
                    String json_user_ratings = jsonObject.getString("user_ratings_total");
                    json_rating = finalRating + "/5 Stars" + "  (" + json_user_ratings + ")";
                    hashMap.put("rating",json_rating);
                }else {
                    json_rating = finalRating + "/5 Stars" + "(0)";
                    hashMap.put("rating", json_rating);
                }
            } else hashMap.put("rating","Rating: NA");

            //photo
            if (jsonObject.has("photos")){
                JSONArray photos = jsonObject.getJSONArray("photos");
                JSONObject tempPhoto = photos.getJSONObject(0);
                String json_photo = tempPhoto.getString("photo_reference");
                String json_photoURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" +
                                        json_photo + "&key=" + MainActivity.apiKey;
                hashMap.put("photoID",json_photoURL);
            } else hashMap.put("photoID",invalidPhotoURL);

            //price
            if (jsonObject.has("price_level")) {
                String json_price;
                String tmpPrice = jsonObject.getString("price_level");
                Integer intPrice = Integer.parseInt(tmpPrice);
                switch (intPrice) {
                    case 1:
                        json_price = "Price level: $";
                        hashMap.put("price", json_price);
                        break;
                    case 2:
                        json_price = "Price level: $$";
                        hashMap.put("price", json_price);
                        break;
                    case 3:
                        json_price = "Price level: $$$";
                        hashMap.put("price", json_price);
                        break;
                    case 4:
                        json_price = "Price level: $$$$";
                        hashMap.put("price", json_price);
                        break;
                    default:
                        json_price = "Price level: NA";
                        hashMap.put("price", json_price);
                }
            } else hashMap.put("price", "Price level: NA");

            //hashmap put
            hashMap.put("name",json_name);
            hashMap.put("placeID",json_placeID);
            hashMap.put("address",json_address);
            hashMap.put("lat",json_lat);
            hashMap.put("lng",json_lng);


        }catch (JSONException e){
            e.printStackTrace();
        }
        return hashMap;
    }

    private List<HashMap<String,String>> parseJsonArray(JSONArray jsonArray){
        List<HashMap<String,String>> arrayList = new ArrayList<>();
        for (int i=0;i<jsonArray.length();i++){
            try {
                HashMap<String,String> hashMapItems = parseJsonObject((JSONObject) jsonArray.get(i));
                arrayList.add(hashMapItems);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }

    public String getURL(String string) throws IOException {
        URL url = new URL(string);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();
        InputStream stream = urlConnection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String tmpData = "";
        while ((tmpData = reader.readLine()) != null){
            builder.append(tmpData);
        }
        String urlData = builder.toString();
        reader.close();
        return urlData;
    }

    public List<HashMap<String,String>> parseResult(JSONObject nJSONObject){
        JSONArray jsonArray = null;
        try {
            jsonArray = nJSONObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseJsonArray(jsonArray);
    }
}
