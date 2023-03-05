package com.github.sdp.mediato;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import com.github.sdp.mediato.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

  ActivityMainBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    replaceFragment(new HomeFragment());
    binding.bottomNavigationView.setBackground(null);

    binding.bottomNavigationView.setOnItemSelectedListener(item -> {

      int itemId = item.getItemId();
      // If/else statement is required instead if a switch case. See: http://tools.android.com/tips/non-constant-fields
      if (itemId == R.id.home) {
        replaceFragment(new HomeFragment());
      } else if (itemId == R.id.search) {
        replaceFragment(new SearchFragment());
      } else if (itemId == R.id.profile) {
        replaceFragment(new ProfileFragment());
      }

      return true;

    });

  }

  private void replaceFragment(Fragment fragment) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.frame_layout, fragment);
    fragmentTransaction.commit();
  }
}