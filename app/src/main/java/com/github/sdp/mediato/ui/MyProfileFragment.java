package com.github.sdp.mediato.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.sdp.mediato.MainActivity;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.data.CollectionsDatabase;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.media.Collection;
import com.github.sdp.mediato.utility.PhotoPicker;
import com.github.sdp.mediato.utility.adapters.CollectionListAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment to display the current user's profile. It extends the basic profile fragment to also include:
 * - Button to edit the profile picture
 * - Button to add new collections
 * - Editable view of collections
 */
public class MyProfileFragment extends BaseProfileFragment {

  private PhotoPicker photoPicker;
  private ImageButton editButton;
  private Button addCollectionButton;

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
    viewModel = ((MainActivity) getActivity()).getCurrentUserViewModel();

    // Initializes the profile header, based on USERNAME
    View view = super.onCreateView(inflater, container, savedInstanceState);

    // Get all UI components
    editButton = view.findViewById(R.id.edit_button);
    addCollectionButton = view.findViewById(R.id.add_collection_button);
    collectionListRecyclerView = view.findViewById(R.id.collection_list_recycler_view);

    // Initialize components
    photoPicker = setupPhotoPicker();
    collectionlistAdapter = setupCollections(collectionListRecyclerView);
    setupAddCollectionsButton(addCollectionButton);

    // Observe the view model's live data to update UI components
    observeCollections(collectionlistAdapter);

    return view;
  }

  @Override
  public CollectionListAdapter setupCollections(RecyclerView recyclerView) {
    // Check if a collection is already in the viewModel, if not create the default one
    List<Collection> collections = viewModel.getCollections();
    if (collections == null) {
      String defaultTitle = getResources().getString(R.string.recently_watched);
      Collection defaultCollection = new Collection(defaultTitle);
      collections = new ArrayList<>();
      collections.add(defaultCollection);
      CollectionsDatabase.addCollection(USERNAME, defaultCollection);
      viewModel.setCollections(collections);
    }

    // Define what happens when the add button inside a collection is clicked
    OnAddMediaButtonClickListener onAddMediaButtonClickListener = (collection, review) -> {
      CollectionsDatabase.addReviewToCollection(USERNAME, collection.getCollectionName(), review);
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
    addCollectionButton.setVisibility(View.VISIBLE);
    addCollectionButton.setOnClickListener(v -> showEnterCollectionNameDialog());
  }

  private PhotoPicker setupPhotoPicker() {
    PhotoPicker photoPicker = new PhotoPicker(this, profileImage);
    editButton.setVisibility(View.VISIBLE);

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

  private void makeToast(String text) {
    Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
  }

  public interface OnAddMediaButtonClickListener {

    void onAddMediaButtonClick(Collection collection, Review review);
  }
}