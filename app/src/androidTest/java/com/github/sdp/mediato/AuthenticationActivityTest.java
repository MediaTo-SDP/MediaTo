package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import com.firebase.ui.auth.AuthUI;
import com.github.sdp.mediato.data.Database;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private final String email = "ph@mediato.ch";
    private final User databaseUser = new User.UserBuilder("authUniqueId1")
            .setUsername("auth_user_test_1")
            .setEmail(email)
            .setRegisterDate("08/03/2023")
            .setLocation(new Location(3.15, 3.15))
            .build();
    private final UiDevice device = UiDevice.getInstance(getInstrumentation());
    @Rule
    public ActivityScenarioRule<AuthenticationActivity> testRule = new ActivityScenarioRule<>(AuthenticationActivity.class);
    private AuthenticationActivity activity;
    private FirebaseUser user;

    /**
     * Logs in the user in the firebase authentication
     */
    public void login() {

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
            Tasks.await(result);
            user = result.getResult().getUser();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Logs out the user in the firebase authentication
     */
    public void logout() {

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
     * Starts the emulator and firebase instance, signs-out if user isn't
     */
    @Before
    public void startTests() {
        init();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        try {
            auth.useEmulator("10.0.2.2", 9099);
            Database.database.useEmulator("10.0.2.2", 9000);
        } catch (IllegalStateException ignored) {
        }

        if (auth.getCurrentUser() != null) {
            auth.signOut();
        }

        Database.database.getReference().setValue(null);

        testRule.getScenario().onActivity(activity1 -> activity = activity1);
    }

    /**
     * Tests the login one tap button using the emulator
     */
    @Test
    public void testSignInWorks() {

        login();
        ViewInteraction loginButton = onView(withId(R.id.google_sign_in));
        loginButton.perform(click());

        // select the account if google account selector pops up
        try {
            device.findObject(By.textContains("@")).click();
        } catch (NullPointerException e) {
            System.out.println("Object wasn't found");
        }

        Intents.intended(hasComponent(NewProfileActivity.class.getName()));

        logout();
    }

    /**
     * Tests the login one tap button using the emulator
     */
    @Test
    public void testLogInWorks() {

        login();
        Database.addUser(databaseUser).thenAccept(u -> {
            ViewInteraction loginButton = onView(withId(R.id.google_sign_in));
            loginButton.perform(click());

            // select the account if google account selector pops up
            try {
                device.findObject(By.textContains("@")).click();
            } catch (NullPointerException e) {
                System.out.println("Object wasn't found");
            }

            Intents.intended(hasComponent(MainActivity.class.getName()));

            Database.deleteUser(databaseUser.getUsername());
            logout();
        });


    }

    /**
     * The post launch of the activity should throw null pointer when user is null
     */
    @Test(expected = NullPointerException.class) //OR any other
    public void testLaunchingPostActivityThrowsWithNull() {
        activity.launchPostActivity(null);
    }

    /**
     * Test expected behavior of launching the post activity with signing in user
     */
    @Test
    public void testLaunchingPostActivitySucceedsWithUserSigningIn() {
        login();
        activity.launchPostActivity(user);
        intended(hasComponent(NewProfileActivity.class.getName()));
        logout();
    }

    /**
     * Test expected behavior of launching the post activity with logging in user
     */
    @Test
    public void testLaunchingPostActivitySucceedsWithUserLoggingIn() {
        login();
        Database.addUser(databaseUser).thenAccept(u -> {
            activity.launchPostActivity(user);
            intended(hasComponent(MainActivity.class.getName()));
            Database.deleteUser(databaseUser.getUsername());
            logout();
        });

    }

    /**
     * Releases the intents
     */
    @After
    public void releaseIntents() {
        release();
    }

}
