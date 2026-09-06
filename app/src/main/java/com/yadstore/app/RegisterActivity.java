package com.yadstore.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.*;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText etName, etEmail, etPassword, etConfirm;
    private Button btnRegister;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirm = findViewById(R.id.et_confirm);
        btnRegister = findViewById(R.id.btn_register);
        
        btnRegister.setOnClickListener(v -> register());
    }
    
    private void register() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm = etConfirm.getText().toString().trim();
        
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Isi semua field!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!password.equals(confirm)) {
            Toast.makeText(this, "Password tidak cocok!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    String userId = mAuth.getCurrentUser().getUid();
                    Map<String, Object> user = new HashMap<>();
                    user.put("name", name);
                    user.put("email", email);
                    user.put("totalOrders", 0);
                    user.put("totalSpent", 0);
                    user.put("createdAt", System.currentTimeMillis());
                    user.put("role", "user");
                    user.put("isActive", true);
                    
                    db.collection("users").document(userId).set(user);
                    
                    Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Registrasi gagal!", Toast.LENGTH_LONG).show();
                }
            });
    }
}