package com.github.sdp.mediato.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sdp.mediato.R;
import com.github.sdp.mediato.databinding.FragmentExploreBinding;
import com.github.sdp.mediato.model.post.ReviewPost;
import com.github.sdp.mediato.ui.viewmodel.ExploreViewModel;
import com.github.sdp.mediato.utility.adapters.ReviewPostListAdapter;

import java.util.List;

public class ExploreFragment extends Fragment {
    private ExploreViewModel viewModel;
    private FragmentExploreBinding binding;
    private ReviewPostListAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("Inflating fragment explore");
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_explore, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        System.out.println("Entering on view created");
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ExploreViewModel.class);
        viewModel.setUsername(getArguments().getString("username"));
        adapter = new ReviewPostListAdapter();
        binding.explorePosts.setAdapter(adapter);

        binding.explorePosts.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.explorePosts.setHasFixedSize(false);
        viewModel.getPosts().observe(getViewLifecycleOwner(), adapter::submitList);
    }
}
