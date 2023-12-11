package com.example.practica03_sebastian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class SushiAddActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference sushiRef;
    private FirebaseStorage storage;
    private StorageReference imageRef;
    private FirebaseAuth firebaseAuth;

    //-----------------------------------------
    private ImageView imageViewSelected;
    private ImageButton imageButton;
    private Button btnSelectImage, btnAdd;
    private EditText nameEt, priceEt;
    private Uri uriImagenSeleccionada;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sushi_add);

        firebaseDatabase = FirebaseDatabase.getInstance("https://sushi-292d6-default-rtdb.europe-west1.firebasedatabase.app/");
        sushiRef = firebaseDatabase.getReference("Sushi");
        storage = FirebaseStorage.getInstance("gs://sushi-292d6.appspot.com");
        imageRef = storage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        imageViewSelected = findViewById(R.id.imageViewSelected);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnAdd = findViewById(R.id.btnAdd);
        nameEt = findViewById(R.id.nameEt);
        priceEt = findViewById(R.id.priceEt);
        imageButton = findViewById(R.id.imageButton);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateData()){
                    String name = nameEt.getText().toString().trim();
                    String price = priceEt.getText().toString().trim();

                    if (uriImagenSeleccionada != null && !name.isEmpty() && !price.isEmpty()) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("name", name);
                        hashMap.put("price", price);

                        subirImagenAFirebaseStorage(uriImagenSeleccionada);

                        sushiRef.push().setValue(hashMap);

                        Toast.makeText(SushiAddActivity.this, "Plate added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SushiAddActivity.this, "Please fill all", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    openGallery();
                } else {
                    requestPermission();
                }
            }
        });
    }

    public void back(){
        startActivity(new Intent(SushiAddActivity.this, MainAdminActivity.class));
    }

    private boolean validateData(){
        String name = nameEt.getText().toString().trim();
        String price = priceEt.getText().toString().trim();
        if (name.isEmpty()){
            Toast.makeText(this, "Enter a plate name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (price.isEmpty()) {
            Toast.makeText(this, "Set a price for the plate", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }
    private void subirImagenAFirebaseStorage(Uri uriImagen) {
        Drawable drawable = imageViewSelected.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        String name = nameEt.getText().toString().trim();

        imageRef = storage.getReference(name);
        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SushiAddActivity.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImagenSeleccionada = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriImagenSeleccionada);
                imageViewSelected.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}