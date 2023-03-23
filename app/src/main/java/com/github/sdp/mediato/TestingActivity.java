package com.github.sdp.mediato;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class TestingActivity extends AppCompatActivity {

/*  This activity can be used to launch fragments in for testing.
    The dependencies for using FragmentScenario are causing issues with some firebase dependencies.
    implementation 'androidx.test.espresso:espresso-contrib:3.5.1'
    debugImplementation 'androidx.fragment:fragment-testing:1.5.5'*/

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_testing);
  }
}