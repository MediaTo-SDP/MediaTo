package com.github.sdp.mediato.data;

import com.google.firebase.database.DatabaseReference;

/**
 * This is a Utils class for the database classes
 */
class Utils {

    //-----------Constant definitions-------------

    static final String USERS_PATH = "Users/";
    static final String REVIEWS_PATH = "reviews/";
    static final String USER_COLLECTIONS_PATH = "/collections/";
    static final String LOCATION_PATH = "/location/";
    static final String FOLLOWING_PATH = "/following/";
    static final String FOLLOWERS_PATH = "/followers/";

    static final String USER_PROFILE_PICS_PATH = "ProfilePics/";

    static final int PROFILE_PIC_MAX_SIZE = 1024 * 1024; //1 Megabyte

    static final int LOCATION_RADIUS = 100;

    //---------------------Util methods-------------------------------------

    /**
     * Helper method that returns the database reference for a collection
     *
     * @param username       the username of the user concerned
     * @param collectionName the name of the collection needed
     * @return the database reference for the collection
     */
    static DatabaseReference getCollectionReference(String username, String collectionName) {
        return CollectionsDatabase.database.getReference().child(USERS_PATH + username + USER_COLLECTIONS_PATH + collectionName);
    }
}
