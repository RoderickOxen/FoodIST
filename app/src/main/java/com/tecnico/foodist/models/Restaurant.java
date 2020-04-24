package com.tecnico.foodist.models;

import com.google.firebase.firestore.GeoPoint;
import com.google.maps.model.Duration;


public class Restaurant {

    private String restaurants_name;
    private String restaurants_id;
    private GeoPoint restaurants_geoPoint;
    private Duration restaurants_time_distance ;
    private MenuIst menu;
    private String horario;


    public Restaurant() {
    }


    public Restaurant(String restaurants_name, String restaurants_id, GeoPoint restaurants_geoPoint, Duration restaurants_time_distance) {
        this.restaurants_name = restaurants_name;
        this.restaurants_id = restaurants_id;
        this.restaurants_geoPoint = restaurants_geoPoint;
        this.restaurants_time_distance = restaurants_time_distance;
    }



    public String getRestaurants_name() {

        return restaurants_name;
    }

    public void setRestaurants_name(String restaurants_name) {
        this.restaurants_name = restaurants_name;
    }

    public String getRestaurants_id() {

        return restaurants_id;
    }

    public void setRestaurants_id(String restaurants_id) {

        this.restaurants_id = restaurants_id;
    }

    public GeoPoint getRestaurants_geoPoint() {

        return restaurants_geoPoint;
    }

    public void setRestaurants_geoPoint(GeoPoint restaurants_alameda_geoPoint) {
        this.restaurants_geoPoint = restaurants_alameda_geoPoint;
    }

    public Duration getRestaurants_time_distance() {

        return restaurants_time_distance;
    }

    public void setRestaurants_time_distance(Duration restaurants_time_distance) {
        this.restaurants_time_distance = restaurants_time_distance;
    }

    public void setMenu(MenuIst menu) {
        this.menu = menu;
    }

    public MenuIst getMenu() {
        return menu;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getHorario() {
        return horario;
    }
}