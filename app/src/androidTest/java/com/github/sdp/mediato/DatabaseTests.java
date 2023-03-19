package com.github.sdp.mediato;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.sdp.mediato.data.Database;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@RunWith(AndroidJUnit4.class)

/**
 * This class contains all the tests for database interactions
 *
 * @TODO add the Cloud Storage tests for the profile pictures
 */
public class DatabaseTests {
    private static int STANDARD_TIMEOUT = 10;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    User user1;
    User user2;
    User user3;

    @Before
    public void setUp() {
        try {
            Database.database.useEmulator("10.0.2.2", 9000);
        } catch (Exception e) {
        }
        //Create new sample users
        user1 = new User.UserBuilder("uniqueId1")
                .setUsername("user_test_1")
                .setEmail("email_test_1")
                .setRegisterDate("09/03/2023")
                .setLocation(new Location(3.14, 3.14))
                .build();
        user2 = new User.UserBuilder("uniqueId2")
                .setUsername("user_test_2")
                .setEmail("email_test_2")
                .setRegisterDate("19/03/2023")
                .setLocation(new Location(3.14, 3.14))
                .build();
        user3 = new User.UserBuilder("uniqueId3")
                .setUsername("user_test_3")
                .setEmail("email_test_3")
                .setRegisterDate("19/03/2023")
                .setLocation(new Location(3.14, 3.14))
                .build();
    }

    @Test
    //Tests that following a user adds the right username in the following list and the followers list
    public void followUserAddsUsernameInFollowingAndFollowers() throws ExecutionException, InterruptedException, TimeoutException {
        Database.addUser(user2).get(STANDARD_TIMEOUT, TimeUnit.SECONDS);
        Database.addUser(user3).get(STANDARD_TIMEOUT, TimeUnit.SECONDS);
        Database.followUser(user2.getUsername(), user3.getUsername());
        DatabaseReference user1FollowingRef = Database.database.getReference().child(Database.USER_PATH + user2.getUsername() + Database.FOLLOWING_PATH);
        user1FollowingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Map<String, Boolean> following = (Map<String, Boolean>) snapshot.getValue();
                assertTrue(following.get(user3.getUsername()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });

        DatabaseReference user2FollowersRef = Database.database.getReference().child(Database.USER_PATH + user3.getUsername() + Database.FOLLOWERS_PATH);
        user2FollowersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Map<String, Boolean> followers = (Map<String, Boolean>) snapshot.getValue();
                assertTrue(followers.get(user2.getUsername()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });
    }

    @Test
    //Tests that unfollowing a user removes the right username from the following list and the followers list
    public void unfollowUserRemovesUsernameFromFollowingAndFollowers() throws ExecutionException, InterruptedException, TimeoutException {
        Database.addUser(user2).get(STANDARD_TIMEOUT, TimeUnit.SECONDS);
        Database.addUser(user3).get(STANDARD_TIMEOUT, TimeUnit.SECONDS);
        Database.followUser(user2.getUsername(), user3.getUsername());
        Database.unfollowUser(user2.getUsername(), user3.getUsername());
        DatabaseReference user1FollowingRef = Database.database.getReference().child(Database.USER_PATH + user2.getUsername() + Database.FOLLOWING_PATH);
        user1FollowingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Map<String, Boolean> following = (Map<String, Boolean>) snapshot.getValue();
                assertFalse(following.get(user3.getUsername()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });

        DatabaseReference user2FollowersRef = Database.database.getReference().child(Database.USER_PATH + user3.getUsername() + Database.FOLLOWERS_PATH);
        user2FollowersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Map<String, Boolean> followers = (Map<String, Boolean>) snapshot.getValue();
                assertFalse(followers.get(user2.getUsername()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });
    }

    @Test
    //Tests that the user is properly added and retrieved from the database
    public void addsAndGetsUserProperly() throws InterruptedException, ExecutionException, TimeoutException {
        Database.addUser(user1).get(STANDARD_TIMEOUT, TimeUnit.SECONDS);

        User retrievedUserByUsername = Database.getUser(user1.getUsername()).get(STANDARD_TIMEOUT, TimeUnit.SECONDS);
        User retrievedUserByEmail = Database.getUserByEmail(user1.getEmail()).get(STANDARD_TIMEOUT, TimeUnit.SECONDS);
        List<User> retrievedUsers = List.of(retrievedUserByUsername, retrievedUserByEmail);

        for (User retrievedUser : retrievedUsers) {
            assertEquals(retrievedUser.getUsername(), user1.getUsername());
            assertEquals(retrievedUser.getLocation().getLatitude(), user1.getLocation().getLatitude(), 0);
            assertEquals(retrievedUser.getLocation().getLongitude(), user1.getLocation().getLongitude(), 0);
            assertEquals(retrievedUser.getEmail(), user1.getEmail());
            assertEquals(retrievedUser.getId(), user1.getId());
            assertEquals(retrievedUser.getRegisterDate(), user1.getRegisterDate());
        }
    }

    @Test
    //Tests that trying to retrieve a non existent user throws an exception
    public void gettingNonExistentUserThrowsException() {
        assertThrows(
                Exception.class, () -> Database.getUser("imaginary user").get(STANDARD_TIMEOUT, TimeUnit.SECONDS)
        );
        assertThrows(
                Exception.class, () -> Database.getUserByEmail("imaginary email").get(STANDARD_TIMEOUT, TimeUnit.SECONDS)
        );
    }

    @Test
    //Tests that database properly removes a user
    public void deletingRemovesUserFromDatabase() throws ExecutionException, InterruptedException, TimeoutException {
        Database.addUser(user1).get(STANDARD_TIMEOUT, TimeUnit.SECONDS);
        Database.deleteUser(user1.getUsername()).get(STANDARD_TIMEOUT, TimeUnit.SECONDS);
        assertThrows(
                Exception.class, () -> Database.getUser(user1.getUsername()).get(STANDARD_TIMEOUT, TimeUnit.SECONDS)
        );
    }

    @Test
    //Tests that isUsernameUnique returns true when a username is unique
    public void isUsernameUniqueReturnsTrueForUniqueUsername() throws ExecutionException, InterruptedException, TimeoutException {
        Database.addUser(user1).get(STANDARD_TIMEOUT, TimeUnit.SECONDS);
        assertTrue(Database.isUsernameUnique("imaginary user").get(STANDARD_TIMEOUT, TimeUnit.SECONDS));
    }

    @Test
    //Tests that isUsernameUnique returns false when a username is unique
    public void isUsernameUniqueReturnsFalseForAlreadyExistingUsername() throws ExecutionException, InterruptedException, TimeoutException {
        Database.addUser(user1).get(STANDARD_TIMEOUT, TimeUnit.SECONDS);
        assertFalse(Database.isUsernameUnique("user_test_1").get(STANDARD_TIMEOUT, TimeUnit.SECONDS));
    }

}
