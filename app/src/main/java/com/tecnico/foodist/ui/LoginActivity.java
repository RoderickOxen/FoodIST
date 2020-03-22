package com.tecnico.foodist.ui;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.tecnico.foodist.MainActivity;
import com.tecnico.foodist.R;
import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Declaration
        Button anonEntry = (Button) findViewById(R.id.link_anon);
        anonEntry.setOnClickListener(LoginActivity.this);
    }


    @Override
    public void onClick(View v) {
        Log.d("mainOnClick", "This is a flagpoint");

        switch (v.getId()){
            case R.id.link_anon:{
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}
