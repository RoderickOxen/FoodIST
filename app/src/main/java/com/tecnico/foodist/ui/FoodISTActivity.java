package com.tecnico.foodist.ui;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.tecnico.foodist.R;

public class FoodISTActivity extends AppCompatActivity{

    RecyclerView recyclerView;
    int image = R.drawable.food900x700;
    String s1[] = {"Central", "Aero ", "Mecanica", "Matematica", "Minas", "Biologia", "Civil"};
    String distance = "300 m";
    String queueTime = "3 min";
    Boolean atAlameda;
    Boolean atTagus;
    TextView location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodist);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get Campus
        Bundle bundle = getIntent().getExtras();
        atAlameda = bundle.getBoolean("atAlameda");
        atTagus = bundle.getBoolean("atTagus");
        location = findViewById(R.id.toolbarTitle);


        if (atAlameda){
            location.setText("Campus Alameda");
        }
        else if (atTagus){
            location.setText("Campus Taguspark");
        }

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        location.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeLocation();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        FoodISTAdapter adapter = new FoodISTAdapter(this, s1, image, distance, queueTime);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fadeIn(toolbar);
    }

    // Menu icons are inflated just as they were with actionbar
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
                        "You clicked the compose button.",
                        Toast.LENGTH_SHORT)
                        .show();
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class ));
                return true;

            //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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

    private void changeLocation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please select the campus that you want to change to.")
                .setCancelable(false)
                .setPositiveButton("Alameda", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        atAlameda = true;
                        atTagus = false;
                        location.setText("Campus Alameda");

                    }
                })
                .setNegativeButton("Taguspark", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        atTagus = true;
                        atAlameda = false;
                        location.setText("Campus Taguspark");

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
