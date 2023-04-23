package com.github.sdp.mediato.DatabaseTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.sdp.mediato.data.CollectionsDatabase;
import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.User;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@RunWith(AndroidJUnit4.class)

/**
 * This class contains all the tests for database interactions
 * @TODO add the Cloud Storage tests for the profile pictures
 */
public class UserTests {
    private final static int STANDARD_USER_TIMEOUT = 10;
    private final static int STANDARD_SLEEP_DELAY = 1000;
    User user1;
    User user2;
    User user3;

    @Before
    public void setUp() {
        try {
            DatabaseTestsUtil.useEmulator();
        } catch (Exception ignored) {
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

    @AfterClass
    public static void cleanDatabase() {
        DatabaseTestsUtil.cleanDatabase();
    }

    @Test
    //Tests that following a user adds the right username in the following list and the followers list
    public void followUserAddsUsernameInFollowingAndFollowers() throws ExecutionException, InterruptedException, TimeoutException {
        UserDatabase.addUser(user2).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
        UserDatabase.addUser(user3).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);

        UserDatabase.followUser(user2.getUsername(), user3.getUsername());
        Thread.sleep(STANDARD_SLEEP_DELAY);

        List<String> followers = UserDatabase.getUser(user3.getUsername()).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS).getFollowers();
        List<String> following = UserDatabase.getUser(user2.getUsername()).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS).getFollowing();

        assertTrue(followers.contains(user2.getUsername()));
        assertTrue(following.contains(user3.getUsername()));

    }

    @Test
    //Tests that unfollowing a user removes the right username from the following list and the followers list
    public void unfollowUserRemovesUsernameFromFollowingAndFollowers() throws ExecutionException, InterruptedException, TimeoutException {
        UserDatabase.addUser(user2).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
        UserDatabase.addUser(user3).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);

        UserDatabase.followUser(user2.getUsername(), user3.getUsername());
        UserDatabase.unfollowUser(user2.getUsername(), user3.getUsername());
        Thread.sleep(STANDARD_SLEEP_DELAY);

        List<String> followers = UserDatabase.getUser(user3.getUsername()).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS).getFollowers();
        List<String> following = UserDatabase.getUser(user2.getUsername()).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS).getFollowing();

        assertFalse(followers.contains(user2.getUsername()));
        assertFalse(following.contains(user3.getUsername()));
    }

    @Test
    //Tests that the user is properly added and retrieved from the database
    public void addsAndGetsUserProperly() throws InterruptedException, ExecutionException, TimeoutException {
        UserDatabase.addUser(user1).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);

        User retrievedUserByUsername = UserDatabase.getUser(user1.getUsername()).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
        User retrievedUserByEmail = UserDatabase.getUserByEmail(user1.getEmail()).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
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
                Exception.class, () -> UserDatabase.getUser("imaginary user").get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS)
        );
        assertThrows(
                Exception.class, () -> UserDatabase.getUserByEmail("imaginary email").get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS)
        );
    }

    @Test
    //Tests that database properly removes a user
    public void deletingRemovesUserFromDatabase() throws ExecutionException, InterruptedException, TimeoutException {
        UserDatabase.addUser(user1).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
        UserDatabase.deleteUser(user1.getUsername()).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
        assertThrows(
                Exception.class, () -> UserDatabase.getUser(user1.getUsername()).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS)
        );
    }

    @Test
    //Tests that isUsernameUnique returns true when a username is unique
    public void isUsernameUniqueReturnsTrueForUniqueUsername() throws ExecutionException, InterruptedException, TimeoutException {
        UserDatabase.addUser(user1).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
        assertTrue(UserDatabase.isUsernameUnique("imaginary user").get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS));
    }

    @Test
    //Tests that isUsernameUnique returns false when a username is unique
    public void isUsernameUniqueReturnsFalseForAlreadyExistingUsername() throws ExecutionException, InterruptedException, TimeoutException {
        UserDatabase.addUser(user1).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
        assertFalse(UserDatabase.isUsernameUnique("user_test_1").get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS));
    }

}
