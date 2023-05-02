package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.adevinta.android.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition;
import static com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn;
import static com.adevinta.android.barista.interaction.BaristaEditTextInteractions.typeTo;
import static com.adevinta.android.barista.interaction.BaristaListInteractions.clickListItemChild;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static java.lang.Thread.sleep;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.adevinta.android.barista.interaction.BaristaClickInteractions;
import com.adevinta.android.barista.interaction.BaristaSleepInteractions;
import com.github.sdp.mediato.DatabaseTests.DataBaseTestUtil;
import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.ui.MyProfileFragment;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class MyProfileFragmentTest {
    private final static int STANDARD_USER_TIMEOUT = 60;
    private final static int WAIT_TIME = 1000;
    private final static String MY_USERNAME = "user_profile";
    private final String email = "ph@mediato.ch";
    FirebaseUser user;
    ActivityScenario<MainActivity> scenario;
    ViewInteraction outerRecyclerView = onView(withId(R.id.collection_list_recycler_view));
    ViewInteraction collectionRecyclerView = onView(withId(R.id.collection_recycler_view));
    ViewInteraction recyclerView = onView(withId(R.id.collection_recycler_view));
    ViewInteraction userNameText = onView(withId(R.id.username_text));
    ViewInteraction editButton = onView(withId(R.id.edit_button));
    ViewInteraction followingButton = onView(withId(R.id.profile_following_button));
    ViewInteraction followersButton = onView(withId(R.id.profile_followers_button));
    ViewInteraction defaultCollection = onView(withId(R.id.collection_list));
    ViewInteraction addMediaButton = onView(withId(R.id.add_media_button));
    ViewInteraction addCollectionButton = onView(withId(R.id.add_collection_button));
    ViewInteraction profilePic = onView(withId(R.id.profile_image));
    ViewInteraction profileMenuItem = onView(withId(R.id.profile));
    ViewInteraction searchMenuItem = onView(withId(R.id.search));
    ViewInteraction myFollowingBar = onView(withId(R.id.myFollowing_bar));
    ViewInteraction myFollowersBar = onView(withId(R.id.myFollowers_bar));

    ViewInteraction movieSearchCategory = onView(withId((R.id.search_category_movie)));
    ViewInteraction addReviewButton = onView(withId((R.id.item_button_add)));

    User user1;
    User user2;

    MainActivity activity;

    /**
     * A matcher to check if a RecyclerView has a certain amount of items.
     *
     * @param count the expected number of items.
     * @return a Matcher to check if a view has count amount of items.
     */
    public static Matcher<View> hasItemCount(int count) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            protected boolean matchesSafely(RecyclerView recyclerView) {
                return recyclerView.getAdapter().getItemCount() == count;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView with item count: " + count);
            }
        };
    }

    /**
     * Used to login users into the firebase emulator when testing
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

    @Before
    public void setUp() throws ExecutionException, InterruptedException, TimeoutException {

        init();

        // Use Database emulator
        FirebaseAuth auth = FirebaseAuth.getInstance();
        try {
            auth.useEmulator("10.0.2.2", 9099);
            DataBaseTestUtil.useEmulator();
        } catch (Exception ignored) {
        }

        //Create new sample users
        user1 = new User.UserBuilder("uniqueId1")
                .setUsername(MY_USERNAME)
                .setEmail("email_test_1")
                .setRegisterDate("09/03/2023")
                .setLocation(new Location(3.14, 3.14))
                .build();
        user2 = new User.UserBuilder("uniqueId2")
                .setUsername("user_test_2_MyProfileFragmentTest")
                .setEmail("email_test_2")
                .setRegisterDate("19/03/2023")
                .setLocation(new Location(3.14, 3.14))
                .build();

        UserDatabase.addUser(user1).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
        UserDatabase.addUser(user2).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);

        // Launch the MainActivity
        scenario = ActivityScenario.launch(MainActivity.class);

        // Set up the MainActivity to display the ProfileFragment
        scenario.onActivity(activity -> {
            this.activity = activity;
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            MyProfileFragment myProfileFragment = new MyProfileFragment();
            activity.getMyProfileViewModel().setUsername(MY_USERNAME);

            // Pass the username to the fragment like at profile creation
            Bundle bundle = new Bundle();
            bundle.putString("username", MY_USERNAME);
            myProfileFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.main_container, myProfileFragment)
                    .commitAllowingStateLoss();
        });
    }

    @AfterClass
    public static void cleanDatabase() {
        DataBaseTestUtil.cleanDatabase();
    }

    // Test whether the "Following" button is displayed and contains the correct text
    @Test
    public void testInitialFollowingButtonState() {
        followingButton.check(matches(isDisplayed()));
        followingButton.check(matches(withText("0 Following")));
    }

    // Tests that clicking the following button opens the following fragment
    @Test
    public void testFollowingButtonOpensFollowingPage() {
        followingButton.perform(click());
        myFollowingBar.check(matches(isDisplayed()));
    }

    // Test whether the "Followers" button is displayed and contains the correct text
    @Test
    public void testInitialFollowersButtonState() {
        followersButton.check(matches(isDisplayed()));
        followersButton.check(matches(withText("0 Followers")));
    }

    // Test whether the "Edit" button is displayed
    @Test
    public void testEditButton() {
        editButton.check(matches(isDisplayed()));
    }

    // Test whether the profile picture is displayed
    @Test
    public void testProfilePicture() {
        profilePic.check(matches(isDisplayed()));
    }

    // Test whether the username text is displayed and contains the correct text
    @Test
    public void testUsername() {
        userNameText.check(matches(isDisplayed()));
        userNameText.check(matches(withText(MY_USERNAME)));
    }

    // Test the initial state of the default collection after profile creation
    @Test
    public void testInitialDefaultCollectionState() {

        // Check that the default collection is displayed
        defaultCollection.check(matches(isDisplayed()));

        // Check that the default title is correct
        defaultCollection.check(matches(hasDescendant(withText("Recently watched"))));

        // Check that the collection has an AddMedia button
        defaultCollection.check(matches(hasDescendant(withId(R.id.add_media_button))));

        // Check that the collection has a recycler view
        defaultCollection.check(matches(hasDescendant(withId(R.id.collection_recycler_view))));

        // Check that the recycler view is initially empty
        recyclerView.check(matches(hasItemCount(0)));
    }

    // This test checks all the steps of adding a review to a collection on the profile
    @Test
    public void testAddMediaButton() {
        int initialItemCount = getRecyclerViewItemCount(R.id.collection_recycler_view);

        // Check that the collection is displayed and click on the add media button
        collectionRecyclerView.check(matches(hasItemCount(initialItemCount)));
        addMediaButton.perform(click());

        // Check that the search is displayed
        movieSearchCategory.check(matches(isDisplayed()));

        // Search for a movie
        clickOn(R.id.search_category_movie);
        clickOn(androidx.appcompat.R.id.search_button);
        typeTo(androidx.appcompat.R.id.search_src_text, "Harry Potter and the half blood prince");
        BaristaSleepInteractions.sleep(WAIT_TIME);

        // Check that the search result is displayed
        assertDisplayedAtPosition(R.id.searchactivity_recyclerView, 0, R.id.text_title, "Harry Potter and the Half-Blood Prince");

        // Click on the search result
        clickListItemChild(R.id.searchactivity_recyclerView, 0, R.id.media_cover);
        BaristaSleepInteractions.sleep(WAIT_TIME);

        // Check that the rating screen is displayed
        addReviewButton.check(matches(isDisplayed()));

        // Click on the add button
        addReviewButton.perform(click());
        BaristaSleepInteractions.sleep(WAIT_TIME);

        // Check that the review was added
        collectionRecyclerView.check(matches(hasItemCount(initialItemCount + 1)));
    }


    // Test the initial state of the list of collections after profile creation
    @Test
    public void testInitialOuterRecyclerViewState() {
        outerRecyclerView.check(matches(isDisplayed()));
        // Check that the outer RecyclerView initially has one item (default collection)
        outerRecyclerView.check(matches(hasItemCount(1)));
    }

    // Check that a new collection has been added to the outer RecyclerView if the user chooses a valid collection name
    @Test
    public void testAddValidCollection() {
        int initialItemCount = getRecyclerViewItemCount(R.id.collection_list_recycler_view);

        outerRecyclerView.check(matches(hasItemCount(initialItemCount)));
        addCollectionButton.perform(click());
        enterTextInAlertBoxAndClickAdd("valid");
        outerRecyclerView.check(matches(hasItemCount(initialItemCount + 1)));
    }

    // Check that no new collection has been added to the outer RecyclerView if the user enters an empty collection name
    @Test
    public void testEmptyCollectionNameNotAdded() {
        int initialItemCount = getRecyclerViewItemCount(R.id.collection_list_recycler_view);

        outerRecyclerView.check(matches(hasItemCount(initialItemCount)));
        addCollectionButton.perform(click());
        enterTextInAlertBoxAndClickAdd("");
        outerRecyclerView.check(matches(hasItemCount(initialItemCount)));
    }

    // Check that only one collection gets added if the user tries to add a collection with the same username twice
    @Test
    public void testDuplicateCollectionNameNotAdded() {
        int initialItemCount = getRecyclerViewItemCount(R.id.collection_list_recycler_view);

        outerRecyclerView.check(matches(hasItemCount(initialItemCount)));
        addCollectionButton.perform(click());
        enterTextInAlertBoxAndClickAdd("duplicate");
        addCollectionButton.perform(click());
        enterTextInAlertBoxAndClickAdd("duplicate");
        outerRecyclerView.check(matches(hasItemCount(initialItemCount + 1)));
    }

    // Check that only one collection gets added if the user tries to add a collection with the same username twice
    @Test
    public void testEnterValidCollectionAndCancelDoesNotAddCollection() {
        int initialItemCount = getRecyclerViewItemCount(R.id.collection_list_recycler_view);

        outerRecyclerView.check(matches(hasItemCount(initialItemCount)));
        addCollectionButton.perform(click());
        enterTextInAlertBoxAndClickCancel("duplicate");
        outerRecyclerView.check(matches(hasItemCount(initialItemCount)));
    }

    // Tests the sign out, should sign out the user and redirect to authentication page
    @Test
    public void testSignOut() throws InterruptedException {
        login();

        // Click on the sign out button
        activity.signOutUser();
        sleep(WAIT_TIME);

        // Check whether the user is signed out
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assertNull(currentUser);

        // Check whether we are redirected to the login activity
        intended(hasComponent(AuthenticationActivity.class.getName()));

    }

    @After
    public void releaseIntents() {
        release();
    }

    /**
     * Returns the number of items in a RecyclerView hosted by the TestingActivity
     *
     * @param id the id of the RecyclerView
     * @return the number of items currently in the RecyclerView
     */
    private int getRecyclerViewItemCount(int id) {
        final int[] itemCount = new int[1];

        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.findViewById(id);
            itemCount[0] = recyclerView.getAdapter().getItemCount();
        });
        return itemCount[0];
    }

    /**
     * Used to interact with the AlertDialog to enter the collection name
     *
     * @param enterText the text to enter into the dialog box
     */
    private void enterTextInAlertBoxAndClickAdd(String enterText) {
        try {
            UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            UiObject inputField = device.findObject(
                    new UiSelector().resourceId("com.github.sdp.mediato:id/collection_name_input"));
            inputField.setText(enterText);

            UiObject addButton = device.findObject(
                    // In case the text of the button is ever changed: this is case sensitive!
                    // If the string resource says Add but the button text is displayed in capital letters, only ADD works!
                    new UiSelector().text("ADD"));
            addButton.click();
        } catch (UiObjectNotFoundException e) {
            fail();
        }
    }

    /**
     * Used to interact with the AlertDialog to enter the collection name
     *
     * @param enterText the text to enter into the dialog box
     */
    private void enterTextInAlertBoxAndClickCancel(String enterText) {
        try {
            UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            UiObject inputField = device.findObject(
                    new UiSelector().resourceId("com.github.sdp.mediato:id/collection_name_input"));
            inputField.setText(enterText);

            UiObject addButton = device.findObject(
                    // In case the text of the button is ever changed: this is case sensitive!
                    // If the string resource says Cancel but the button text is displayed in capital letters, only CANCEL works!
                    new UiSelector().text("CANCEL"));
            addButton.click();
        } catch (UiObjectNotFoundException e) {
            fail();
        }
    }

}


