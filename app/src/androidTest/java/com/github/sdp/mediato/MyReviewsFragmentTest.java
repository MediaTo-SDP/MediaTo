package com.github.sdp.mediato;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.adevinta.android.barista.assertion.BaristaRecyclerViewAssertions.assertRecyclerViewItemCount;
import static com.adevinta.android.barista.interaction.BaristaListInteractions.clickListItemChild;

import static org.hamcrest.Matchers.allOf;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
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
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class MyReviewsFragmentTest {
    private final static int STANDARD_USER_TIMEOUT = 10;
    private User user1;
    private User user2;
    private User user3;
    private Collection collection1;
    private Collection collection2;
    private Collection collection3;

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
            bundle.putSerializable("feedType", FeedFragment.FeedType.MY_REVIEWS);
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
        feedText.check(matches(withText("My Reviews")));
    }

    // Test that all the reviews from the followed users are displayed
    // In this test, only user 2 is followed and they have 2 reviews
    @Test
    public void testItemCount() throws InterruptedException {
        Thread.sleep(5000);
        assertRecyclerViewItemCount(R.id.feed_posts, 2);
    }

    /**
     * --------------Util functions--------------------
     */

    //Helper function that creates users and adds them to the database
    private void createUsers() throws ExecutionException, InterruptedException, TimeoutException {
        //Create new sample users
        user1 = new User.UserBuilder("uniqueId1")
                .setUsername("user_myreviews_test_1")
                .setEmail("email_test_1")
                .setRegisterDate("09/03/2023")
                .setLocation(new Location(3.14, 3.14))
                .build();
        user2 = new User.UserBuilder("uniqueId2")
                .setUsername("user_myreviews_test_2")
                .setEmail("email_test_2")
                .setRegisterDate("19/03/2023")
                .setLocation(new Location(3.14, 3.14))
                .build();
        user3 = new User.UserBuilder("uniqueId3")
                .setUsername("user_myreviews_test_3")
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
        CollectionsDatabase.addCollection(user1.getUsername(), collection1);
        CollectionsDatabase.addCollection(user1.getUsername(), collection2);
        CollectionsDatabase.addCollection(user3.getUsername(), collection3);
        Thread.sleep(1000);
    }
}