package com.github.sdp.mediato.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GenreMovies {
    private static final Map<Integer, String> genre;

    static {
        genre = new HashMap<>();
        genre.put(0, "Genre");
        genre.put(28, "Action");
        genre.put(12, "Adventure");
        genre.put(16, "Animation");
        genre.put(35, "Comedy");
        genre.put(80, "Crime");
        genre.put(99, "Documentary");
        genre.put(18, "Drama");
        genre.put(10751, "Family");
        genre.put(14, "Fantasy");
        genre.put(36, "History");
        genre.put(27, "Horror");
        genre.put(10402, "Music");
        genre.put(9648, "Mystery");
        genre.put(10749, "Romance");
        genre.put(878, "Science Fiction");
        genre.put(10770, "TV Movie");
        genre.put(53, "Thriller");
        genre.put(10752, "War");
        genre.put(37, "Western");
    }

    private GenreMovies() {

    }

    public static Map<Integer, String> getGenre() {
        return genre;
    }

    public static List<String> getGenreName() {
        return new ArrayList<>(genre.values());
    }

    public static Integer getGenreId(String genreName) {
        for (Map.Entry<Integer, String> entry : genre.entrySet()) {
            if (entry.getValue().equals(genreName)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
