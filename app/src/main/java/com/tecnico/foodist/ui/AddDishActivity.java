package com.tecnico.foodist.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tecnico.foodist.R;
import com.tecnico.foodist.models.Dish;
import com.tecnico.foodist.models.Restaurant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AddDishActivity extends AppCompatActivity {

    private EditText name;
    private EditText price;
    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dish);

        name   = (EditText)findViewById(R.id.dishName);
        price   = (EditText)findViewById(R.id.dishPrice);

        Button button = findViewById(R.id.addDishButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDishToFirebase();
            }
        });


    }

    private void addDishToFirebase(){

        if (name.getText().toString().matches("")){
            name.setError("Name is required");
        }
        else if (price.getText().toString().matches("")){
            price.setError("Price is required");
        }
        else{
            firebase(name.getText().toString(),  price.getText().toString());
        }

    }

    private void firebase(String name, String price) {

        // Create a new user with a first, middle, and last name
        Map<String, Object> dish = new HashMap<>();
        Double d = Double.valueOf(price);
        dish.put("Name", name);
        dish.put("Price", d);
        dish.put("Photos", Arrays.asList());

        GlobalClass globalVariable = (GlobalClass) getApplicationContext();

        String collection =  "restaurantsTagus";
        if(globalVariable.isAtAlameda()){
            collection = "restaurants";
        }

        Log.d("Path   ", collection +"  " + globalVariable.getCurrentRestaurant() + "  " + "Menu");
        // Add a new document with a generated ID
        CollectionReference restaurantsRef = rootRef.collection(collection).document(globalVariable.getCurrentRestaurant()).collection("Menu");

        restaurantsRef.add(dish)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Fire lets see", "DocumentSnapshot added with ID: " + documentReference.getId());


                        ArrayList<String> list = new ArrayList<String>();
                        Dish dish = new Dish(d,name,list, documentReference.getId());
                        ArrayList<Restaurant> rest = globalVariable.getRestaurants();
                        for(Restaurant restaurant : rest) {
                            if(restaurant.getRestaurants_id() == globalVariable.getCurrentRestaurant()) {
                                restaurant.getMenu().addDish(dish);
                            }
                        }
                        globalVariable.setRestaurants(rest);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("base", "Error adding document", e);
                    }
                });


    }

}
