package com.pravinkandala.projects.smtracker;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by Pravin on 9/16/16.
 * Project: SmTracker
 */
public class MarkerData {

    private static MarkerData markerData;

    //All final attributes
    private static  String title; // required
    private static LatLng latLng; // required

    public LatLng getLatLng() {
        return latLng;
    }

    public String getTitle() {
        return title;
    }

//    public void setLatLng(LatLng latLng) {
//        this.latLng = latLng;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }

    public MarkerData(String title, LatLng latLng){
        title = title;
        latLng = latLng;
    }



//    public static MarkerData getInstance() {
//        if(markerData == null)
//            markerData = new MarkerData(title,latLng);
//        return markerData;
//    }
}
