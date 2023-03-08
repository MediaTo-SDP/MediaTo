package com.github.sdp.mediato.model;

import com.github.sdp.mediato.errorCheck.Preconditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    public static final int LIMIT_LOCATION_SIZE = 2;
    public static final String FAVOURITES_COLLECTION = "Favourites";
    public static final String RECENTLY_WATCHED_COLLECTION = "Recently Watched";

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
        this.displayedName = builder.displayedName;
        this.email = builder.email;
        this.birthDate = builder.birthDate;
        this.registerDate = builder.registerDate;
        this.location = builder.location;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayedName() {
        return displayedName;
    }

    public String getEmail() {
        return email;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public List<Double> getLocation() {
        return location;
    }

    public List<User> getFriends() {
        return friends;
    }

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
            Preconditions.checkUID(id);
            this.id = id;
        }

        public UserBuilder setUsername(String username) {
            Preconditions.checkUsername(username);
            this.username = username;
            return this;
        }
        public UserBuilder setDisplayedName(String displayedName) {
            Preconditions.checkDisplayedName(displayedName);
            this.displayedName = displayedName;
            return this;
        }
        public UserBuilder setEmail(String email) {
            Preconditions.checkEmail(email);
            this.email = email;
            return this;
        }

        public UserBuilder setBirthDate(String birthDate) {
            Preconditions.checkBirthDate(birthDate);
            this.birthDate = birthDate;
            return this;
        }

        public UserBuilder setRegisterDate(String registerDate) {
            Preconditions.checkRegisterDate(registerDate);
            this.registerDate = registerDate;
            return this;
        }
        public UserBuilder setLocation(List<Double> location) {
            Preconditions.checkLocation(location);
            this.location = location;
            return this;
        }

        public User build(){
            User user = new User(this);
            //Check mandatory fields are here
            Preconditions.checkUser(user);
            return user;
        }

    }

}
