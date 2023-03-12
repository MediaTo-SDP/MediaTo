package com.github.sdp.mediato;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.github.sdp.mediato.utility.PhotoPicker;

/**
 * A fragment that displays the user's profile information, including their profile picture,
 * username, and collections of their favorite media types. The profile picture and collections can be edited by the user.
 */
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

        // On click on the edit button, open a photo picker to choose the profile image
        photoPicker = new PhotoPicker(this, profileImage);
        edit_button.setOnClickListener(v ->
                photoPicker.getOnClickListener(requireActivity().getActivityResultRegistry()).onClick(v)
        );

        return view;
    }
}