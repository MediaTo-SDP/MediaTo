package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfileFragmentTest {

  @Before
  public void setUp() {
    // Launch the TestingActivity
    ActivityScenario<TestingActivity> scenario = ActivityScenario.launch(TestingActivity.class);

    // Set up the TestingActivity to display the ProfileFragment
    scenario.onActivity(activity -> {
      FragmentManager fragmentManager = activity.getSupportFragmentManager();
      fragmentManager.beginTransaction().replace(R.id.fragment_container, new ProfileFragment())
          .commitAllowingStateLoss();
    });
  }

  // Test whether the "Friends" button is displayed and contains the correct text
  @Test
  public void testFriendsButton() {
    ViewInteraction friendsButton = onView(withId(R.id.friends_button));
    friendsButton.check(matches(isDisplayed()));
    friendsButton.check(matches(withText("Friends")));
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
    userNameText.check(matches(withText("Username")));
  }

  // Test whether the "Recently watched" title is displayed  and contains the correct text
  @Test
  public void testRecentlyWatchedTitle() {
    ViewInteraction recentlyWatchedText = onView(withId(R.id.recently_watched_text));
    recentlyWatchedText.check(matches(isDisplayed()));
    recentlyWatchedText.check(matches(withText("Recently watched")));
  }

  // Test whether the "Add movie" button is displayed
  @Test
  public void testAddMovieButton() {
    ViewInteraction addMovieButton = onView(withId(R.id.add_movie_button));
    addMovieButton.check(matches(isDisplayed()));
  }

  // Test whether the horizontal scroll list is displayed and contains the addMovieButton
  @Test
  public void testHorizontalScrollList() {
    ViewInteraction horizontalScrollView = onView(withId(R.id.horizontal_scroll_view));
    horizontalScrollView.check(matches(isDisplayed()));
    horizontalScrollView.check(matches(hasDescendant(withId(R.id.add_movie_button))));
  }

  // Test whether a movie item, which consists of an image of a movie and a title and rating text,
  // is displayed and has the correct title and rating
  @Test
  public void testMovieItem() {
    ViewInteraction movieItem = onView(withId(R.id.test_movie_item));
    movieItem.check(matches(isDisplayed()));
    movieItem.check(matches(hasDescendant(withText("Title"))));
    movieItem.check(matches(hasDescendant(withText("Rating"))));
  }

}


