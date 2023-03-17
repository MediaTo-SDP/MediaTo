package com.github.sdp.mediato;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.github.sdp.mediato.data.Database;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
                launchPostActivity(authUser);
            }
        });

    }

    /**
     * Called when user is login in. If login is successful, the user is greeted.
     *
     * @param result: firebase authentication result
     */
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {

        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());

            // launch greeting activity with user's name
            launchPostActivity(user);
        }
    }

    /**
     * Launches the next activity with the user signed in (NewProfileActivity here)
     *
     * @param user: user's name
     */
    public void launchPostActivity(FirebaseUser user){
        Objects.requireNonNull(user);
        User databaseUser=null;
        Intent postIntent;

        Database.database.useEmulator("10.0.2.2", 9000);

        Database.getUserByEmail("ph.levieil123@gmail.com").thenAccept(u -> testmethod1(u)).exceptionally(e -> {
            testmethod2(user);
            return null;
        });
    }

    private void testmethod1(User databaseUser) {
        Intent postIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
        postIntent.putExtra("username", databaseUser.getUsername());
        AuthenticationActivity.this.startActivity(postIntent);

    }

    private void testmethod2(FirebaseUser user) {
        Intent  postIntent = new Intent(AuthenticationActivity.this, NewProfileActivity.class);
        postIntent.putExtra("uid", user.getUid());
        postIntent.putExtra("email", user.getEmail());
        AuthenticationActivity.this.startActivity(postIntent);

    }


}
