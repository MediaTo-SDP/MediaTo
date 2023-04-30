package com.github.sdp.mediato;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.lifecycle.ViewModelProvider;
import com.github.sdp.mediato.databinding.ActivityMainBinding;
import com.github.sdp.mediato.ui.ExploreFragment;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.ui.HomeFragment;
import com.github.sdp.mediato.ui.MyProfileFragment;
import com.github.sdp.mediato.ui.SearchFragment;
import com.github.sdp.mediato.ui.viewmodel.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

/**
 * The main activity of the app that displays a bottom navigation bar and manages the navigation
 * between the home, search, and profile fragments.
 */
public class MainActivity extends AppCompatActivity implements FragmentSwitcher {

  ActivityMainBinding binding;
  MyProfileFragment myProfileFragment;
  SearchFragment searchFragment;
  ExploreFragment exploreFragment;

  private ProfileViewModel currentUserViewModel;
  private ProfileViewModel otherUsersViewModel;

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
    } else if (itemId == R.id.explore) {
      replaceFragment(exploreFragment);
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
    exploreFragment = new ExploreFragment();

    // Get the username set by the profile creation activity
    String username = getIntent().getStringExtra("username");
    Review review = new Gson().fromJson(
            getIntent().getStringExtra("review"), Review.class);
    Bundle args = new Bundle();

    // Give the username as an argument to the profile page and switch to it
    args.putString("username", username);
    if (review != null) {
      args.putSerializable("review", review);
    }
    searchFragment.setArguments(args);
    myProfileFragment.setArguments(args);
    exploreFragment.setArguments(args);

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

  @Override
  public void switchCurrentFragmentWithChildFragment(Fragment childFragment) {
    replaceFragment(childFragment);
  }

  /**
   * Signs out the user and launches the authentication page
   */
  public void signOutUser() {
    FirebaseAuth.getInstance().signOut(); // sign out user and go back to auth page

    SharedPreferences sharedPreferences =
            getSharedPreferences(getString(R.string.login_shared_preferences), MODE_PRIVATE);

    SharedPreferences.Editor editor = sharedPreferences.edit();

    editor.remove(getString(R.string.google_id_token_key));
    editor.remove(getString(R.string.google_access_token_key));
    editor.remove(getString(R.string.username_key));

    editor.apply();

    Intent intent = new Intent(this, AuthenticationActivity.class);
    startActivity(intent);
  }
}