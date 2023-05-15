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
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.sdp.mediato.MainActivity;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.media.Collection;
import com.github.sdp.mediato.ui.viewmodel.MyProfileViewModel;
import com.github.sdp.mediato.utility.PhotoPicker;
import com.github.sdp.mediato.utility.adapters.CollectionListAdapter;
import com.google.gson.Gson;
import java.util.List;
import java.util.function.Consumer;

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
        viewModel = ((MainActivity)getActivity()).getMyProfileViewModel();
        USERNAME = viewModel.getUsername();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initializes the profile header, based on USERNAME
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // Get all UI components
        editButton = view.findViewById(R.id.edit_button);
        addCollectionButton = view.findViewById(R.id.add_collection_button);
        collectionListRecyclerView = view.findViewById(R.id.collection_list_recycler_view);

        // Initialize components
        photoPicker = setupPhotoPicker();
        setupAddCollectionsButton(addCollectionButton);

        // Set up collections
        fetchCollectionsFromDatabaseWithRetry(0).thenAccept(collections -> {
            collectionlistAdapter = setupCollections(collectionListRecyclerView, collections);
            // Observe the view model's live data to update UI components
            observeCollections(collectionlistAdapter);
            // Add a review if there is one
            addReview();
        });

        // Add on click listener to sign out button
        Button signOutButton = view.findViewById(R.id.signout_button);
        signOutButton.setOnClickListener(v -> ((MainActivity) getActivity()).signOutUser());

        return view;
    }

    @Override
    public CollectionListAdapter setupCollections(RecyclerView recyclerView,  List<Collection> collections) {
        viewModel.setCollections(collections);

        Consumer<Collection> onAddMediaButtonClickListener = (collection) ->
            openSearchAddingToCollection(collection);

        Consumer<Collection> onDeleteCollectionButtonClickListener = (collection) ->
            showDeleteCollectionDialog(collection.getCollectionName());

        // Create an adapter to display the list of collections in a RecycleView
        CollectionListAdapter collectionsAdapter = new CollectionListAdapter(getContext(), collections,
                onAddMediaButtonClickListener, onDeleteCollectionButtonClickListener);
        recyclerView.setAdapter(collectionsAdapter);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        return collectionsAdapter;
    }

    private void openSearchAddingToCollection(Collection collection){
        String collectionName = collection.getCollectionName();

        // Pass the name of the collection to add the review to to the search fragment and switch to it
        SearchFragment searchFragment = new SearchFragment();

        Bundle args = new Bundle();
        args.putString("collection", collectionName);
        args.putString("general_search", "false");
        searchFragment.setArguments(args);

        fragmentSwitcher.switchCurrentFragmentWithChildFragment(searchFragment);
    }

    private void addReview(){
        Bundle args = getArguments();
        String reviewSerialized = args.getString("review");
        String collectionName = args.getString("collection");
        Review review = new Gson().fromJson(reviewSerialized, Review.class);

        if (review != null && collectionName != null) {
            ((MyProfileViewModel)viewModel).addReviewToCollection(review, collectionName);
        }
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

    private void showDeleteCollectionDialog(String collectionName) {
        // Build the dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_remove_collection, null);

        // Set the question text
        TextView textView = view.findViewById(R.id.delete_collection_question);
        String question = getString(R.string.delete_collection, collectionName);
        textView.setText(question);

        builder.setView(view);

        // Set the yes button
        String yesText = getResources().getString(R.string.yes);
        builder.setPositiveButton(yesText,
            (dialog, which) -> ((MyProfileViewModel)viewModel).removeCollection(collectionName));

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
        try {
            ((MyProfileViewModel)viewModel).addCollection(collectionName);
        } catch (IllegalArgumentException exception){
            String toastDuplicateName = getResources().getString(R.string.collection_name_already_exists);
            makeToast(toastDuplicateName);
        }
    }

    private void makeToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
    }

}