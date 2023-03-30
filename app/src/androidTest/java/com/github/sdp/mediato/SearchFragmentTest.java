package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.adevinta.android.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition;
import static com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn;
import static com.adevinta.android.barista.interaction.BaristaEditTextInteractions.typeTo;
import static com.adevinta.android.barista.interaction.BaristaKeyboardInteractions.pressImeActionButton;
import static com.adevinta.android.barista.interaction.BaristaListInteractions.clickListItemChild;
import static com.adevinta.android.barista.interaction.BaristaSleepInteractions.sleep;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.sdp.mediato.data.Database;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.ui.SearchFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class SearchFragmentTest {
  private final static int STANDARD_USER_TIMEOUT = 10;
  User user1;
  User user2;
  User user3;


  @Before
  public void setUp() throws ExecutionException, InterruptedException, TimeoutException
  {
    try {
      Database.database.useEmulator("10.0.2.2", 9000);
    } catch (Exception ignored) {
    }
    //Create new sample users
    user1 = new User.UserBuilder("uniqueId1")
            .setUsername("user_test_1")
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
    user3 = new User.UserBuilder("uniqueId3")
            .setUsername("user_test_3")
            .setEmail("email_test_3")
            .setRegisterDate("19/03/2023")
            .setLocation(new Location(3.14, 3.14))
            .build();

    Database.addUser(user1).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
    Database.addUser(user2).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
    Database.addUser(user3).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);

    // Launch the TestingActivity
    ActivityScenario<TestingActivity> scenario = ActivityScenario.launch(TestingActivity.class);

    // Set up the TestingActivity to display the SearchFragment
    scenario.onActivity(activity -> {
      FragmentManager fragmentManager = activity.getSupportFragmentManager();
      SearchFragment searchFragment = new SearchFragment();

      // Pass the username to the fragment like at profile creation
      Bundle bundle = new Bundle();
      bundle.putString("username", "user_test_1");
      searchFragment.setArguments(bundle);
      fragmentManager.beginTransaction().replace(R.id.fragment_container, searchFragment)
              .commitAllowingStateLoss();
    });
  }

  @Test
  public void testUserSearchWithUnknownUser() {
    clickOn(androidx.appcompat.R.id.search_button);
    typeTo(androidx.appcompat.R.id.search_src_text, "something");
    pressImeActionButton();

    sleep(500);

    onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.searchUserFailed)))
            .check(matches(isDisplayed()));

    onView(withId(com.google.android.material.R.id.snackbar_action))
            .perform(click());
  }

  @Test
  public void testUserSearchWithKnownUser() {
    clickOn(androidx.appcompat.R.id.search_button);
    typeTo(androidx.appcompat.R.id.search_src_text, user2.getUsername());
    pressImeActionButton();

    sleep(1000);

    assertDisplayedAtPosition(R.id.searchactivity_recyclerView, 0, R.id.searchUserAdapter_userName, user2.getUsername());
  }

  @Test
  public void testFollowAndUnfollow() {
    clickOn(androidx.appcompat.R.id.search_button);
    typeTo(androidx.appcompat.R.id.search_src_text, user2.getUsername());
    pressImeActionButton();

    sleep(1000);

    assertDisplayedAtPosition(R.id.searchactivity_recyclerView, 0, R.id.searchUserAdapter_userName, user2.getUsername());
    assertDisplayedAtPosition(R.id.searchactivity_recyclerView, 0, R.id.searchUserAdapter_followButton, R.string.searchUser_follow);
    clickListItemChild(R.id.searchactivity_recyclerView, 0, R.id.searchUserAdapter_followButton);

    sleep(1000);

    assertDisplayedAtPosition(R.id.searchactivity_recyclerView, 0, R.id.searchUserAdapter_userName, user2.getUsername());
    assertDisplayedAtPosition(R.id.searchactivity_recyclerView, 0, R.id.searchUserAdapter_unfollowButton, R.string.searchUser_unfollow);
    clickListItemChild(R.id.searchactivity_recyclerView, 0, R.id.searchUserAdapter_unfollowButton);

    sleep(1000);

    assertDisplayedAtPosition(R.id.searchactivity_recyclerView, 0, R.id.searchUserAdapter_userName, user2.getUsername());
    assertDisplayedAtPosition(R.id.searchactivity_recyclerView, 0, R.id.searchUserAdapter_followButton, R.string.searchUser_follow);
  }
}


