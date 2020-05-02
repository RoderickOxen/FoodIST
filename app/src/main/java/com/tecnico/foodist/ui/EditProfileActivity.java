package com.tecnico.foodist.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tecnico.foodist.R;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity  extends AppCompatActivity implements View.OnClickListener {

    private Spinner dropdown;
    private String status = "";
    private ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Button update = findViewById(R.id.change_status);
        update.setOnClickListener(EditProfileActivity.this);

        //setUp Progress Dialog
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Please wait.");

        //setup Spinner
        dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{"Student", "Professor", "Researcher", "Staff", "General Public"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String item = dropdown.getSelectedItem().toString();
                status = item;
                Log.i("Selected item:", item);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.change_status:{

                if (status.equals("")){
                    ((TextView)dropdown.getSelectedView()).setError("Nothing selected");
                }
                else{
                    Log.w("status",status);
                    dialog.show();
                    updateUserStatus();
                }


            }
        }
    }


    private void updateUserStatus() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //get doc ref
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);

        //
        Map<String, String> note = new HashMap<>();
        note.put("UniversityStatus", status);



        userRef.update("UniversityStatus", status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog.hide();
                Toast.makeText(EditProfileActivity.this, "User status updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.hide();

                Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                Log.w("error123", e.getMessage());
            }
        });
    }

}
