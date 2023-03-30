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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.sdp.mediato.data.Database;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.media.Collection;
import com.github.sdp.mediato.ui.viewmodel.ProfileViewModel;
import com.github.sdp.mediato.utility.PhotoPicker;
import com.github.sdp.mediato.utility.adapters.CollectionListAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A fragment that displays the user's profile information, including their profile picture,
 * username, and custom collections of their favorite media types. The profile picture can be edited
 * by the user.
 */
public class ProfileFragment extends Fragment {

  private ProfileViewModel viewModel;
  private PhotoPicker photoPicker;
  private Button editButton;
  private Button addCollectionButton;
  private TextView usernameView;
  private ImageView profileImage;
  private RecyclerView collectionListRecyclerView;
  private CollectionListAdapter collectionlistAdapter;

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
    collectionListRecyclerView = view.findViewById(R.id.collection_list_recycler_view);

    // Initialize components
    photoPicker = setupPhotoPicker();
    collectionlistAdapter = setupCollections(collectionListRecyclerView);
    setupAddCollectionsButton(addCollectionButton);

    // Observe the view model's live data to update UI components
    observeUsername();
    observeProfilePic();
    observeCollections(collectionlistAdapter);

    return view;
  }

  private CollectionListAdapter setupCollections(RecyclerView recyclerView) {
    // Check if a collection is already in the viewModel, if not create the default one
    List<Collection> collections = viewModel.getCollections();
    if (collections == null) {
      String defaultTitle = getResources().getString(R.string.recently_watched);
      Collection defaultCollection = new Collection(defaultTitle);
      collections = new ArrayList<>();
      collections.add(defaultCollection);
      Database.addCollection(USERNAME, defaultCollection);
      viewModel.setCollections(collections);
    }

    // Define what happens when the add button inside a collection is clicked
    OnAddMediaButtonClickListener onAddMediaButtonClickListener = (collection, review) -> {
      Database.addReviewToCollection(USERNAME, collection.getCollectionName(), review);
      Collection currentCollection = viewModel.getCollection(collection.getCollectionName());
      viewModel.addReviewToCollection(review, "sample collection");
    };

    // Create an adapter to display the list of collections in a RecycleView
    CollectionListAdapter collectionsAdapter = new CollectionListAdapter(getContext(), collections,
        onAddMediaButtonClickListener);
    recyclerView.setAdapter(collectionsAdapter);
    recyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    return collectionsAdapter;
  }

  private void setupAddCollectionsButton(Button addCollectionButton) {
    addCollectionButton.setOnClickListener(v -> showEnterCollectionNameDialog());
  }

  private void observeCollections(CollectionListAdapter collectionsAdapter) {
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

  private void showEnterCollectionNameDialog() {
    // Build the dialog box
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    LayoutInflater inflater = requireActivity().getLayoutInflater();
    View view = inflater.inflate(R.layout.dialog_add_collection, null);
    final EditText textInput = view.findViewById(R.id.collection_name_input);

    builder.setView(view);

    // Set the add button
    String addText = getResources().getString(R.string.add);
    builder.setPositiveButton(addText,
        (dialog, which) -> handleEnteredCollectionName(textInput));

    // Set the cancel button
    String cancelText = getResources().getString(R.string.cancel);
    builder.setNegativeButton(cancelText,
        (dialog, which) -> dialog.cancel());

    builder.show();
  }

  private void handleEnteredCollectionName(EditText textInput) {
    String collectionName = textInput.getText().toString();

    // Check if the entered name is empty and make a toast if yes
    if (collectionName.isEmpty()) {
      String toastEmptyName = getResources().getString(R.string.collection_empty_name);
      makeToast(toastEmptyName);
      return;
    }

    // Check if the entered name is the same as an already existing collection and make a toast if yes
    String toastDuplicateName = getResources().getString(R.string.collection_name_already_exists);
    if (!viewModel.addCollection(collectionName)) {
      makeToast(toastDuplicateName);
    }
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

  private void makeToast(String text) {
    Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
  }

  public interface OnAddMediaButtonClickListener {

    void onAddMediaButtonClick(Collection collection, Review review);
  }

}