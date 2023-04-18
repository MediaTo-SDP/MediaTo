package com.github.sdp.mediato;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Collections;
import java.util.Objects;

/**
 * Authentication activity for login in via google one tap to firebase
 */
public class AuthenticationActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    // Sign-in launcher callback for button, calls onSignInResult
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(), this::onSignInResult
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("mySharedPreferences", MODE_PRIVATE);

        // Check if there is a saved authentication token
        String authToken = sharedPreferences.getString("authToken", "");

        if (!authToken.isEmpty()) {
            // Use the saved authentication token to authenticate the user automatically
            AuthCredential credential = GoogleAuthProvider.getCredential(authToken, null);
            FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser user = authResult.getUser();
                        launchPostActivity(user);
                    })
                    .addOnFailureListener(e -> {
                        // If authentication fails, remove the saved authentication token
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("authToken");
                        editor.apply();
                        throw new RuntimeException(e);
                    });
        } else {

            // We assign a callback to Google sign in button
            findViewById(R.id.google_sign_in).setOnClickListener(view -> {
                FirebaseUser authUser = FirebaseAuth.getInstance().getCurrentUser();
                /*TODO if (authUser == null) {
                    // Initialize sign in intent
                    Intent signInIntent = AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Collections.singletonList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build())) // only available login is Google
                            .build();
                    // Start the intent
                    signInLauncher.launch(signInIntent);
                } else {
                    launchPostActivity(authUser);
                }*/
                Intent signInIntent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(
                                new AuthUI.IdpConfig.GoogleBuilder().build())) // only available login is Google
                        .build();
                // Start the intent
                signInLauncher.launch(signInIntent);
            });
        }

    }


    /**
     * Called when user is login in. If login is successful, the user is greeted.
     *
     * @param result: firebase authentication result
     */
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                currentUser.getIdToken(false)
                        .addOnSuccessListener(result1 -> {
                            String authToken = result1.getToken();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("authToken", authToken);
                            editor.apply(); // Save the authentication token in SharedPreferences
                            launchPostActivity(currentUser);
                        })
                        .addOnFailureListener(e -> {
                            // Handle error getting ID token
                        });
            }
        }
    }



    /**
     * Launches the next activity with the user signed in, either the main if user exists in database,
     * or the profile creation one otherwise
     *
     * @param user: user's name
     */
    public void launchPostActivity(FirebaseUser user) {
        Objects.requireNonNull(user);
        UserDatabase.getUserByEmail(user.getEmail()).thenAccept(this::launchMainActivity)
                .exceptionally(e -> {
                    launchProfileCreationActivity(user);
                    return null;
                });
    }

    /**
     * Launches the main activity when the user already has a profile
     *
     * @param databaseUser: the user's profile from the database
     */
    private void launchMainActivity(User databaseUser) {
        Intent postIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
        postIntent.putExtra("username", databaseUser.getUsername());
        AuthenticationActivity.this.startActivity(postIntent);

    }

    /**
     * Launches the profile creation activity when the user doesn't have one
     *
     * @param user: the authenticated user
     */
    private void launchProfileCreationActivity(FirebaseUser user) {
        Intent postIntent = new Intent(AuthenticationActivity.this, NewProfileActivity.class);
        postIntent.putExtra("uid", user.getUid());
        postIntent.putExtra("email", user.getEmail());
        AuthenticationActivity.this.startActivity(postIntent);

    }


}
