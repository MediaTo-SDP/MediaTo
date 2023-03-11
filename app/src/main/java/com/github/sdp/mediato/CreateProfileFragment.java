package com.github.sdp.mediato;

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
import com.github.sdp.mediato.utility.PhotoPicker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * create an instance of this fragment.
 */
public class CreateProfileFragment extends Fragment {

    private ImageView profileImage;
    private PhotoPicker photoPicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_profile, container, false);

        final TextInputLayout usernameTextInput = view.findViewById(R.id.username_text_input);
        final TextInputEditText usernameEditText = view.findViewById(R.id.username_edit_text);
        final MaterialButton createProfileButton = view.findViewById(R.id.create_profile_button);
        final FloatingActionButton profileImageButton = view.findViewById(R.id.profile_image_add_button);

        profileImage = view.findViewById(R.id.profile_image);
        photoPicker = new PhotoPicker(this, profileImage);

        // Open a photo picker to choose the profile image
        profileImageButton.setOnClickListener(v -> {
            photoPicker.getOnClickListener(requireActivity().getActivityResultRegistry()).onClick(v);
        });

        // Generate a username
        usernameTextInput.setEndIconOnClickListener(generateUsername(usernameTextInput, usernameEditText));

        // Remove the error if the user fix its username
        usernameEditText.addTextChangedListener(new UsernameWatcher(usernameTextInput, usernameEditText));

        // Create the profile if valid otherwise error
        createProfileButton.setOnClickListener(tryCreateProfile(usernameTextInput, usernameEditText));

        // Inflate the layout for this fragment
        return view;
    }

    @NonNull
    private View.OnClickListener generateUsername(TextInputLayout usernameTextInput, TextInputEditText usernameEditText) {
        return v -> {
            usernameTextInput.setError(null);
            Faker faker = new Faker();
            String animal = faker.animal().name();
            String number = faker.number().digits(5);
            usernameEditText.setText(animal.concat(number));
        };
    }

    @NonNull
    private View.OnClickListener tryCreateProfile(TextInputLayout usernameTextInput, TextInputEditText usernameEditText) {
        return view -> {
            String errorMsg = getUsernameErrorMsg(usernameEditText.getText());
            usernameTextInput.setError(errorMsg);

            if (null == errorMsg) {
                //TODO Add navigation to Add favorite page
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

    private class UsernameWatcher implements TextWatcher{

        private TextInputLayout usernameTextInput;
        private TextInputEditText usernameEditText;

        UsernameWatcher(TextInputLayout usernameTextInput, TextInputEditText usernameEditText) {

            this.usernameTextInput = usernameTextInput;
            this.usernameEditText = usernameEditText;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (UsernameError.GOOD == CreateProfileFragment.this.isUsernameValid(usernameEditText.getText())) {
                usernameTextInput.setError(null);
            }
        }

        // Useless functions
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {}
    }
}