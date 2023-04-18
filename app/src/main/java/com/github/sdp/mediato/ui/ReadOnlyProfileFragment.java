package com.github.sdp.mediato.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.sdp.mediato.MainActivity;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.data.CollectionsDatabase;
import com.github.sdp.mediato.model.media.Collection;
import com.github.sdp.mediato.utility.adapters.CollectionListAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment to display another user's profile. It extends the basic profile fragment to also include:
 * - Non-editable view of collections
 * - TODO: follow buttom
 */
public class ReadOnlyProfileFragment extends BaseProfileFragment {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // TODO: get the username that was clicked on
    USERNAME = getArguments().getString("username");
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    viewModel = ((MainActivity) getActivity()).getOtherUsersViewModel();
    // This should already set the profile pic and username, based on USERNAME
    View view = super.onCreateView(inflater, container, savedInstanceState);

    // Get all UI components
    collectionListRecyclerView = view.findViewById(R.id.collection_list_recycler_view);

    // Initialize components
    collectionlistAdapter = setupCollections(collectionListRecyclerView);

    // Observe the view model's live data to update UI components
    observeCollections(collectionlistAdapter);

    return view;
  }

  @Override
  public CollectionListAdapter setupCollections(RecyclerView recyclerView) {
    // TODO: get the collections from the user that was clicked on in the search
    List<Collection> collections = new ArrayList<>();
    viewModel.setCollections(collections);

    // Create an adapter to display the list of collections in a RecycleView
    // (with no AddMediaButtonClickListener, so that the add media button is not displayed)
    CollectionListAdapter collectionsAdapter = new CollectionListAdapter(getContext(), collections,
        null);
    recyclerView.setAdapter(collectionsAdapter);
    recyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    return collectionsAdapter;
  }
}