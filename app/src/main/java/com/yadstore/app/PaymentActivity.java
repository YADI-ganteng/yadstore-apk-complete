package com.yadstore.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

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
        
        btnSubmit.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                submitOrder();
            }
        });
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
        
        String paymentMethod = "";
        if (selectedPaymentId == R.id.rb_dana) {
            paymentMethod = "DANA";
        } else if (selectedPaymentId == R.id.rb_gopay) {
            paymentMethod = "GoPay";
        } else if (selectedPaymentId == R.id.rb_seabank) {
            paymentMethod = "SeaBank";
        }
        
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Silakan login dulu!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String userId = user.getUid();
        
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
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(PaymentActivity.this, "Pesanan berhasil!", Toast.LENGTH_LONG).show();
                    finish();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(PaymentActivity.this, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }
}