package com.github.sdp.mediato.DatabaseTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.github.sdp.mediato.data.LocationDatabase;
import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.User;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LocationTests {
    private final static int RADIUS = 100;
    private final static int STANDARD_USER_TIMEOUT = 10;

    private final static int STANDARD_SLEEP_DELAY = 1000;

    private final static double VALID_LATITUDE = 46.5;

    private final static double VALID_LONGITUDE = 6.6;

    private final static double INVALID_LATITUDE = 100;

    private final static double INVALID_LONGITUDE = -200;

    User user1;
    User user2;
    User user3;
    User user4;
    User user5;

    @Before
    public void setUp() throws ExecutionException, InterruptedException, TimeoutException {
        try {
            DataBaseTestUtil.useEmulator();
        } catch (Exception ignored) {
        }
        //Create new sample user
        user1 = new User.UserBuilder("uniqueId1")
                .setUsername("user_test_1")
                .setEmail("email_test_1")
                .setRegisterDate("09/03/2023")
                .setLocation(new Location(46.5, 6.6))
                .build();
        user2 = new User.UserBuilder("uniqueId2")
                .setUsername("user_test_2")
                .setEmail("email_test_2")
                .setRegisterDate("09/03/2023")
                .build();
        user3 = new User.UserBuilder("uniqueId3")
                .setUsername("user_test_3")
                .setEmail("email_test_3")
                .setRegisterDate("09/03/2023")
                .setLocation(new Location(46.5, 6.49))
                .build();
        user4 = new User.UserBuilder("uniqueId4")
                .setUsername("user_test_4")
                .setEmail("email_test_4")
                .setRegisterDate("09/03/2023")
                .setLocation(new Location(46.51, 6.56))
                .build();
        user5 = new User.UserBuilder("uniqueId5")
                .setUsername("user_test_5")
                .setEmail("email_test_5")
                .setRegisterDate("09/03/2023")
                .setLocation(new Location(39.54, 116.23))
                .build();
        UserDatabase.addUser(user1).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
        UserDatabase.addUser(user2).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
        UserDatabase.addUser(user3).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
        UserDatabase.addUser(user4).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
        UserDatabase.addUser(user5).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
    }

    @AfterClass
    public static void cleanDatabase() {
        DataBaseTestUtil.cleanDatabase();
    }

    @Test
    //Tests that the location is updated, set to valid and retrieved properly in the database
    public void updatesAndRetrievesLocationProperly() throws InterruptedException, ExecutionException, TimeoutException {
        LocationDatabase.updateLocation(user1.getUsername(), VALID_LATITUDE, VALID_LONGITUDE);
        Thread.sleep(STANDARD_SLEEP_DELAY);
        Location location = LocationDatabase.getSavedLocation(user1.getUsername()).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
        assertTrue(location.isValid());
        assertEquals(VALID_LONGITUDE, location.getLongitude(), 0);
        assertEquals(VALID_LATITUDE, location.getLatitude(), 0);
    }

    @Test
    //Tests that the location update throws an exception with an invalid location
    public void invalidLocationUpdateThrowsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> LocationDatabase.updateLocation(user1.getUsername(), INVALID_LATITUDE, INVALID_LONGITUDE)
        );
    }


    @Test
    //Tests that all the usernames are returned if the location is invalid
    public void returnsAllUsernamesForInvalidLocation() throws ExecutionException, InterruptedException, TimeoutException {
        List<String> nearbyUsers = LocationDatabase.getNearbyUsernames(user2.getUsername(), RADIUS).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
        Thread.sleep(STANDARD_SLEEP_DELAY);
        assertTrue(nearbyUsers.containsAll(List.of(user1.getUsername(), user3.getUsername(), user4.getUsername(), user5.getUsername())));
    }

    @Test
    //Tests that the right nearby users' usernames are retrieved
    public void retrievesNearbyUsernamesProperly() throws ExecutionException, InterruptedException, TimeoutException {
        List<String> nearbyUsers = LocationDatabase.getNearbyUsernames(user1.getUsername(), RADIUS).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
        assertTrue(nearbyUsers.containsAll(List.of(user3.getUsername(), user4.getUsername()))
                    && !nearbyUsers.contains(user5.getUsername()));
    }

    @Test
    //Tests that the right nearby users are retrieved
    public void retrievesNearbyUsersProperly() throws ExecutionException, InterruptedException, TimeoutException {
        List<User> nearbyUsers = LocationDatabase.getNearbyUsers(user1.getUsername(), RADIUS).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
        nearbyUsers.forEach(
                fetchedUser -> {
                    assertTrue(fetchedUser.getUsername().equals(user3.getUsername())
                            || fetchedUser.getUsername().equals(user4.getUsername()));
                }
        );
    }
}
