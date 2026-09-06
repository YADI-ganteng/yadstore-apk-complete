package com.yadstore.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class OrderActivity extends AppCompatActivity {
    private TextView tvGameTitle;
    private LinearLayout itemsContainer;
    private String gameName;
    
    private static final Map<String, String[]> GAME_ITEMS = new HashMap<>();
    private static final Map<String, long[]> GAME_PRICES = new HashMap<>();
    
    static {
        GAME_ITEMS.put("Mobile Legends", new String[]{
            "3 Diamonds", "5 Diamonds", "12 Diamonds", "19 Diamonds", 
            "28 Diamonds", "44 Diamonds", "59 Diamonds", "85 Diamonds",
            "170 Diamonds", "240 Diamonds", "296 Diamonds", "408 Diamonds",
            "568 Diamonds", "875 Diamonds", "2010 Diamonds", "4830 Diamonds"
        });
        GAME_PRICES.put("Mobile Legends", new long[]{
            1500, 2500, 6000, 9500, 14000, 22000, 29500, 42500,
            85000, 120000, 148000, 204000, 284000, 437500, 1005000, 2415000
        });
        
        GAME_ITEMS.put("Free Fire", new String[]{
            "5 Diamonds", "12 Diamonds", "50 Diamonds", "70 Diamonds",
            "140 Diamonds", "355 Diamonds", "720 Diamonds", "1450 Diamonds",
            "2180 Diamonds", "3640 Diamonds", "7290 Diamonds"
        });
        GAME_PRICES.put("Free Fire", new long[]{
            1000, 2400, 10000, 14000, 28000, 71000, 144000, 290000, 436000, 728000, 1458000
        });
        
        GAME_ITEMS.put("PUBG Mobile", new String[]{"60 UC", "325 UC", "660 UC", "1800 UC", "3850 UC", "8100 UC"});
        GAME_PRICES.put("PUBG Mobile", new long[]{15000, 80000, 160000, 430000, 900000, 1850000});
        
        GAME_ITEMS.put("Genshin Impact", new String[]{"60 GC", "330 GC", "1090 GC", "2240 GC", "3880 GC", "8080 GC"});
        GAME_PRICES.put("Genshin Impact", new long[]{15000, 80000, 260000, 530000, 900000, 1850000});
        
        GAME_ITEMS.put("Honkai Star Rail", new String[]{"60 Shard", "330 Shard", "1090 Shard", "2240 Shard", "3880 Shard", "8080 Shard"});
        GAME_PRICES.put("Honkai Star Rail", new long[]{15000, 80000, 260000, 530000, 900000, 1850000});
        
        GAME_ITEMS.put("Valorant", new String[]{"475 Points", "1000 Points", "2050 Points", "3650 Points", "5350 Points", "11000 Points"});
        GAME_PRICES.put("Valorant", new long[]{50000, 100000, 200000, 350000, 500000, 1000000});
        
        GAME_ITEMS.put("Roblox", new String[]{"80 Robux", "400 Robux", "800 Robux", "1700 Robux", "4500 Robux", "10000 Robux"});
        GAME_PRICES.put("Roblox", new long[]{15000, 75000, 150000, 300000, 750000, 1500000});
        
        GAME_ITEMS.put("Steam Wallet", new String[]{"SW 45K", "SW 60K", "SW 90K", "SW 120K", "SW 250K", "SW 400K", "SW 600K"});
        GAME_PRICES.put("Steam Wallet", new long[]{50000, 65000, 95000, 125000, 255000, 405000, 605000});
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        
        gameName = getIntent().getStringExtra("gameName");
        tvGameTitle = findViewById(R.id.tv_game_title);
        itemsContainer = findViewById(R.id.items_container);
        
        if (gameName != null) {
            tvGameTitle.setText(gameName);
            displayItems();
        }
    }
    
    private void displayItems() {
        String[] items = GAME_ITEMS.get(gameName);
        long[] prices = GAME_PRICES.get(gameName);
        
        if (items == null || prices == null) {
            return;
        }
        
        for (int i = 0; i < items.length; i++) {
            final String itemName = items[i];
            final long modalPrice = prices[i];
            final long sellPrice = Math.round(modalPrice * 1.1);
            
            Button btnItem = new Button(this);
            btnItem.setText(itemName + " - Rp " + String.format("%,d", sellPrice));
            btnItem.setTextColor(0xFFFFFFFF);
            btnItem.setBackgroundColor(0xFF1a1a2e);
            btnItem.setPadding(10, 15, 10, 15);
            btnItem.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    Intent intent = new Intent(OrderActivity.this, PaymentActivity.class);
                    intent.putExtra("gameName", gameName);
                    intent.putExtra("itemName", itemName);
                    intent.putExtra("sellPrice", sellPrice);
                    startActivity(intent);
                }
            });
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 10);
            btnItem.setLayoutParams(params);
            
            itemsContainer.addView(btnItem);
        }
    }
}