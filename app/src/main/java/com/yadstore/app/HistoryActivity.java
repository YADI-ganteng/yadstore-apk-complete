package com.yadstore.app;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

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
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            return;
        }
        
        String userId = user.getUid();
        
        db.collection("orders")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot snap) {
                    ordersContainer.removeAllViews();
                    
                    if (snap.isEmpty()) {
                        TextView tvEmpty = new TextView(HistoryActivity.this);
                        tvEmpty.setText("Belum ada pesanan");
                        tvEmpty.setTextColor(0xFF888888);
                        tvEmpty.setTextSize(16);
                        tvEmpty.setPadding(0, 20, 0, 0);
                        ordersContainer.addView(tvEmpty);
                        return;
                    }
                    
                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        String game = doc.getString("game");
                        String itemName = doc.getString("itemName");
                        Long sellPrice = doc.getLong("sellPrice");
                        String status = doc.getString("status");
                        
                        TextView tvOrder = new TextView(HistoryActivity.this);
                        String orderText = game + " - " + itemName + "\n" +
                            "Rp " + String.format("%,d", sellPrice != null ? sellPrice : 0) + "\n" +
                            "Status: " + status + "\n---";
                        
                        tvOrder.setText(orderText);
                        tvOrder.setTextColor(0xFFFFFFFF);
                        tvOrder.setTextSize(14);
                        tvOrder.setPadding(0, 0, 0, 20);
                        
                        ordersContainer.addView(tvOrder);
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    TextView tvError = new TextView(HistoryActivity.this);
                    tvError.setText("Gagal: " + e.getMessage());
                    tvError.setTextColor(0xFFFF4444);
                    tvError.setTextSize(14);
                    ordersContainer.addView(tvError);
                }
            });
    }
}