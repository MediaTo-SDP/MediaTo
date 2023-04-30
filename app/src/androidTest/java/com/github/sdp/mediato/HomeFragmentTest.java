package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.adevinta.android.barista.assertion.BaristaRecyclerViewAssertions.assertRecyclerViewItemCount;
import static com.adevinta.android.barista.interaction.BaristaSleepInteractions.sleep;

import androidx.fragment.app.FragmentManager;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.javafaker.App;
import com.github.sdp.mediato.cache.AppCache;
import com.github.sdp.mediato.ui.HomeFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class HomeFragmentTest {
  private final static int MOVIE_PER_CLICK = 20;
  private final static int BOOK_PER_CLICK = 40;
  private final static int WAIT_TIME = 1000;


  ActivityScenario<MainActivity> scenario;
  ViewInteraction trendingMovieButton = onView(withId(R.id.movie_trending));
  ViewInteraction trendingBookButton = onView(withId(R.id.books_trending));
  ViewInteraction trendingItems = onView(withId(R.id.trending_items));
  AppCache globalCache;

  @Before
  public void setUp() {
    // Launch the MainActivity
    ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);


    // Set up the MainActivity to display the HomeFragment
    scenario.onActivity(activity -> {
      globalCache = Room.databaseBuilder(activity.getApplicationContext(), AppCache.class, "global-cache")
              .build();
      FragmentManager fragmentManager = activity.getSupportFragmentManager();
      fragmentManager.beginTransaction().replace(R.id.main_container, new HomeFragment(globalCache))
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

  @Test
  public void testHomeFragmentContainsCorrectButtons() {
    trendingMovieButton.check(matches(isDisplayed()));
    trendingBookButton.check(matches(isDisplayed()));
    trendingBookButton.check(matches(withText(R.string.books)));
    trendingMovieButton.check(matches(withText(R.string.search_category_movies)));

  }

  @Test
  public void correctMovieCountAfterClick(){
    sleep(WAIT_TIME);
    assertRecyclerViewItemCount(R.id.trending_items, MOVIE_PER_CLICK);
    trendingMovieButton.perform(click());
    sleep(WAIT_TIME);
    assertRecyclerViewItemCount(R.id.trending_items, 2*MOVIE_PER_CLICK);
  }

  @Test
  public void correctBookCountAfterClick(){
    sleep(WAIT_TIME);
    trendingBookButton.perform(click());
    sleep(WAIT_TIME);
    //assertRecyclerViewItemCount(R.id.trending_items, BOOK_PER_CLICK);
    trendingBookButton.perform(click());
    sleep(WAIT_TIME);
    assertRecyclerViewItemCount(R.id.trending_items, 2*BOOK_PER_CLICK);
  }
}



