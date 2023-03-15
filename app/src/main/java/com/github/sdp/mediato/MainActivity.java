package com.github.sdp.mediato;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.github.sdp.mediato.databinding.ActivityMainBinding;

/**
 * The main activity of the app that displays a bottom navigation bar and manages the navigation
 * between the home, search, and profile fragments.
 */
public class MainActivity extends AppCompatActivity {

  ActivityMainBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    // Choose the default fragment that opens on creation of the MainActivity
    replaceFragment(new HomeFragment());

    // Set the bottomNavigationView
    binding.bottomNavigationView.setBackground(null);
    binding.bottomNavigationView.setOnItemSelectedListener(
        item -> navigateFragments(item.getItemId()));

  }

  private boolean navigateFragments(int itemId) {
    // If/else statement is required instead if a switch case.
    // See: http://tools.android.com/tips/non-constant-fields
    if (itemId == R.id.home) {
      replaceFragment(new HomeFragment());
    } else if (itemId == R.id.search) {
      replaceFragment(new SearchFragment());
    } else if (itemId == R.id.profile) {
      ProfileFragment profileFragment = new ProfileFragment();

      // Get the username set by the profile creation activity
      String username = getIntent().getStringExtra("username");
      Bundle args = new Bundle();

      // Give the username as an argument to the profile page and switch to it
      args.putString("username", username);
      profileFragment.setArguments(args);
      replaceFragment(profileFragment);
    }

    return true;
  }

  private void replaceFragment(Fragment fragment) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.main_contrainer, fragment);
    fragmentTransaction.commit();
  }
}