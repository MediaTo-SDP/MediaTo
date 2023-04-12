package com.github.sdp.mediato.ui;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.github.sdp.mediato.R;
import com.github.sdp.mediato.databinding.FragmentHomeBinding;
import com.github.sdp.mediato.location.LocationHelper;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.ui.viewmodel.HomeViewModel;
import com.github.sdp.mediato.utility.adapters.MediaListAdapter;

import javax.annotation.Nonnull;

/**
 * The View class of the Home fragment
 */
public class HomeFragment extends Fragment {
  private HomeViewModel viewModel;
  private FragmentHomeBinding binding;
  private MediaListAdapter adapter;

  // Register the permissions callback, which handles the user's response to the
// system permissions dialog. Save the return value, an instance of
// ActivityResultLauncher, as an instance variable.
  private ActivityResultLauncher<String> requestPermissionLauncher =
          registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
              LocationHelper.startLocationService(getActivity());
            } else {
              System.out.println("LOCATION PERMISSIONS NOT GRANTED");
            }
          });


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
    Log.d("Location", "Entering Home Fragment");
    LocationHelper.startTrackingLocation(getContext(), getActivity(), requestPermissionLauncher);
    viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    adapter = new MediaListAdapter();
    binding.trendingItems.setAdapter(adapter);
    binding.trendingItems.setLayoutManager(new GridLayoutManager(getContext(), 3));
    binding.trendingItems.setHasFixedSize(true);
    viewModel.getMovies().observe(getViewLifecycleOwner(), adapter::submitList);
  }

}