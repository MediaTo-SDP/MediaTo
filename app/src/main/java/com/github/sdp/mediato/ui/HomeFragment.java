package com.github.sdp.mediato.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.github.sdp.mediato.R;
import com.github.sdp.mediato.databinding.FragmentHomeBinding;
import com.github.sdp.mediato.ui.viewmodel.HomeViewModel;
import com.github.sdp.mediato.utility.adapters.MediaListAdapter;

/**
 * The View class of the Home fragment
 */
public class HomeFragment extends Fragment {
  private HomeViewModel viewModel;
  private FragmentHomeBinding binding;
  private MediaListAdapter adapter;


  @Override
  public View onCreateView(@NonNull LayoutInflater  inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
    // Inflate the layout for this fragment
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    viewModel.wipeOldData();
    adapter = new MediaListAdapter(getActivity(), null);
    binding.trendingItems.setAdapter(adapter);


    binding.trendingItems.setLayoutManager(new GridLayoutManager(getContext(), 3));
    binding.trendingItems.setHasFixedSize(true);
    viewModel.getMedias().observe(getViewLifecycleOwner(), adapter::submitList);
    binding.booksTrending.setOnClickListener(v -> viewModel.getBooks());
    binding.movieTrending.setOnClickListener(v -> viewModel.getMovies());
    binding.movieTrending.performClick();
  }
}