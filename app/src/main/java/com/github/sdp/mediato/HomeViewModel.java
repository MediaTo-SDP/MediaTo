package com.github.sdp.mediato;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.github.sdp.mediato.api.themoviedb.TheMovieDBAPI;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.Movie;

import java.util.List;
import java.util.stream.Collectors;

public class HomeViewModel  extends AndroidViewModel {

    Application application;
    private final MutableLiveData<List<Media>> movies = new MutableLiveData<>();
    private final TheMovieDBAPI api;

    public HomeViewModel(Application application){
        super(application);
        this.application = application;
        api = new TheMovieDBAPI(application.getApplicationContext().getString(R.string.tmdb_url),
                application.getApplicationContext().getString(R.string.TMDBAPIKEY));
    }
    public MutableLiveData<List<Media>> getMovies() {
        api.trending(20).thenAccept( list -> {
            movies.setValue(list.stream().map(Movie::new).collect(Collectors.toList()));
        });
        return movies;
    }

}
