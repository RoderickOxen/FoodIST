package com.tecnico.foodist.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.tecnico.foodist.R;
import com.tecnico.foodist.models.Dish;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MenuActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    int image = R.drawable.food900x700;
    //String s1[] = {"Lasanha", "Bifinhos ", "Historias Repetidas do Rodrigo", "Feels bad man", "Tosta de Frango", "Caviar", "Bacalhau com Natas"};
    String queueTime = "3 min";
    private ArrayList<Dish> menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        menu = (ArrayList<Dish>) getIntent().getSerializableExtra("serialzable");

        Button button = findViewById(R.id.addButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GlobalClass globalVariable = (GlobalClass) getApplicationContext();

                Log.d("Global", globalVariable.getRestaurants().get(0).getRestaurants_name());
                addDishActivity();
            }
        });


        Log.w("MenuActivity - Menu", menu.get(0).getName());


        recyclerView = findViewById(R.id.menuRecycler);
        MenuAdapter adapter = new MenuAdapter(this, menu, image);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void addDishActivity(){
        Intent intent = new Intent(this, AddDishActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("serialzable" , menu);
        intent.putExtras(b);


        startActivity(intent);
    }
}
