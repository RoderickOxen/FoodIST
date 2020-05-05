package com.tecnico.foodist.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Dish implements Serializable {

    private static final long serialVersionUID = 1L;
    private double price;
    private String name;
    private ArrayList<String> photos;
    private String document;

    public Dish(double price, String name,ArrayList<String> photos, String document){
        this.name = name;
        this.price = price;
        this.photos = photos;
        this.document = document;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public void addPhotos(String photo) {
        this.photos.add(photo);
    }

    public void setPhotos(ArrayList<String> photos) {
        this.photos = photos;
    }

}
