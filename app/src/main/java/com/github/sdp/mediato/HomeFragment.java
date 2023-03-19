package com.github.sdp.mediato;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.sdp.mediato.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
  private HomeViewModel viewModel;
  private FragmentHomeBinding binding;


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
    // Inflate the layout for this fragment
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    final MediaRecyclerViewAdapter  adapter = new MediaRecyclerViewAdapter();
    viewModel.getMovies().observe(getViewLifecycleOwner(), adapter::submitList);
    binding.trendingItems.setLayoutManager(new GridLayoutManager(getContext(), 4));
    binding.trendingItems.setHasFixedSize(true);
    binding.trendingItems.setAdapter(adapter);
  }
}