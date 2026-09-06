package com.yadstore.app;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.*;

public class PaymentActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText etGameId, etServerId, etEmail, etWhatsapp;
    private RadioGroup radioPayment;
    private Button btnSubmit;
    private String gameName, itemName;
    private long sellPrice;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        gameName = getIntent().getStringExtra("gameName");
        itemName = getIntent().getStringExtra("itemName");
        sellPrice = getIntent().getLongExtra("sellPrice", 0);
        
        etGameId = findViewById(R.id.et_game_id);
        etServerId = findViewById(R.id.et_server_id);
        etEmail = findViewById(R.id.et_email);
        etWhatsapp = findViewById(R.id.et_whatsapp);
        radioPayment = findViewById(R.id.radio_payment);
        btnSubmit = findViewById(R.id.btn_submit);
        
        btnSubmit.setOnClickListener(v -> submitOrder());
    }
    
    private void submitOrder() {
        String gameId = etGameId.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String whatsapp = etWhatsapp.getText().toString().trim();
        int selectedPaymentId = radioPayment.getCheckedRadioButtonId();
        
        if (gameId.isEmpty() || email.isEmpty() || whatsapp.isEmpty() || selectedPaymentId == -1) {
            Toast.makeText(this, "Isi semua field!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String[] paymentMethods = {"DANA", "GoPay", "SeaBank"};
        String paymentMethod = paymentMethods[selectedPaymentId];
        String userId = mAuth.getCurrentUser().getUid();
        
        Map<String, Object> order = new HashMap<>();
        order.put("userId", userId);
        order.put("game", gameName);
        order.put("itemName", itemName);
        order.put("sellPrice", sellPrice);
        order.put("gameId", gameId);
        order.put("serverId", etServerId.getText().toString().trim());
        order.put("email", email);
        order.put("whatsapp", whatsapp);
        order.put("paymentMethod", paymentMethod);
        order.put("status", "pending");
        order.put("createdAt", System.currentTimeMillis());
        
        db.collection("orders").add(order)
            .addOnSuccessListener(doc -> {
                Toast.makeText(this, "Pesanan berhasil! ID: " + doc.getId(), Toast.LENGTH_LONG).show();
                finish();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
}