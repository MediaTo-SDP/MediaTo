package com.github.sdp.mediato.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.sdp.mediato.R;

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