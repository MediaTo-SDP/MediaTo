package com.github.sdp.mediato;

import org.junit.Assert;
import org.junit.Test;


import static org.junit.Assert.*;

import com.github.sdp.mediato.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class modelTests {

    @Test
    public void user_builder_registers_mandatory_attributes(){
        //Build new user
        User  user = new User.UserBuilder("uniqueId")
                .setUsername("user")
                .setDisplayedName("displayed name")
                .setEmail("email")
                .setRegisterDate("09/03/2023")
                .setBirthDate("09/03/2023")
                .setLocation(Arrays.asList(3.14, 3.14))
                .build();

        //Check values
        Assert.assertEquals("uniqueId", user.getId());
        Assert.assertEquals("user", user.getUsername());
        Assert.assertEquals("displayed name", user.getDisplayedName());
        Assert.assertEquals("email", user.getEmail());
        Assert.assertEquals("09/03/2023", user.getRegisterDate());
        Assert.assertEquals("09/03/2023", user.getBirthDate());
        assertTrue(user.getLocation().containsAll(Arrays.asList(3.14, 3.14)));
    }

    @Test
    public void user_builder_fails_with_invalid_strings(){
        assertThrows(IllegalArgumentException.class,
                () -> {
                    //Build new user with missing attributes
                    User  user = new User.UserBuilder("uniqueId")
                            .setUsername("user")
                            .setEmail("email")
                            .setRegisterDate("09/03/2023")
                            .build();
                });
    }
}
