package com.github.sdp.mediato;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.sdp.mediato.api.themoviedb.TheMovieDB;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.Movie;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The ViewModel for the {@link HomeFragment}
 */
public class HomeViewModel extends AndroidViewModel {

    Application application;
    private final MutableLiveData<List<Media>> movies = new MutableLiveData<>();
    private final TheMovieDB api;

    /**
     * Default constructor
     *
     * @param application
     */
    public HomeViewModel(Application application) {
        super(application);
        this.application = application;
        api = new TheMovieDB(application.getApplicationContext().getString(R.string.tmdb_url),
                application.getApplicationContext().getString(R.string.TMDBAPIKEY));
    }

    /**
     * Returns the list of all downloaded movies
     *
     * @return a {@link LiveData} of the list
     */
    public LiveData<List<Media>> getMovies() {
        api.trending(20).thenAccept(list -> {
            movies.setValue(list.stream().map(Movie::new).collect(Collectors.toList()));
        });
        return movies;
    }

}
