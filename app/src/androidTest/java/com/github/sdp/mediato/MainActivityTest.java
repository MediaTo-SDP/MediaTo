package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.*;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.AllOf.allOf;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * Initialises intents
     */
    @Before
    public void initIntents(){
        init();
    }

    @Test
    public void CheckMainGoButton() {

        ViewInteraction mainName = onView(withId(R.id.mainName));
        ViewInteraction mainGoButton = onView(withId(R.id.mainGoButton));

        mainName.perform(typeText("Michel"), closeSoftKeyboard());
        mainGoButton.perform(click());

        intended(allOf(
                hasExtra("mainName", "Michel"),
                hasComponent(GreetingActivity.class.getName())
        ));
    }

    /**
     * Releases the intents
     */
    @After
    public void releaseIntents() {release();}
}