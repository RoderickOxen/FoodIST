package com.tecnico.foodist.ui;

import android.os.Bundle;
import android.util.Log;

import com.tecnico.foodist.R;
import com.tecnico.foodist.models.Dish;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MenuActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    int image = R.drawable.food900x700;
    String s1[] = {"Lasanha", "Bifinhos ", "Historias Repetidas do Rodrigo", "Feels bad man", "Tosta de Frango", "Caviar", "Bacalhau com Natas"};
    String queueTime = "3 min";
    private ArrayList<Dish> menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        menu = (ArrayList<Dish>) getIntent().getSerializableExtra("serialzable");

        Log.w("MenuActivity - Menu", menu.get(0).getName());


        recyclerView = findViewById(R.id.menuRecycler);
        MenuAdapter adapter = new MenuAdapter(this, menu, image, queueTime);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
