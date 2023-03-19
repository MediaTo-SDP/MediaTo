package com.github.sdp.mediato;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.github.sdp.mediato.api.themoviedb.TheMovieDB;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.Movie;

import java.util.List;
import java.util.stream.Collectors;

public class HomeViewModel  extends AndroidViewModel {

    //
    private final MutableLiveData<List<Media>> movies = new MutableLiveData<>();

    public HomeViewModel(Application application){
        super(application);
        TheMovieDB api = new TheMovieDB(application.getApplicationContext().getString(R.string.tmdb_url),
                application.getApplicationContext().getString(R.string.TMDBAPIKEY));
        api.trending(10).thenAccept( list ->{
                        System.out.println("first part completed" + list.get(0).getTitle());
            movies.postValue(list.stream().map(Movie::new).collect(Collectors.toList()));
        }).join();
    }
    public MutableLiveData<List<Media>> getMovies() {
        return movies;
    }

}
