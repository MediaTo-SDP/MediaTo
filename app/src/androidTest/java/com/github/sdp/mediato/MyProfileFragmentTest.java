package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static java.lang.Thread.sleep;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.adevinta.android.barista.assertion.BaristaListAssertions;
import com.adevinta.android.barista.interaction.BaristaClickInteractions;
import com.adevinta.android.barista.interaction.BaristaListInteractions;
import com.github.sdp.mediato.DatabaseTests.DataBaseTestUtil;
import com.github.sdp.mediato.data.CollectionsDatabase;
import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.model.media.Collection;
import com.github.sdp.mediato.model.media.Movie;
import com.github.sdp.mediato.ui.AuthenticationActivity;
import com.github.sdp.mediato.ui.MainActivity;
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
    private final static int STANDARD_USER_TIMEOUT = 10;
    private final static int WAIT_TIME = 1000;
    private final static String MY_USERNAME = "user_profile";
    private final static String COLLECTION_NAME = "Recently watched";
    private final static String MOVIE_TITLE = "Title";

    private final static String ADD = "ADD";
    private final static String CANCEL = "CANCEL";
    private final static String YES = "YES";

    private final String email = "ph@mediato.ch";
    FirebaseUser user;
    ActivityScenario<MainActivity> scenario;
    ViewInteraction collectionListRecyclerView = onView(withId(R.id.collection_list_recycler_view));
    ViewInteraction collectionRecyclerView = onView(withId(R.id.collection_recycler_view));
    ViewInteraction recyclerView = onView(withId(R.id.collection_recycler_view));
    ViewInteraction userNameText = onView(withId(R.id.username_text));
    ViewInteraction editButton = onView(withId(R.id.edit_button));
    ViewInteraction followingButton = onView(withId(R.id.profile_following_button));
    ViewInteraction followersButton = onView(withId(R.id.profile_followers_button));
    ViewInteraction defaultCollection = onView(withId(R.id.collection_list));
    ViewInteraction addMediaButton = onView(withId(R.id.add_media_button));
    ViewInteraction addCollectionButton = onView(withId(R.id.add_collection_button));
    ViewInteraction deleteCollectionButton = onView(withId(R.id.delete_collection_button));
    ViewInteraction profilePic = onView(withId(R.id.profile_image));
    ViewInteraction profileMenuItem = onView(withId(R.id.profile));
    ViewInteraction searchMenuItem = onView(withId(R.id.search));
    ViewInteraction myFollowingBar = onView(withId(R.id.myFollowing_bar));
    ViewInteraction movieSearchCategory = onView(withId((R.id.search_category_movie)));

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

        // Create a collection with one review for user1 and upload it to the database
        Collection collection = new Collection(COLLECTION_NAME);
        Movie movie = new Movie(MOVIE_TITLE, "summary", "url", 1);
        Review review = new Review(MY_USERNAME, movie);
        collection.addReview(review);
        CollectionsDatabase.addCollection(user1.getUsername(), collection);

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

    // Test that the list of collections displays the collection downloaded from the database
    @Test
    public void testCollectionListState() {
        collectionListRecyclerView.check(matches(isDisplayed()));

        // Check that the collection list has exactly one item
        collectionListRecyclerView.check(matches(hasItemCount(2)));

        // Check that the collection list displays the correct collection
        collectionListRecyclerView.check(matches(hasDescendant(withText(COLLECTION_NAME))));
    }

    // Test that a click on the add media button opens the search
    @Test
    public void testAddMediaButton() {
        BaristaListInteractions.clickListItemChild(R.id.collection_list_recycler_view, 0, R.id.add_media_button);

        // Check that the search is displayed
        movieSearchCategory.check(matches(isDisplayed()));
    }

    // Check that a new collection has been added to the collections list if the user chooses a valid collection name
    @Test
    public void testAddValidCollection() {
        int initialItemCount = getRecyclerViewItemCount(R.id.collection_list_recycler_view);
        collectionListRecyclerView.check(matches(hasItemCount(initialItemCount)));
        addCollectionButton.perform(click());
        enterCollectionNameAndClickButton("valid", ADD);
        searchMenuItem.perform(click());
        profileMenuItem.perform(click());
        collectionListRecyclerView.check(matches(hasItemCount(initialItemCount + 1)));
    }

    // Check that no new collection has been added to the collections list if the user enters an empty collection name
    @Test
    public void testEmptyCollectionNameNotAdded() {
        int initialItemCount = getRecyclerViewItemCount(R.id.collection_list_recycler_view);

        collectionListRecyclerView.check(matches(hasItemCount(initialItemCount)));
        addCollectionButton.perform(click());
        enterCollectionNameAndClickButton("", ADD);
        collectionListRecyclerView.check(matches(hasItemCount(initialItemCount)));
    }

    // Check that only one collection gets added if the user tries to add a collection with the same username twice
    @Test
    public void testDuplicateCollectionNameNotAdded() {
        int initialItemCount = getRecyclerViewItemCount(R.id.collection_list_recycler_view);

        collectionListRecyclerView.check(matches(hasItemCount(initialItemCount)));
        addCollectionButton.perform(click());
        enterCollectionNameAndClickButton("duplicate", ADD);
        addCollectionButton.perform(click());
        enterCollectionNameAndClickButton("duplicate", ADD);
        searchMenuItem.perform(click());
        profileMenuItem.perform(click());
        collectionListRecyclerView.check(matches(hasItemCount(initialItemCount + 1)));
    }

    // Check that the collection does not get added if the user clicks cancel
    @Test
    public void testEnterValidCollectionAndCancelDoesNotAddCollection() {
        int initialItemCount = getRecyclerViewItemCount(R.id.collection_list_recycler_view);

        collectionListRecyclerView.check(matches(hasItemCount(initialItemCount)));
        addCollectionButton.perform(click());
        enterCollectionNameAndClickButton("don't add this", CANCEL);
        collectionListRecyclerView.check(matches(hasItemCount(initialItemCount)));
    }

    // Check that the collection does not get deleted if the user clicks cancel
    @Test
    public void testCancelingDeleteCollectionDoesNotRemoveCollection() {
        int initialItemCount = getRecyclerViewItemCount(R.id.collection_list_recycler_view);

        BaristaListInteractions.clickListItemChild(R.id.collection_list_recycler_view, 1, R.id.delete_collection_button);

        clickAlertDialogButton(CANCEL);
        collectionListRecyclerView.check(matches(hasItemCount(initialItemCount)));
    }

    // Check that the collection get deleted if the user confirms the deletion
    @Test
    public void testConfirmDeleteCollectionRemovesCollection() {
        int initialItemCount = getRecyclerViewItemCount(R.id.collection_list_recycler_view);

        BaristaListInteractions.clickListItemChild(R.id.collection_list_recycler_view, 1, R.id.delete_collection_button);
        clickAlertDialogButton(YES);
        searchMenuItem.perform(click());
        profileMenuItem.perform(click());
        collectionListRecyclerView.check(matches(hasItemCount(initialItemCount - 1)));
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
     * Used to interact with the AlertDialog to enter the collection name and click a button
     *
     * @param enterText the text to enter into the dialog box
     * @param buttonText click on the button with this button text (CASE SENSITIVE!)
     */
    private void enterCollectionNameAndClickButton(String enterText, String buttonText) {
        try {
            UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            UiObject inputField = device.findObject(
                    new UiSelector().resourceId("com.github.sdp.mediato:id/collection_name_input"));
            inputField.setText(enterText);

            clickAlertDialogButton(buttonText);
        } catch (UiObjectNotFoundException e) {
            fail();
        }
    }

    /**
     * Used to interact with an AlertDialog to click on a button with the given text
     *
     * @param buttonText click on the button with this button text (CASE SENSITIVE!)
     */
    private void clickAlertDialogButton(String buttonText) {
        try {
            UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            UiObject button = device.findObject(new UiSelector().text(buttonText));
            button.click();
        } catch (UiObjectNotFoundException e) {
            fail();
        }
    }

}


