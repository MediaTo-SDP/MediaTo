package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.github.sdp.mediato.ui.MainActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

  @Rule
  public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<MainActivity>(
      MainActivity.class);

  // Test that selecting Home on the bottom bar displays the HomeFragment
  @Test
  public void testSelectHome() {
    onView(withId(R.id.home)).perform(click());
    onView(withId(R.id.main_container))
        .check(matches(isDisplayed()))
        .check(matches(hasDescendant(withText("Home"))));
  }

  // Test that selecting Search on the bottom bar displays the SearchFragment
  @Test
  public void testSelectSearch() {
    onView(withId(R.id.search)).perform(click());
    onView(withId(R.id.main_container))
        .check(matches(isDisplayed()))
        .check(matches(hasDescendant(withText("Search"))));
  }

  // Test that selecting Profile on the bottom bar displays the ProfileFragment
  @Test
  public void testSelectProfile() {
    onView(withId(R.id.profile)).perform(click());
    onView(withId(R.id.main_container))
        .check(matches(isDisplayed()))
        .check(matches(hasDescendant(withId(R.id.profile_header))));
  }
}






