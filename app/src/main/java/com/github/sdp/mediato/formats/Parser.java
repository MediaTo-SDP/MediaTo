package com.github.sdp.mediato.formats;

public class Parser {

    /**
     * Parses the input string and returns the username
     * @param input the username concatenated with a hash code
     * @return the username
     */
    public static String parseUsername(String input) {
        return input.split(" ")[0];
    }
}
