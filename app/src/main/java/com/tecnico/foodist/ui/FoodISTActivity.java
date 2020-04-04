package com.tecnico.foodist.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.tecnico.foodist.R;
import com.tecnico.foodist.models.User;

import java.util.ArrayList;

import static com.tecnico.foodist.util.AuxFunctions.df2;
import static com.tecnico.foodist.util.AuxFunctions.distance;

public class FoodISTActivity extends AppCompatActivity {

    //recycler view elements
    private RecyclerView recyclerView;
    private FoodISTAdapter adapter;
    private ArrayList<String> restaurants_name = new ArrayList<String>();
    private ArrayList<String> restaurants_id = new ArrayList<String>();
    private ArrayList<GeoPoint> restaurants_alameda_geoPoint = new ArrayList<GeoPoint>();
    private ArrayList<GeoPoint> restaurants_tagus_geoPoint = new ArrayList<GeoPoint>();

    //Bundle extras
    private Boolean atAlameda;
    private Boolean atTagus;
    private Double userLatitude;
    private Double userLongitude;

    //toolbar campus locaiton
    private Toolbar toolbar;
    private TextView location;

    //Instance for Firebase storage
    private FirebaseStorage storage;


    //TTO DO
    private ArrayList<String> distanceTime = new ArrayList<String>();
    String queueTime = "Queue time: XXX";
    private int image = R.drawable.food900x700;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodist);

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
                Toast.makeText(FoodISTActivity.this, "Refresh successful!", Toast.LENGTH_SHORT).show();
                adapter.clear();

                //fetch the current data from database
                if (atAlameda){getRestaurantsAlameda();} else {getRestaurantsTagusPark();}

                pullToRefresh.setRefreshing(false);

                //TO DO nees to fetch te current user position to make the new calculation!!!!
            }
        });

        //firebasestorage();

        //not complete
        //calculateDirections();


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

        restaurantsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        //get restaurant name
                        String name = document.getString("Name");
                        Log.w("Firebase-Name", name);
                        addRestaurantName(name);

                        //get restaurant location and calculate restaurant distance + time from user
                        GeoPoint geoPoint = document.getGeoPoint("location");
                        Log.w("Firebase-location", String.valueOf(geoPoint));
                        addRestaurantAlamedaGeoPoint(geoPoint);

                        //TO DOO with Goodle Directions
                        setRestauranteDistanceAndTime(geoPoint);

                        //get restaurante id
                        String id = document.getString("r_id");
                        Log.w("Firebase-Id", id);
                        addRestaurantId(id);

                    }
                    setRecyclerViewRestaurants();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Firebase", e);
            }
        });

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

                        //get restaurant location and calculate restaurant distance + time from user
                        GeoPoint geoPoint = document.getGeoPoint("location");
                        Log.w("Firebase-location", String.valueOf(geoPoint));
                        addRestaurantTagusGeoPoint(geoPoint);

                        //TO DOO with Goodle Directions
                        setRestauranteDistanceAndTime(geoPoint);

                        //get restaurante id
                        String id = document.getString("r_id");
                        Log.w("Firebase-Id", id);
                        addRestaurantId(id);
                    }
                    setRecyclerViewRestaurants();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Firebase-Tagus", e);
            }
        });

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



    //TO DOO - Arranjar outra maneira ja vejo
    private void setRestauranteDistanceAndTime(GeoPoint geoPoint) {
        distanceTime.add(
                (df2.format((
                        distance(geoPoint.getLatitude(),
                                geoPoint.getLongitude(),
                                userLatitude,
                                userLongitude,
                                "K") / 5) * 60) //vByFoot=5km/h v=d/t => t=d/v * 60 para min
                )+ " min (by foot)");

    }

    public void setRecyclerViewRestaurants(){
        recyclerView = findViewById(R.id.recyclerView);
        if (atAlameda){
            adapter = new FoodISTAdapter(this, restaurants_id , restaurants_name, image, distanceTime, queueTime, restaurants_alameda_geoPoint);

        }
        else{
           adapter = new FoodISTAdapter(this, restaurants_id , restaurants_name, image, distanceTime, queueTime, restaurants_tagus_geoPoint);

        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fadeIn(toolbar);
    }

    //TO DOO
    public void firebasestorage(){

        //get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();

        StorageReference imgReference = storage.getReference()
                .child("restaurantsProfilesPictures")  //image folder
                .child("food900x700.jpg");

        //download files as bytes
        final long ONE_MEGABYTE = 1024 * 1024;
        imgReference.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.w("storage","aqui");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("storage",e);
            }
        });
    }



    //get distance and time via google directions
    //Marker marker
    private void calculateDirections(){
        GeoApiContext geoApiContext = new GeoApiContext.Builder()
                .apiKey(getString(R.string.Google_Maps_API))
                .build();

        Log.d("TAG", "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(38.736367, -9.137236);
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(38.736367, -9.137236)
        );

        Log.d("TAG", "calculateDirections: destination: " + destination.toString());


        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d("TAG", "calculateDirections: routes: " + result.routes[0].toString());
                Log.d("TAG", "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d("TAG", "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d("TAG", "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e("TAG", "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }

}
