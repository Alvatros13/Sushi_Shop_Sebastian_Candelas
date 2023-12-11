package com.example.practica03_sebastian;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finalizar esta actividad para evitar volver a ella presionando "atrás"
            }
        }, 2000); // Delay de 2 segundos
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        } else {
            // El usuario está autenticado, puedes realizar acciones adicionales aquí si es necesario
            final String currentUserID = firebaseAuth.getCurrentUser().getUid();
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    String userType = dataSnapshot.child("userType").getValue(String.class);

                    if (userType != null) {
                        if (userType.equals("user")) {
                            startActivity(new Intent(SplashActivity.this, MainUserActivity.class));
                            finish();
                        } else if (userType.equals("admin")) {
                            startActivity(new Intent(SplashActivity.this, MainUserActivity.class));
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressDialog.dismiss();
                    Toast.makeText(SplashActivity.this, "Failed checking user due to " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}