package com.yadstore.app;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvName, tvEmail, tvOrders, tvSpent;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email);
        tvOrders = findViewById(R.id.tv_orders);
        tvSpent = findViewById(R.id.tv_spent);
        
        loadProfile();
    }
    
    private void loadProfile() {
        String userId = mAuth.getCurrentUser().getUid();
        
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    tvName.setText("Nama: " + doc.getString("name"));
                    tvEmail.setText("Email: " + doc.getString("email"));
                    tvOrders.setText("Total Pesanan: " + doc.getLong("totalOrders"));
                    tvSpent.setText("Total Belanja: Rp " + String.format("%,d", doc.getLong("totalSpent")));
                }
            });
    }
}