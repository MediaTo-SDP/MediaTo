package com.github.sgueissa.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;

import androidx.core.view.GravityCompat;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void openDrawer() {
        // Open the drawer by swiping from the left edge of the screen
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open(GravityCompat.START));
        onView(withId(R.id.nav_view)).check(matches(isDisplayed()));
    }

    @Test
    public void closeDrawer() {
        // Close the drawer by swiping from the right edge of the screen
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close(GravityCompat.END));
        onView(withId(R.id.nav_view)).check(matches(not(isDisplayed())));
    }

    @Test
    public void selectHome() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open(GravityCompat.START));
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_home));
        onView(withId(R.id.fragment_container)).check(matches(hasDescendant(withText("Home Fragment"))));
    }

    @Test
    public void selectSettings() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open(GravityCompat.START));
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_settings));
        onView(withId(R.id.fragment_container)).check(matches(hasDescendant(withText("Settings Fragment"))));
    }

    @Test
    public void selectShare() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open(GravityCompat.START));
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_share));
        onView(withId(R.id.fragment_container)).check(matches(hasDescendant(withText("Share Fragment"))));
    }

    @Test
    public void selectAbout() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open(GravityCompat.START));
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_about));
        onView(withId(R.id.fragment_container)).check(matches(hasDescendant(withText("About Fragment"))));
    }
}