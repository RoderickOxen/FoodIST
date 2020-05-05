package com.tecnico.foodist.ui;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.collection.LruCache;

import com.tecnico.foodist.models.Dish;
import com.tecnico.foodist.models.Restaurant;

import java.util.ArrayList;

public class GlobalClass extends Application {

    private ArrayList<Restaurant> restaurants;
    private boolean atAlameda;
    private static LruCache<String, Bitmap> mCache;
    private Dish currentDish;
    private String currentRestaurant;
    private String allQueueTime;
    private String allQueueTimeTagus;

    public String getAllQueueTimeTagus() {
        return allQueueTimeTagus;
    }

    public void setAllQueueTimeTagus(String allQueueTimeTagus) {
        this.allQueueTimeTagus = allQueueTimeTagus;
    }

    public String getAllQueueTime() {
        return allQueueTime;
    }

    public void setAllQueueTime(String allQueueTime) {
        this.allQueueTime = allQueueTime;
    }

    public static void setmCache(LruCache<String, Bitmap> mCache) {
        GlobalClass.mCache = mCache;
    }

    public static LruCache<String, Bitmap> getmCache() {
        return mCache;
    }

    public Bitmap getBitmapCache(String key){
        return mCache.get(key);
    }

    public void setBitmapCache(String key, Bitmap bitmap){
        if(getBitmapCache(key)== null){
            mCache.put(key,bitmap);
        }
    }

    public void setRestaurants(ArrayList<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    public ArrayList<Restaurant> getRestaurants() {
        return restaurants;
    }

    public boolean isAtAlameda() {
        return atAlameda;
    }

    public void setAtAlameda(boolean atAlameda) {
        this.atAlameda = atAlameda;
    }

    public void setCurrentDish(Dish currentDish) {
        this.currentDish = currentDish;
    }

    public Dish getCurrentDish() {
        return currentDish;
    }

    public void setCurrentRestaurant(String currentRestaurant) {
        this.currentRestaurant = currentRestaurant;
    }

    public String getCurrentRestaurant() {
        return currentRestaurant;
    }
}
