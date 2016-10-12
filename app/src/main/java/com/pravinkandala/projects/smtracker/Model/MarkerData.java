package com.pravinkandala.projects.smtracker.Model;

import com.mapbox.mapboxsdk.geometry.LatLng;


public class MarkerData {

    private String mTitle;
    private String mCategory;
    private String mType;

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        this.mCategory = category;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    private LatLng latLng;

    public String getIcon() {
        final String prefix = "ic_pin_";
        final String typePrefix = this.mType.equalsIgnoreCase("custom") ? "custom_" : "poi_";
        final String categoryPrefix = mCategory.equalsIgnoreCase("fishing") ? "fishing" : "hunting";
        return prefix + typePrefix + categoryPrefix;
    }
}
