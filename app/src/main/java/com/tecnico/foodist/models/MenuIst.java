package com.tecnico.foodist.models;

import java.util.ArrayList;

public class MenuIst  {
    private ArrayList<Dish> dishes = new ArrayList<>();
    private int nOfDishes = 0;

    public MenuIst(){}

    public void setDishes(ArrayList<Dish> dishes) {
        this.dishes = dishes;
        this.nOfDishes = dishes.size();
    }

    public ArrayList<Dish> getDishes() {
        return dishes;
    }

    public void addDish(Dish dish) {
        this.dishes.add(dish);
        this.nOfDishes = dishes.size();
    }

    public int getnOfDishes() {
        return nOfDishes;
    }
}
