package com.github.sdp.mediato;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.sdp.mediato.data.Database;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.User;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
/**
 * This class contains all the tests for database interactions
 * @TODO add the Cloud Storage tests for the profile pictures
 */
public class DatabaseTests {
    //User user1;
    //User user2;
    //User user3;

    @Test
    public void sampleTest(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.useEmulator("10.0.2.2", 9000);
        CompletableFuture<String> future = new CompletableFuture<>();
        database.getReference().child("test").setValue(1234,

                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error == null) future.complete("test");
                        else future.completeExceptionally(error.toException());
                    }
                }
        );
        String res = future.orTimeout(10, TimeUnit.SECONDS).join();
        assertEquals("test", res);
    }

    /**
    @Before
    public void setUp(){
       try {
           Database.database.useEmulator("10.0.2.2", 9000);
        }
        catch(Exception e){}
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
                .setRegisterDate("09/03/2023")
                .setLocation(new Location(3.14, 3.14))
                .build();
        user3 = new User.UserBuilder("uniqueId3")
                .setUsername("user_test_3")
                .setEmail("email_test_3")
                .setRegisterDate("09/03/2023")
                .setLocation(new Location(3.14, 3.14))
                .build();
    }

    @After
    //Cleans database values after each test
    public void cleanDatabase(){
        Database.database.getReference().setValue(null);
    }

    @Test
    //Tests that the user is properly added and retrieved from the database
    public void addsAndGetsUserProperly() throws InterruptedException {
        Database.addUser(user1).orTimeout(5, TimeUnit.SECONDS).join();
        User retrievedUser = Database.getUser(user1.getUsername()).orTimeout(5, TimeUnit.SECONDS).join();
        assertEquals(retrievedUser.getUsername(), user1.getUsername());
        assertEquals(retrievedUser.getLocation().getLatitude(), user1.getLocation().getLatitude(), 0);
        assertEquals(retrievedUser.getLocation().getLongitude(), user1.getLocation().getLongitude(), 0);
        assertEquals(retrievedUser.getEmail(), user1.getEmail());
        assertEquals(retrievedUser.getId(), user1.getId());
        assertEquals(retrievedUser.getRegisterDate(), user1.getRegisterDate());
    }

    @Test
    //Tests that trying to retrieve a non existent user throws an exception
    public void gettingNonExistentUserThrowsException(){
        assertThrows(
                Exception.class, ()-> Database.getUser("imaginary user").orTimeout(5, TimeUnit.SECONDS).join()
        );
    }

    @Test
    //Tests that database properly removes a user
    public void deletingRemovesUserFromDatabase(){
        Database.deleteUser(user1.getUsername()).orTimeout(5, TimeUnit.SECONDS).join();
        assertThrows(
                Exception.class, ()-> Database.getUser(user1.getUsername()).orTimeout(5, TimeUnit.SECONDS).join()
        );
    }

    @Test
    //Tests that isUsernameUnique returns true when a username is unique
    public void isUsernameUniqueReturnsTrueForUniqueUsername(){
        Database.addUser(user1).orTimeout(5, TimeUnit.SECONDS).join();
        assertTrue(Database.isUsernameUnique("imaginary user").orTimeout(5, TimeUnit.SECONDS).join());
    }

    @Test
    //Tests that isUsernameUnique returns false when a username is unique
    public void isUsernameUniqueReturnsFalseForAlreadyExistingUsername(){
        Database.addUser(user1).orTimeout(5, TimeUnit.SECONDS).join();
        assertFalse(Database.isUsernameUnique("user_test_1").orTimeout(5, TimeUnit.SECONDS).join());
    }*/

}
