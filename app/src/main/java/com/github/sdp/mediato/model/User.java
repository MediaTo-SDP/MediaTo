package com.github.sdp.mediato.model;

import static android.text.TextUtils.isEmpty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    static final int LIMIT_LOCATION_SIZE = 2;
    static final String FAVOURITES_COLLECTION = "Favourites";
    static final String RECENTLY_WATCHED_COLLECTION = "Recently Watched";

    private String id = "";
    private String username = "";
    private String displayedName = "";
    private String email = "";
    private String birthDate = "";
    private String registerDate = "";

    private List<Double> location = new ArrayList<>();
    private List<User> friends = new ArrayList<>();
    private Map<String, List<Review>> reviews = new HashMap<>();
    private User(){}
    public User(UserBuilder builder){
        this.id = builder.id;
        this.username = builder.username;
        this.email = builder.email;
        this.birthDate = builder.birthDate;
        this.registerDate = builder.registerDate;
        this.reviews.put(FAVOURITES_COLLECTION, new ArrayList<>());
        this.reviews.put(RECENTLY_WATCHED_COLLECTION, new ArrayList<>());
    }

    public String getId() {return id;}

    public String getUsername() {return username;}

    public String getDisplayedName() {return displayedName;}

    public String getEmail() {return email;}

    public String getBirthDate() {return birthDate;}

    public String getRegisterDate() {return registerDate;}

    public List<Double> getLocation() {return location;}

    public List<User> getFriends() {return friends;}

    public static class UserBuilder {
        private String id = "";
        private String username = "";
        private String displayedName = "";
        private String email = "";
        private String birthDate = "";
        private String registerDate = "";

        private List<Double> location = new ArrayList<>();
        private List<User> friends = new ArrayList<>();
        private Map<String, List<Review>> reviews = new HashMap<>();

        public UserBuilder(String id){
            if(isEmpty(id)) throw new IllegalArgumentException("id can't be empty");
            this.id = id;
        }

        public UserBuilder setUsername(String username) {
            if(isEmpty(username)) throw new IllegalArgumentException("username can't be empty");
            this.username = username;
            return this;
        }
        public UserBuilder setDisplayedName(String displayedName) {
            if(isEmpty(displayedName)) throw new IllegalArgumentException("displayedName can't be empty");
            this.displayedName = displayedName;
            return this;
        }
        public UserBuilder setEmail(String email) {
            if(isEmpty(email)) throw new IllegalArgumentException("email can't be empty");
            this.email = email;
            return this;
        }

        public UserBuilder setBirthDate(String birthDate) {
            if(isEmpty(birthDate)) throw new IllegalArgumentException("birthDate can't be empty");
            this.birthDate = birthDate;
            return this;
        }

        public UserBuilder setRegisterDate(String registerDate) {
            if(isEmpty(registerDate)) throw new IllegalArgumentException("registerDate can't be empty");
            this.registerDate = registerDate;
            return this;
        }
        public UserBuilder setLocation(List<Double> location) {
            if (location.size() != LIMIT_LOCATION_SIZE){
                throw new IllegalArgumentException("Location must have two doubles");
            }
            this.location = location;
            return this;
        }

    }

}
