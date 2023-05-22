package com.github.sdp.mediato.DatabaseTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.sdp.mediato.data.CollectionsDatabase;
import com.github.sdp.mediato.data.ReviewInteractionDatabase;
import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.Comment;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.model.media.Collection;
import com.github.sdp.mediato.model.media.Movie;

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

/**
 * This class contains all the tests for database collections interactions
 */
public class CollectionsTests {
    private final static int STANDARD_COLLECTION_TIMEOUT = 10;
    private final static String COMMENTER_USERNAME = "commenter";
    private final User user1 = new User.UserBuilder("uniqueId1")
        .setUsername("user_test_1")
        .setEmail("email_test_1")
        .setRegisterDate("09/03/2023")
        .setLocation(new Location(3.14, 3.14))
        .build();

    private Collection collection1;
    private Collection collection2;

    private final Review review1 = new Review(user1.getUsername(),
        new Movie("Harry Potter 1", "the chosen one", "url", 1));
    private final Review review2 = new Review(user1.getUsername(),
        new Movie("Harry Potter 2", "the chosen two", "url", 2), 9);
    private final Review review3 = new Review(user1.getUsername(),
        new Movie("Harry Potter 3", "the chosen three", "url", 3), 2, "meh");

    private final Map<String, Review> reviews1 = new HashMap<>() {
    };
    private final Map<String, Review> reviews2 = new HashMap<>() {
    };
    private final Map<String, Review> reviews3 = new HashMap<>() {
    };


    @Before
    public void setUp() throws ExecutionException, InterruptedException, TimeoutException {
        try {
            DataBaseTestUtil.useEmulator();
        } catch (Exception ignored) {
        }
        UserDatabase.addUser(user1).get(STANDARD_COLLECTION_TIMEOUT, TimeUnit.SECONDS);
        reviews1.put(review1.getMedia().getTitle(), review1);
        reviews2.put(review2.getMedia().getTitle(), review2);
        collection1 = new Collection("MyHighlights", reviews1);
        collection2 = new Collection("MyFavs", reviews2);
    }

    @AfterClass
    public static void cleanDatabase() {
        DataBaseTestUtil.cleanDatabase();
    }


    @Test
    //Tests that the collections are added, retrieved and removed properly
    public void addsAndRetrievesCollectionProperly() throws ExecutionException, InterruptedException, TimeoutException {
        //Adds the collection
        CollectionsDatabase.addCollection(user1.getUsername(), collection1);
        Thread.sleep(1000);

        //Test retrieving the collection
        Collection retrievedCollection = CollectionsDatabase.getCollection(user1.getUsername(), collection1.getCollectionName())
                .get(STANDARD_COLLECTION_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(collection1.getCollectionName(), retrievedCollection.getCollectionName());
        assertEquals(collection1.getCollectionType(), retrievedCollection.getCollectionType());
        Review retrievedReview = retrievedCollection.getReviews().get(review1.getMedia().getTitle());
        assertEquals(review1.getMedia().getTitle(), retrievedReview.getMedia().getTitle());
        assertEquals(review1.getMedia().getId(), retrievedReview.getMedia().getId());
        assertEquals(review1.getUsername(), retrievedReview.getUsername());
        assertEquals(review1.getComment(), retrievedReview.getComment());
        assertEquals(review1.getGrade(), retrievedReview.getGrade());
    }

    @Test
    //Tests that the collections are removed properly
    public void RemovesCollectionProperly() throws ExecutionException, InterruptedException, TimeoutException {
        CollectionsDatabase.addCollection(user1.getUsername(), collection1);
        Thread.sleep(1000);
        //Test removing the collection
        CollectionsDatabase.removeCollection(user1.getUsername(), collection1.getCollectionName());
        Thread.sleep(1000);
        assertThrows(
                Exception.class, () -> {
                    CollectionsDatabase.getCollection(user1.getUsername(), collection1.getCollectionName()).get(STANDARD_COLLECTION_TIMEOUT, TimeUnit.SECONDS);
                });
    }
    @Test
    //Tests that you can add a review to an existent collection
    public void addsReviewToCollectionProperly() throws InterruptedException, ExecutionException, TimeoutException {
        //Add collection
        CollectionsDatabase.addCollection(user1.getUsername(), collection2);
        Thread.sleep(1000);

        //Add review to the collection
        CollectionsDatabase.addReviewToCollection(user1.getUsername(), collection2.getCollectionName(), review1);
        Thread.sleep(1000);

        //Retrieve collection and check that the review was added properly
        Collection retrievedCollection = CollectionsDatabase.getCollection(user1.getUsername(), collection2.getCollectionName())
                .get(STANDARD_COLLECTION_TIMEOUT, TimeUnit.SECONDS);
        assertTrue(retrievedCollection.getReviews().containsKey(review1.getMedia().getTitle())
        && retrievedCollection.getReviews().containsKey(review2.getMedia().getTitle()));
    }

    @Test
    public void commentsReviewProperly() throws InterruptedException, ExecutionException, TimeoutException {
        //Add collection
        CollectionsDatabase.addCollection(user1.getUsername(), collection2);
        Thread.sleep(1000);

        //Add review to the collection
        CollectionsDatabase.addReviewToCollection(user1.getUsername(), collection2.getCollectionName(), review1);
        Thread.sleep(1000);

        //Comment review
        Comment comment = new Comment(collection2.getCollectionName(), review1.getMedia().getTitle(), "This is a comment", COMMENTER_USERNAME);
        ReviewInteractionDatabase.commentReview(user1.getUsername(), comment);
        Thread.sleep(1000);

        //Retrieve review
        Review retrievedReview = CollectionsDatabase.getCollection(user1.getUsername(), collection2.getCollectionName())
                .get(STANDARD_COLLECTION_TIMEOUT, TimeUnit.SECONDS).getReviews().get(review1.getMedia().getTitle());

        assertTrue(retrievedReview.getComments().containsKey(COMMENTER_USERNAME + " " + comment.hashCode()));
        assertEquals(retrievedReview.getComments().get(COMMENTER_USERNAME + " " + comment.hashCode()), "This is a comment");
    }

}
