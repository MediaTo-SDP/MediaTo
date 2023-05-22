package com.github.sdp.mediato.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.github.sdp.mediato.R;
import com.github.sdp.mediato.data.UserDatabase;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
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
    private SignInButton signInButton;
    private TextView signInText;

    private String idToken, accessToken, username;

    /**
     * Checks if there is a network connection available.
     *
     * @param context: the context of the calling activity
     * @return true if there is an active network connection, false otherwise
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        signInButton = findViewById(R.id.google_sign_in);
        signInText = findViewById(R.id.authentication_login_text);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(getString(R.string.login_shared_preferences), MODE_PRIVATE);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Request the ID token
                .requestServerAuthCode(getString(R.string.default_web_client_id)) // Request the server auth code
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        checkSavedCredentialsAndConnection(isNetworkAvailable(this));
        setUpSignInButton();
    }

    /**
     * Logs in the user if it has credentials
     */
    public void checkSavedCredentialsAndConnection(boolean networkAvailable) {
        fetchSavedCredentials();

        if (!idToken.isEmpty()) {
            if (networkAvailable) {
                authenticateUserWithCredentials(idToken, accessToken);
            } else if (!username.isEmpty()) {
                launchMainActivity(username);
            }
        }
    }

    /**
     * Fetches the user's credentials and updates the display accordingly
     */
    public void fetchSavedCredentials() {
        // Check if there is a saved id token, authentication token and username
        idToken = sharedPreferences.getString(getString(R.string.google_id_token_key), "");
        accessToken = sharedPreferences.getString(getString(R.string.google_access_token_key), "");
        username = sharedPreferences.getString(getString(R.string.username_key), "");

        if (!idToken.isEmpty()) {
            signInButton.setVisibility(View.INVISIBLE);
            signInText.setText(R.string.authentication_page_waiting_text);
        } else {
            signInButton.setVisibility(View.VISIBLE);
            signInText.setText(R.string.authentication_page_login_text);
        }

    }

    /**
     * update the tokens in shared preferences
     *
     * @param idToken:     the id token
     * @param accessToken: the access token
     */
    public void updatePreferencesToken(String idToken, String accessToken) {
        this.idToken = idToken;
        this.accessToken = accessToken;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.google_id_token_key), idToken);
        editor.putString(getString(R.string.google_access_token_key), accessToken);
        editor.apply();
    }

    /**
     * update the username in shared preferences
     *
     * @param username: the username
     */
    public void updatePreferencesUsername(String username) {
        this.username = username;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.username_key), username);
        editor.apply();
    }

    /**
     * Authenticates the user with the credentials in the SharedPreferences if possible.
     * Otherwise, sets up the sign-in button.
     *
     * @param idToken:     the ID token of the user
     * @param accessToken: the access token of the user
     */
    public void authenticateUserWithCredentials(String idToken, String accessToken) {
        // Authenticate user with Firebase
        if (accessToken.length() == 0) accessToken = null;
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, accessToken);

        FirebaseAuth.getInstance().signInWithCredential(credential) // we try to sign in using the tokens
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = Objects.requireNonNull(authResult.getUser()); // if successful, we store the username and launch the main activity

                    UserDatabase.getUserByEmail(user.getEmail())
                            .thenAccept(u -> {
                                updatePreferencesUsername(u.getUsername());
                            });

                    launchPostActivity(user);
                })
                .addOnFailureListener(e -> {
                    // If authentication fails, remove the saved authentication credential
                    clearSharedPreferences();
                    setUpSignInButton();
                    signInButton.setVisibility(View.VISIBLE);
                    signInText.setText(R.string.authentication_page_login_text);
                });
    }

    /**
     * Clears the stored tokens and user name in android's shared preferences
     */
    public void clearSharedPreferences() {
        idToken = "";
        accessToken = "";
        username = "";

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(getString(R.string.google_id_token_key));
        editor.remove(getString(R.string.google_access_token_key));
        editor.remove(getString(R.string.username_key));

        editor.apply();
    }

    /**
     * Sets up the sign in button
     */
    private void setUpSignInButton() {
        // We assign a callback to Google sign in button
        signInButton.setOnClickListener(view -> {
            Intent signInIntent = googleSignInClient.getSignInIntent(); // if the button is clicked, try to sign in
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
                GoogleSignInAccount account = task.getResult(ApiException.class); // get the user's google account and login with it
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) { throw new RuntimeException(e); } // Handle Google Sign-In error
        }
    }

    /**
     * Authenticates the user in firebase with Google sign-in
     *
     * @param account: the account of the user
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        // we try to sign in using the credentials of the google account
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(authResult -> {
                    // if the login is successful, we get the tokens from the user account
                    FirebaseUser currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
                    currentUser.updateEmail(Objects.requireNonNull(account.getEmail()));

                    String idToken = account.getIdToken();
                    String accessToken = account.getServerAuthCode();

                    // and we save them to SharedPreferences
                    updatePreferencesToken(idToken, accessToken);

                    // finally, we need to save the username, which is done by querying the database to give it using the email
                    UserDatabase.getUserByEmail(currentUser.getEmail())
                            .thenAccept(user -> {
                                updatePreferencesUsername(user.getUsername());
                            });


                    launchPostActivity(currentUser);

                })
                .addOnFailureListener(e -> {
                    throw new RuntimeException(e);
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

    /**
     * s
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