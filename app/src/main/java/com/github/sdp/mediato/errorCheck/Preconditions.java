package com.github.sdp.mediato.errorCheck;

import static com.github.sdp.mediato.model.Review.MAX_GRADE;
import static com.github.sdp.mediato.model.Review.MIN_GRADE;

import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.MediaType;

import java.util.List;
import java.util.Objects;

/**
 * Class to check preconditions on attributes
 * @TODO Corner cases, dates and email formatting to be discussed and checked
 */
public class Preconditions {

    public static final double MAX_LATITUDE = 90;
    public static final double MAX_LONGITUDE = 180;
    public static final double MIN_LATITUDE = -90;
    public static final double MIN_LONGITUDE = -180;

    /**
     * Checks if mandatory user fields are valid
     * @param user
     */
    public static void checkUser(User user){
        Preconditions.checkUID(user.getId());
        Preconditions.checkUsername(user.getUsername());
        Preconditions.checkEmail(user.getEmail());
        Preconditions.checkRegisterDate(user.getRegisterDate());
        Preconditions.checkLocation(user.getLocation());
    }

    /**
     * Checks if Media attributes are valid
     * @param mediaType
     * @param strings the list of the strings needed to create a media as a list
     * cf : <a href="https://github.com/MediaTo-SDP/MediaTo/pull/82#discussion_r1148591086">https://github.com/MediaTo-SDP/MediaTo/pull/82#discussion_r1148591086</a>
     */
    public static void checkMedia(MediaType mediaType, List<String> strings){
        Preconditions.checkTitle(strings.get(0));
        Preconditions.checkSummary(strings.get(1));
        Preconditions.checkImageURL(strings.get(2));
        Preconditions.checkImageURL(strings.get(3));
        Objects.requireNonNull(mediaType);
    }

    /**
     * Checks if Review mandatory attributes are valid
     * @param username
     * @param media
     */
    public static void checkReview(String username, Media media){
        Preconditions.checkUsername(username);
        Preconditions.checkMedia(media);
    }

    /**
     * Checks if Review attributes are valid when adding the grade
     * @param username
     * @param media
     * @param grade
     */
    public static void checkReview(String username, Media media, int grade){
        Preconditions.checkReview(username, media);
        Preconditions.checkGrade(grade);
    }

    /**
     * Checks if Review attributes are valid when adding the grade and a comment
     * @param username
     * @param media
     * @param grade
     * @param comment
     */
    public static void checkReview(String username, Media media, int grade, String comment){
        Preconditions.checkReview(username, media, grade);
        Preconditions.checkComment(comment);
    }

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
     * Checks if email address is valid
     * @param email
     * @TODO determine - if necessary - condition for email validity (format, existence  using google auth)
     */
    public static void checkEmail(String email){
        checkNullOrEmptyString(email, "email");
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
    public static void checkLocation(Location location) {
        if (location.isValid() && !locationWithinBounds(location)){
            throw new IllegalArgumentException("Location mus be between -90 and 90 for latitude and -180 and 180 for longitude");
        }
    }

    /**
     * Checks if the latitude and longitude are within the authorised bounds
     */
    public static boolean locationWithinBounds(Location location){
        return (location.getLatitude() > MIN_LATITUDE && location.getLatitude() < MAX_LATITUDE)
                && (location.getLongitude() > MIN_LONGITUDE && location.getLongitude() < MAX_LONGITUDE);
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
        //checkNullOrEmptyString(comment, "comment");
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
     * Checks if image url is valid
     * @param imageUrl to be checked
     * @TODO determine remaining criteria: format, existence etc...
     */
    public static void checkImageURL(String imageUrl){ checkNullOrEmptyString(imageUrl, "image url");}


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


    /**
     * Checks that a value is greater and not equal to zero
     * @param value to be checked
     * @throws IllegalArgumentException indicating that the @value is smaller or equal to zero
     */
    public static void checkStrictlyPositive(int value) {
        if (value <= 0){
            throw new IllegalArgumentException("The value must be strictly positive. Value : " + value);
        }
    }

    /**
     * Checks that a value is greater or equal to zero
     * @param value to be checked
     * @throws IllegalArgumentException indicating that the @value is smaller than zero
     */
    public static void checkPositive(double value) {
        if (value < 0){
            throw new IllegalArgumentException("The value must be positive. Value : " + value);
        }
    }
}
