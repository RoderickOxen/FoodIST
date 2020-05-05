package com.tecnico.foodist.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tecnico.foodist.R;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class DietryConstraintActivity extends AppCompatActivity implements View.OnClickListener{

    private CheckBox vegan, vegetarian, meat, fish;
    private Button submit;
    StringBuffer result = new StringBuffer();
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietary_constraints);

        addListenerOnButton();

        //setUp Progress Dialog
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Please wait.");

    }

    public void addListenerOnButton() {

        vegan = (CheckBox) findViewById(R.id.Vegan);
        vegetarian = (CheckBox) findViewById(R.id.Vegetarian);
        meat = (CheckBox) findViewById(R.id.Meat);
        fish = (CheckBox) findViewById(R.id.fish);
        submit = (Button) findViewById(R.id.checkBoxBtn);

        submit.setOnClickListener(new View.OnClickListener() {

            //Run when button is clicked
            @Override
            public void onClick(View v) {

                if(vegan.isChecked()){
                    result.append("vegan");
                    result.append("-");
                }
                if(vegetarian.isChecked()){
                    result.append("vegetarian");
                    result.append("-");
                }
                if(meat.isChecked()){
                    result.append("meat");
                    result.append("-");
                }
                if(fish.isChecked()){
                    result.append("fish");
                    result.append("-");
                }

                dialog.show();
                updateUserStatus();
            }
        });

    }

    private void updateUserStatus() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //get doc ref
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);

        //
        Map<String, String> note = new HashMap<>();
        note.put("DC", result.toString());


        userRef.update("DC", result.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog.hide();
                Toast.makeText(DietryConstraintActivity.this, "User dietary updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.hide();

                Toast.makeText(DietryConstraintActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                Log.w("error123", e.getMessage());
            }
        });
    }


    @Override
    public void onClick(View v) {

    }
}
