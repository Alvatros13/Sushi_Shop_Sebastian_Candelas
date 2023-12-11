package com.example.practica03_sebastian;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainUserActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TextView txtMail;
    private ImageButton logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        firebaseAuth = FirebaseAuth.getInstance();
        txtMail = findViewById(R.id.txtMail);
        logoutBtn = findViewById(R.id.logoutBtn);
        checkUser();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(MainUserActivity.this, MainActivity.class));
            finish();
        } else {
            String email = firebaseUser.getEmail();
            txtMail.setText(email);
        }
    }
}