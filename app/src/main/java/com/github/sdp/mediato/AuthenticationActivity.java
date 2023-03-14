package com.github.sdp.mediato;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.Objects;

/**
 * Authentication activity for login in via google one tap to firebase
 */
public class AuthenticationActivity extends AppCompatActivity {

    // Sign-in launcher callback for button, calls onSignInResult
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(), this::onSignInResult
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        // We assign a callback to Google sign in button
        findViewById(R.id.google_sign_in).setOnClickListener(view -> {
            FirebaseUser authUser = FirebaseAuth.getInstance().getCurrentUser();
            if (authUser == null) {
                // Initialize sign in intent
                Intent signInIntent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(
                                new AuthUI.IdpConfig.GoogleBuilder().build())) // only available login is Google
                        .build();
                // Start the intent
                signInLauncher.launch(signInIntent);
            } else {
                launchGreetingActivity(authUser.getDisplayName());
            }
        });

    }

    /**
     * Called when user is login in. If login is successful, the user is greeted.
     *
     * @param result: firebase authentication result
     */
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) throws IllegalArgumentException {

        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());

            // launch greeting activity with user's name
            launchGreetingActivity(user.getDisplayName());
        } else {
            // Sign in failed
            System.out.println("Unable to login: " + Objects.requireNonNull(result.getIdpResponse()).getError());
        }
    }

    /**
     * Launches the greeting activity with the username when signed in
     * @param : user's name
     */
    private void launchGreetingActivity(String userName) {
        Intent myIntent = new Intent(AuthenticationActivity.this, GreetingActivity.class);
        myIntent.putExtra("mainName", userName);
        AuthenticationActivity.this.startActivity(myIntent);
    }


}
