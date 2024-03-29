package com.github.sdp.mediato.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.javafaker.Faker;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.data.UserDatabase;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;


/**
 * create an instance of this fragment.
 */
public class CreateProfileFragment extends Fragment {

  private StorageTask<TaskSnapshot> uploadProfilePicTask;
  private ImageView profileImage;
  private PhotoPicker photoPicker;
  private final User.UserBuilder userBuilder;

  public CreateProfileFragment(String uid, String email) {
    userBuilder = new User.UserBuilder(uid).setEmail(email);
  }

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

    PhotoPicker.OnImagePickedListener listener = new PhotoPicker.OnImagePickedListener() {
      @Override
      public void onImagePicked(Uri imageUri) {
      }
    };

    photoPicker = new PhotoPicker(this, profileImage, listener);

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
        String username = Objects.requireNonNull(usernameEditText.getText()).toString();
        createUser(username);
      }
    };
  }

  private void createUser(String username){
    // Create a new user and add it to the database
    userBuilder.setUsername(username);
    userBuilder.setRegisterDate(Dates.getToday());
    userBuilder.setLocation(new Location());
    User user = userBuilder.build();
    UserDatabase.addUser(user);

    // Set profile picture
    Uri profilePicUri = photoPicker.getProfileImageUri();
    if (photoPicker.getProfileImageUri() != null) {
      uploadProfilePicTask = UserDatabase.setProfilePic(user.getUsername(), profilePicUri).addOnCompleteListener(v ->  {
        switchToMainActivity(username);
        makeToast(getString(R.string.profile_creation_success));
      });

    } else {
      addDefaultProfilePic(username);
    }
  }

  /**
   * Adds the default profile image to the user
   * @param username: the user's username
   */
  private void addDefaultProfilePic(String username) {
    // we need to create a new thread to download the default profile pic, otherwise it will make the app lag
    new Thread(() -> {
      try {
        // the url of the default profile picture
        URL url = new URL(getString(R.string.default_profile_pic));

        // connect via html to download
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(input);

        // store the file as a temporary image in the cache
        File cacheDir = requireContext().getCacheDir();
        File imageFile = File.createTempFile("profile_pic", ".jpg", cacheDir);
        FileOutputStream outputStream = new FileOutputStream(imageFile);

        // get the bitmap out of it, and convert to uri
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.close();
        Uri uri = Uri.fromFile(imageFile);

        // we can now store it in the database, and then switch to main activity
        UserDatabase.setProfilePic(username, uri).addOnCompleteListener(v -> {
          switchToMainActivity(username);
          makeToast(getString(R.string.profile_creation_success));
        });;
      } catch (IOException e) {
        throw new RuntimeException("Failed to download and save the default profile pic", e);
      }
    }).start();
  }

  private void switchToMainActivity(String username) {
    Intent intent = new Intent(getActivity(), MainActivity.class);
    intent.putExtra("username", username);
    startActivity(intent);
  }

  private void makeToast(String text) {
    Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
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

    private final TextInputLayout usernameTextInput;
    private final TextInputEditText usernameEditText;

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