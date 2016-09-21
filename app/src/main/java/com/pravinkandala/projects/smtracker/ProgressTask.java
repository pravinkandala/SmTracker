package com.pravinkandala.projects.smtracker;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Pravin on 9/21/16.
 * Project: SmTracker
 */

public class ProgressTask extends AsyncTask<String, Void, Boolean> {

    String url = "https://api.myjson.com/bins/38mbo";

    private MapView mapView;
    private Activity activity;

    public ProgressTask(MapView view, Activity activity) {
        this.activity = activity;
        this.mapView = view;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                for (MarkerData d: DataStore.markers) {
                    mapboxMap.addMarker(new MarkerOptions()
                            .position(d.getLatLng())
                            .icon(MainActivity.createIcon(d.getIcon(), activity))
                            .title(d.getTitle())
                            .snippet(d.getCategory()+" ("+d.getType()+")"));
                }
            }
        });
    }

    @Override
    protected Boolean doInBackground(String... params) {
        Log.d("[CLEANUP]", "Cleaning existing data");
        DataStore.markers.clear();

        Log.d("answers:","doing");
        JSONParser jParser = new JSONParser(); // get JSON data from URL
        JSONArray json = jParser.getJSONFromUrl(url);
        for (int i = 0; i < json.length(); i++) {
            try {

                JSONObject jsonObject = json.getJSONObject(i);
                MarkerData markerData = new MarkerData();
                markerData.setTitle(jsonObject.getString("title"));
                markerData.setCategory(jsonObject.getString("category"));
                markerData.setType(jsonObject.getString("type"));
                markerData.setLatLng(new LatLng(Double.parseDouble(jsonObject.getString("latitude")), Double.parseDouble(jsonObject.getString("longitude"))));
                DataStore.markers.add(markerData);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
//            markerDataList.add(markerData);
        return null;
    }


}