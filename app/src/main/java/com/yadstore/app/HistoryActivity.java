package com.yadstore.app;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.*;

public class HistoryActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private LinearLayout ordersContainer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        ordersContainer = findViewById(R.id.orders_container);
        
        loadOrders();
    }
    
    private void loadOrders() {
        String userId = mAuth.getCurrentUser().getUid();
        
        db.collection("orders")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(snap -> {
                ordersContainer.removeAllViews();
                
                if (snap.isEmpty()) {
                    TextView tvEmpty = new TextView(this);
                    tvEmpty.setText("Belum ada pesanan");
                    tvEmpty.setTextColor(0xFF888888);
                    ordersContainer.addView(tvEmpty);
                    return;
                }
                
                for (var doc : snap) {
                    Map<String, Object> order = doc.getData();
                    
                    TextView tvOrder = new TextView(this);
                    tvOrder.setText(
                        order.get("game") + " - " + order.get("itemName") + "\n" +
                        "Rp " + String.format("%,d", (long) order.get("sellPrice")) + "\n" +
                        "Status: " + order.get("status") + "\n" +
                        "---"
                    );
                    tvOrder.setTextColor(0xFFFFFFFF);
                    tvOrder.setTextSize(14);
                    tvOrder.setPadding(0, 0, 0, 20);
                    
                    ordersContainer.addView(tvOrder);
                }
            });
    }
}