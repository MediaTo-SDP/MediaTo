package com.github.sdp.mediato;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.github.sdp.mediato.utility.PhotoPicker;

/**
 * A fragment that displays the user's profile information, including their profile picture,
 * username, and collections of their favorite media types. The profile picture and collections can
 * be edited by the user.
 */
public class ProfileFragment extends Fragment {

  private ImageView profileImage;
  private PhotoPicker photoPicker;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_profile, container, false);

    // Get all user interface components
    Button edit_button = view.findViewById(R.id.edit_button);
    AppCompatImageButton add_movie_button = view.findViewById(R.id.add_movie_button);
    TextView username_tv = view.findViewById(R.id.username_text);
    profileImage = view.findViewById(R.id.profile_image);

    // Get the username set by the profile creation page and forwarded by the MainActivity
    String username = getArguments().getString("username");
    username_tv.setText(username);

    // On click on the edit button, open a photo picker to choose the profile image
    photoPicker = new PhotoPicker(this, profileImage);
    edit_button.setOnClickListener(v ->
        photoPicker.getOnClickListener(requireActivity().getActivityResultRegistry()).onClick(v)
    );

    // On click on the add movie button, open a search window
    add_movie_button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        replaceFragment(new SearchFragment());
      }
    });

    return view;
  }

  private void replaceFragment(Fragment fragment) {
    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.main_container, fragment);
    fragmentTransaction.commit();
  }
}