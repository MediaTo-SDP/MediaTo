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
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    viewModel = ((MainActivity) getActivity()).getCurrentUserViewModel();
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
    MyProfileFragment.OnAddMediaButtonClickListener onAddMediaButtonClickListener = (collection, review) -> {
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

}