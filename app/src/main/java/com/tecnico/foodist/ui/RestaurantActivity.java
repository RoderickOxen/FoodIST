/*package com.tecnico.foodist.ui;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.tecnico.foodist.R;
import java.util.ArrayList;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RestaurantActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<String> mProperties = new ArrayList<>();
    private String rest_name;
    private String rest_id;
    private double rest_location_lat;
    private double rest_location_lon;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        //get restaurant properties
        Bundle bundle = getIntent().getExtras();
        rest_name = bundle.getString("rest_names");
        rest_id =  bundle.getString("rest_id");
        rest_location_lat = bundle.getDouble("latitude");
        rest_location_lon =bundle.getDouble("longitude");

        //init fragment view
        initProperties();

        //init map view
        addMapFragmentViwe();

    }

    private void addMapFragmentViwe(){
        hideSoftKeyboard();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MapViewFragment fragment = MapViewFragment.newInstance(rest_location_lat, rest_location_lon, rest_name);
        fragmentTransaction.add(R.id.map_container, fragment);
        fragmentTransaction.commit();
    }

    private void initProperties(){
        mProperties.add(rest_name);

        //TO DO
        mProperties.add("Schedule: "+"TO DO");
        mProperties.add("Queue Time:\n TO DO");
        mProperties.add("Menu:\n TO DO");

        initRecycleView();
    }

    private void initRecycleView(){
        RecyclerView recyclerView = findViewById(R.id.properties_recycler_view);
        RestaurantProperties restaurantProperties = new RestaurantProperties(mProperties, this);
        recyclerView.setAdapter(restaurantProperties);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onClick(View v) {


    }





















    //para apagar
    private void inflateUserListFragment(){
        //ainda nao sei ao certo o que é isto
        hideSoftKeyboard();

        //creates a mapview fragment and passes the rest locatiion to display
        MapViewFragment fragment = MapViewFragment.newInstance(rest_location_lat, rest_location_lon, rest_name);

        //ainda nao sei ao certo o que é isto
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
       // transaction.replace(R.id.user_list_container, fragment);
        transaction.commit();
    }


}*/