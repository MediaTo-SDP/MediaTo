package com.github.sdp.mediato.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.data.Database;
import com.github.sdp.mediato.model.media.Collection;
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
  private TextView usernameView;

  private ImageView profileImage;
  private RecyclerView collectionRecyclerView;
  private static String USERNAME;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

    // The username must be stored locally because it is used as a key to access the DB
    // For now it is passed as an argument from the profile creation.
    USERNAME = getArguments().getString("username");
    viewModel.setUsername(USERNAME);

    // This function is a temporary fix. The delay of uploading the profile picture should be
    // handled before we create the profile fragment (like with a loading screen). Ideally we
    // could set viewModel.setProfilePic(bitmap) here
    downloadProfilePicWithRetry();
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_profile, container, false);

    // Get all UI components
    editButton = view.findViewById(R.id.edit_button);
    usernameView = view.findViewById(R.id.username_text);
    profileImage = view.findViewById(R.id.profile_image);
    collectionRecyclerView = view.findViewById(R.id.collectionRecyclerView);
    
    // On click on the edit button, open a photo picker to choose the profile image
    photoPicker = new PhotoPicker(this, profileImage);
    editButton.setOnClickListener(v ->
        photoPicker.getOnClickListener(requireActivity().getActivityResultRegistry()).onClick(v)
    );

    Collection sampleCollection = new Collection("Recently watched");
    CollectionAdapter collectionAdapter = setupCollection(collectionRecyclerView,
        sampleCollection);

    ImageButton add_movie_button = view.findViewById(R.id.add_button);
    add_movie_button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        /*replaceFragment(new SearchFragment());*/
        SampleReviews s = new SampleReviews();
        sampleCollection.addReview(s.getMovieReview());
        collectionAdapter.notifyDataSetChanged();
      }
    });

    // Observe the view model's live data to update UI components
    observeUsername();
    observeProfilePic();
    observeCollection(collectionAdapter);

    return view;
  }

  private CollectionAdapter setupCollection(RecyclerView recyclerView, Collection collection) {
    viewModel.setCollection(collection);
    CollectionAdapter collectionAdapter = new CollectionAdapter(getContext(), collection);
    recyclerView.setAdapter(collectionAdapter);
    recyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    return collectionAdapter;
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


  private CompletableFuture<Bitmap> getProfilePicFromDB(String username) {
    CompletableFuture<byte[]> imageFuture = Database.getProfilePic(username);

    return imageFuture.thenApply(imageBytes -> {
      Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
      return bitmap;
    });
  }

  private void downloadProfilePicWithRetry() {
    CompletableFuture<Bitmap> future = getProfilePicFromDB(USERNAME);

    future.thenAccept(bitmap -> {
      viewModel.setProfilePic(bitmap);
    }).exceptionally(throwable -> {
      // Could not download image, try again in 1 second
      Handler handler = new Handler();
      handler.postDelayed(() -> {
        downloadProfilePicWithRetry();
      }, 1000);

      return null;
    });
  }


}