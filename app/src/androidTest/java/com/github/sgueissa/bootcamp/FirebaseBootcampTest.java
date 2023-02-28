package com.github.sgueissa.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.database.FirebaseDatabase;

@RunWith(AndroidJUnit4.class)
public class FirebaseBootcampTest {

    @Rule
    public ActivityScenarioRule<FirebaseBootcamp> testRule = new ActivityScenarioRule<>(FirebaseBootcamp.class);

    @Test
    public void setAndGet(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        try {
            database.useEmulator("10.0.2.2", 9000);
        } catch (Exception e) {

        }
        //Set
        onView(withId(R.id.email_text)).perform(replaceText("email_test"));
        onView(withId(R.id.phone_text)).perform(replaceText("12345"));
        onView(withId(R.id.set_button)).perform(click());
        //Empty fields
        onView(withId(R.id.email_text)).perform(replaceText(""));
        onView(withId(R.id.phone_text)).perform(replaceText(""));
        //Get
        onView(withId(R.id.phone_text)).perform(replaceText("12345"));
        onView(withId(R.id.get_button)).perform(click());
        //Check
        onView(withId(R.id.email_text)).check(
          matches(
                  withText("email_test")
          )
        );
        //Empty database
        database.getReference().setValue(null);
    }
}
