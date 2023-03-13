package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;

/**
 * Tests for the authentication activity using the firebase authentication emulator
 */
@RunWith(AndroidJUnit4.class)
public class AuthenticationActivityTest {

    private UiDevice device;


    @Rule
    public ActivityScenarioRule<AuthenticationActivity> testRule = new ActivityScenarioRule<>(AuthenticationActivity.class);


    /**
     * Starts the emulator and firebase instance, signs-out if user isn't
     */
    @Before
    public void startTests() {
        init();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.useEmulator("10.0.2.2", 9099);
        if (auth.getCurrentUser() != null) {auth.signOut();}

        device = UiDevice.getInstance(getInstrumentation());
    }

    /**
     * Logs in the user in the firebase authentication
     * @param email: user email
     */
    public static void login(String email) {

        // create user json
        String userJson;
        try {
            userJson = new JSONObject()
                    .put("sub", email)
                    .put("email", email)
                    .put("email_verified", "true")
                    .toString();
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }

        // log in user and await result
        Task<AuthResult> result = FirebaseAuth
                .getInstance()
                .signInWithCredential(GoogleAuthProvider
                        .getCredential(userJson, null));

        try {
            AuthResult authResult = Tasks.await(result);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Logs out the user in the firebase authentication
     */
    public static void logout() {

        // log out user and await result
        Task<Void> result = AuthUI.getInstance()
                .signOut(ApplicationProvider
                        .getApplicationContext());
        try {
            Tasks.await(result);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tests the login one tap button using the emulator
     * @throws InterruptedException: for thread.sleep
     */
    @Test
    public void testLogInButtonWorks() throws InterruptedException {
        //login("foo@example.com");
        ViewInteraction loginButton = onView(withId(R.id.google_sign_in));
        loginButton.perform(click());
        Thread.sleep(5000);

        UiObject object = device.findObject(new UiSelector().className("android.widget.button").textContains("@"));
        try {
            if (object.exists()) {object.click();}
        } catch (UiObjectNotFoundException e) {
            System.out.print("OBJECT NOT FOUND");
        }

        Thread.sleep(5000);
        Intents.intended(IntentMatchers.hasComponent(GreetingActivity.class.getName()));


        logout();
    }

    /**
     * Releases the intents
     */
    @After
    public void releaseIntents() {release();}

}
