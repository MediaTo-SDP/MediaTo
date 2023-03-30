package com.github.sdp.mediato.model;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.User;

import org.junit.Assert;
import org.junit.Test;

public class UserTests {

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
}
