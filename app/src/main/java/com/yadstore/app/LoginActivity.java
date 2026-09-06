package com.yadstore.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient googleSignInClient;
    
    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoogle;
    private TextView tvRegister;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("666352014774-8q7t7g6hq2.apps.googleusercontent.com")
            .requestEmail()
            .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnGoogle = findViewById(R.id.btn_google);
        tvRegister = findViewById(R.id.tv_register);
        
        btnLogin.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                loginWithEmail();
            }
        });
        
        btnGoogle.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                loginWithGoogle();
            }
        });
        
        tvRegister.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        
        // Check if already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
    
    private void loginWithEmail() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Isi email dan password!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        btnLogin.setEnabled(false);
        
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    btnLogin.setEnabled(true);
                    
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login berhasil!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login gagal!", Toast.LENGTH_LONG).show();
                    }
                }
            });
    }
    
    private void loginWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign-in failed: " + e.getMessage(), 
                             Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        
                        if (user != null) {
                            // Check if user exists in Firestore
                            checkAndCreateUser(user);
                            
                            Toast.makeText(LoginActivity.this, "Google login berhasil!", 
                                         Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed!", 
                                     Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
    
    private void checkAndCreateUser(FirebaseUser user) {
        db.collection("users").document(user.getUid())
            .get()
            .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<com.google.firebase.firestore.DocumentSnapshot>() {
                @Override
                public void onSuccess(com.google.firebase.firestore.DocumentSnapshot documentSnapshot) {
                    if (!documentSnapshot.exists()) {
                        // Create new user
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("name", user.getDisplayName() != null ? user.getDisplayName() : "User");
                        userData.put("email", user.getEmail());
                        userData.put("totalOrders", 0);
                        userData.put("totalSpent", 0);
                        userData.put("createdAt", System.currentTimeMillis());
                        userData.put("role", "user");
                        userData.put("isActive", true);
                        userData.put("authProvider", "google");
                        
                        db.collection("users").document(user.getUid()).set(userData);
                    }
                }
            });
    }
}