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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SearchFragmentTest {

  @Before
  public void setUp() {
    // Launch the TestingActivity
    ActivityScenario<TestingActivity> scenario = ActivityScenario.launch(TestingActivity.class);

    // Set up the TestingActivity to display the SearchFragment
    scenario.onActivity(activity -> {
      FragmentManager fragmentManager = activity.getSupportFragmentManager();
      fragmentManager.beginTransaction().replace(R.id.fragment_container, new SearchFragment())
          .commitAllowingStateLoss();
    });
  }

  // Test whether the search text is displayed and contains the correct text
  @Test
  public void testSearchFragmentTextView() {
    ViewInteraction searchText = onView(withId(R.id.text_search));
    searchText.check(matches(isDisplayed()));
    searchText.check(matches(withText("Search")));
  }

}


