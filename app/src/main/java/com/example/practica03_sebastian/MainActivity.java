package com.example.practica03_sebastian;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button loginBtn, noLogBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        noLogBtn = (Button) findViewById(R.id.noLogBtn);
    }

    public void login (View view){
        Intent log = new Intent(this, LoginActivity.class);
        startActivity(log);
    }

    public void skipLogin (View view){
        Intent skipLog = new Intent(this, MainUserActivity.class);
        startActivity(skipLog);
    }
}