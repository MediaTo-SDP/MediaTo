package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.github.sdp.mediato.ui.CreateProfileFragment;
import com.github.sdp.mediato.ui.MainActivity;
import com.google.android.material.textfield.TextInputLayout;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
    // Launch the MainActivity
    ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

    // Set up the MainActivity to display the HomeFragment
    scenario.onActivity(activity -> {
      FragmentManager fragmentManager = activity.getSupportFragmentManager();
      fragmentManager.beginTransaction()
          .replace(R.id.main_container, new CreateProfileFragment("uid", "email@test.com"))
          .commitAllowingStateLoss();
    });
  }

  @Test
  public void testCreateProfileBar() {
    ViewInteraction createProfileBar = onView(withId(R.id.create_profile_bar));
    createProfileBar.check(matches(isDisplayed()));
  }

  @Test
  public void testProfileImage() {
    ViewInteraction profileImage = onView(withId(R.id.profile_image));
    profileImage.check(matches(isDisplayed()));
  }

  @Test
  public void testProfileImageAddButton() {
    ViewInteraction profileImageAddButton = onView(withId(R.id.profile_image_add_button));
    profileImageAddButton.check(matches(isDisplayed()));
  }

  @Test
  public void testUsernameTextInput() {
    ViewInteraction usernameTextInput = onView(withId(R.id.username_text_input));
    usernameTextInput.check(matches(isDisplayed()));

    // Check Hint
    usernameTextInput.check(matches(hasText(
        context.getString(R.string.mt_username_hint),
        (View view) -> {
          return ((TextInputLayout) view).getHint();
        })));

    // Check Helper Text
    usernameTextInput.check(matches(hasText(
        context.getString(R.string.mt_username_helper_text),
        (View view) -> {
          return ((TextInputLayout) view).getHelperText();
        })));
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
        (View view) -> {
          return ((TextInputLayout) view).getError();
        })));
  }

  @Test
  public void testUsernameTextInputRemoveErrorWhenUsernameValid() {
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
        (View view) -> {
          return ((TextInputLayout) view).getError();
        })));

    usernameEditText.perform(new ClickDrawableAction(ClickDrawableAction.Right));

    // Check Helper Text
    usernameTextInput.check(matches(hasText(
        context.getString(R.string.mt_username_helper_text),
        (View view) -> {
          return ((TextInputLayout) view).getHelperText();
        })));
  }

  private interface viewFunction {

    CharSequence getText(View view);
  }

  private static Matcher<View> hasText(final String expectedErrorText, viewFunction fct) {
    return new TypeSafeMatcher<View>() {

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

  public static class ClickDrawableAction implements ViewAction {

    public static final int Left = 0;
    public static final int Top = 1;
    public static final int Right = 2;
    public static final int Bottom = 3;

    @Location
    private final int drawableLocation;

    public ClickDrawableAction(@Location int drawableLocation) {
      this.drawableLocation = drawableLocation;
    }

    @Override
    public Matcher<View> getConstraints() {
      return allOf(isAssignableFrom(TextView.class),
          new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            protected boolean matchesSafely(final TextView tv) {
              //get focus so drawables are visible and if the textview has a drawable in the position then return a match
              return tv.requestFocusFromTouch()
                  && tv.getCompoundDrawables()[drawableLocation] != null;

            }

            @Override
            public void describeTo(Description description) {
              description.appendText("has drawable");
            }
          });
    }

    @Override
    public String getDescription() {
      return "click drawable ";
    }

    @Override
    public void perform(final UiController uiController, final View view) {
      TextView tv = (TextView) view;//we matched
      if (tv != null && tv.requestFocusFromTouch())//get focus so drawables are visible
      {
        //get the bounds of the drawable image
        Rect drawableBounds = tv.getCompoundDrawables()[drawableLocation].getBounds();

        //calculate the drawable click location for left, top, right, bottom
        final Point[] clickPoint = new Point[4];
        clickPoint[Left] = new Point(tv.getLeft() + (drawableBounds.width() / 2),
            (int) (tv.getPivotY() + (drawableBounds.height() / 2)));
        clickPoint[Top] = new Point((int) (tv.getPivotX() + (drawableBounds.width() / 2)),
            tv.getTop() + (drawableBounds.height() / 2));
        clickPoint[Right] = new Point(tv.getRight() + (drawableBounds.width() / 2),
            (int) (tv.getPivotY() + (drawableBounds.height() / 2)));
        clickPoint[Bottom] = new Point((int) (tv.getPivotX() + (drawableBounds.width() / 2)),
            tv.getBottom() + (drawableBounds.height() / 2));

        if (tv.dispatchTouchEvent(MotionEvent.obtain(android.os.SystemClock.uptimeMillis(),
            android.os.SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN,
            clickPoint[drawableLocation].x, clickPoint[drawableLocation].y, 0))) {
          tv.dispatchTouchEvent(MotionEvent.obtain(android.os.SystemClock.uptimeMillis(),
              android.os.SystemClock.uptimeMillis(), MotionEvent.ACTION_UP,
              clickPoint[drawableLocation].x, clickPoint[drawableLocation].y, 0));
        }
      }
    }

    @IntDef({Left, Top, Right, Bottom})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Location {

    }
  }

}