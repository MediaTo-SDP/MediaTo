package com.github.sdp.mediato;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

/**
 * Authentication activity for login in via google one tap to firebase
 */
public class AuthenticationActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    // Sign-in launcher callback for button, calls onSignInResult
    private final ActivityResultLauncher<Intent> googleSignInActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::onSignInResult);
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("mySharedPreferences", MODE_PRIVATE);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Request the ID token
                .requestServerAuthCode(getString(R.string.default_web_client_id)) // Request the server auth code
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check if there is a saved authentication token
        String idToken = sharedPreferences.getString("google_id_token", "");
        String accessToken = sharedPreferences.getString("google_access_token", "");

        if (!idToken.isEmpty() && !accessToken.isEmpty()) {
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, accessToken);
            FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser user = authResult.getUser();
                        launchPostActivity(user);
                    })
                    .addOnFailureListener(e -> {
                        // If authentication fails, remove the saved authentication credential
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("credential");
                        editor.apply();
                    });
        } else {

            // We assign a callback to Google sign in button
            findViewById(R.id.google_sign_in).setOnClickListener(view -> {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                googleSignInActivityResultLauncher.launch(signInIntent);
            });
        }
    }

    /**
     * Called when user is login in. If login is successful, the user is greeted.
     *
     * @param result: Google sign-in activity result
     */
    private void onSignInResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Handle Google Sign-In error
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(authResult -> {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        String idToken = account.getIdToken();
                        String accessToken = account.getServerAuthCode();
                        // Save the Google ID token and access token to SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("google_id_token", idToken);
                        editor.putString("google_access_token", accessToken);
                        editor.apply();
                        launchPostActivity(currentUser);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error getting ID token
                });
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
