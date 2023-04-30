package com.github.sdp.mediato.model;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.github.sdp.mediato.data.CollectionsDatabase;
import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.model.media.Collection;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.MediaType;
import com.github.sdp.mediato.model.post.ReviewPost;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class UserTests {
    User user;
    Collection collection1;
    Collection collection2;
    Collection collection3;

    @Test
    //Tests that the review posts from the user are fetched properly
    public void fetches_review_posts_properly() {
        user = createUser();
        createReviews();
        addReviews();

        List<ReviewPost> reviewPosts = user.fetchReviewPosts();
        Assert.assertEquals(3, reviewPosts.size());
    }

    @Test
    //Tests that the user builder registers mandatory attributes correctly
    public void user_builder_registers_mandatory_attributes() {
        //Build new user
        User user = new User.UserBuilder("uniqueId")
                .setUsername("user")
                .setEmail("email")
                .setRegisterDate("09/03/2023")
                .setLocation(new Location(3.14, 3.14))
                .build();

        //Check values
        Assert.assertEquals("uniqueId", user.getId());
        Assert.assertEquals("user", user.getUsername());
        Assert.assertEquals("email", user.getEmail());
        Assert.assertEquals("09/03/2023", user.getRegisterDate());
        assertTrue(user.getLocation().getLatitude() == 3.14 && user.getLocation().getLongitude() ==3.14);
    }

    @Test
    //Checks that the user builder fails when it's missing mandatory attributes
    public void user_builder_fails_with_missing_mandatory_attributes(){
        assertThrows(IllegalArgumentException.class,
                () -> {
                    //Build new user with missing attributes
                    User  user = new User.UserBuilder("uniqueId")
                            .setEmail("email")
                            .setRegisterDate("09/03/2023")
                            .build();
                });
    }

    /**
     * --------------Util functions--------------------
     */

    //Helper function that creates a sample user
    private User createUser() {
        //Create new sample user
        return new User.UserBuilder("uniqueId")
                .setUsername("user_test_model")
                .setEmail("email_test")
                .setRegisterDate("09/03/2023")
                .setLocation(new Location(3.14, 3.14))
                .build();

    }

    //Helper function that creates reviews and adds them to the collections
    private void createReviews(){
        Media media1 = new Media(MediaType.MOVIE, "Harry Potter 1", "In the closet", "validUrl", 123);
        Media media2 = new Media(MediaType.MOVIE, "Harry Potter 2", "In the WC", "validUrl", 1234);
        Media media3 = new Media(MediaType.MOVIE, "Harry Potter 3", "In the prison", "validUrl", 12345);

        Review review1 = new Review(user.getUsername(), media1, 4, "Best movie in the world");
        Review review2 = new Review(user.getUsername(), media2, 5, "Pretty bad");
        Review review3 = new Review(user.getUsername(), media3, 6, "Really bad");

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

    //Helper function that adds the reviews to the user
    private void addReviews() {
        user.addCollection(collection1);
        user.addCollection(collection2);
        user.addCollection(collection3);
    }
}
