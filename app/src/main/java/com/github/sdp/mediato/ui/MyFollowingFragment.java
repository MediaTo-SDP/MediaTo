package com.github.sdp.mediato.ui;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.sdp.mediato.R;
import com.github.sdp.mediato.ui.viewmodel.MyFollowingViewModel;

public class MyFollowingFragment extends Fragment {

    private MyFollowingViewModel mViewModel;

    public static MyFollowingFragment newInstance() {
        return new MyFollowingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_following, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MyFollowingViewModel.class);
        // TODO: Use the ViewModel
    }

}