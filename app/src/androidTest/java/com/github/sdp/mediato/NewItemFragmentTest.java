package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn;
import static com.adevinta.android.barista.interaction.BaristaEditTextInteractions.typeTo;
import static com.adevinta.android.barista.interaction.BaristaKeyboardInteractions.closeKeyboard;
import static com.github.sdp.mediato.ui.NewItemFragment.MAX_REVIEW_LENGTH;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.SeekBar;

import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;

import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.Movie;
import com.github.sdp.mediato.ui.NewItemFragment;

import org.hamcrest.Description;
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
    final UiDevice device = UiDevice.getInstance(getInstrumentation());
    ViewInteraction addItemButton = onView(withId(R.id.item_button_add));
    ViewInteraction seekBar = onView(withId(R.id.item_rating_slider));
    ViewInteraction editText = onView(withId(R.id.item_review_edittext));
    ViewInteraction seekBarIndicator = onView(withId(R.id.item_rating_slider_progress));
    NewItemFragment newItemFragment = new NewItemFragment();

    ActivityScenario<MainActivity> scenario;
    Review review;

    Activity activity;

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

    public static Matcher<View> withUrl(final String url) {
        return new BoundedMatcher<>(WebView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with url: " + url);
            }

            @Override
            protected boolean matchesSafely(WebView webView) {
                return webView.getUrl().equals(url);
            }
        };
    }

    @Before
    public void setUp() {
        init();

        // Launch the TestingActivity
        Intent testingIntent = new Intent(ApplicationProvider.getApplicationContext(),
                MainActivity.class);

        Media movie1 = new Movie("The Godfather",
                "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.",
                "https://image.tmdb.org/t/p/original/3bhkrj58Vtu7enYsRolD1fZdja1.jpg", 1);
        review = new Review("Alice", movie1, 8, "One of the best movies I've ever seen.");

        testingIntent.putExtra("username", review.getUsername());
        scenario = ActivityScenario.launch(testingIntent);

        // Set up the TestingActivity to display the ProfileFragment
        scenario.onActivity(a -> {
            activity = a;
            FragmentManager fragmentManager = a.getSupportFragmentManager();
            newItemFragment = new NewItemFragment();

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
        seekBar.perform(setProgress(5));
        seekBarIndicator.check(matches(withText("5")));
    }

    // Test that no error message is displayed when review is correct
    @Test
    public void checkNoErrorsWhenAddingACorrectLengthComment() {
        seekBar.perform(setProgress(review.getGrade()));

        String comment = review.getComment();
        editText.perform(typeText(
                comment.length() >= MAX_REVIEW_LENGTH ? comment.substring(0, MAX_REVIEW_LENGTH - 1) : comment));
        editText.perform(closeSoftKeyboard());

        addItemButton.perform(click());

        onView(withId(R.id.main_container))
                .check(matches(isDisplayed()))
                .check(matches(hasDescendant(withId(R.id.profile_header))));
    }

    @After
    public void after() {
        release();
    }

}


