package com.tecnico.foodist.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tecnico.foodist.MainActivity;
import com.tecnico.foodist.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email;
    private EditText password;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //setUp Progress Dialog
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Please wait.");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Declaration
        Button button = (Button) findViewById(R.id.create_acount_button);
        button.setOnClickListener(CreateAccountActivity.this);
        email   = (EditText)findViewById(R.id.create_email);
        password   = (EditText)findViewById(R.id.create_password);

        //Auth setup
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
                if (email.getText().toString().matches("")){
                    email.setError("Email is required");
                }
                else if (password.getText().toString().matches("")){
                    password.setError("Password is required");
                }
                else if (password.getText().toString().length() < 6){
                    password.setError("Password must be >= 6 characters");
                }
                else{
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
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));


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
