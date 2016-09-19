package com.pravinkandala.projects.smtracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MapView mapView;
    MapboxMap mapboxMap;
    LocationServices locationServices;
    private static final int PERMISSIONS_LOCATION = 0;
    ArrayList<HashMap<String, String>> jsonlist = new ArrayList<HashMap<String, String>>();
    String url = "https://api.myjson.com/bins/2qp3m";
    Icon pinIcon, pinUserLocation;
    Location userLocation;
    Boolean locationChanged = false;
    ArrayList<String> titleList = new ArrayList<>();
    ArrayList<LatLng> latLngList = new ArrayList<>();

    //marker list
    List<Marker> markers = new ArrayList<Marker>();
    String title;
    LatLng latLng;
    int layer = 1;
    int zoom = 1;



    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapview);

        if(isNetworkAvailable()){
            locationServices = LocationServices.getLocationServices(MainActivity.this);
            mapView.onCreate(savedInstanceState);
            new ProgressTask().execute();
        }


        // Create an Icon object for the marker to use
        IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
        Drawable markerPin = ContextCompat.getDrawable(MainActivity.this, R.drawable.pin);
        pinIcon = iconFactory.fromDrawable(markerPin);

        Drawable userPin = ContextCompat.getDrawable(MainActivity.this, R.drawable.blue_dot);
        pinUserLocation = iconFactory.fromDrawable(userPin);


        // Change color of pinIcon
        Drawable icon_add = getResources().getDrawable( R.drawable.icon_location_add );
        ColorFilter white = new LightingColorFilter( Color.WHITE, Color.WHITE);
        icon_add.setColorFilter(white);

        Drawable icon_set = getResources().getDrawable( R.drawable.icon_location_set );
        ColorFilter gray = new LightingColorFilter( Color.GRAY, Color.GRAY);
        icon_set.setColorFilter(gray);

        Drawable crosshair = getResources().getDrawable(R.drawable.crosshair);
        crosshair.setColorFilter(white);

        Drawable icon_search = getResources().getDrawable(R.drawable.icon_search);
        icon_search.setColorFilter(white);

        Drawable icon_layer = getResources().getDrawable(R.drawable.icon_layers);
        icon_layer.setColorFilter(white);






        //get json and store in object
//        parseJson();

        //set default style
        mapView.setStyleUrl("mapbox://styles/pravinkandala/cit611cqz00292wqmqgqvnuyt");
        layer++;


        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                //set user location pinIcon
                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(locationServices.getLastLocation()))
                        .icon(pinUserLocation)
                        .title("You are here"));
            }
        });



    }



    public void goUserLocation(View view){


        if(mapboxMap != null) {
            mapboxMap.isMyLocationEnabled();
        }


        if (!locationServices.areLocationPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_LOCATION);
        }

            userLocation();





    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    public Location getLastLocation() {
        return locationServices.getLastLocation();
    }

    private void userLocation() {

        locationServices.addLocationListener(new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                locationChanged = true;
                if (location != null) {
                    // Move the map camera to where the user location is
                    mapboxMap.setCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(location))
                            .zoom(16)
                            .build());
                }
            }
        });

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                if(locationChanged!=true){
                        mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                .target(new LatLng(locationServices.getLastLocation()))
                                .zoom(16)
                                .build());
                        locationChanged = false;

                }

            }
        });



    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_LOCATION: {
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    userLocation();
                }
            }
        }
    }

    private class ProgressTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
          super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);



            if(titleList.size()>1&&latLngList.size()>1){




                    mapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(MapboxMap mapboxMap) {

                            for(int i = 0; i<titleList.size(); i++) {

                                title = titleList.get(i);
                                latLng = latLngList.get(i);

                                mapboxMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .icon(pinIcon)
                                        .title(title));
                            }


                        }
                    });




            }


        }

        @Override
        protected Boolean doInBackground(String... params) {



            Log.d("answers:","doing");
            JSONParser jParser = new JSONParser(); // get JSON data from URL
            JSONArray json = jParser.getJSONFromUrl(url);
            for (int i = 0; i < json.length(); i++) {
                try {
                    JSONObject c = json.getJSONObject(i);
                    String title = c.getString("title");
                    String latitude = c.getString("latitude");
                    String longitude = c.getString("longitude");

                    titleList.add(title);
                    latLngList.add(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

        }
//            markerDataList.add(markerData);
            return null;
    }


    }

    //check if internet is available
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click


        switch (item.getItemId()) {
            case R.id.action_search:
                // search action
                return true;
            case R.id.action_layer:
                // location found

                if(layer==1){
                mapView.setStyleUrl("mapbox://styles/pravinkandala/cit611cqz00292wqmqgqvnuyt");
                    layer++;
                }else if(layer == 2){
                    mapView.setStyleUrl("mapbox://styles/pravinkandala/citahk9mp000s2ipg6tktgha3");
                    layer++;
                }else if(layer == 3){
                    mapView.setStyleUrl("mapbox://styles/pravinkandala/citahmbia001d2ip6aw7whxi3");
                    layer = 1;
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
