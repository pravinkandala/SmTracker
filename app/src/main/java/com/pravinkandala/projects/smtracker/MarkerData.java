package com.pravinkandala.projects.smtracker;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by Pravin on 9/16/16.
 * Project: SmTracker
 */

public class MarkerData {

    private String title;
    private String category;
    private String type;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private LatLng latLng;

    public String getIcon() {
        final String prefix = "pin_";
        final String typePrefix = this.type.equalsIgnoreCase("custom") ? "custom_" : "poi_";
        final String categoryPrefix = category.equalsIgnoreCase("fishing") ? "fishing" : "hunting";
        return prefix + typePrefix + categoryPrefix;
    }
}
