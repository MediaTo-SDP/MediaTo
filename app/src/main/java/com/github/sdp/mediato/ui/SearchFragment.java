package com.github.sdp.mediato.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import com.github.sdp.mediato.R;
import java.util.ArrayList;

public class SearchFragment extends Fragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_search, container, false);

    // Get a reference to the ListView
    ListView listView = view.findViewById(R.id.searchactivity_listview_searchresults);

// Initialize the adapter and set it as the adapter for the ListView
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
        android.R.layout.simple_list_item_1, new ArrayList<String>());
    listView.setAdapter(adapter);

// Add an item to the adapter
    adapter.add("New Item");

// Notify the adapter that the data has changed
    adapter.notifyDataSetChanged();

    return view;

  }

}