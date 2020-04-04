package com.tecnico.foodist.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.tecnico.foodist.R;

public class MenuActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    int image = R.drawable.food900x700;
    String s1[] = {"Lasanha", "Bifinhos ", "Historias Repetidas do Rodrigo", "Feels bad man", "Tosta de Frango", "Caviar", "Bacalhau com Natas"};
    String distance = "300.000$ ";
    String queueTime = "3 min";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        recyclerView = findViewById(R.id.menuRecycler);
        MenuAdapter adapter = new MenuAdapter(this, s1, image, distance, queueTime);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
