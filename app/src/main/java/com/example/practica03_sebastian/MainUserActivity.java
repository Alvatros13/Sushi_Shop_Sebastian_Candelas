package com.example.practica03_sebastian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MainUserActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TextView txtMail;
    private ImageButton logoutBtn;
    private ArrayList<SushiItem> sushiItems;
    private ArrayList<byte[]> imgList;
    private SushiAdapter sushiAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        firebaseAuth = FirebaseAuth.getInstance();
        txtMail = findViewById(R.id.txtMail);
        logoutBtn = findViewById(R.id.logoutBtn);
        checkUser();
        loadSushi();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });
    }

    private void loadSushi() {
        sushiItems = new ArrayList<>();
        imgList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance("https://sushi-292d6-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Sushi");
        ref.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot){
                sushiItems.clear();

                for (DataSnapshot ds : snapshot.getChildren()){
                    SushiItem si = ds.getValue(SushiItem.class);
                    sushiItems.add(si);

                    StorageReference imgRef = FirebaseStorage.getInstance("gs://sushi-292d6.appspot.com").getReference(si.getName());
                    final long ONE_MEGABYTE =  10024 * 1024;
                    imgRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            imgList.add(bytes);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                    sushiAdapter = new SushiAdapter(sushiItems, imgList, MainUserActivity.this);
                    RecyclerView recyclerView = findViewById(R.id.recyclerviewCards);
                    recyclerView.setAdapter(sushiAdapter);

                }
            }
            @Override
            public void onCancelled(@NotNull DatabaseError error){

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