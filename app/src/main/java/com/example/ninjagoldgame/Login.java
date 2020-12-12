package com.example.ninjagoldgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    EditText ptEmail, ptPassword;
    Button btnLogin;
    TextView ptRegisterHere;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ptEmail = findViewById(R.id.ptEmail);
        ptPassword = findViewById(R.id.ptPassword);
        ptRegisterHere = findViewById(R.id.ptCreateAccount);
        progressBar = findViewById(R.id.progressBar);
        btnLogin = findViewById(R.id.btnLogin);
        fAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ptEmail.getText().toString().trim();
                String password = ptPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    ptEmail.setError("Email is required.");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    ptPassword.setError("Password is required.");
                    return;
                }

                if(password.length() < 6){
                    ptPassword.setError("Password should be >= 6 character");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Logged in Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            Toast.makeText(Login.this, "Error occured: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        ptRegisterHere.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Register.class)));
    }
}