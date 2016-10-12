package com.pravinkandala.projects.smtracker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.pravinkandala.projects.smtracker.Service.GetLocation;
import com.pravinkandala.projects.smtracker.Service.ProgressTask;

public class MainActivity extends AppCompatActivity {

    MapView mMapView;
    int mLayer = 1;
    int mZoom = 1;
    Marker mMarker;
    TextView mWarningScreen;
    GetLocation mGetLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapboxAccountManager.start(this, getString(R.string.access_token));

        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.mapview);
        mWarningScreen = (TextView) findViewById(R.id.warning_screen);
        mGetLocation = new GetLocation(this);


        if (isNetworkAvailable()) {

            //init mapview..
            mMapView.setVisibility(View.VISIBLE);

            try {
                mMapView.onCreate(savedInstanceState);
                //set default style..
                mMapView.setStyleUrl("mapbox://styles/pravinkandala/cit611cqz00292wqmqgqvnuyt");
            }catch (Exception e){
                Log.e(getClass().getCanonicalName(), "Error building URL.  Exception = " + e);
                Toast.makeText(this, "Error building request url.  Please try again.", Toast.LENGTH_LONG).show();
                return;
            }

            mLayer++;

            //create marker at user location..
            userLocationMarker();

            //Download MarkerData from json file..
            new ProgressTask(mMapView, MainActivity.this).execute();


        } else {
            //Display warning when no network..
            displayWarning();
        }

        // Change color of pinIcon
        changeIconColor("ic_icon_location_add", this, Color.WHITE);
        changeIconColor("ic_icon_location_set", this, Color.GRAY);
        changeIconColor("ic_crosshair", this, Color.WHITE);

    }

    /**
     * Display warning when no network.
     */
    public void displayWarning() {
        mMapView.setVisibility(View.GONE);
        mWarningScreen.setVisibility(View.VISIBLE);
        mWarningScreen.setText("Please turn on wifi/data plan. Close the app and try again.");
    }

    /**
     * Change color of the icon..
     */
    public void changeIconColor(String iconName, Context activity, int colorId) {
        int resID = activity.getResources().getIdentifier(iconName, "drawable", activity.getPackageName());
        Drawable markerPin = ContextCompat.getDrawable(activity, resID);
        ColorFilter colorFilter = new LightingColorFilter(colorId, colorId);
        markerPin.setColorFilter(colorFilter);

    }

    /**
     * Create Icon
     */
    public static Icon createIcon(String iconName, Context activity) {
        IconFactory iconFactory = IconFactory.getInstance(activity);
        int resID = activity.getResources().getIdentifier(iconName, "drawable", activity.getPackageName());
        Drawable markerPin = ContextCompat.getDrawable(activity, resID);
        return iconFactory.fromDrawable(markerPin);
    }

    /**
     * Onclick -> goUserLocation..
     *
     * @param view
     * @Description : zooms to the user location. Toggles to three zoom levels.
     */
    public void goUserLocation(View view) {

        if (mGetLocation.canGetLocation()) {
            userLocationMarker();
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(MapboxMap mapboxMap) {
                    //Three level mZoom for user location

                    if (mZoom == 1) {
                        mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                .target(new LatLng(mGetLocation.getLatitude(),mGetLocation.getLongitude()))
                                .zoom(16)
                                .build());
                        mZoom++;

                    } else if (mZoom == 2) {
                        mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                .target(new LatLng(mGetLocation.getLatitude(),mGetLocation.getLongitude()))
                                .zoom(10)
                                .build());
                        mZoom++;
                    } else {
                        mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                .target(new LatLng(mGetLocation.getLatitude(),mGetLocation.getLongitude()))
                                .zoom(5)
                                .build());
                        mZoom = 1;
                    }

                }
            });

        } else {
            mGetLocation.showSettingsAlert();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    /**
     * @Description: method to create a marker at user's location.
     */
    private void userLocationMarker() {
        if (mGetLocation.canGetLocation()) {
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(MapboxMap mapboxMap) {
                    if (mMarker != null) mMarker.remove();
                    mMarker = mapboxMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mGetLocation.getLatitude(),mGetLocation.getLongitude()))
                            .icon(MainActivity.createIcon("ic_blue_dot", MainActivity.this))
                            .title("You are here!"));

                    Log.d("Explorer","Latitude:"+mGetLocation.getLatitude()+", Longitude:"+mGetLocation.getLongitude());
                }
            });
        } else {
            mGetLocation.showSettingsAlert();
        }

    }


    /**
     * check if internet is available or not
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        } else
            return false;

    }


    /**
     * @Description: Enabled two actionbar menu buttons.
     * 1. search button (TODO)
     * 2. Layer button - Changes to different map styles.
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        changeIconColor("ic_icon_search", this, Color.WHITE);
        changeIconColor("ic_icon_layers", this, Color.WHITE);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click


        switch (item.getItemId()) {
            case R.id.action_search:
                //TODO:@pravin

                return true;
            case R.id.action_layer:
                if (isNetworkAvailable()) {
                    if (mLayer == 1) {
                        mMapView.setStyleUrl("mapbox://styles/pravinkandala/cit611cqz00292wqmqgqvnuyt");
                        mLayer++;
                    } else if (mLayer == 2) {
                        mMapView.setStyleUrl("mapbox://styles/pravinkandala/citahk9mp000s2ipg6tktgha3");
                        mLayer++;
                    } else if (mLayer == 3) {
                        mMapView.setStyleUrl("mapbox://styles/pravinkandala/citahmbia001d2ip6aw7whxi3");
                        mLayer = 1;
                    }
                } else {
                    displayWarning();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
