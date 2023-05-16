package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.adevinta.android.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition;
import static com.adevinta.android.barista.assertion.BaristaRecyclerViewAssertions.assertRecyclerViewItemCount;
import static com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertContains;
import static com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn;
import static com.adevinta.android.barista.interaction.BaristaEditTextInteractions.clearText;
import static com.adevinta.android.barista.interaction.BaristaEditTextInteractions.typeTo;
import static com.adevinta.android.barista.interaction.BaristaKeyboardInteractions.pressImeActionButton;
import static com.adevinta.android.barista.interaction.BaristaListInteractions.clickListItem;
import static com.adevinta.android.barista.interaction.BaristaListInteractions.clickListItemChild;
import static com.adevinta.android.barista.interaction.BaristaSleepInteractions.sleep;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.sdp.mediato.DatabaseTests.DataBaseTestUtil;
import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.ui.MyProfileFragment;
import com.github.sdp.mediato.ui.SearchFragment;
import com.github.sdp.mediato.ui.viewmodel.MyProfileViewModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class SearchFragmentTest {
  private final static int STANDARD_USER_TIMEOUT = 10;
  private final static int WAIT_TIME = 1000;
  User user1;
  User user2;
  User user3;
  User user4;

  @Before
  public void setUp() throws ExecutionException, InterruptedException, TimeoutException
  {
    try {
      DataBaseTestUtil.useEmulator();
    } catch (Exception ignored) {
    }
    //Create new sample users
    user1 = new User.UserBuilder("uniqueId1")
            .setUsername("user_1_search")
            .setEmail("email_test_1")
            .setRegisterDate("09/03/2023")
            .setLocation(new Location(3.14, 3.14))
            .build();
    user2 = new User.UserBuilder("uniqueId2")
            .setUsername("user_2_search")
            .setEmail("email_test_2")
            .setRegisterDate("19/03/2023")
            .setLocation(new Location(3.14, 3.14))
            .build();
    user3 = new User.UserBuilder("uniqueId3")
            .setUsername("user_3_search")
            .setEmail("email_test_3")
            .setRegisterDate("19/03/2023")
            .setLocation(new Location(3.14, 3.14))
            .build();
    user4 = new User.UserBuilder("uniqueId4")
            .setUsername("oser_1_search")
            .setEmail("email_test_3")
            .setRegisterDate("19/03/2023")
            .setLocation(new Location(3.14, 3.14))
            .build();

    UserDatabase.addUser(user1).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
    UserDatabase.addUser(user2).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
    UserDatabase.addUser(user3).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
    UserDatabase.addUser(user4).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);

    // Launch the MainActivity
    ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

    // Set up the MainActivity to display the SearchFragment
    scenario.onActivity(activity -> {
      FragmentManager fragmentManager = activity.getSupportFragmentManager();
      SearchFragment searchFragment = new SearchFragment();
      activity.getMyProfileViewModel().setUsername(user1.getUsername());

      // Pass the username to the fragment like at profile creation
      Bundle bundle = new Bundle();
      bundle.putString("username", user1.getUsername());
      bundle.putString("general_search", "true");
      bundle.putString("collection", "Recently watched");
      searchFragment.setArguments(bundle);
      fragmentManager.beginTransaction().replace(R.id.main_container, searchFragment)
              .commitAllowingStateLoss();
    });
  }

  @AfterClass
  public static void cleanDatabase() {
    DataBaseTestUtil.cleanDatabase();
  }

  @Test
  public void testUserSearchWithEmptyString() {
    clickOn(androidx.appcompat.R.id.search_button);
    typeTo(androidx.appcompat.R.id.search_src_text, "user");
    pressImeActionButton();

    sleep(WAIT_TIME);

    clearText(androidx.appcompat.R.id.search_src_text);
    pressImeActionButton();

    sleep(WAIT_TIME);

    assertRecyclerViewItemCount(R.id.userSearch_recyclerView, 0);
  }

  @Test
  public void testUserSearchWithUnknownUser() {
    clickOn(androidx.appcompat.R.id.search_button);
    typeTo(androidx.appcompat.R.id.search_src_text, "something");
    pressImeActionButton();

    sleep(WAIT_TIME);

    assertRecyclerViewItemCount(R.id.userSearch_recyclerView, 0);
  }

  @Test
  public void testUserSearchWithKnownUser() {
    clickOn(androidx.appcompat.R.id.search_button);
    typeTo(androidx.appcompat.R.id.search_src_text, user2.getUsername());
    pressImeActionButton();

    sleep(WAIT_TIME);

    assertDisplayedAtPosition(R.id.userSearch_recyclerView, 0, R.id.userAdapter_userName, user2.getUsername());
  }

  @Test
  public void testUserSearchWithBeginningOfKnownUser() {
    clickOn(androidx.appcompat.R.id.search_button);
    typeTo(androidx.appcompat.R.id.search_src_text, "user");
    pressImeActionButton();

    sleep(WAIT_TIME);

    assertDisplayedAtPosition(R.id.userSearch_recyclerView, 0, R.id.userAdapter_userName, user2.getUsername());
    assertDisplayedAtPosition(R.id.userSearch_recyclerView, 1, R.id.userAdapter_userName, user3.getUsername());
    assertNotDisplayed(R.id.userAdapter_userName, user4.getUsername());
    assertNotDisplayed(R.id.userAdapter_userName, user1.getUsername());
  }

  @Test
  public void testFollowAndUnfollow() {
    clickOn(androidx.appcompat.R.id.search_button);
    typeTo(androidx.appcompat.R.id.search_src_text, user2.getUsername());
    pressImeActionButton();

    sleep(WAIT_TIME);

    assertDisplayedAtPosition(R.id.userSearch_recyclerView, 0, R.id.userAdapter_userName, user2.getUsername());
    assertDisplayedAtPosition(R.id.userSearch_recyclerView, 0, R.id.userAdapter_followButton, R.string.searchUser_follow);
    clickListItemChild(R.id.userSearch_recyclerView, 0, R.id.userAdapter_followButton);

    sleep(WAIT_TIME);

    assertDisplayedAtPosition(R.id.userSearch_recyclerView, 0, R.id.userAdapter_userName, user2.getUsername());
    assertDisplayedAtPosition(R.id.userSearch_recyclerView, 0, R.id.userAdapter_unfollowButton, R.string.searchUser_unfollow);
    clickListItemChild(R.id.userSearch_recyclerView, 0, R.id.userAdapter_unfollowButton);

    sleep(WAIT_TIME);

    assertDisplayedAtPosition(R.id.userSearch_recyclerView, 0, R.id.userAdapter_userName, user2.getUsername());
    assertDisplayedAtPosition(R.id.userSearch_recyclerView, 0, R.id.userAdapter_followButton, R.string.searchUser_follow);
  }

  @Test
  public void testClickOnCard() {
    clickOn(androidx.appcompat.R.id.search_button);
    typeTo(androidx.appcompat.R.id.search_src_text, user2.getUsername());
    pressImeActionButton();

    sleep(WAIT_TIME);

    assertDisplayedAtPosition(R.id.userSearch_recyclerView, 0, R.id.userAdapter_userName, user2.getUsername());
    assertDisplayedAtPosition(R.id.userSearch_recyclerView, 0, R.id.userAdapter_followButton, R.string.searchUser_follow);
    clickListItemChild(R.id.userSearch_recyclerView, 0, R.id.userAdapter_followButton);

    sleep(WAIT_TIME);

    clickListItem(R.id.userSearch_recyclerView, 0);

    sleep(WAIT_TIME);

    assertDisplayed(R.id.profile_header);
    assertNotDisplayed(R.id.signout_button);
  }

  @Test
  public void testTrendingBooks() {
    clickOn(R.id.search_category_books);

    sleep(15 * WAIT_TIME);

    assertDisplayed(R.id.bookTrending_recyclerView);
    assertNotDisplayed(R.id.userSearch_recyclerView);
    assertNotDisplayed(R.id.bookSearch_recyclerView);
    assertNotDisplayed(R.id.movieSearch_recyclerView);
    assertNotDisplayed(R.id.movieTrending_recyclerView);

    assertRecyclerViewItemCount(R.id.bookTrending_recyclerView, 100);
  }

  @Test
  public void testSearchBooks() {
    clickOn(R.id.search_category_books);

    clickOn(androidx.appcompat.R.id.search_button);
    typeTo(androidx.appcompat.R.id.search_src_text, "Harry Potter");
    pressImeActionButton();

    sleep(15 * WAIT_TIME);

    assertNotDisplayed(R.id.bookTrending_recyclerView);
    assertNotDisplayed(R.id.userSearch_recyclerView);
    assertDisplayed(R.id.bookSearch_recyclerView);
    assertNotDisplayed(R.id.movieSearch_recyclerView);
    assertNotDisplayed(R.id.movieTrending_recyclerView);

    assertContains("Harry Potter and the Deathly Hallows");
  }

  @Test
  public void testDisplayTrendingWhenEmptyQuery() {
    clickOn(R.id.search_category_books);

    clickOn(androidx.appcompat.R.id.search_button);
    typeTo(androidx.appcompat.R.id.search_src_text, "Harry Potter");
    pressImeActionButton();

    assertNotDisplayed(R.id.bookTrending_recyclerView);
    assertNotDisplayed(R.id.userSearch_recyclerView);
    assertDisplayed(R.id.bookSearch_recyclerView);
    assertNotDisplayed(R.id.movieSearch_recyclerView);
    assertNotDisplayed(R.id.movieTrending_recyclerView);

    sleep(WAIT_TIME);

    clickOn(androidx.appcompat.R.id.search_close_btn);

    sleep(WAIT_TIME);

    assertDisplayed(R.id.bookTrending_recyclerView);
    assertNotDisplayed(R.id.userSearch_recyclerView);
    assertNotDisplayed(R.id.bookSearch_recyclerView);
    assertNotDisplayed(R.id.movieSearch_recyclerView);
    assertNotDisplayed(R.id.movieTrending_recyclerView);
  }

//  @Test
//  public void testMovieSearchWithEmptyString() {
//    clickOn(R.id.search_category_movie);
//    clickOn(androidx.appcompat.R.id.search_button);
//    typeTo(androidx.appcompat.R.id.search_src_text, "Potter");
//
//    sleep(WAIT_TIME);
//
//    clearText(androidx.appcompat.R.id.search_src_text);
//    pressImeActionButton();
//
//    sleep(WAIT_TIME);
//
//    assertRecyclerViewItemCount(R.id.userSearch_recyclerView, 0);
//  }
//
//  @Test
//  public void testMovieSearchWithUnknownMovie() {
//    clickOn(R.id.search_category_movie);
//    clickOn(androidx.appcompat.R.id.search_button);
//    typeTo(androidx.appcompat.R.id.search_src_text, "jadvbipehsjdb");
//    pressImeActionButton();
//
//    sleep(WAIT_TIME);
//
//    assertRecyclerViewItemCount(R.id.userSearch_recyclerView, 0);
//  }
//
//  @Test
//  public void testMovieSearchWithKnownMovie() {
//    clickOn(R.id.search_category_movie);
//    clickOn(androidx.appcompat.R.id.search_button);
//    typeTo(androidx.appcompat.R.id.search_src_text, "Harry Potter and the half blood prince");
//
//    sleep(WAIT_TIME);
//    assertDisplayedAtPosition(R.id.userSearch_recyclerView, 0, R.id.text_title, "Harry Potter and the Half-Blood Prince");
//  }
//
//  @Test
//  public void testClickOnMovieSearchResultOpensRatingScreen() {
//    clickOn(R.id.search_category_movie);
//    clickOn(androidx.appcompat.R.id.search_button);
//    typeTo(androidx.appcompat.R.id.search_src_text, "Harry Potter and the half blood prince");
//
//    sleep(WAIT_TIME);
//    assertDisplayedAtPosition(R.id.userSearch_recyclerView, 0, R.id.text_title, "Harry Potter and the Half-Blood Prince");
//
//    onView(withId(R.id.searchactivity_recyclerView))
//        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
//
//    sleep(WAIT_TIME);
//    assertDisplayed(R.id.item_description_text);
//  }

}


