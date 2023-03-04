package com.github.sgueissa.bootcamp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GreetingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);

        Intent myIntent = getIntent();
        String mainName = myIntent.getStringExtra("mainName");

        TextView greetingMessage = findViewById(R.id.greetingMessage);
        greetingMessage.setText("Hello " + mainName + "!");
    }
}