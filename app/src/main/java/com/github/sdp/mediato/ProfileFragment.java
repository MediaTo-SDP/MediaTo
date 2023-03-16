package com.github.sdp.mediato;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.github.sdp.mediato.data.Database;
import com.github.sdp.mediato.utility.PhotoPicker;
import java.util.concurrent.CompletableFuture;

/**
 * A fragment that displays the user's profile information, including their profile picture,
 * username, and collections of their favorite media types. The profile picture and collections can
 * be edited by the user.
 */
public class ProfileFragment extends Fragment {

  private PhotoPicker photoPicker;

  // this string is used to access the user's data in the database
  private String username = "Username";

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_profile, container, false);

    // Get all UI components
    Button edit_button = view.findViewById(R.id.edit_button);
    AppCompatImageButton add_movie_button = view.findViewById(R.id.add_movie_button);
    TextView username_view = view.findViewById(R.id.username_text);
    ImageView profileImage = view.findViewById(R.id.profile_image);

    setUsername(username_view);
    // This does not work yet
    //setProfileImage(profileImage);

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
        LinearLayout scrollView = view.findViewById(R.id.scroll_view_content);
        String itemText = getArguments().getString("data_key");
        addItemToScrollView(itemText, scrollView);
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

  private void setProfileImage(ImageView profileImage) {
    CompletableFuture<byte[]> imageFuture = Database.getProfilePic(username);

    imageFuture.thenAccept(imageBytes -> {
      // Create a Bitmap object from the byte array
      Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

      // Set the bitmap to the ImageView
      profileImage.setImageBitmap(bitmap);
    });
  }


  /**
   * For now this gets the username set by the profile creation page and forwarded by the
   * MainActivity, but this might be changed in the future when the user is already logged in.
   */
  private void setUsername(TextView username) {
    String arg = getArguments().getString("username");
    username.setText(arg);
  }

  public void addItemToScrollView(String itemText, LinearLayout scrollViewContent) {
    // Inflate the layout
    LayoutInflater inflater = LayoutInflater.from(getContext());
    FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.layout_movie_item,
        scrollViewContent, false);

    // Set the title
    TextView titleView = layout.findViewById(R.id.text_title);
    titleView.setText(itemText);

    // Add the layout to the scroll view
    scrollViewContent.addView(layout);
  }

}