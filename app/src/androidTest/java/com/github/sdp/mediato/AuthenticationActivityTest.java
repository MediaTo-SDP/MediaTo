package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;

@RunWith(AndroidJUnit4.class)
public class AuthenticationActivityTest {


    @Rule
    public ActivityScenarioRule<AuthenticationActivity> testRule = new ActivityScenarioRule<>(AuthenticationActivity.class);
    private Intent doneIntent;

    public static void loginSync(String email) {
        String userJson;
        try {
            userJson = new JSONObject()
                    .put("sub", email)
                    .put("email", email)
                    .put("email_verified", "true")
                    .toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        AuthCredential credential = GoogleAuthProvider.getCredential(userJson, null);
        Task<AuthResult> result = FirebaseAuth.getInstance().signInWithCredential(credential);

        try {
            AuthResult authResult = Tasks.await(result);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void logoutSync() {
        Task<Void> result = AuthUI.getInstance().signOut(ApplicationProvider.getApplicationContext());
        try {
            Tasks.await(result);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Before
    public void prepare() {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.useEmulator("10.0.2.2", 9099);

        if (auth.getCurrentUser() != null) {
            auth.signOut();
        }

        doneIntent = new Intent(ApplicationProvider.getApplicationContext(), GreetingActivity.class);
    }

    @Test
    public void testLoggedIn() throws InterruptedException {
        loginSync("ph@example.com");
        Intents.init();
        ViewInteraction loginButton = onView(withId(R.id.google_sign_in));
        loginButton.perform(click());

        Thread.sleep(3000);
        Intents.intended(IntentMatchers.hasComponent(GreetingActivity.class.getName()));

        Intents.release();
        logoutSync();
    }

}
