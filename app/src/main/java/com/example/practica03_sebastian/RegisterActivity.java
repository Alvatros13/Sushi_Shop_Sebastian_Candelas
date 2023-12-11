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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button btnRegister;
    private EditText nameEt, emailEt, passwordEt, confirmPasswordEt;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersRef;
    private ProgressDialog progressDialog;

    private String name = "";
    private String email = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://sushi-292d6-default-rtdb.europe-west1.firebasedatabase.app/");
        usersRef = firebaseDatabase.getReference("Users");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        btnRegister = findViewById(R.id.btnRegister);
        nameEt = findViewById(R.id.nameEt);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        confirmPasswordEt = findViewById(R.id.confirmPasswordEt);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private void validateData() {
        name = nameEt.getText().toString().trim();
        email = emailEt.getText().toString().trim();
        password = passwordEt.getText().toString().trim();
        String cPassword = confirmPasswordEt.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Enter your name...", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid Email Pattern...", Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Enter password...", Toast.LENGTH_SHORT).show();
        } else if (cPassword.isEmpty()) {
            Toast.makeText(this, "Confirm password...", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(cPassword)) {
            Toast.makeText(this, "Password doesn´t match...", Toast.LENGTH_SHORT).show();
        } else {
            createUserAccount();
        }
    }

    private void createUserAccount() {
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        updateUserInfo();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Failed creating account due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserInfo() {
        progressDialog.setMessage("Saving user info...");
        long timestamp = System.currentTimeMillis();
        String uid = firebaseAuth.getUid();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("email", email);
        hashMap.put("name", name);
        hashMap.put("profileImage", "");
        hashMap.put("userType", "user");
        hashMap.put("timestamp", timestamp);
        usersRef.child(uid).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Account created...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainUserActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Failed saving user info due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}