package com.example.practica03_sebastian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Button btnRegis, btnLogin;
    private EditText emailEt, passwordEt;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private String email = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        btnRegis = (Button) findViewById(R.id.btnRegis);
        btnLogin = findViewById(R.id.btnLogin);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private void validateData() {
        email = emailEt.getText().toString().trim();
        password = passwordEt.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format...", Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Enter password...", Toast.LENGTH_SHORT).show();
        } else {
            loginUser();
        }
    }

    private void loginUser() {
        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Login failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUser() {
        progressDialog.setMessage("Checking User...");
        final String currentUserID = firebaseAuth.getCurrentUser().getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://sushi-292d6-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference userRef = firebaseDatabase.getReference("Users");

        userRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                String userType = dataSnapshot.child("userType").getValue(String.class);
                Toast.makeText(LoginActivity.this, String.valueOf(userRef.child(currentUserID)) , Toast.LENGTH_SHORT).show();
                if (userType != null) {
                    if (userType.equals("user")) {
                        startActivity(new Intent(LoginActivity.this, MainUserActivity.class));
                        finish();
                    } else if (userType.equals("admin")) {
                        startActivity(new Intent(LoginActivity.this, MainAdminActivity.class));
                        finish();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "Este usuario no existe" , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Failed checking user due to " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void register (View view){
        Intent reg = new Intent(this, RegisterActivity.class);
        startActivity(reg);
    }
}