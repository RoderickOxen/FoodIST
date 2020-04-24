package com.tecnico.foodist.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tecnico.foodist.R;
import com.tecnico.foodist.models.Dish;
import com.tecnico.foodist.models.MenuIst;

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
    private String horario;
    private ArrayList<Dish> menu;

    TextView nameRest;
    ImageView restProfile;
    TextView horarioRes;

    //TO DO RESTAURANTE SCHEDULE


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_profile);

        //get restaurant properties
        Bundle bundle = getIntent().getExtras();
        rest_name = bundle.getString("rest_names");
        rest_id =  bundle.getString("rest_id");
        rest_location_lat = bundle.getDouble("latitude");
        rest_location_lon = bundle.getDouble("longitude");
        horario = bundle.getString("horario");
        Log.w("Horario", horario);

        menu = (ArrayList<Dish>) getIntent().getSerializableExtra("serialzable");

        Log.w("Menu", menu.get(0).getName());



        //set image view
        setImageView();

        Button button = findViewById(R.id.menu_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenuActivity();
            }
        });

        //init
        inflateMapFragment();


    }

    private void setImageView() {

        //set name
        horarioRes = findViewById(R.id.horarioProfile);
        nameRest =  findViewById(R.id.textDetail);
        nameRest.setText(rest_name);
        horarioRes.setText(horario);

        //set profile image
        restProfile = (ImageView)findViewById(R.id.imageDetail);
        switch(rest_id) {
            case "ae":
                restProfile.setImageResource(R.drawable.ae);
                break;
            case "civil":
                restProfile.setImageResource(R.drawable.civil);
                break;
            case "matematica":
                restProfile.setImageResource(R.drawable.matematica);
                break;
            case "mecanica":
                restProfile.setImageResource(R.drawable.mecanica);
                break;
            case "central":
                restProfile.setImageResource(R.drawable.central);
                break;
            case "quimica":
                restProfile.setImageResource(R.drawable.quimica);
                break;
            case "amarelo":
                restProfile.setImageResource(R.drawable.amarelo);
                break;
            case "maquinas":
                restProfile.setImageResource(R.drawable.maquinas);
                break;
            case "redbar":
                restProfile.setImageResource(R.drawable.redbar);
                break;
            case "cantina":
                restProfile.setImageResource(R.drawable.cantina);
                break;
            default:
                restProfile.setImageResource(R.drawable.buffet1600x800);
        }
    }

    public void openMenuActivity(){
        Intent intent = new Intent(this, MenuActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("serialzable",menu);
        intent.putExtras(b);


        startActivity(intent);
    }

    private void inflateMapFragment(){
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