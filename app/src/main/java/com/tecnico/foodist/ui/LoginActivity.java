package com.tecnico.foodist.ui;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.tecnico.foodist.MainActivity;
import com.tecnico.foodist.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Make sure to sign out any user that was previosly loggin
        FirebaseAuth.getInstance().signOut();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Declaration
        Button anonEntry = (Button) findViewById(R.id.link_anon);
        anonEntry.setOnClickListener(LoginActivity.this);

        Button createAccount = (Button) findViewById(R.id.link_register);
        createAccount.setOnClickListener(LoginActivity.this);

        Button logIn = (Button) findViewById(R.id.email_sign_in_button);
        logIn.setOnClickListener(LoginActivity.this);

        email   = (EditText)findViewById(R.id.email);
        password   = (EditText)findViewById(R.id.password);


        //setUp Progress Dialog
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Please wait.");


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
            case R.id.link_register:{
                Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.email_sign_in_button:{
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
                    logIn(email.getText().toString(), password.getText().toString());
                }
                break;
            }
        }
    }

    private void logIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this, "User Login with Success.", Toast.LENGTH_SHORT).show();
                            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                            LoginActivity.this.startActivity(myIntent);
                            Log.d("logInSuccess", "signInWithEmailAndPassword:success");


                        } else {
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Error:Failed to login User: "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("logInFailed", "signInWithEmailAndPassword:failed");
                        }
                    }
                });


    }
}
