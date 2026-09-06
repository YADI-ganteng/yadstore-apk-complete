package com.yadstore.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.*;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        
        btnLogin.setOnClickListener(v -> login());
        tvRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
    
    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Isi email dan password!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Login gagal!", Toast.LENGTH_LONG).show();
                }
            });
    }
}