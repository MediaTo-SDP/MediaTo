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
import com.github.sdp.mediato.cache.AppCache;
import com.github.sdp.mediato.databinding.FragmentHomeBinding;
import com.github.sdp.mediato.ui.viewmodel.HomeViewModel;
import com.github.sdp.mediato.utility.adapters.MediaListAdapter;

/**
 * The View class of the Home fragment
 */
public class HomeFragment extends Fragment {
  private static final String GLOBAL_CACHE_KEY = "GlobalCache";
  private HomeViewModel viewModel;
  private FragmentHomeBinding binding;
  private MediaListAdapter adapter;
  AppCache globalCache;

  public HomeFragment(AppCache globalCache) {
    super();
    Bundle bundle = new Bundle();
    bundle.putSerializable(GLOBAL_CACHE_KEY, globalCache);
    this.setArguments(bundle);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater  inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
    globalCache = (AppCache) getArguments().get(GLOBAL_CACHE_KEY);

    // Inflate the layout for this fragment
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    viewModel.setGlobalCache(globalCache.mediaDao());
    viewModel.wipeOldData();
    adapter = new MediaListAdapter();
    binding.trendingItems.setAdapter(adapter);


    binding.trendingItems.setLayoutManager(new GridLayoutManager(getContext(), 3));
    binding.trendingItems.setHasFixedSize(true);
    viewModel.getMedias().observe(getViewLifecycleOwner(), adapter::submitList);
    binding.booksTrending.setOnClickListener(v -> viewModel.getBooks());
    binding.movieTrending.setOnClickListener(v -> viewModel.getMovies());
    binding.movieTrending.performClick();
  }
}