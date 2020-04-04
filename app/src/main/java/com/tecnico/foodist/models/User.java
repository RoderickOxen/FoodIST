package com.tecnico.foodist.models;

import com.google.android.gms.maps.model.LatLng;



public class User {

    private LatLng location;

    public User(LatLng latLng) {
        this.location = latLng;
    }

    public LatLng getUserLocation() {
        return location;
    }


}
