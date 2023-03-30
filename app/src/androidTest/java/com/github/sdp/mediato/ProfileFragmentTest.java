package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.Bundle;
import android.view.View;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfileFragmentTest {

  ActivityScenario<TestingActivity> scenario;

  @Before
  public void setUp() {
    // Launch the TestingActivity
    scenario = ActivityScenario.launch(TestingActivity.class);

    // Set up the TestingActivity to display the ProfileFragment
    scenario.onActivity(activity -> {
      FragmentManager fragmentManager = activity.getSupportFragmentManager();
      ProfileFragment profileFragment = new ProfileFragment();

      // Pass the username to the fragment like at profile creation
      Bundle bundle = new Bundle();
      bundle.putString("username", "myUsername");
      profileFragment.setArguments(bundle);
      fragmentManager.beginTransaction().replace(R.id.fragment_container, profileFragment)
          .commitAllowingStateLoss();
    });
  }

  // Test whether the "Friends" button is displayed and contains the correct text
  @Test
  public void testFollowingButton() {
    ViewInteraction followingButton = onView(withId(R.id.following_button));
    followingButton.check(matches(isDisplayed()));
    followingButton.check(matches(withText(R.string.following)));
  }

  // Test whether the "Edit" button is displayed and contains the correct text
  @Test
  public void testEditButton() {
    ViewInteraction editButton = onView(withId(R.id.edit_button));
    editButton.check(matches(isDisplayed()));
    editButton.check(matches(withText("Edit")));
  }

  // Test whether the profile picture is displayed
  @Test
  public void testProfilePicture() {
    onView(withId(R.id.profile_image)).check(matches(isDisplayed()));
  }

  // Test whether the username text is displayed and contains the correct text
  @Test
  public void testUsername() {
    ViewInteraction userNameText = onView(withId(R.id.username_text));
    userNameText.check(matches(isDisplayed()));
    userNameText.check(matches(withText("myUsername")));
  }

  // Test the initial state of the default collection after profile creation
  @Test
  public void testInitialDefaultCollectionState() {
    ViewInteraction defaultCollection = onView(withId(R.id.default_collection));
    ViewInteraction recyclerView = onView(withId(R.id.collection_recycler_view));

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
    ViewInteraction collectionRecyclerView = onView(withId(R.id.collection_recycler_view));
    ViewInteraction addMediaButton = onView(withId(R.id.add_media_button));

    int initialItemCount = getRecyclerViewItemCount(R.id.collection_recycler_view);

    collectionRecyclerView.check(matches(hasItemCount(initialItemCount)));
    addMediaButton.perform(click());
    collectionRecyclerView.check(matches(hasItemCount(initialItemCount + 1)));
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

}


