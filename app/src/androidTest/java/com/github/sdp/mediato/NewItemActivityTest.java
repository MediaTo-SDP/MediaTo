package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;
import android.widget.SeekBar;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for new item activity, used to add ratings and comments
 */
@RunWith(AndroidJUnit4.class)
public class NewItemActivityTest {

    @Rule
    public ActivityScenarioRule<NewItemActivity> testRule = new ActivityScenarioRule<>(NewItemActivity.class);

    // Changes the slide bar value to test it
    private static ViewAction setProgress(final int progress) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                ((SeekBar) view).setProgress(progress);
            }

            @Override
            public String getDescription() {
                return "Set a progress on a SlideBar";
            }

            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }
        };
    }

    // Check if indicator text is well displayed when using the slide bar
    @Test
    public void checkSlideBarAndIndicator() {
        init();

        ViewInteraction seekBar = onView(withId(R.id.item_rating_slider));
        ViewInteraction seekBarIndicator = onView(withId(R.id.item_rating_slider_progress));

        seekBar.perform(setProgress(5));
        seekBarIndicator.check(matches(withText("5")));

    }

}
