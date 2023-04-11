package com.github.sdp.mediato.DatabaseTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.User;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LocationTests {
    private final static int STANDARD_USER_TIMEOUT = 10;

    private final static int STANDARD_SLEEP_DELAY = 1000;

    private final static double VALID_LATITUDE = 3.14;

    private final static double VALID_LONGITUDE = 3.14;

    private final static double INVALID_LATITUDE = 100;

    private final static double INVALID_LONGITUDE = -200;

    User user1;

    @Before
    public void setUp() throws ExecutionException, InterruptedException, TimeoutException {
        try {
            UserDatabase.database.useEmulator("10.0.2.2", 9000);
        } catch (Exception ignored) {
        }
        //Create new sample user
        user1 = new User.UserBuilder("uniqueId1")
                .setUsername("user_test_1")
                .setEmail("email_test_1")
                .setRegisterDate("09/03/2023")
                .setLocation(new Location(3.14, 3.14))
                .build();
        UserDatabase.addUser(user1).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
    }

    @AfterClass
    public static void cleanDatabase() {
        UserDatabase.database.getReference().setValue(null);
    }

    @Test
    //Tests that the location is updated, set to valid and retrieved properly in the database
    public void updatesAndRetrievesLocationProperly() throws InterruptedException, ExecutionException, TimeoutException {
        UserDatabase.updateLocation(user1.getUsername(), VALID_LATITUDE, VALID_LONGITUDE);
        Thread.sleep(STANDARD_SLEEP_DELAY);
        Location location = UserDatabase.getSavedLocation(user1.getUsername()).get(STANDARD_USER_TIMEOUT, TimeUnit.SECONDS);
        assertTrue(location.isValid());
        assertEquals(VALID_LONGITUDE, location.getLongitude(), 0);
        assertEquals(VALID_LATITUDE, location.getLatitude(), 0);
    }

    @Test
    //Tests that the location update throws an exception with an invalid location
    public void invalidLocationUpdateThrowsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> UserDatabase.updateLocation(user1.getUsername(), INVALID_LATITUDE, INVALID_LONGITUDE)
        );
    }

}
