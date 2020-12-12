package com.example.ninjagoldgame;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    EditText ptFullName, ptEmail, ptPassword, ptPhone;
    Button btnRegister;
    TextView ptLoginHere;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ptFullName = findViewById(R.id.ptFullName);
        ptEmail = findViewById(R.id.ptEmail);
        ptPassword = findViewById(R.id.ptPassword);
        ptPhone = findViewById(R.id.ptPhone);
        btnRegister = findViewById(R.id.btnLogin);
        ptLoginHere = findViewById(R.id.ptLoginHere);
        progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        btnRegister.setOnClickListener(new View.OnClickListener(){
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

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            Toast.makeText(Register.this, "Error occured: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        ptLoginHere.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Login.class)));
    }
}