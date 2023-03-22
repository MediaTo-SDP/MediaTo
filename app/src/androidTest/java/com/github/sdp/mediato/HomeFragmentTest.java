package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.github.sdp.mediato.ui.HomeFragment;
import com.github.sdp.mediato.ui.TestingActivity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class HomeFragmentTest {

  @Before
  public void setUp() {
    // Launch the TestingActivity
    ActivityScenario<TestingActivity> scenario = ActivityScenario.launch(TestingActivity.class);

    // Set up the TestingActivity to display the HomeFragment
    scenario.onActivity(activity -> {
      FragmentManager fragmentManager = activity.getSupportFragmentManager();
      fragmentManager.beginTransaction().replace(R.id.fragment_container, new HomeFragment())
          .commitAllowingStateLoss();
    });
  }

  // Test whether the home text is displayed and contains the correct text
  @Test
  public void testHomeFragmentTextView() {
    ViewInteraction homeText = onView(withId(R.id.text_home));
    homeText.check(matches(isDisplayed()));
    homeText.check(matches(withText("Home")));
  }

}



