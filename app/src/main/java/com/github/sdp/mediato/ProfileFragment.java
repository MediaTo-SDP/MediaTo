package com.github.sdp.mediato;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.sdp.mediato.utility.PhotoPicker;

public class ProfileFragment extends Fragment {

  private ImageView profileImage;
  private PhotoPicker photoPicker;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_profile, container, false);

    final Button edit_button = view.findViewById(R.id.edit_button);

    profileImage = view.findViewById(R.id.profile_image);
    photoPicker = new PhotoPicker(this, profileImage);

    // Open a photo picker to choose the profile image
    edit_button.setOnClickListener(v -> {
      photoPicker.getOnClickListener(requireActivity().getActivityResultRegistry()).onClick(v);
    });

    return view;
  }
}