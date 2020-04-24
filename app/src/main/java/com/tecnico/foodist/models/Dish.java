package com.tecnico.foodist.models;

import java.io.Serializable;

public class Dish implements Serializable {

    private static final long serialVersionUID = 1L;
    private double price;
    private String name;
    private String imageName; // ainda nao sei

    public Dish(double price, String name){
        this.name = name;
        this.price = price;
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
}
