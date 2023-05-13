package com.github.sdp.mediato.ui.viewmodel;

import android.app.Application;
import android.content.Context;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.github.sdp.mediato.MainActivity;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.api.openlibrary.OLAPI;
import com.github.sdp.mediato.api.themoviedb.TheMovieDBAPI;
import com.github.sdp.mediato.model.media.Book;
import com.github.sdp.mediato.model.media.Movie;

import java.util.ArrayList;
import java.util.List;

public class SearchMediaViewModel extends AndroidViewModel {
    private TheMovieDBAPI theMovieDB;
    private OLAPI oLAPI;

    private final MutableLiveData<List<Movie>> searchMoviesLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Movie>> trendingMoviesLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Book>> searchBooksLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Book>> trendingBooksLiveData = new MutableLiveData<>(new ArrayList<>());

    public SearchMediaViewModel(Application application) {
        super(application);
        theMovieDB = new TheMovieDBAPI(application.getString(R.string.tmdb_url), application.getString(R.string.TMDBAPIKEY));
        oLAPI = new OLAPI("https://openlibrary.org/");
        oLAPI.trending(1).thenAccept(x -> trendingBooksLiveData.getValue().addAll(x));
    }

    public MutableLiveData<List<Movie>> getSearchMoviesLiveData() {
        return searchMoviesLiveData;
    }

    public MutableLiveData<List<Movie>> getTrendingMoviesLiveData() {
        return trendingMoviesLiveData;
    }

    public MutableLiveData<List<Book>> getSearchBooksLiveData() {
        return searchBooksLiveData;
    }

    public MutableLiveData<List<Book>> getTrendingBooksLiveData() {
        return trendingBooksLiveData;
    }
}
