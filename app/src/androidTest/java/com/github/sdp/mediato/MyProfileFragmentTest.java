package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static java.lang.Thread.sleep;
import static org.junit.Assert.fail;

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
import com.adevinta.android.barista.interaction.BaristaSleepInteractions;
import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.ui.MyProfileFragment;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MyProfileFragmentTest {
  private final static int STANDARD_USER_TIMEOUT = 10;
  private final static String MY_USERNAME = "test_username";

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
  ViewInteraction profileMenuItem= onView(withId(R.id.profile));
  ViewInteraction searchMenuItem = onView(withId(R.id.search));
  ViewInteraction myFollowingBar = onView(withId(R.id.myFollowing_bar));
  ViewInteraction myFollowersBar = onView(withId(R.id.myFollowers_bar));

  User user1;
  User user2;

  @Before
  public void setUp() throws ExecutionException, InterruptedException, TimeoutException {

    // Use Database emulator
    try {
      UserDatabase.database.useEmulator("10.0.2.2", 9000);
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
        .setUsername("user_test_2")
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
      FragmentManager fragmentManager = activity.getSupportFragmentManager();
      MyProfileFragment myProfileFragment = new MyProfileFragment();

      // Pass the username to the fragment like at profile creation
      Bundle bundle = new Bundle();
      bundle.putString("username", MY_USERNAME);
      myProfileFragment.setArguments(bundle);
      fragmentManager.beginTransaction().replace(R.id.main_container, myProfileFragment)
          .commitAllowingStateLoss();
    });
  }

  private void storeUsers(){

  }

  // Test whether the "Following" button is displayed and contains the correct text
  @Test
  public void testInitialFollowingButtonState() {
    followingButton.check(matches(isDisplayed()));
    followingButton.check(matches(withText("0 Following")));
  }

  // Tests that following a user updates the count on the following button when getting back to the profile page
  @Test
  public void testAddFollowingUpdatesFollowingButton() {
    UserDatabase.followUser(MY_USERNAME, user2.getUsername());
    searchMenuItem.perform(click());
    profileMenuItem.perform(click());
    followingButton.check(matches(withText("1 Following")));
  }

  // Tests that clicking the following button opens the following fragment
  @Test
  public void testFollowingButtonOpensFollowingPage() {
    followingButton.check(matches(isCompletelyDisplayed()));
    followingButton.perform(click());
    myFollowingBar.check(matches(isDisplayed()));
  }

  // Test whether the "Followers" button is displayed and contains the correct text
  @Test
  public void testInitialFollowersButtonState() {
    followersButton.check(matches(isDisplayed()));
    followersButton.check(matches(withText("0 Followers")));
  }

  // Tests getting a new follower updates the count on the following button when getting back to the profile page
  @Test
  public void testAddFollowerUpdatesFollowerButton() {
    UserDatabase.followUser(user2.getUsername(), MY_USERNAME);
    searchMenuItem.perform(click());
    profileMenuItem.perform(click());
    followersButton.check(matches(withText("1 Followers")));
  }

  // Tests that clicking the followers button opens the followers fragment
  @Test
  public void testFollowersButtonOpensFollowersPage() {
    followersButton.check(matches(isCompletelyDisplayed()));
    followersButton.perform(click());
    myFollowersBar.check(matches(isDisplayed()));
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

  // Test that an item is added to the collection on click of the AddMediaButton
  @Test
  public void testAddMediaButton() {
    int initialItemCount = getRecyclerViewItemCount(R.id.collection_recycler_view);

    collectionRecyclerView.check(matches(hasItemCount(initialItemCount)));
    addMediaButton.perform(click());
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
  public void testDuplicateCollectionNameNotAdded(){
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


