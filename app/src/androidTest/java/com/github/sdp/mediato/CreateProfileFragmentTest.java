package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.material.textfield.TextInputLayout;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CreateProfileFragmentTest {

    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @Before
    public void setUp() {
        // Launch the TestingActivity
        ActivityScenario<TestingActivity> scenario = ActivityScenario.launch(TestingActivity.class);

        // Set up the TestingActivity to display the HomeFragment
        scenario.onActivity(activity -> {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, new CreateProfileFragment())
                    .commitAllowingStateLoss();
        });
    }

    @Test
    public void testProfileImageAddButton() {
        ViewInteraction profileImageAddButton = onView(withId(R.id.profile_image_add_button));
        profileImageAddButton.check(matches(isDisplayed()));
        profileImageAddButton.perform(click());
    }

    @Test
    public void testUsernameTextInput() {
        ViewInteraction usernameTextInput = onView(withId(R.id.username_text_input));
        usernameTextInput.check(matches(isDisplayed()));

        // Check Hint
        usernameTextInput.check(matches(hasText(
                context.getString(R.string.mt_username_hint),
                (View view) -> ((TextInputLayout) view).getHint())));

        // Check Helper Text
        usernameTextInput.check(matches(hasText(
                context.getString(R.string.mt_username_helper_text),
                (View view) -> ((TextInputLayout) view).getHelperText())));
    }

    @Test
    public void testUsernameTextInputErrorTooShort() {
        ViewInteraction usernameTextInput = onView(withId(R.id.username_text_input));
        ViewInteraction usernameEditText = onView(withId(R.id.username_edit_text));
        ViewInteraction createProfileButton = onView(withId(R.id.create_profile_button));

        usernameTextInput.check(matches(isDisplayed()));
        usernameEditText.check(matches(isDisplayed()));
        createProfileButton.check(matches(isDisplayed()));

        createProfileButton.perform(click());

        // Check Error
        usernameTextInput.check(matches(hasText(
                context.getString(R.string.mt_username_error_too_short),
                (View view) -> ((TextInputLayout) view).getError())));
    }

    @Test
    public void testUsernameTextInputRemoveErrorWhenValidUsername() {
        ViewInteraction usernameTextInput = onView(withId(R.id.username_text_input));
        ViewInteraction usernameEditText = onView(withId(R.id.username_edit_text));
        ViewInteraction createProfileButton = onView(withId(R.id.create_profile_button));

        usernameTextInput.check(matches(isDisplayed()));
        usernameEditText.check(matches(isDisplayed()));
        createProfileButton.check(matches(isDisplayed()));

        createProfileButton.perform(click());

        // Check Error
        usernameTextInput.check(matches(hasText(
                context.getString(R.string.mt_username_error_too_short),
                (View view) -> ((TextInputLayout) view).getError())));

        usernameEditText.perform(typeText("Username"), closeSoftKeyboard());

        // Check Helper Text
        usernameTextInput.check(matches(hasText(
                context.getString(R.string.mt_username_helper_text),
                (View view) -> ((TextInputLayout) view).getHelperText())));
    }

    @Test
    public void testGenerateUsernameButton() {
        ViewInteraction usernameTextInput = onView(withId(R.id.username_text_input));
        ViewInteraction usernameEditText = onView(withId(R.id.username_edit_text));
        ViewInteraction createProfileButton = onView(withId(R.id.create_profile_button));
        ViewInteraction generateUsernameButton = onView(withContentDescription(R.string.mt_username_description_end_icon));

        usernameTextInput.check(matches(isDisplayed()));
        usernameEditText.check(matches(isDisplayed()));
        createProfileButton.check(matches(isDisplayed()));
        generateUsernameButton.check(matches(isDisplayed()));

        createProfileButton.perform(click());

        // Check Error
        usernameTextInput.check(matches(hasText(
                context.getString(R.string.mt_username_error_too_short),
                (View view) -> ((TextInputLayout) view).getError())));

        generateUsernameButton.perform(click());

        // Check Helper Text
        usernameTextInput.check(matches(hasText(
                context.getString(R.string.mt_username_helper_text),
                (View view) -> ((TextInputLayout) view).getHelperText())));
    }

    @Test
    public void testCreateValidProfile() {
        ViewInteraction usernameTextInput = onView(withId(R.id.username_text_input));
        ViewInteraction usernameEditText = onView(withId(R.id.username_edit_text));
        ViewInteraction createProfileButton = onView(withId(R.id.create_profile_button));
        ViewInteraction generateUsernameButton = onView(withContentDescription(R.string.mt_username_description_end_icon));

        usernameTextInput.check(matches(isDisplayed()));
        usernameEditText.check(matches(isDisplayed()));
        createProfileButton.check(matches(isDisplayed()));
        generateUsernameButton.check(matches(isDisplayed()));

        createProfileButton.perform(click());

        // Check Error
        usernameTextInput.check(matches(hasText(
                context.getString(R.string.mt_username_error_too_short),
                (View view) -> ((TextInputLayout) view).getError())));

        generateUsernameButton.perform(click());

        // Check Helper Text
        usernameTextInput.check(matches(hasText(
                context.getString(R.string.mt_username_helper_text),
                (View view) -> ((TextInputLayout) view).getHelperText())));

        createProfileButton.perform(click());
    }

    private interface viewFunction {

        CharSequence getText(View view);
    }

    private static Matcher<View> hasText(final String expectedErrorText, viewFunction fct) {
        return new TypeSafeMatcher<>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextInputLayout)) {
                    return false;
                }

                CharSequence error = fct.getText(view);

                if (error == null) {
                    return false;
                }

                String text = error.toString();

                return expectedErrorText.equals(text);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Expected " + expectedErrorText);
            }
        };
    }
}