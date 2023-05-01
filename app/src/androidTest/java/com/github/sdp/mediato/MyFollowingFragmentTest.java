package com.github.sdp.mediato;

import static com.adevinta.android.barista.assertion.BaristaRecyclerViewAssertions.assertRecyclerViewItemCount;
import static com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.adevinta.android.barista.interaction.BaristaListInteractions.clickListItemChild;
import static com.adevinta.android.barista.interaction.BaristaSleepInteractions.sleep;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.sdp.mediato.DatabaseTests.DataBaseTestUtil;
import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.ui.MyFollowingFragment;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class MyFollowingFragmentTest {
  private final static int STANDARD_USER_TIMEOUT = 10;
  private final static int WAIT_TIME = 1000;
  User user1;
  User user2;
  User user3;

  @Before
  public void setUp() throws ExecutionException, InterruptedException, TimeoutException
  {
    try {
      DataBaseTestUtil.useEmulator();
    } catch (Exception ignored) {
    }
    //Create new sample users
    user1 = new User.UserBuilder("uniqueId1")
            .setUsername("user_test_1_MyFollowingFragmentTest")
            .setEmail("email_test_1")
            .setRegisterDate("09/03/2023")
            .setLocation(new Location(3.14, 3.14))
            .build();
    user2 = new User.UserBuilder("uniqueId2")
            .setUsername("user_test_2_MyFollowingFragmentTest")
            .setEmail("email_test_2")
            .setRegisterDate("19/03/2023")
            .setLocation(new Location(3.14, 3.14))
            .build();
    user3 = new User.UserBuilder("uniqueId3")
            .setUsername("user_test_3_MyFollowingFragmentTest")
            .setEmail("email_test_3")
            .setRegisterDate("19/03/2023")
            .setLocation(new Location(3.14, 3.14))
            .build();

    UserDatabase.addUser(user1).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
    UserDatabase.addUser(user2).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
    UserDatabase.addUser(user3).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);

    // Launch the MainActivity
    ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

    // Set up the MainActivity to display the SearchFragment
    scenario.onActivity(activity -> {
      FragmentManager fragmentManager = activity.getSupportFragmentManager();
      MyFollowingFragment myFollowingFragment = new MyFollowingFragment();
      activity.getMyProfileViewModel().setUsername(user1.getUsername());

      // Pass the username to the fragment like at profile creation
      Bundle bundle = new Bundle();
      bundle.putString("username", user1.getUsername());
      myFollowingFragment.setArguments(bundle);
      fragmentManager.beginTransaction().replace(R.id.main_container, myFollowingFragment)
              .commitAllowingStateLoss();
    });
  }

  @AfterClass
  public static void cleanDatabase() {
    DataBaseTestUtil.cleanDatabase();
  }

  @Test
  public void testRecyclerViewWithNoneFollowing() {
    assertRecyclerViewItemCount(R.id.myFollowing_recyclerView, 0);
  }

  @Test
  public void testRecyclerViewWithTwoFollowings() {
    UserDatabase.followUser(user1.getUsername(), user2.getUsername());
    UserDatabase.followUser(user1.getUsername(), user3.getUsername());

    sleep(WAIT_TIME);

    assertRecyclerViewItemCount(R.id.myFollowing_recyclerView, 2);
    assertDisplayed(user2.getUsername());
    assertDisplayed(user3.getUsername());
  }


  @Test
  public void testOneUnfollowWithTwoFollowings() {
    UserDatabase.followUser(user1.getUsername(), user2.getUsername());
    UserDatabase.followUser(user1.getUsername(), user3.getUsername());

    sleep(WAIT_TIME);

    assertRecyclerViewItemCount(R.id.myFollowing_recyclerView, 2);
    assertDisplayed(user2.getUsername());
    assertDisplayed(user3.getUsername());

    clickListItemChild(R.id.myFollowing_recyclerView, 0, R.id.userAdapter_unfollowButton);

    sleep(WAIT_TIME);

    assertRecyclerViewItemCount(R.id.myFollowing_recyclerView, 1);
  }
}


