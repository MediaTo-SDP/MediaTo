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
import android.widget.ImageButton;
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
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.media.Collection;
import com.github.sdp.mediato.model.media.CollectionType;
import com.github.sdp.mediato.ui.viewmodel.ProfileViewModel;
import com.github.sdp.mediato.utility.PhotoPicker;
import com.github.sdp.mediato.utility.SampleReviews;
import com.github.sdp.mediato.utility.adapters.CollectionAdapter;
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
  private ImageButton addMediaButton;
  private TextView usernameView;
  private ImageView profileImage;
  private RecyclerView collectionRecyclerView;
  private CollectionAdapter collectionAdapter;


  // Used as a key to access the database
  public static String USERNAME;

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
    addMediaButton = view.findViewById(R.id.add_media_button);
    usernameView = view.findViewById(R.id.username_text);
    profileImage = view.findViewById(R.id.profile_image);
    collectionRecyclerView = view.findViewById(R.id.collectionRecyclerView);

    // Initialize components
    photoPicker = setupPhotoPicker();
    collectionAdapter = setupCollection(collectionRecyclerView);
    setupAddButton(addMediaButton);

    // Observe the view model's live data to update UI components
    observeUsername();
    observeProfilePic();
    observeCollection(collectionAdapter);

    return view;
  }

  private CollectionAdapter setupCollection(RecyclerView recyclerView) {
    // Check if a collection is already in the viewModel, if not create one
    Collection collection = viewModel.getCollection();
    if (collection == null) {
      collection = new Collection("Some Title");
      Database.addCollection(USERNAME, collection);
      viewModel.setCollection(collection);
    }

    // Create an adapter to display the collection in a RecycleView
    CollectionAdapter collectionAdapter = new CollectionAdapter(getContext(), collection);
    recyclerView.setAdapter(collectionAdapter);
    recyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    return collectionAdapter;
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

  private void setupAddButton(ImageButton addMediaButton) {
    //TODO connect this to the SearchFragment
    SampleReviews s = new SampleReviews();
    addMediaButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        /*replaceFragment(new SearchFragment());*/
        Review review = s.getMovieReview();
        Database.addReviewToCollection(USERNAME, viewModel.getCollection().getCollectionName(), review);
        viewModel.addReviewToCollection(review);
      }
    });

  }

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

  private void observeCollection(CollectionAdapter collectionAdapter) {
    viewModel.getCollectionLiveData().observe(getViewLifecycleOwner(), new Observer<Collection>() {
      @Override
      public void onChanged(Collection collection) {
        collectionAdapter.notifyDataSetChanged();
      }
    });
  }

  private void replaceFragment(Fragment fragment) {
    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.main_container, fragment);
    fragmentTransaction.commit();
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