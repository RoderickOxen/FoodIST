package com.tecnico.foodist.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import com.tecnico.foodist.R;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class RestaurantProfileActivity extends AppCompatActivity   {

    private ArrayList<String> mProperties = new ArrayList<>();
    private String rest_name;
    private String rest_id;
    private double rest_location_lat;
    private double rest_location_lon;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_profile);

        //get restaurant properties
        Bundle bundle = getIntent().getExtras();
        rest_name = bundle.getString("rest_names");
        rest_id =  bundle.getString("rest_id");
        rest_location_lat = bundle.getDouble("latitude");
        rest_location_lon =bundle.getDouble("longitude");

        Button button = findViewById(R.id.menu_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenuActivity();
            }
        });

        //init
        inflateUserListFragment();


    }
    public void openMenuActivity(){
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }


    //para apagar
    private void inflateUserListFragment(){
        hideSoftKeyboard();

        MapViewFragment fragment = MapViewFragment.newInstance(rest_location_lat, rest_location_lon, rest_name);
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.user_list_container, fragment);
        transaction.commit();
    }


    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }



}