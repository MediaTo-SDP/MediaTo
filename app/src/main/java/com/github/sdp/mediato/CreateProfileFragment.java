package com.github.sdp.mediato;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.javafaker.Faker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * create an instance of this fragment.
 */
public class CreateProfileFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_profile, container, false);

        final TextInputLayout usernameTextInput = view.findViewById(R.id.username_text_input);
        final TextInputEditText usernameEditText = view.findViewById(R.id.username_edit_text);
        MaterialButton createButton = view.findViewById(R.id.create_profile_button);

        usernameTextInput.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameTextInput.setError(null);
                Faker faker = new Faker();
                String username = faker.animal().name();
                String number = faker.number().digits(5);
                usernameEditText.setText(username + number);
            }
        });

        //Set an error if the password is less than 8 characters.
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int error = 0;
                String errorMsg;
                if (0 != (error = isPasswordValid(usernameEditText.getText()))) {
                    errorMsg = (error == 1) ? getString(R.string.mt_username_error_1)
                            : (error == 2) ? getString(R.string.mt_username_error_2)
                            : getString(R.string.mt_username_error_3);

                    usernameTextInput.setError(errorMsg);
                } else {
                    usernameTextInput.setError(null);
                    //((NavigationHost) getActivity()).navigateTo(new ProductGridFragment(), false);
                }
            }
        });

        usernameEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (0 == isPasswordValid(usernameEditText.getText())) {
                    usernameTextInput.setError(null);
                }
                return false;
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    // "isPasswordValid" from "Navigate to the next Fragment" section method goes here
    private int isPasswordValid(@Nullable Editable text) {
        if (text == null) {
            return 1;
        } else if (text.length() < 4) {
            return 2;
        } else if (text.length() > 20) {
            return 3;
        } else {
            return 0;
        }

    }
}