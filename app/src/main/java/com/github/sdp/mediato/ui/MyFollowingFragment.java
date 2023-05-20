package com.github.sdp.mediato.ui;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.sdp.mediato.R;
import com.github.sdp.mediato.utility.adapters.UserAdapter;
import com.github.sdp.mediato.ui.viewmodel.MyFollowingViewModel;

public class MyFollowingFragment extends Fragment {
    private static String USERNAME;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        USERNAME = getArguments().getString("username");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_following, container, false);

        // Create and init the Search User ViewModel
        MyFollowingViewModel myFollowingViewModel = new ViewModelProvider(getActivity()).get(MyFollowingViewModel.class);
        myFollowingViewModel.setUserName(USERNAME);
        myFollowingViewModel.setMainActivity((MainActivity) getActivity());

        // Set the Search User RecyclerView with its adapter
        RecyclerView recyclerView = view.findViewById(R.id.myFollowing_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(new UserAdapter(myFollowingViewModel));

        return view;
    }

}