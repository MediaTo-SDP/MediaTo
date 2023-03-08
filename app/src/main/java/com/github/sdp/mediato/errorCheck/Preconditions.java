package com.github.sdp.mediato.errorCheck;

import static com.github.sdp.mediato.model.Review.MAX_GRADE;
import static com.github.sdp.mediato.model.Review.MIN_GRADE;
import static com.github.sdp.mediato.model.User.LIMIT_LOCATION_SIZE;

import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.MediaType;

import java.util.List;

/**
 * Class to check preconditions on attributes
 * @TODO Corner cases, dates and email formatting to be discussed and checked
 */
public class Preconditions {

    /**
     * Checks if the user id is valid
     * @param uId the user id
     * @TODO determine conditions for user id validity
     */
    public static void checkUID(String uId){
        checkNullOrEmptyString(uId, "user Id");
    }

    /**
     * Checks if username is valid
     * @param username
     * @TODO determine condition for username validity (formatting, uniqueness...)
     */
    public static void checkUsername(String username){
        checkNullOrEmptyString(username, "username");
    }

    /**
     * Checks if displayed name is valid
     * @param displayedName
     * @TODO determine condition for displayed name validity (formatting, uniqueness...)
     */
    public static void checkDisplayedName(String displayedName){
        checkNullOrEmptyString(displayedName, "displayed name");
    }

    /**
     * Checks if email address is valid
     * @param email
     * @TODO determine - if necessary - condition for email validity (format, existence  using google auth)
     */
    public static void checkEmail(String email){
        checkNullOrEmptyString(email, "email");
    }

    /**
     * Checks if birth date is valid
     * @param birthDate
     * @TODO determine birth date validity (format, date)
     */
    public static void checkBirthDate(String birthDate){
       checkNullOrEmptyString(birthDate, "birth date");
    }

    /**
     * Checks if register date is valid
     * @param registerDate
     * @TODO determine register date validity (format, date)
     */
    public static void checkRegisterDate(String registerDate){
        checkNullOrEmptyString(registerDate, "register date");
    }

    /**
     * Checks if the location is valid
     * @param location
     */
    public static void checkLocation(List<Double> location) {
        if (location == null || location.isEmpty()) throw new IllegalArgumentException("Location is unknown: list is null or empty");
        if (location.size() != LIMIT_LOCATION_SIZE) throw new IllegalArgumentException("Location should contain exactly two doubles");
    }

    /**
     * Checks if media is not null
     * @param media
     */
    public static void checkMedia(Media media){
        if (media == null) {
            throw new IllegalArgumentException("Media must not be null");
        }
    }

    /**
     * Checks if grade is within bounds
     * @param grade
     */
    public static void checkGrade(int grade){
        if ((grade > MAX_GRADE) || (grade < MIN_GRADE)) {
            throw new IllegalArgumentException("Grade must be between " + MIN_GRADE + " and " + MAX_GRADE);
        }
    }

    /**
     * Checks if comment is valid
     * @param comment
     * @TODO determine remaining criteria: char limit etc...
     */
    public static void checkComment(String comment){
        checkNullOrEmptyString(comment, "comment");
    }

    /**
     * Checks if title is valid
     * @param title
     * @TODO determine - if necessary - other criteria for title validity
     */
    public static void checkTitle(String title){
        checkNullOrEmptyString(title, "title");
    }

    /**
     * Checks if summary is valid
     * @param summary
     * @TODO determine remaining criteria: char limit etc...
     */
    public static void checkSummary(String summary){
        checkNullOrEmptyString(summary, "summary");
    }

    /**
     * Checks if a string is null or empty
     * @param string to be checked
     * @param variable corresponding to string
     * @throws IllegalArgumentException indicating that the @param variable cannot not be null or empty
     */
    public static void checkNullOrEmptyString(String string, String variable){
        if (string == null || string.isEmpty()){
            throw new IllegalArgumentException(variable + "must not be null or empty");
        }
    }
}
