package com.github.sgueissa.bootcamp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText mainName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button mainGoButton = findViewById(R.id.mainGoButton);
        mainName = findViewById(R.id.mainName);

        mainGoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(MainActivity.this, AuthenticationActivity.class);
                        myIntent.putExtra("mainName", mainName.getText().toString());
                        MainActivity.this.startActivity(myIntent);
                    }
                }
        );
    }
}