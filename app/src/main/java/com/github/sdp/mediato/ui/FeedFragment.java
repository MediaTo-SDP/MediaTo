package com.github.sdp.mediato.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sdp.mediato.R;
import com.github.sdp.mediato.databinding.FragmentExploreBinding;
import com.github.sdp.mediato.databinding.FragmentFeedBinding;
import com.github.sdp.mediato.ui.viewmodel.FeedViewModel;
import com.github.sdp.mediato.utility.adapters.ReviewPostListAdapter;

public class FeedFragment extends Fragment {
    private String USERNAME;
    private FeedViewModel viewModel;
    private FragmentFeedBinding binding;
    private ReviewPostListAdapter adapter;
    private FeedType feedType;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        USERNAME = getArguments().getString("username");
        feedType = (FeedType) getArguments().getSerializable("feedType");

        binding.textFeed.setText(feedType.toString());

        viewModel = new ViewModelProvider(this).get(FeedViewModel.class);
        viewModel.setData(USERNAME, feedType);

        adapter = new ReviewPostListAdapter();
        adapter.setUsername(USERNAME);
        binding.feedPosts.setAdapter(adapter);
        adapter.setCallerFragment(viewModel, ReviewPostListAdapter.CallerFragment.FEED);

        binding.feedPosts.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.feedPosts.setHasFixedSize(false);
        viewModel.getPosts().observe(getViewLifecycleOwner(), adapter::submitList);
    }

    public enum FeedType {
        MY_REVIEWS, FEED;

        @Override
        public String toString() {
            switch (this) {
                case MY_REVIEWS:
                    return "My";
                case FEED:
                    return "Feed";
            }
            return "";
        }
    }

}
