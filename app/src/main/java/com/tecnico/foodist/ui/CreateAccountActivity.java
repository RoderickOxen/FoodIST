package com.tecnico.foodist.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tecnico.foodist.R;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email;
    private EditText password;
    private EditText name;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;
    private Spinner dropdown;
    private String status = "";
    private FirebaseFirestore fstore;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //setUp Progress Dialog
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Please wait.");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Button Declaration
        Button button = (Button) findViewById(R.id.create_acount_button);
        button.setOnClickListener(CreateAccountActivity.this);

        //Find Views Declaration
        email   = (EditText)findViewById(R.id.create_email);
        password   = (EditText)findViewById(R.id.create_password);
        name   = (EditText)findViewById(R.id.Name);

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

        //instanciate firestore
        fstore = FirebaseFirestore.getInstance();

        //See if user is already loggedIn
        if (mAuth.getCurrentUser() != null){
            Toast.makeText(CreateAccountActivity.this, "User is currently LogIn", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.create_acount_button:{

                if (name.getText().toString().matches("")){
                    name.setError("Name is required");
                }
                else if (email.getText().toString().matches("")){
                    email.setError("Email is required");
                }
                else if (password.getText().toString().matches("")){
                    password.setError("Password is required");
                }
                else if (password.getText().toString().length() < 6){
                    password.setError("Password must be >= 6 characters");
                }
                else if (status.equals("")){
                    ((TextView)dropdown.getSelectedView()).setError("Nothing selected");
                }
                else{
                    Log.w("status",status);
                    dialog.show();
                    createAccount(email.getText().toString(), password.getText().toString());
                }

                break;
            }
        }
    }

    private void createAccount(String email, String password) {

        Log.w("email", email);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(CreateAccountActivity.this, "User created. Please login LogIn", Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            Log.d("createSuccess", "createUserWithEmail:success");

                            String userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fstore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("Name", name.getText().toString());
                            user.put("Email", email);
                            user.put("UniversityStatus", status.toString());
                            user.put("DC", "fish meat vegan vegetarian");


                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("userUpload", "User fully uploaded");
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("userUploadFailed", "Failed");

                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            dialog.dismiss();
                            Toast.makeText(CreateAccountActivity.this, "Error:No User created: "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Log.w("createFailed", "createUserWithEmail:failure", task.getException());

                        }
                    }
                });
    }

}
