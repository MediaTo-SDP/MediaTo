package com.github.sdp.mediato;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.sdp.mediato.data.Database;
import com.github.sdp.mediato.model.media.Collection;
import com.github.sdp.mediato.ui.viewmodel.ProfileViewModel;
import com.github.sdp.mediato.utility.PhotoPicker;
import com.github.sdp.mediato.utility.SampleReviews;
import com.github.sdp.mediato.utility.adapters.CollectionsAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A fragment that displays the user's profile information, including their profile picture,
 * username, and collections of their favorite media types. The profile picture and collections can
 * be edited by the user.
 */
public class ProfileFragment extends Fragment {

  private ProfileViewModel viewModel;
  private PhotoPicker photoPicker;
  private Button editButton;
  private Button addCollectionButton;
  private TextView usernameView;
  private ImageView profileImage;
  private RecyclerView collectionsRecyclerView;

  private CollectionsAdapter collectionsAdapter;


  // Used as a key to access the database
  private static String USERNAME;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // The username must be stored locally because it is used as a key to access the DB
    // For now it is passed as an argument from the profile creation.
    USERNAME = getArguments().getString("username");
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_profile, container, false);
    viewModel = new ViewModelProvider(getActivity()).get(ProfileViewModel.class);
    viewModel.setUsername(USERNAME);

    // This function is a temporary fix. The delay of uploading the profile picture should be
    // handled before we create the profile fragment (like with a loading screen). Ideally we
    // could set viewModel.setProfilePic(bitmap) here
    downloadProfilePicWithRetry(USERNAME);

    // Get all UI components
    editButton = view.findViewById(R.id.edit_button);
    addCollectionButton = view.findViewById(R.id.add_collection_button);
    usernameView = view.findViewById(R.id.username_text);
    profileImage = view.findViewById(R.id.profile_image);
    collectionsRecyclerView = view.findViewById(R.id.collection_list_recycler_view);

    // Initialize components
    photoPicker = setupPhotoPicker();
    collectionsAdapter = setupCollections(collectionsRecyclerView);
    setupAddCollectionsButton(addCollectionButton);

    // Observe the view model's live data to update UI components
    observeUsername();
    observeProfilePic();
    observeCollections(collectionsAdapter);

    return view;
  }

  private CollectionsAdapter setupCollections(RecyclerView recyclerView) {
    // Check if a collection is already in the viewModel, if not create one
    List<Collection> collections = viewModel.getCollections();
    if (collections == null) {
      Collection collection = new Collection("Recently watched");
      collections = new ArrayList<>();
      collections.add(collection);
      Database.addCollection(USERNAME, collection);
      viewModel.setCollections(collections);
    }

    // Create an adapter to display the collections in a RecycleView
    CollectionsAdapter collectionsAdapter = new CollectionsAdapter(getContext(), collections);
    recyclerView.setAdapter(collectionsAdapter);
    recyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    return collectionsAdapter;
  }

  private void setupAddCollectionsButton(Button addCollectionButton) {
    //TODO connect this to the SearchFragment
    SampleReviews s = new SampleReviews();
    addCollectionButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // random string to test for now
        String randomString = java.util.UUID.randomUUID().toString().substring(0, 6);
        viewModel.addCollection(randomString);
      }
    });
  }

  private void observeCollections(CollectionsAdapter collectionsAdapter) {
    viewModel.getCollectionsLiveData()
        .observe(getViewLifecycleOwner(), new Observer<List<Collection>>() {
          @Override
          public void onChanged(List<Collection> collections) {
            collectionsAdapter.notifyDataSetChanged();
          }
        });
  }

   /* private void setupAddButton(ImageButton addMediaButton) {
    //TODO connect this to the SearchFragment
    SampleReviews s = new SampleReviews();
    addMediaButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        *//*replaceFragment(new SearchFragment());*//*
        Review review = s.getMovieReview();
        Database.addReviewToCollection(USERNAME, viewModel.getCollection().getCollectionName(), review);
        viewModel.addReviewToCollection(review);
      }
    });

  }*/

  private void observeUsername() {
    viewModel.getUsernameLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String username) {
        usernameView.setText(username);
      }
    });
  }

  private void observeProfilePic() {
    viewModel.getProfilePicLiveData().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
      @Override
      public void onChanged(Bitmap bitmap) {
        profileImage.setImageBitmap(bitmap);

      }
    });
  }

  private void replaceFragment(Fragment fragment) {
    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.main_container, fragment);
    fragmentTransaction.commit();
  }

  private PhotoPicker setupPhotoPicker() {
    PhotoPicker photoPicker = new PhotoPicker(this, profileImage);

    // On click on the edit button, open a photo picker to choose the profile image
    editButton.setOnClickListener(v -> {
          photoPicker.getOnClickListener(requireActivity().getActivityResultRegistry()).onClick(v);
          //TODO This does not work (does not update the viewModel), change the PhotoPicker to return a Bitmap instead
          Drawable drawable = profileImage.getDrawable();
          BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
          Bitmap bitmap = bitmapDrawable.getBitmap();
          viewModel.setProfilePic(bitmap);
        }

    );
    return photoPicker;
  }


  // TODO: Should be improved so it does not need to use the hardcoded retry
  private void downloadProfilePicWithRetry(String username) {

    CompletableFuture<byte[]> imageFuture = Database.getProfilePic(username);

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

}