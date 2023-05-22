package com.github.sdp.mediato.ui;

import static com.github.sdp.mediato.data.UserDatabase.followUser;
import static com.github.sdp.mediato.data.UserDatabase.unfollowUser;

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
import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.ui.viewmodel.FollowerViewModel;
import com.github.sdp.mediato.ui.viewmodel.FollowingViewModel;
import com.github.sdp.mediato.utility.adapters.UserAdapter;

import java.util.ArrayList;

public class MyFollowersFragment extends Fragment implements UserAdapter.OnUserInteractionListener {

  private static String USERNAME;
  private FollowerViewModel viewModel;
  private UserAdapter userAdapter;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    USERNAME = getArguments().getString("username");
    viewModel = new ViewModelProvider(this).get(FollowerViewModel.class);
    userAdapter = new UserAdapter(
            getActivity(),
            ((MainActivity)getActivity()).getMyProfileViewModel().getUser(),
            new ArrayList<>()
    );
    userAdapter.setOnUserInteractionListener(this);
    viewModel.setConnectedUsername(((MainActivity)getActivity()).getMyProfileViewModel().getUsername());
    viewModel.setWatchedUsername(USERNAME);
    viewModel.getUserListLiveData().observe(this, userAdapter::updateUserList);
    viewModel.getConnectedUserLiveData().observe(this, userAdapter::updateConnectedUser);

    UserDatabase.getUser(viewModel.getWatchedUsername()).thenAccept(user -> viewModel.loadFollowingFollower(user.getFollowers()));
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_my_followers, container, false);

    // Set the Search User RecyclerView with its adapter
    RecyclerView recyclerView = view.findViewById(R.id.myFollowers_recyclerView);
    recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    recyclerView.setAdapter(userAdapter);

    viewModel.reloadUser();

    return view;
  }

  @Override
  public void onFollowClick(User user) {
    followUser(viewModel.getConnectedUser().getUsername(), user.getUsername());
    viewModel.reloadUser();
  }

  @Override
  public void onUnfollowClick(User user) {
    unfollowUser(viewModel.getConnectedUser().getUsername(), user.getUsername());
    viewModel.reloadUser();
  }
}