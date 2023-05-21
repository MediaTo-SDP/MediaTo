package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.adevinta.android.barista.assertion.BaristaRecyclerViewAssertions.assertRecyclerViewItemCount;
import static com.adevinta.android.barista.interaction.BaristaListInteractions.clickListItemChild;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.not;

import android.os.Bundle;

import android.view.View;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.matcher.ViewMatchers.Visibility;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.sdp.mediato.DatabaseTests.DataBaseTestUtil;
import com.github.sdp.mediato.data.CollectionsDatabase;
import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.model.media.Collection;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.MediaType;
import com.github.sdp.mediato.ui.FeedFragment;

import com.github.sdp.mediato.ui.MainActivity;
import org.hamcrest.Matcher;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class FeedFragmentTest {
    private final static int STANDARD_USER_TIMEOUT = 10;
    private final static int WAIT_TIME = 3000;
    private User user1;
    private User user2;
    private User user3;
    private Collection collection1;
    private Collection collection2;
    private Collection collection3;
    private final static int MAX_COMMENT_LENGTH = 140;
    private final static String VALID_COMMENT = "valid comment";
    private final static String TOO_LONG_COMMENT = generateString(MAX_COMMENT_LENGTH + 10, 'a');

    ViewInteraction feedText = onView(withId(R.id.text_feed));

    @Before
    public void setUp() throws ExecutionException, InterruptedException, TimeoutException {
        try {
            DataBaseTestUtil.useEmulator();
        } catch (Exception ignored) {
        }

        //Setup test data
        createUsers();
        createReviews();
        addReviews();
        Thread.sleep(1000);
        UserDatabase.followUser(user1.getUsername(), user2.getUsername());
        Thread.sleep(1000);

        // Launch the MainActivity
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        // Set up the MainActivity to display the FeedFragment
        scenario.onActivity(activity -> {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FeedFragment feedFragment = new FeedFragment();

            // Pass the username to the fragment
            Bundle bundle = new Bundle();
            bundle.putString("username", user1.getUsername());
            bundle.putSerializable("feedType", FeedFragment.FeedType.FEED);
            feedFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.main_container, feedFragment)
                    .commitAllowingStateLoss();
        });
    }

    @AfterClass
    public static void cleanDatabase() {
        DataBaseTestUtil.cleanDatabase();
    }

    // Test whether the feed text is displayed and contains the correct text
    @Test
    public void testFeedFragmentTextView() {
        feedText.check(matches(isDisplayed()));
        feedText.check(matches(withText("Feed")));
    }

    // Test that all the reviews from the followed users are displayed
    // In this test, only user 2 is followed and they have 2 reviews
    @Test
    public void testItemCount() throws InterruptedException {
        Thread.sleep(WAIT_TIME);
        assertRecyclerViewItemCount(R.id.feed_posts, 2);
    }

    // Test that expanding the comment section by clicking on the comments tab in a review post works
    @Test
    public void testExpandingCommentSection() throws InterruptedException {
        Thread.sleep(WAIT_TIME);
        // Check that by default the comment section is not displayed
        onView(withId(R.id.feed_posts))
            .check(childOfElementAtPositionMatches(0, R.id.comment_section, withEffectiveVisibility(
                Visibility.GONE)));

        // Click on the expand arrow
        onView(withId(R.id.feed_posts)).perform(
            RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.comments_card)));

        // Check that the comment section is now displayed
        onView(withId(R.id.feed_posts))
            .check(childOfElementAtPositionMatches(0, R.id.comment_section, withEffectiveVisibility(
                Visibility.VISIBLE)));
    }

    // Test that entering a valid comment works and is displayed correctly
    @Test
    public void testAddValidComment() throws InterruptedException {
        Thread.sleep(WAIT_TIME);
        // Click on the expand comment section arrow
        onView(withId(R.id.feed_posts)).perform(
            RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.comments_card)));

        // Check that the comment text field is displayed
        onView(withId(R.id.feed_posts))
            .check(childOfElementAtPositionMatches(0, R.id.comment_text_field, withEffectiveVisibility(
                Visibility.VISIBLE)));

        // Type a valid comment into the text field
        onView(withId(R.id.feed_posts)).perform(
            RecyclerViewActions.actionOnItemAtPosition(0, actionOnChildViewWithId(R.id.comment_text_field, typeText(VALID_COMMENT))));

        // Hit Enter
        onView(withId(R.id.feed_posts)).perform(
            RecyclerViewActions.actionOnItemAtPosition(0, actionOnChildViewWithId(R.id.comment_text_field, pressImeActionButton())));
    }

    // Test that entering a too long comment only adds a comment with the allowed comment length
    @Test
    public void testCannotAddTooLongComment() throws InterruptedException {
        Thread.sleep(WAIT_TIME);
        // Click on the expand comment section arrow
        onView(withId(R.id.feed_posts)).perform(
            RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.comments_card)));

        // Type a too long comment into the text field
        onView(withId(R.id.feed_posts)).perform(
            RecyclerViewActions.actionOnItemAtPosition(0, actionOnChildViewWithId(R.id.comment_text_field, typeText(
                TOO_LONG_COMMENT))));

        // Hit Enter
        onView(withId(R.id.feed_posts)).perform(
            RecyclerViewActions.actionOnItemAtPosition(0, actionOnChildViewWithId(R.id.comment_text_field, pressImeActionButton())));
    }

    // Test that adding an empty comment does not work
    @Test
    public void testCannotAddEmptyComment() throws InterruptedException {
        Thread.sleep(WAIT_TIME);
        // Click on the expand comment section arrow
        onView(withId(R.id.feed_posts)).perform(
            RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.comments_card)));

        // Hit Enter
        onView(withId(R.id.feed_posts)).perform(
            RecyclerViewActions.actionOnItemAtPosition(0, actionOnChildViewWithId(R.id.comment_text_field, pressImeActionButton())));

        // Check that the comment was not added
        onView(withId(R.id.feed_posts)).check(matches(not(hasDescendant(withText(user1.getUsername())))));
    }

    /**
     * --------------Util functions--------------------
     */

    //Helper function that creates users and adds them to the database
    private void createUsers() throws ExecutionException, InterruptedException, TimeoutException {
        //Create new sample users
        user1 = new User.UserBuilder("uniqueId1")
                .setUsername("user_feed_test_1")
                .setEmail("email_test_1")
                .setRegisterDate("09/03/2023")
                .setLocation(new Location(3.14, 3.14))
                .build();
        user2 = new User.UserBuilder("uniqueId2")
                .setUsername("user_feed_test_2")
                .setEmail("email_test_2")
                .setRegisterDate("19/03/2023")
                .setLocation(new Location(3.14, 3.14))
                .build();
        user3 = new User.UserBuilder("uniqueId3")
                .setUsername("user_feed_test_3")
                .setEmail("email_test_3")
                .setRegisterDate("19/03/2023")
                .setLocation(new Location(3.14, 3.14))
                .build();
        UserDatabase.addUser(user1).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
        UserDatabase.addUser(user2).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
        UserDatabase.addUser(user3).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);

    }

    //Helper function that creates reviews and adds them to the collections
    private void createReviews() {
        Media media1 = new Media(MediaType.MOVIE, "Harry Potter 1", "In the closet", "validUrl", 123);
        Media media2 = new Media(MediaType.MOVIE, "Harry Potter 2", "In the WC", "validUrl", 1234);
        Media media3 = new Media(MediaType.MOVIE, "Harry Potter 3", "In the prison", "validUrl", 12345);

        Review review1 = new Review(user1.getUsername(), media1, 4, "Best movie in the world");
        Review review2 = new Review(user1.getUsername(), media2, 5, "Pretty bad");
        Review review3 = new Review(user1.getUsername(), media3, 6, "Really bad");

        Map<String, Review> collection1Reviews = new HashMap<>();
        collection1Reviews.put(review1.getMedia().getTitle(), review1);

        Map<String, Review> collection2Reviews = new HashMap<>();
        collection2Reviews.put(review2.getMedia().getTitle(), review2);

        Map<String, Review> collection3Reviews = new HashMap<>();
        collection3Reviews.put(review3.getMedia().getTitle(), review3);

        collection1 = new Collection("The best", collection1Reviews);
        collection2 = new Collection("The bad", collection2Reviews);
        collection3 = new Collection("The worst", collection3Reviews);
    }

    //Helper function that adds the reviews to the database
    private void addReviews() throws ExecutionException, InterruptedException, TimeoutException {
        CollectionsDatabase.addCollection(user2.getUsername(), collection1);
        CollectionsDatabase.addCollection(user2.getUsername(), collection2);
        CollectionsDatabase.addCollection(user3.getUsername(), collection3);
        Thread.sleep(1000);
    }

    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(View.class);
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }
        };
    }

    public static ViewAssertion childOfElementAtPositionMatches(final int position, final int nestedChildViewId, final Matcher<View> matcher) {
        return (view, noViewFoundException) -> {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            if (!(view instanceof RecyclerView)) {
                throw new AssertionError("View is not a RecyclerView");
            }

            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);

            if (viewHolder == null) {
                throw new AssertionError("No ViewHolder at position " + position);
            }

            View nestedChildView = viewHolder.itemView.findViewById(nestedChildViewId);
            if (nestedChildView == null) {
                throw new AssertionError("No nested child view with specified id found in ViewHolder");
            }

            assertThat(nestedChildView, matcher);
        };
    }

    public static ViewAction actionOnChildViewWithId(final int childViewId, final ViewAction viewAction) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), isDescendantOfA(any(View.class)));
            }

            @Override
            public String getDescription() {
                return "Perform action on child view with id";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View childView = view.findViewById(childViewId);
                viewAction.perform(uiController, childView);
            }
        };
    }

    private static String generateString(int length, char c) {
        return String.valueOf(c).repeat(length);
    }

}