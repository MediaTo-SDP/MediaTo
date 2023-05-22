package com.github.sdp.mediato.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.github.sdp.mediato.utility.FragmentSwitcher;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.cache.AppCache;
import com.github.sdp.mediato.databinding.ActivityMainBinding;
import com.github.sdp.mediato.ui.viewmodel.MyProfileViewModel;
import com.github.sdp.mediato.ui.viewmodel.ReadOnlyProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;

/**
 * The main activity of the app that displays a bottom navigation bar and manages the navigation
 * between the feeds, search, and profile fragments.
 */
public class MainActivity extends AppCompatActivity implements FragmentSwitcher {

  ActivityMainBinding binding;
  MyProfileFragment myProfileFragment;
  SearchFragment searchFragment;
  ExploreFragment exploreFragment;
  FeedFragment feedFragment;
  FeedFragment myReviewsFragment;
  private MyProfileViewModel myProfileViewModel;
  AppCache globalCache;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    globalCache = Room.databaseBuilder(getApplicationContext(), AppCache.class, "global-cache")
            .build();

    // Initialize the ViewModels
    myProfileViewModel = new ViewModelProvider(this).get(MyProfileViewModel.class);

    // Get the username set by the profile creation activity
    String username = getIntent().getStringExtra("username");

    myProfileViewModel.setUsername(username);

    // Choose the default fragment that opens on creation of the MainActivity
    setDefaultFragment(username);

    // Set the bottomNavigationView
    binding.bottomNavigationView.setBackground(null);
    binding.bottomNavigationView.setOnItemSelectedListener(
        item -> navigateFragments(item.getItemId()));

  }

  private boolean navigateFragments(int itemId) {
    // If/else statement is required instead if a switch case.
    // See: http://tools.android.com/tips/non-constant-fields
    if (itemId == R.id.feed) {
      replaceFragment(feedFragment);
    } else if (itemId == R.id.search) {
      replaceFragment(searchFragment);
    } else if (itemId == R.id.profile) {
      replaceFragment(myProfileFragment);
    } else if (itemId == R.id.explore) {
      replaceFragment(exploreFragment);
    } else if (itemId == R.id.my_reviews) {
      replaceFragment(myReviewsFragment);
    }
    return true;
  }

  private void replaceFragment(Fragment fragment) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.main_container, fragment);
    fragmentTransaction.addToBackStack(null);
    fragmentTransaction.commit();
  }

  /**
   * Right now this method just considers the case where the MainActivity gets started from the
   * profile creation. Later this can be changed to show for example the Home Screen if the user is
   * already logged in.
   */
  private void setDefaultFragment(String username) {
    myProfileFragment = new MyProfileFragment();
    searchFragment = new SearchFragment();
    exploreFragment = new ExploreFragment();
    feedFragment = new FeedFragment();
    myReviewsFragment = new FeedFragment();

    Bundle args = new Bundle();

    Bundle argsFeed = new Bundle();
    Bundle argsMyReviews = new Bundle();

    // Give the username as an argument to the profile page and switch to it
    args.putString("username", username);
    args.putString("general_search", "true");
    args.putString("collection", getString(R.string.default_collection));

    argsFeed.putString("username", username);
    argsMyReviews.putString("username", username);

    argsFeed.putSerializable("feedType", FeedFragment.FeedType.FEED);
    argsMyReviews.putSerializable("feedType", FeedFragment.FeedType.MY_REVIEWS);

    SearchFragment.OFFLINE_CACHE = globalCache;
    searchFragment.setArguments(args);
    myProfileFragment.setArguments(args);
    exploreFragment.setArguments(args);
    feedFragment.setArguments(argsFeed);
    myReviewsFragment.setArguments(argsMyReviews);

    // Mark the profile item in the bottom bar as selected
    binding.bottomNavigationView.setSelectedItemId(R.id.profile);

    replaceFragment(myProfileFragment);
  }

  public ReadOnlyProfileViewModel getMyProfileViewModel(){
    return myProfileViewModel;
  }

  public MyProfileFragment getMyProfileFragment() {
    return myProfileFragment;
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