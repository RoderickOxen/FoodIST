package com.tecnico.foodist.ui;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tecnico.foodist.MainActivity;
import com.tecnico.foodist.R;

public class ProfileActivity  extends AppCompatActivity implements View.OnClickListener {

    private FirebaseUser user;
    TextView name;
    TextView status;
    TextView dc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button logoutButton = findViewById(R.id.logoutButton);
        Button editProfile = findViewById(R.id.editProfile);
        Button dietryConstraints = findViewById(R.id.dietryConstraints);


        logoutButton.setOnClickListener(ProfileActivity.this);
        editProfile.setOnClickListener(ProfileActivity.this);
        dietryConstraints.setOnClickListener(ProfileActivity.this);


        user = FirebaseAuth.getInstance().getCurrentUser();
        TextView email = (TextView)findViewById(R.id.email);
        name = (TextView)findViewById(R.id.name);
        status = (TextView)findViewById(R.id.status);
        dc = (TextView)findViewById(R.id.status2);
        email.setText(user.getEmail());
        getUserProperties();


    }

    private void getUserProperties() {
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        DocumentReference userRef = rootRef.collection("users").document(user.getUid());

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                name.setText(documentSnapshot.getString("Name"));
                status.setText(documentSnapshot.getString("UniversityStatus"));

                String temp = documentSnapshot.getString("DC");
                StringBuilder stringBuffer = new StringBuilder();
                String temp1[] = temp.split("-");
                for (int i=0;i<temp1.length;i++){
                    stringBuffer.append(temp1[i]+" ");
                }
                dc.setText(stringBuffer.toString());
            }
        });

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
            case R.id.editProfile:{
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.dietryConstraints:{
                Intent intent = new Intent(ProfileActivity.this, DietryConstraintActivity.class);
                startActivity(intent);
                break;
            }

        }

    }
}
