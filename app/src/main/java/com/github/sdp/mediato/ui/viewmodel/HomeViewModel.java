package com.github.sdp.mediato.ui.viewmodel;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.sdp.mediato.R;
import com.github.sdp.mediato.api.gbook.GBookAPI;
import com.github.sdp.mediato.api.themoviedb.TheMovieDBAPI;
import com.github.sdp.mediato.model.media.Book;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.Movie;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The ViewModel for the {@link com.github.sdp.mediato.ui.HomeFragment}
 */
public class HomeViewModel extends AndroidViewModel {

    Application application;
    private final MutableLiveData<List<Media>> medias = new MutableLiveData<>();
    private final TheMovieDBAPI movieApi;
    private final GBookAPI bookApi;

    /**
     * Default constructor
     *
     * @param application
     */
    public HomeViewModel(Application application) {
        super(application);
        this.application = application;
        movieApi = new TheMovieDBAPI(application.getApplicationContext().getString(R.string.tmdb_url),
                application.getApplicationContext().getString(R.string.TMDBAPIKEY));
        bookApi = new GBookAPI(application.getApplicationContext().getString(R.string.gbook_url));
    }

    /**
     * Returns the list of all downloaded movies
     *
     * @return a {@link LiveData} of the list
     */
    public LiveData<List<Media>> getMovies() {
        movieApi.clearCache();
        movieApi.trending(20).thenAccept(list ->
            medias.setValue(list.stream().map(Movie::new).collect(Collectors.toList())));
        return medias;
    }

    public LiveData<List<Media>> getBooks(){
        movieApi.clearCache();
        bookApi.searchItems("maze", 40).thenAccept(list ->
            medias.setValue(list.stream().map(Book::new).collect(Collectors.toList())));
        return medias;
    }

}
