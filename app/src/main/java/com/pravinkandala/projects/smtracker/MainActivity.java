package com.pravinkandala.projects.smtracker;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class MainActivity extends AppCompatActivity {

    MapView mapView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        // Create an Icon object for the marker to use
        IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
        Drawable iconDrawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.pin);
        final Icon icon = iconFactory.fromDrawable(iconDrawable);

        mapView.setStyleUrl("mapbox://styles/pravinkandala/cit611cqz00292wqmqgqvnuyt");
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                MarkerViewOptions marker = new MarkerViewOptions()
                        .position(new LatLng(-33.85699436, 151.21510684));
                marker.title("Demo");
                marker.icon(icon);
                mapboxMap.addMarker(marker);
            }
        });

    }



}
