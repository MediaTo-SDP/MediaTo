package com.github.sgueissa.bootcamp;

import static androidx.fragment.app.testing.FragmentScenario.launchInContainer;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FragmentsTest {

    @Test
    public void testHomeFragmentContent() {
        FragmentScenario<HomeFragment> scenario = launchInContainer(HomeFragment.class);
        onView(withId(R.id.home_text)).check(matches(withText("Home Fragment")));
    }

    @Test
    public void testSettingsFragmentContent() {
        FragmentScenario<SettingsFragment> scenario = launchInContainer(SettingsFragment.class);
        onView(withId(R.id.settings_text)).check(matches(withText("Settings Fragment")));
    }

    @Test
    public void testShareFragmentContent() {
        FragmentScenario<ShareFragment> scenario = launchInContainer(ShareFragment.class);
        onView(withId(R.id.share_text)).check(matches(withText("Share Fragment")));
    }

    @Test
    public void testAboutFragmentContent() {
        FragmentScenario<AboutFragment> scenario = launchInContainer(AboutFragment.class);
        onView(withId(R.id.about_text)).check(matches(withText("About Fragment")));
    }

}
