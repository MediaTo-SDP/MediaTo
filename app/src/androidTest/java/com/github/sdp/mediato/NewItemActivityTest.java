package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.github.sdp.mediato.NewItemActivity.MAX_REVIEW_LENGTH;

import android.view.View;
import android.widget.SeekBar;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

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


    @Before
    public void setUp() {
        init();
    }

    // Check if indicator text is well displayed when using the slide bar
    @Test
    public void checkSlideBarAndIndicator() {

        ViewInteraction seekBar = onView(withId(R.id.item_rating_slider));
        ViewInteraction seekBarIndicator = onView(withId(R.id.item_rating_slider_progress));

        seekBar.perform(setProgress(5));
        seekBarIndicator.check(matches(withText("5")));
    }

    @Test
    public void checkToastDisplayedWhenAddingACorrectLengthComment() {
        ViewInteraction addButton = onView(withId(R.id.item_add_button));
        addButton.perform(click());
        // TODO: add tests when functionalities are implemented

    }

    @Test
    public void checkErrorMessageWhenAddingAIncorrectLengthComment() {
        ViewInteraction addButton = onView(withId(R.id.item_add_button));
        ViewInteraction editText = onView(withId(R.id.item_review_edittext));

        editText.perform(typeText("A".repeat(MAX_REVIEW_LENGTH + 1)), closeSoftKeyboard());
        addButton.perform(click());

        onView(withId(R.id.new_item_review_error_msg))
                .check(matches(withText(
                        String.format(Locale.ENGLISH, "Exceeded character limit: %d", MAX_REVIEW_LENGTH))));
    }

    // After the error message is displayed, it should disappears when user edits the comment to make it shorter
    // It reappears if the length is still to long when adding the review
    @Test
    public void checkErrorMessageDisappearsWhenEditing() {
        ViewInteraction addButton = onView(withId(R.id.item_add_button));
        ViewInteraction editText = onView(withId(R.id.item_review_edittext));

        editText.perform(typeText("A".repeat(MAX_REVIEW_LENGTH + 1)), closeSoftKeyboard());
        addButton.perform(click());
        editText.perform(click(), closeSoftKeyboard());

        onView(withId(R.id.new_item_review_error_msg))
                .check(matches(withText("")));
    }

    @After
    public void releaseIntents() {
        release();
    }
}


