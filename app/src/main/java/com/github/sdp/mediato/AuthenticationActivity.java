package com.github.sdp.mediato;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
        // Check if there is a saved authentication token and username
        String idToken = sharedPreferences.getString("google_id_token", "");
        String accessToken = sharedPreferences.getString("google_access_token", "");
        String username = sharedPreferences.getString("username", "");

        if (!idToken.isEmpty() && !accessToken.isEmpty()) {
            if (isNetworkAvailable(this)) {
                // Authenticate user with Firebase
                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, accessToken);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnSuccessListener(authResult -> {
                            FirebaseUser user = authResult.getUser();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            UserDatabase.getUserByEmail(user.getEmail())
                                    .thenAccept(u -> {
                                        editor.putString("username", u.getUsername());
                                        editor.apply();
                                    });

                            launchPostActivity(user);
                        })
                        .addOnFailureListener(e -> {
                            // If authentication fails, remove the saved authentication credential
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove("credential");
                            editor.apply();
                            setUpSignInButton();
                        });
            } else if (!username.isEmpty()) {
                // Authenticate user using stored username
                launchMainActivity(username);
            } else {
                // No saved authentication token or username, show sign-in button
                setUpSignInButton();
            }
        } else {
            // No saved authentication token, show sign-in button
            setUpSignInButton();
        }

    }

    private void setUpSignInButton() {
        // We assign a callback to Google sign in button
        findViewById(R.id.google_sign_in).setOnClickListener(view -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInActivityResultLauncher.launch(signInIntent);
        });
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
                throw new RuntimeException(e); // Handle Google Sign-In error
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
                        UserDatabase.getUserByEmail(currentUser.getEmail())
                                .thenAccept(user -> {
                                    editor.putString("username", user.getUsername());
                                    editor.apply();
                                });

                        editor.apply();
                        launchPostActivity(currentUser);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error getting ID token
                });
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Launches the next activity with the user signed in, either the main if user exists in database,
     * or the profile creation one otherwise
     *
     * @param user: user's name
     */
    public void launchPostActivity(FirebaseUser user) {
        Objects.requireNonNull(user);
        UserDatabase.getUserByEmail(user.getEmail()).thenAccept(u -> launchMainActivity(u.getUsername()))
                .exceptionally(e -> {
                    launchProfileCreationActivity(user);
                    return null;
                });
    }

    /**
     * Launches the main activity when the user already has a profile
     *
     * @param username: the user's name from the database
     */
    private void launchMainActivity(String username) {
        Intent postIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
        postIntent.putExtra("username", username);
        AuthenticationActivity.this.startActivity(postIntent);
    }

    /**s
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
