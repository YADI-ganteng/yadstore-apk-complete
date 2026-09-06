package com.yadstore.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvWelcome;
    private Button btnML, btnFF, btnPUBG, btnGI, btnHSR, btnVAL, btnRBX, btnSteam;
    private Button btnHistory, btnProfile, btnLogout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        tvWelcome = findViewById(R.id.tv_welcome);
        
        btnML = findViewById(R.id.btn_ml);
        btnFF = findViewById(R.id.btn_ff);
        btnPUBG = findViewById(R.id.btn_pubg);
        btnGI = findViewById(R.id.btn_gi);
        btnHSR = findViewById(R.id.btn_hsr);
        btnVAL = findViewById(R.id.btn_val);
        btnRBX = findViewById(R.id.btn_rbx);
        btnSteam = findViewById(R.id.btn_steam);
        
        btnHistory = findViewById(R.id.btn_history);
        btnProfile = findViewById(R.id.btn_profile);
        btnLogout = findViewById(R.id.btn_logout);
        
        loadUserData();
        
        btnML.setOnClickListener(v -> openGame("Mobile Legends"));
        btnFF.setOnClickListener(v -> openGame("Free Fire"));
        btnPUBG.setOnClickListener(v -> openGame("PUBG Mobile"));
        btnGI.setOnClickListener(v -> openGame("Genshin Impact"));
        btnHSR.setOnClickListener(v -> openGame("Honkai Star Rail"));
        btnVAL.setOnClickListener(v -> openGame("Valorant"));
        btnRBX.setOnClickListener(v -> openGame("Roblox"));
        btnSteam.setOnClickListener(v -> openGame("Steam Wallet"));
        
        btnHistory.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
    
    private void loadUserData() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    String name = doc.getString("name");
                    tvWelcome.setText("Welcome, " + name + "!");
                }
            });
    }
    
    private void openGame(String gameName) {
        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra("gameName", gameName);
        startActivity(intent);
    }
}