package com.tecnico.foodist.ui;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tecnico.foodist.MainActivity;
import com.tecnico.foodist.R;

public class ProfileActivity  extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(ProfileActivity.this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        TextView email = (TextView)findViewById(R.id.emailText);
        email.setText(user.getEmail());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logoutButton:{
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(ProfileActivity.this, "Sign out successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            }

        }

    }
}
