package com.github.sgueissa.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FragmentsTest {

    @Test
    public void testFragmentContent() {
     /*   // Launch the fragment in a container using FragmentScenario
        FragmentScenario<HomeFragment> scenario = FragmentScenario.launch(HomeFragment.class);
            // Use onView() to interact with the fragment's UI elements and verify the content
        onView(withId(R.id.home_text)).check(matches(withText("Home Fragment")));*/

    }

}
