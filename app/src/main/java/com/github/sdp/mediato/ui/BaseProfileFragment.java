package com.github.sdp.mediato.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.ui.viewmodel.ProfileViewModel;
import com.github.sdp.mediato.utility.adapters.CollectionListAdapter;
import java.util.concurrent.CompletableFuture;

/**
 * A fragment that displays the header of a user's profile:
 * - username
 * - profile picture
 * - follower and following buttons
 * It also displays a user's collections, the setup of which needs to be defined in the subclass.
 *
 */
public abstract class BaseProfileFragment extends Fragment {

  protected ProfileViewModel viewModel;
  protected Button followingButton;
  protected Button followersButton;
  protected TextView usernameView;
  protected ImageView profileImage;
  protected CollectionListAdapter collectionlistAdapter;
  protected RecyclerView collectionListRecyclerView;

  // Used as a key to access the database
  protected static String USERNAME;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_profile, container, false);
    viewModel.setUsername(USERNAME);

    // This function is a temporary fix. The delay of uploading the profile picture should be
    // handled before we create the profile fragment (like with a loading screen). Ideally we
    // could set viewModel.setProfilePic(bitmap) here
    downloadProfilePicWithRetry(USERNAME);

    // Get all UI components
    followingButton = view.findViewById(R.id.profile_following_button);
    followersButton = view.findViewById(R.id.profile_followers_button);
    usernameView = view.findViewById(R.id.username_text);
    profileImage = view.findViewById(R.id.profile_image);

    // Initialize components
    setupFollowingButton(followingButton);
    setupFollowersButton(followersButton);

    // Observe the view model's live data to update UI components
    observeUsername();
    observeProfilePic();

    observeFollowingAndFollowersCount();
    updateFollowingAndFollowersCount();

    return view;
  }

  abstract CollectionListAdapter setupCollections(RecyclerView recyclerView);

  public void observeCollections(CollectionListAdapter collectionsAdapter) {
    viewModel.getCollectionsLiveData()
        .observe(getViewLifecycleOwner(), collections -> collectionsAdapter.notifyDataSetChanged());
  }

  private void observeUsername() {
    viewModel.getUsernameLiveData().observe(getViewLifecycleOwner(),
        username -> usernameView.setText(username));
  }

  private void observeProfilePic() {
    viewModel.getProfilePicLiveData().observe(getViewLifecycleOwner(),
        bitmap -> profileImage.setImageBitmap(bitmap));
  }

  private void observeFollowingAndFollowersCount() {
    viewModel.getFollowersLiveData().observe(getViewLifecycleOwner(), followersCount -> {
      followersButton.setText(followersCount + " " + getResources().getString(R.string.followers));
    });
    viewModel.getFollowingLiveData().observe(getViewLifecycleOwner(), followingCount -> {
      followingButton.setText(followingCount + " " + getResources().getString(R.string.following));
    });
  }

  private void setupFollowingButton(Button followingButton) {
    followingButton.setOnClickListener(v -> switchFragment(new MyFollowingFragment()));
  }
  
  private void setupFollowersButton(Button followersButton) {
    followersButton.setOnClickListener(v -> switchFragment(new MyFollowersFragment()));
  }

  private void updateFollowingAndFollowersCount() {
    UserDatabase.getUser(USERNAME).thenAccept(user -> {
      viewModel.setFollowing(user.getFollowingCount());
      viewModel.setFollowers(user.getFollowersCount());
    });
  }

  // TODO: Should be improved so it does not need to use the hardcoded retry
  private void downloadProfilePicWithRetry(String username) {

    CompletableFuture<byte[]> imageFuture = UserDatabase.getProfilePic(username);

    // It would probably be better to do this directly in the database class
    // (getProfilePic would return CompletableFuture<Bitmap>)
    CompletableFuture<Bitmap> future = imageFuture.thenApply(imageBytes -> {
      Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
      return bitmap;
    });

    future.thenAccept(bitmap -> {
      // Try to set the profile pic
      viewModel.setProfilePic(bitmap);
    }).exceptionally(throwable -> {
      // Could not download image, try again in 1 second
      Handler handler = new Handler();
      handler.postDelayed(() -> {
        downloadProfilePicWithRetry(username);
      }, 1000);

      return null;
    });
  }

  private void switchFragment(Fragment fragment) {
    Bundle args = new Bundle();
    args.putString("username", USERNAME);
    fragment.setArguments(args);

    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.main_container, fragment);
    fragmentTransaction.addToBackStack(null);
    fragmentTransaction.commit();
  }

}