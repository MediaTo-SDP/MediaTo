package com.github.sdp.mediato;

import static com.github.sdp.mediato.data.UserDatabase.updateLocation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.lifecycle.ViewModelProvider;

import com.github.sdp.mediato.databinding.ActivityMainBinding;
import com.github.sdp.mediato.ui.HomeFragment;
import com.github.sdp.mediato.ui.MyProfileFragment;
import com.github.sdp.mediato.ui.SearchFragment;
import com.github.sdp.mediato.ui.viewmodel.ProfileViewModel;

import java.util.List;

/**
 * The main activity of the app that displays a bottom navigation bar and manages the navigation
 * between the home, search, and profile fragments.
 */
public class MainActivity extends AppCompatActivity implements LocationListener{

  ActivityMainBinding binding;
  MyProfileFragment myProfileFragment;
  SearchFragment searchFragment;

  private ProfileViewModel currentUserViewModel;
  private ProfileViewModel otherUsersViewModel;

  private LocationManager locationManager;
  private Boolean locationEnabled;
  private String username;

  private final int LOCATION_REQUEST = 0;
  private final int UPDATE_LOCATION_EACH = 900000; // 900 000ms for 15min
  private final int UPDATE_LOCATION_ON_POSITION_CHANGE_OF = 1; // 1M

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    // Choose the default fragment that opens on creation of the MainActivity
    setDefaultFragment();

    // Initialize the ViewModels
    currentUserViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    otherUsersViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

    // Set the bottomNavigationView
    binding.bottomNavigationView.setBackground(null);
    binding.bottomNavigationView.setOnItemSelectedListener(
            item -> navigateFragments(item.getItemId()));

    // after login register the position of the user
    locationEnabled = true;
    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

      // request permission to access the location
      Toast.makeText(MainActivity.this, "gps location not allowed", Toast.LENGTH_SHORT).show();
      this.locationEnabled = false;
      ActivityCompat.requestPermissions(MainActivity.this, new String[]{
              android.Manifest.permission.ACCESS_COARSE_LOCATION,
              android.Manifest.permission.ACCESS_FINE_LOCATION
      }, LOCATION_REQUEST);
    }

    if(locationEnabled){
      this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_LOCATION_EACH, UPDATE_LOCATION_ON_POSITION_CHANGE_OF,MainActivity.this);
    }
  }

  @Override
  public void onLocationChanged(@NonNull Location location) {
    Toast.makeText(getApplicationContext(), "new location : " + location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();
    updateLocation(username, location.getLatitude(), location.getLongitude());
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if(requestCode == LOCATION_REQUEST){
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // Showing the toast message
        Toast.makeText(MainActivity.this, "location permission granted, gps location service running in the background", Toast.LENGTH_SHORT).show();
        this.locationEnabled = true;
      }else {
        Toast.makeText(MainActivity.this, "location permission denied", Toast.LENGTH_SHORT).show();
        this.locationEnabled = false;
      }
    }
  }

  private boolean navigateFragments(int itemId) {
    // If/else statement is required instead if a switch case.
    // See: http://tools.android.com/tips/non-constant-fields
    if (itemId == R.id.home) {
      replaceFragment(new HomeFragment());
    } else if (itemId == R.id.search) {
      replaceFragment(searchFragment);
    } else if (itemId == R.id.profile) {
      replaceFragment(myProfileFragment);
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
    myProfileFragment = new MyProfileFragment();
    searchFragment = new SearchFragment();

    // Get the username set by the profile creation activity
    username = getIntent().getStringExtra("username");
    Bundle args = new Bundle();

    // Give the username as an argument to the profile page and switch to it
    args.putString("username", username);
    searchFragment.setArguments(args);
    myProfileFragment.setArguments(args);

    // Mark the profile item in the bottom bar as selected
    binding.bottomNavigationView.setSelectedItemId(R.id.profile);

    replaceFragment(myProfileFragment);
  }

  public ProfileViewModel getCurrentUserViewModel(){
    return currentUserViewModel;
  }

  public ProfileViewModel getOtherUsersViewModel(){
    return otherUsersViewModel;
  }

  public String getCurrentUserUsername(){
    return username;
  }

  public Boolean isLocationEnabled(){
    return locationEnabled;
  }
}