package com.github.sdp.mediato;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


/**
 * This activity hosts the CreateProfileFragment. After the profile creation, it is destroyed and
 * switched to the MainActivity.
 */
public class NewProfileActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_profile);
    if (savedInstanceState == null) {
      getSupportFragmentManager()
          .beginTransaction()
          .add(R.id.new_profile_container, new CreateProfileFragment())
          .commit();
    }
  }
}