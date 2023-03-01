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

import androidx.test.espresso.ViewInteraction;
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
        email_view().perform(replaceText("email_test"));
        phone_view().perform(replaceText("12345"));
        set_view().perform(click());
        //Empty fields
        email_view().perform(replaceText(""));
        phone_view().perform(replaceText(""));
        //Get
        phone_view().perform(replaceText("12345"));
        get_view().perform(click());
        //Check
        email_view().check(
          matches(
                  withText("email_test")
          )
        );
        //Empty database
        database.getReference().setValue(null);
    }

    private ViewInteraction email_view (){return onView(withId(R.id.email_text));}
    private ViewInteraction phone_view (){return onView(withId(R.id.phone_text));}
    private ViewInteraction get_view (){return onView(withId(R.id.get_button));}
    private ViewInteraction set_view (){return onView(withId(R.id.set_button));}
}
