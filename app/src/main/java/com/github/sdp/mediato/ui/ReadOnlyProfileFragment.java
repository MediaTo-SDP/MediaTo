package com.github.sdp.mediato.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.model.media.Collection;
import com.github.sdp.mediato.utility.adapters.CollectionListAdapter;
import java.util.List;

/**
 * A fragment to display another user's profile. It extends the basic profile fragment to also include:
 * - Non-editable view of collections
 */
public class ReadOnlyProfileFragment extends BaseProfileFragment {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    viewModel = ((MainActivity)getActivity()).getReadOnlyProfileViewModel();
    USERNAME = viewModel.getUsername();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    // Initializes the profile header, based on USERNAME
    View view = super.onCreateView(inflater, container, savedInstanceState);

    Button signOutButton = view.findViewById(R.id.signout_button);
    signOutButton.setVisibility(View.GONE);

    // Get all UI components
    collectionListRecyclerView = view.findViewById(R.id.collection_list_recycler_view);

    // Initialize components
    // Set up collections
    fetchCollectionsFromDatabaseWithRetry(0).thenAccept(collections -> {
      collectionlistAdapter = setupCollections(collectionListRecyclerView, collections);
      // Observe the view model's live data to update UI components
      observeCollections(collectionlistAdapter);
    });

    return view;
  }

  @Override
  public CollectionListAdapter setupCollections(RecyclerView recyclerView, List<Collection> collections) {
    viewModel.setCollections(collections);

    // Create an adapter to display the list of collections in a RecycleView
    // (with no AddMediaButtonClickListener, so that the add media button is not displayed)
    CollectionListAdapter collectionsAdapter = new CollectionListAdapter(getContext(), collections,
        null, null);
    recyclerView.setAdapter(collectionsAdapter);
    recyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    return collectionsAdapter;
  }


}