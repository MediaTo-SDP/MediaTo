package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class HomeFragmentTest {

  @Before
  public void setUp() {
    ActivityScenario<TestingActivity> scenario = ActivityScenario.launch(TestingActivity.class);
    scenario.onActivity(activity -> {
      FragmentManager fragmentManager = activity.getSupportFragmentManager();
      fragmentManager.beginTransaction().replace(R.id.fragment_container, new HomeFragment())
          .commitAllowingStateLoss();
    });
  }

  @Test
  public void testHomeFragmentTextView() {
    onView(withId(R.id.text_home)).check(matches(withText("Home")));
  }

}



