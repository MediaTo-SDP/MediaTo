package com.github.sdp.mediato;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.sdp.mediato.databinding.ActivityMainBinding;
import com.github.sdp.mediato.location.LocationHelper;
import com.github.sdp.mediato.location.LocationService;
import com.github.sdp.mediato.ui.HomeFragment;
import com.github.sdp.mediato.ui.SearchFragment;
import com.google.android.gms.location.LocationServices;

import javax.annotation.Nonnull;

/**
 * The main activity of the app that displays a bottom navigation bar and manages the navigation
 * between the home, search, and profile fragments.
 */
public class MainActivity extends AppCompatActivity {

  ActivityMainBinding binding;
  ProfileFragment profileFragment;
  SearchFragment searchFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    // Choose the default fragment that opens on creation of the MainActivity
    setDefaultFragment();
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
      replaceFragment(searchFragment);
    } else if (itemId == R.id.profile) {
      replaceFragment(profileFragment);
    }

    return true;
  }

  private void replaceFragment(Fragment fragment) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.main_container, fragment);
    fragmentTransaction.commit();
  }

  /**
   * Right now this method just considers the case where the MainActivity gets started from the
   * profile creation. Later this can be changed to show for example the Home Screen if the user is
   * already logged in.
   */
  private void setDefaultFragment() {
    profileFragment = new ProfileFragment();
    searchFragment = new SearchFragment();

    // Get the username set by the profile creation activity
    String username = getIntent().getStringExtra("username");
    Bundle args = new Bundle();

    // Give the username as an argument to the profile page and switch to it
    args.putString("username", username);
    searchFragment.setArguments(args);
    profileFragment.setArguments(args);

    // Mark the profile item in the bottom bar as selected
    binding.bottomNavigationView.setSelectedItemId(R.id.profile);

    replaceFragment(profileFragment);
  }
}