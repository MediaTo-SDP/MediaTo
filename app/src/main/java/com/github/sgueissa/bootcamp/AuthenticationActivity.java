package com.github.sgueissa.bootcamp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthenticationActivity extends AppCompatActivity {

    private SignInButton btSignIn;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private final static int SIGNIN_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        btSignIn = findViewById(R.id.google_sign_in);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(String.valueOf(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(AuthenticationActivity.this, googleSignInOptions);

        btSignIn.setOnClickListener((View.OnClickListener) view -> {
            // Initialize sign in intent
            Intent intent = googleSignInClient.getSignInIntent();
            // Start activity for result
            signInResultLauncher.launch(intent);
        });

        // Initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        // Initialize firebase user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        // Check condition
        if (firebaseUser != null) {
            // When user already sign in redirect to profile activity
            displayToast("Firebase authentication successful");
        }
    }

    ActivityResultLauncher<Intent> signInResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // Check condition
                    if (result.getResultCode() == SIGNIN_REQUEST_CODE) {
                        // When request code is equal to 100 initialize task
                        Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        // check condition
                        if (signInAccountTask.isSuccessful()) {
                            // Display Toast
                            displayToast("Google sign in successful\"");
                            // Initialize sign in account
                            try {
                                // Initialize sign in account
                                GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                                // Check condition
                                if (googleSignInAccount != null) {
                                    // When sign in account is not equal to null initialize auth credential
                                    AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                                    // Check credential
                                    firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(AuthenticationActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            // Check condition
                                            if (task.isSuccessful()) {
                                                // When task is successful redirect to profile activity display Toast
                                                displayToast("Firebase authentication successful");
                                            } else {
                                                // When task is unsuccessful display Toast
                                                displayToast("Authentication Failed :" + task.getException().getMessage());
                                            }
                                        }
                                    });
                                }
                            } catch (ApiException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });

    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}