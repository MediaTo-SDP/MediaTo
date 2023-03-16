package com.github.sdp.mediato;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.javafaker.Faker;
import com.github.sdp.mediato.data.Database;
import com.github.sdp.mediato.formats.Dates;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.utility.PhotoPicker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;
import java.util.concurrent.CompletableFuture;


/**
 * create an instance of this fragment.
 */
public class CreateProfileFragment extends Fragment {

  private StorageTask<TaskSnapshot> uploadProfilePicTask;
  private ImageView profileImage;
  private PhotoPicker photoPicker;

  //@TODO add userId when linked with authentication
  private User.UserBuilder userBuilder = new User.UserBuilder("userGoogleAuthId");

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_create_profile, container, false);

    final TextInputLayout usernameTextInput = view.findViewById(R.id.username_text_input);
    final TextInputEditText usernameEditText = view.findViewById(R.id.username_edit_text);
    final MaterialButton createProfileButton = view.findViewById(R.id.create_profile_button);
    final FloatingActionButton profileImageButton = view.findViewById(
        R.id.profile_image_add_button);

    profileImage = view.findViewById(R.id.profile_image);
    photoPicker = new PhotoPicker(this, profileImage);

    // Open a photo picker to choose the profile image
    profileImageButton.setOnClickListener(v ->
        photoPicker.getOnClickListener(requireActivity().getActivityResultRegistry()).onClick(v)
    );

    // Generate a username
    usernameTextInput.setEndIconOnClickListener(
        generateUsername(usernameTextInput, usernameEditText));

    // Remove the error if the user fix its username
    usernameEditText.addTextChangedListener(
        new UsernameWatcher(usernameTextInput, usernameEditText));

    // Create the profile if valid otherwise error
    createProfileButton.setOnClickListener(tryCreateProfile(usernameTextInput, usernameEditText));

    // Inflate the layout for this fragment
    return view;
  }

  @NonNull
  private View.OnClickListener generateUsername(TextInputLayout usernameTextInput,
      TextInputEditText usernameEditText) {
    return v -> {
      usernameTextInput.setError(null);
      Faker faker = new Faker();
      String animal = faker.animal().name();
      String number = faker.number().digits(5);
      usernameEditText.setText(animal.concat(number));
    };
  }

  @NonNull
  private View.OnClickListener tryCreateProfile(TextInputLayout usernameTextInput,
      TextInputEditText usernameEditText) {
    return view -> {
      String errorMsg = getUsernameErrorMsg(usernameEditText.getText());
      usernameTextInput.setError(errorMsg);
      if (null == errorMsg) {
        //TODO Add navigation to Add favorite page
        //Creating new user and adding it to the database
        String username = usernameEditText.getText().toString();
        System.out.println("username no null " + username);
        userBuilder.setUsername(username);
        userBuilder.setRegisterDate(Dates.getToday());
        //@TODO get current user's email when linked with authentication activity
        userBuilder.setEmail("set email when linked");
        //@TODO by default the location is not set - to be changed when we implement the GPS feature
        userBuilder.setLocation(new Location());
        User user = userBuilder.build();
        Uri profilePicUri = photoPicker.getProfileImageUri();
        if (photoPicker.getProfileImageUri() == null) {
          System.out.println("no uri");
          CompletableFuture<String> userAddition = Database.addUser(user);
          userAddition.complete("User added");
        } else {
          System.out.println("valid uri");
          Database.addUser(user);
          uploadProfilePicTask = Database.setProfilePic(user.getUsername(), profilePicUri);
          try {
            Thread.sleep(500);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }

        // Switch to the main activity
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
      }
    };
  }

  private enum UsernameError {
    NULL,
    TOO_SHORT,
    ALREADY_TAKEN,
    GOOD
  }

  private String getUsernameErrorMsg(@Nullable Editable text) {
    switch (isUsernameValid(text)) {
      case NULL:
        return getString(R.string.mt_username_error_null);
      case TOO_SHORT:
        return getString(R.string.mt_username_error_too_short);
      case ALREADY_TAKEN:
        return getString(R.string.mt_username_error_already_taken);
      default:
        return null;
    }
  }

  private UsernameError isUsernameValid(@Nullable Editable text) {
    if (text == null) {
      return UsernameError.NULL;
    } else if (text.length() < getResources().getInteger(R.integer.mt_username_min_length)) {
      return UsernameError.TOO_SHORT;
    } else {
      return UsernameError.GOOD;
    }
  }

  private class UsernameWatcher implements TextWatcher {

    private TextInputLayout usernameTextInput;
    private TextInputEditText usernameEditText;

    UsernameWatcher(TextInputLayout usernameTextInput, TextInputEditText usernameEditText) {

      this.usernameTextInput = usernameTextInput;
      this.usernameEditText = usernameEditText;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      if (UsernameError.GOOD == CreateProfileFragment.this.isUsernameValid(
          usernameEditText.getText())) {
        usernameTextInput.setError(null);
      }
    }

    // Useless functions
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
  }
}