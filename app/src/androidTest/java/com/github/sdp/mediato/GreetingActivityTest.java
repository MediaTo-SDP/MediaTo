package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.github.sdp.mediato.ui.GreetingActivity;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GreetingActivityTest {

  @Test
  public void CheckGreetingMessage() {
    Intent myIntent = new Intent(ApplicationProvider.getApplicationContext(),
        GreetingActivity.class);
    myIntent.putExtra("mainName", "Michel");

    try (ActivityScenario test = ActivityScenario.launch(myIntent)) {
      onView(withId(R.id.greetingMessage)).check(matches(withText("Hello Michel!")));
    }
  }

}
