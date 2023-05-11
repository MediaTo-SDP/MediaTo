package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers.Visibility;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.github.sdp.mediato.DatabaseTests.DataBaseTestUtil;
import com.github.sdp.mediato.ui.ReadOnlyProfileFragment;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ReadOnlyProfileFragmentTest {
  private final static String USERNAME = "user_readprofile";
  ActivityScenario<MainActivity> scenario;
  ViewInteraction userNameText = onView(withId(R.id.username_text));
  ViewInteraction followingButton = onView(withId(R.id.profile_following_button));
  ViewInteraction followersButton = onView(withId(R.id.profile_followers_button));
  ViewInteraction profilePic = onView(withId(R.id.profile_image));
  ViewInteraction editButton = onView(withId(R.id.edit_button));
  ViewInteraction addCollectionButton = onView(withId(R.id.add_collection_button));
  ViewInteraction addMediaButton = onView(withId(R.id.add_media_button));

  @Before
  public void setUp() throws ExecutionException, InterruptedException, TimeoutException {
    // Use Database emulator
    try {
      DataBaseTestUtil.useEmulator();
    } catch (Exception ignored) {
    }

    // Launch the MainActivity
    scenario = ActivityScenario.launch(MainActivity.class);

    // Set up the MainActivity to display the ProfileFragment
    scenario.onActivity(activity -> {
      FragmentManager fragmentManager = activity.getSupportFragmentManager();
      ReadOnlyProfileFragment readOnlyProfileFragment = new ReadOnlyProfileFragment();
      activity.getReadOnlyProfileViewModel().setUsername(USERNAME);
      fragmentManager.beginTransaction().replace(R.id.main_container, readOnlyProfileFragment)
          .commitAllowingStateLoss();
    });
  }

  @AfterClass
  public static void cleanDatabase() {
    DataBaseTestUtil.cleanDatabase();
  }

  // Test whether the "Following" button is displayed and contains the correct text
  @Test
  public void testFollowingButton() {
    followingButton.check(matches(isDisplayed()));
    followingButton.check(matches(withText("0 Following")));
  }

  // Test whether the "Followers" button is displayed and contains the correct text
  @Test
  public void testFollowersButton() {
    followersButton.check(matches(isDisplayed()));
    followersButton.check(matches(withText("0 Followers")));
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
    userNameText.check(matches(withText(USERNAME)));
  }

  // Test that the edit button is not displayed
  @Test
  public void testEditButtonNotDisplayed() {
    editButton.check(matches(withEffectiveVisibility(Visibility.GONE)));
  }

  // Test that the add collection button is not displayed
  @Test
  public void testAddCollectionButtonNotDisplayed() {
    addCollectionButton.check(matches(withEffectiveVisibility(Visibility.GONE)));
  }

}


