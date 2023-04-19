package com.github.sdp.mediato.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.ui.viewmodel.MyFollowersViewModel;
import com.github.sdp.mediato.utility.adapters.UserAdapter;

public class MyFollowersFragment extends Fragment {

  private static String USERNAME;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    USERNAME = getArguments().getString("username");
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_my_followers, container, false);

    // Create and init the Search User ViewModel
    MyFollowersViewModel myFollowersViewModel = new ViewModelProvider(this).get(
        MyFollowersViewModel.class);
    myFollowersViewModel.setUserName(USERNAME);

    // Set the Search User RecyclerView with its adapter
    RecyclerView recyclerView = view.findViewById(R.id.myFollowers_recyclerView);
    recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    recyclerView.setAdapter(new UserAdapter(myFollowersViewModel));

    return view;
  }

}