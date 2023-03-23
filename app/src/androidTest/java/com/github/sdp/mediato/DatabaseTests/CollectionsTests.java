package com.github.sdp.mediato.DatabaseTests;

import static org.junit.Assert.assertEquals;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.sdp.mediato.data.Database;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.model.media.Collection;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.MediaType;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@RunWith(AndroidJUnit4.class)

/**
 * This class contains all the tests for database interactions
 * @TODO add the Cloud Storage tests for the profile pictures
 */
public class CollectionsTests {
    private final static int STANDARD_COLLECTION_TIMEOUT = 10;
    User user1 = new User.UserBuilder("uniqueId1")
            .setUsername("user_test_1")
            .setEmail("email_test_1")
            .setRegisterDate("09/03/2023")
            .setLocation(new Location(3.14, 3.14))
            .build();
    Review review1 = new Review(user1.getUsername(), new Media(MediaType.MOVIE, "Harry Potter 1", "the chosen one", "url"));
    Review review2 = new Review(user1.getUsername(), new Media(MediaType.MOVIE, "Harry Potter 2", "the chosen two", "url"), 9);
    Review review3 = new Review(user1.getUsername(), new Media(MediaType.MOVIE, "Harry Potter 3", "the chosen three", "url"), 2, "meh");

    Map<String, Review> reviews1 = new HashMap<>() {
    };
    Map<String, Review> reviews2 = new HashMap<>() {
    };
    Map<String, Review> reviews3 = new HashMap<>() {
    };


    @Before
    public void setUp() throws ExecutionException, InterruptedException, TimeoutException {
        try {
            Database.database.useEmulator("10.0.2.2", 9000);
        } catch (Exception ignored) {
        }
        Database.addUser(user1).get(STANDARD_COLLECTION_TIMEOUT, TimeUnit.SECONDS);
        reviews1.put(review1.getMedia().getTitle(), review1);
        reviews2.put(review1.getMedia().getTitle(), review1);
        reviews2.put(review2.getMedia().getTitle(), review2);
    }

    //@AfterClass
    //public static void cleanDatabase() {
    //  Database.database.getReference().setValue(null);
    //}

    @Test
    public void addsCollectionProperly() throws ExecutionException, InterruptedException, TimeoutException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Collection collection1 = new Collection("MyHighlights", reviews1);
        Database.addCollection(user1.getUsername(), collection1);
        Collection retrievedCollection = Database.getCollection(user1.getUsername(), collection1.getCollectionName())
                .get(STANDARD_COLLECTION_TIMEOUT, TimeUnit.SECONDS);

                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Collection collection = snapshot.getValue(Collection.class);
                assertEquals(collection.getCollectionName(), collection1.getCollectionName());
                countDownLatch.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });
        countDownLatch.await(STANDARD_COLLECTION_TIMEOUT, TimeUnit.SECONDS);
    }

    @Test
    public void addsReviewToCollectionProperly() {

    }

}
