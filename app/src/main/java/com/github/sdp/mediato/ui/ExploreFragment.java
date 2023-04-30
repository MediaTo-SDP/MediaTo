package com.github.sdp.mediato.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.github.sdp.mediato.location.LocationHelper;
import com.github.sdp.mediato.model.post.ReviewPost;
import com.github.sdp.mediato.ui.viewmodel.ExploreViewModel;
import com.github.sdp.mediato.ui.viewmodel.MyFollowingViewModel;
import com.github.sdp.mediato.utility.adapters.ReviewPostListAdapter;

import java.util.List;

/**
 * Fragment for the explore page where the user can see review posts
 * from the nearby users they don't follow yet
 * @TODO add a follow button or link the profile of the users
 */
public class ExploreFragment extends Fragment {
    private String USERNAME;
    private ExploreViewModel viewModel;
    private FragmentExploreBinding binding;
    private ReviewPostListAdapter adapter;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_explore, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Location", "Entering Home Fragment");
        LocationHelper.startTrackingLocation(getContext(), getActivity(), requestPermissionLauncher);

        USERNAME = getArguments().getString("username");

        viewModel = new ViewModelProvider(this).get(ExploreViewModel.class);
        viewModel.setUsername(USERNAME);

        adapter = new ReviewPostListAdapter();
        binding.explorePosts.setAdapter(adapter);

        binding.explorePosts.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.explorePosts.setHasFixedSize(false);
        viewModel.getPosts().observe(getViewLifecycleOwner(), adapter::submitList);
    }
}
