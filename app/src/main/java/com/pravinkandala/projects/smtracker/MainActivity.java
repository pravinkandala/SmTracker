package com.pravinkandala.projects.smtracker;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class MainActivity extends AppCompatActivity {

    MapView mapView;
    MapboxMap mapboxMap;
    LocationServices locationServices;
    private static final int PERMISSIONS_LOCATION = 0;
    int layer = 1;
    int zoom = 1;
    Icon pinIcon;
    Marker marker;
    TextView warningScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapview);
        warningScreen = (TextView) findViewById(R.id.warning_screen);

        if(isNetworkAvailable()){
            mapView.setVisibility(View.VISIBLE);
            locationServices = LocationServices.getLocationServices(MainActivity.this);
            mapView.onCreate(savedInstanceState);

            //set default style
            mapView.setStyleUrl("mapbox://styles/pravinkandala/cit611cqz00292wqmqgqvnuyt");
            layer++;

            setUserMarkerLocation();

            new ProgressTask(mapView, MainActivity.this).execute();
        }else{
           displayWarning();
        }

        // Change color of pinIcon
        changeIconColor("icon_location_add",this,Color.WHITE);
        changeIconColor("icon_location_set",this,Color.GRAY);
        changeIconColor("crosshair",this,Color.WHITE);
        changeIconColor("icon_search",this,Color.WHITE);
        changeIconColor("icon_layers",this,Color.WHITE);


    }

    public void displayWarning(){
        mapView.setVisibility(View.GONE);
        warningScreen.setVisibility(View.VISIBLE);
        warningScreen.setText("Please turn on wifi/data plan. Close the app and try again.");
    }

    public void changeIconColor(String iconName, Context activity, int colorId){
        int resID = activity.getResources().getIdentifier(iconName , "drawable", activity.getPackageName());
        Drawable markerPin = ContextCompat.getDrawable(activity, resID);
        ColorFilter colorFilter = new LightingColorFilter( colorId, colorId);
        markerPin.setColorFilter(colorFilter);

    }

    public static Icon createIcon(String iconName, Context activity){
        IconFactory iconFactory = IconFactory.getInstance(activity);
        int resID = activity.getResources().getIdentifier(iconName , "drawable", activity.getPackageName());
        Drawable markerPin = ContextCompat.getDrawable(activity, resID);
        return iconFactory.fromDrawable(markerPin);
    }


    public boolean checkPermissionsAndConnections(){
        if(mapboxMap != null) {
            mapboxMap.isMyLocationEnabled();
        }

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if (!locationServices.areLocationPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_LOCATION);
        }else if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }else{
            return true;
        }

        return false;
    }


    public void goUserLocation(View view){

        if(isNetworkAvailable()){
            if(checkPermissionsAndConnections()){
            setUserMarkerLocation();
            userLocation();
            }
        }else{
            displayWarning();
        }

    }

    //Check if GPS is available else ask to allow.
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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

    public void setUserMarkerLocation(){

        if(checkPermissionsAndConnections()) {
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(MapboxMap mapboxMap) {
                    if(marker!=null) marker.remove();
                    marker = mapboxMap.addMarker(new MarkerOptions()
                            .position(new LatLng(locationServices.getLastLocation()))
                            .icon(MainActivity.createIcon("blue_dot", MainActivity.this))
                            .title("You are here!"));

                }
            });
        }

    }

    private void userLocation() {

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                //Three level zoom for user location

                if(zoom == 1){
                        mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                .target(new LatLng(locationServices.getLastLocation()))
                                .zoom(16)
                                .build());

                    zoom++;

                }else if(zoom == 2){
                    mapboxMap.setCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(locationServices.getLastLocation()))
                            .zoom(10)
                            .build());
                    zoom++;
                }else{
                    mapboxMap.setCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(locationServices.getLastLocation()))
                            .zoom(5)
                            .build());
                    zoom = 1;
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


    //check if internet is available
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        else
            return false;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
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

                if(isNetworkAvailable()) {
                    if (layer == 1) {
                        mapView.setStyleUrl("mapbox://styles/pravinkandala/cit611cqz00292wqmqgqvnuyt");
                        layer++;
                    } else if (layer == 2) {
                        mapView.setStyleUrl("mapbox://styles/pravinkandala/citahk9mp000s2ipg6tktgha3");
                        layer++;
                    } else if (layer == 3) {
                        mapView.setStyleUrl("mapbox://styles/pravinkandala/citahmbia001d2ip6aw7whxi3");
                        layer = 1;
                    }
                }else{
                    displayWarning();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
