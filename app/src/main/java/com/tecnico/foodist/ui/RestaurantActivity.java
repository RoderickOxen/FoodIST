package com.tecnico.foodist.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import com.tecnico.foodist.R;
import java.util.ArrayList;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RestaurantActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<String> mProperties = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        //init
        initProperties();
        inflateUserListFragment();

        //get Campus
        Bundle bundle = getIntent().getExtras();
        //Boolean atAlameda = bundle.getBoolean("atAlameda");
        //Boolean atTagus = bundle.getBoolean("atTagus");

        //Log.d("alameda", String.valueOf(atAlameda));
        //Log.d("tagus", String.valueOf(atTagus));

    }

    private void initProperties(){
        //depois levar set consoante o click que é passa
        //if ID=1 entao set properties tais etc
        mProperties.add("Restaurant:"+"\nCantina");
        mProperties.add("Schedule: "+"\n12h30-14h30\n19h30-21h00");
        mProperties.add("Queue Time:\n X minutes");
        mProperties.add("Distance:\n X meters");
        mProperties.add("Menu:\n XXXX");

        initRecycleView();
    }

    private void initRecycleView(){
        RecyclerView recyclerView = findViewById(R.id.properties_recycler_view);
        RestaurantProperties restaurantProperties = new RestaurantProperties(mProperties, this);
        recyclerView.setAdapter(restaurantProperties);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void inflateUserListFragment(){
        //ainda nao sei ao certo o que é isto
        hideSoftKeyboard();
        MapViewFragment fragment = MapViewFragment.newInstance();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
        transaction.replace(R.id.user_list_container, fragment); //, getString(R.string.fragment_map_view));
        //transaction.addToBackStack(getString(R.string.fragment_map_view));
        transaction.commit();
    }
    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onClick(View v) {

    }


}