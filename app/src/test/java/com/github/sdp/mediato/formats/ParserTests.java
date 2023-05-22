package com.github.sdp.mediato.formats;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ParserTests {

    @Test
    //Tests that the Parser class returns the username properly
    public void parsesUsernameConcatenatedWithHashcode() {
        String username = "username";
        String result = Parser.parseUsername(username + " 1234");
        assertEquals(username, result);
    }
}
