package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.github.sdp.mediato.NewItemFragment.MAX_REVIEW_LENGTH;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.utility.SampleReviews;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

/**
 * Test class for new item activity, used to add ratings and comments
 */
@RunWith(AndroidJUnit4.class)
public class NewItemFragmentTest {

    ActivityScenario<MainActivity> scenario;
    Review review;

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

        // Launch the TestingActivity
        Intent testingIntent = new Intent(ApplicationProvider.getApplicationContext(),
                MainActivity.class);
        SampleReviews samples = new SampleReviews();
        review = samples.getMovieReview();
        testingIntent.putExtra("username", review.getUsername());
        scenario = ActivityScenario.launch(testingIntent);

        // Set up the TestingActivity to display the ProfileFragment
        scenario.onActivity(activity -> {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            NewItemFragment newItemFragment = new NewItemFragment();

            // Pass the username to the fragment like at profile creation
            Bundle bundle = new Bundle();

            bundle.putSerializable("media", review.getMedia());
            newItemFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.main_container, newItemFragment)
                    .commitAllowingStateLoss();
        });
    }

    // check if all the film info is displayed
    @Test
    public void checkFragmentIsLaunchedWithCorrectInfo() {
        onView(withId(R.id.item_title)).check(matches(withText(review.getMedia().getTitle())));
        onView(withId(R.id.item_description_text)).check(matches(withText(review.getMedia().getSummary())));
    }


    // Check if indicator text is well displayed when using the slide bar
    @Test
    public void checkSlideBarAndIndicator() {

        ViewInteraction seekBar = onView(withId(R.id.item_rating_slider));
        ViewInteraction seekBarIndicator = onView(withId(R.id.item_rating_slider_progress));

        seekBar.perform(setProgress(5));
        seekBarIndicator.check(matches(withText("5")));
    }

    // Test that no error message is displayed when review is correct
    @Test
    public void checkNoErrorsWhenAddingACorrectLengthComment() {

        ViewInteraction seekBar = onView(withId(R.id.item_rating_slider));
        seekBar.perform(setProgress(review.getGrade()));

        ViewInteraction editText = onView(withId(R.id.item_review_edittext));
        String comment = review.getComment();
        editText.perform(typeText(
                comment.length() >= MAX_REVIEW_LENGTH ? comment.substring(0, MAX_REVIEW_LENGTH - 1) : comment));
        editText.perform(closeSoftKeyboard());

        onView(withId(R.id.item_button_add)).perform(click());

        onView(withId(R.id.main_container))
                .check(matches(isDisplayed()))
                .check(matches(hasDescendant(withId(R.id.profile_header))));

        intended(hasExtra("review", review));

    }

    // Test that error message is displayed after writing a comment exceeding MAX_REVIEW_LENGTH
    @Test
    public void checkErrorMessageWhenAddingAIncorrectLengthComment() {
        ViewInteraction editText = onView(withId(R.id.item_review_edittext));

        editText.perform(typeText("a".repeat(MAX_REVIEW_LENGTH + 1)));
        editText.perform(closeSoftKeyboard());

        onView(withId(R.id.item_button_add)).perform(click());
        //activity.addItem();

        onView(withId(R.id.new_item_review_error_msg))
                .check(matches(withText(
                        String.format(Locale.ENGLISH, "Exceeded character limit: %d", MAX_REVIEW_LENGTH))));
    }

    // After the error message is displayed, it should disappears when user edits the comment to make it shorter
    // It reappears if the length is still to long when adding the review
    @Test
    public void checkErrorMessageDisappearsWhenEditing() {
        ViewInteraction editText = onView(withId(R.id.item_review_edittext));

        editText.perform(typeText("a".repeat(MAX_REVIEW_LENGTH + 1)));
        editText.perform(closeSoftKeyboard());

        onView(withId(R.id.item_button_add)).perform(click());
        //activity.addItem();

        editText.perform(click(), closeSoftKeyboard());

        onView(withId(R.id.new_item_review_error_msg))
                .check(matches(withText("")));
    }

    @After
    public void after() {
        release();
    }

}

