package com.tecnico.foodist.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.Duration;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.tecnico.foodist.R;
import java.io.IOException;
import java.util.ArrayList;



public class FoodISTActivity extends AppCompatActivity {

    //recycler view elements
    private RecyclerView recyclerView;
    private FoodISTAdapter adapter;
    private ArrayList<String> restaurants_name = new ArrayList<String>();
    private ArrayList<String> restaurants_id = new ArrayList<String>();
    private ArrayList<GeoPoint> restaurants_alameda_geoPoint = new ArrayList<GeoPoint>();
    private ArrayList<GeoPoint> restaurants_tagus_geoPoint = new ArrayList<GeoPoint>();
    private ArrayList<Duration> restaurants_time_distance = new ArrayList<Duration>();

    //Bundle extras
    private Boolean atAlameda;
    private Boolean atTagus;
    private Double userLatitude;
    private Double userLongitude;

    //toolbar campus locaiton
    private Toolbar toolbar;
    private TextView location;

    //TTO DO
    String queueTime = "Queue time: XXX";
    private int image = R.drawable.food900x700;


    //For calculating the duration
    GeoApiContext geoApiContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodist);

        //initiate the Google Directions API with he apiKey so we can calculate the durantion travel
        geoApiContext = new GeoApiContext.Builder().apiKey("AIzaSyBKj_1qFGu2CzxY18nYR-Zb-rxU-Xjhv2Y").build();

        //set toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set current campus on toolbar
        setCampusOnToolbar();

        //add listener to the location button to change
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLocationSearch();
            }
        });

        //add the recycler view
        if (atAlameda){
            getRestaurantsAlameda();
        }else {
            getRestaurantsTagusPark();
        }

        //refresh the contents of a view via a vertical swipe gesture
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.refreshFood);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();

                restaurants_name.clear();
                restaurants_alameda_geoPoint.clear();
                restaurants_tagus_geoPoint.clear();
                restaurants_id.clear();
                restaurants_time_distance.clear();

                //fetch the current data from database
                if (atAlameda){getRestaurantsAlameda();} else {getRestaurantsTagusPark();}

                Toast.makeText(FoodISTActivity.this, "Refresh successful!", Toast.LENGTH_SHORT).show();

                pullToRefresh.setRefreshing(false);

            }
        });
    }

    private void setCampusOnToolbar() {
        Bundle bundle = getIntent().getExtras();
        atAlameda = bundle.getBoolean("atAlameda");
        atTagus = bundle.getBoolean("atTaguspark");
        userLatitude = bundle.getDouble("userLatitude");
        userLongitude = bundle.getDouble("userLongitude");

        location = findViewById(R.id.toolbarTitle);
        if (atAlameda){
            location.setText("Campus Alameda");
        }
        else if (atTagus){
            location.setText("Campus Taguspark");
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_foodist_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miCompose:
                Toast.makeText(
                        getApplicationContext(),
                        "You clicked on the profile button",
                        Toast.LENGTH_SHORT)
                        .show();
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class ));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fadeIn(View view) {
        // Create an AlphaAnimation variable
        // 0.0f makes the view invisible
        // 1.0f makes the view fully visible
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        // Set out long you want the animation to be. * Measured in milliseconds *
        // 1000 milliseconds = 1 second
        anim.setDuration(1500);
        // Start the animation on our passed in view
        view.startAnimation(anim);
        /*  After the animation is complete we want to make sure we set the visibility of the view
            to VISIBLE. Otherwise it will go back to being INVISIBLE due to our previous lines
            that set the view to INVISIBLE */
        view.setVisibility(View.VISIBLE);
    }

    private void changeLocationSearch(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please select the campus that you want to change to.")
                .setCancelable(false)
                .setPositiveButton("Alameda", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        atAlameda = true;
                        atTagus = false;
                        location.setText("Campus Alameda");
                        adapter.clear();
                        getRestaurantsAlameda();

                    }
                })
                .setNegativeButton("Taguspark", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        atTagus = true;
                        atAlameda = false;
                        location.setText("Campus Taguspark");
                        adapter.clear();
                        getRestaurantsTagusPark();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void getRestaurantsAlameda(){
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference restaurantsRef = rootRef.collection("restaurants");

        //for each restaurante on the database
        restaurantsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        //get restaurant name
                        String name = document.getString("Name");
                        Log.w("Firebase-Name", name);
                        addRestaurantName(name);

                        //get restaurant location
                        GeoPoint geoPoint = document.getGeoPoint("location");
                        Log.w("Firebase-location", String.valueOf(geoPoint));
                        addRestaurantAlamedaGeoPoint(geoPoint);

                        //Calculate restaurant time to get there walking
                        durantionWalkingToRestaurant(geoPoint.getLatitude(),geoPoint.getLongitude());

                        //get restaurante id
                        String id = document.getString("r_id");
                        Log.w("Firebase-Id", id);
                        addRestaurantId(id);

                    }
                    setRecyclerViewRestaurants();
                }
            }
        }).addOnFailureListener(e -> Log.w("Firebase", e));

    }

    private void getRestaurantsTagusPark() {
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference restaurantsRef = rootRef.collection("restaurantsTagus");

        restaurantsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        //get restaurant name
                        String name = document.getString("Name");
                        Log.w("Firebase-Name", name);
                        addRestaurantName(name);

                        //get restaurant location
                        GeoPoint geoPoint = document.getGeoPoint("location");
                        Log.w("Firebase-location", String.valueOf(geoPoint));
                        addRestaurantTagusGeoPoint(geoPoint);

                        //Calculate restaurant time to get there walking
                        durantionWalkingToRestaurant(geoPoint.getLatitude(),geoPoint.getLongitude());

                        //get restaurante id
                        String id = document.getString("r_id");
                        Log.w("Firebase-Id", id);
                        addRestaurantId(id);
                    }
                    setRecyclerViewRestaurants();
                }

            }
        }).addOnFailureListener(e -> Log.w("Firebase-Tagus", e));

    }

    private void addRestaurantAlamedaGeoPoint(GeoPoint geoPoint) {
        restaurants_alameda_geoPoint.add(geoPoint);
    }
    private void addRestaurantTagusGeoPoint(GeoPoint geoPoint) {
        restaurants_tagus_geoPoint.add(geoPoint);
    }
    public void addRestaurantName(String name){

        restaurants_name.add(name);
    }
    public void addRestaurantId(String name){
        restaurants_id.add(name);
    }
    public void addDistanceTime(Duration time){
        restaurants_time_distance.add(time);
    }


    public void durantionWalkingToRestaurant(double lat, double lon){
        try {
            DirectionsResult result = DirectionsApi.newRequest(geoApiContext)
                    .mode(TravelMode.WALKING)
                    .origin(new LatLng(userLatitude, userLongitude))
                    .destination(new LatLng(lat, lon))
                    .await();

            Log.w("DirectionsResult", String.valueOf(result.routes[0].legs[0].duration));
            addDistanceTime(result.routes[0].legs[0].duration);

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setRecyclerViewRestaurants(){
        recyclerView = findViewById(R.id.recyclerView);

        if (atAlameda){
            adapter = new FoodISTAdapter(this, restaurants_id , restaurants_name, image, restaurants_time_distance, queueTime, restaurants_alameda_geoPoint);
        }
        else{
            adapter = new FoodISTAdapter(this, restaurants_id , restaurants_name, image, restaurants_time_distance, queueTime, restaurants_tagus_geoPoint);

        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fadeIn(toolbar);
    }

}
