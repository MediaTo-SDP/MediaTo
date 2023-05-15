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
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.Movie;

import java.util.ArrayList;
import java.util.List;

public class SearchMediaViewModel extends AndroidViewModel {
    private TheMovieDBAPI theMovieDB;
    private OLAPI oLAPI;
    private int trendingBookPage = 1;

    private final MutableLiveData<List<Media>> searchMoviesLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Media>> trendingMoviesLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Media>> searchBooksLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Media>> trendingBooksLiveData = new MutableLiveData<>(new ArrayList<>());

    public SearchMediaViewModel(Application application) {
        super(application);
        theMovieDB = new TheMovieDBAPI(application.getString(R.string.tmdb_url), application.getString(R.string.TMDBAPIKEY));
        oLAPI = new OLAPI("https://openlibrary.org/");

        LoadFirstTrendingBooksPage();
    }

    public MutableLiveData<List<Media>> getSearchMoviesLiveData() {
        return searchMoviesLiveData;
    }

    public MutableLiveData<List<Media>> getTrendingMoviesLiveData() {
        return trendingMoviesLiveData;
    }

    public MutableLiveData<List<Media>> getSearchBooksLiveData() {
        return searchBooksLiveData;
    }

    public MutableLiveData<List<Media>> getTrendingBooksLiveData() {
        return trendingBooksLiveData;
    }

    public void LoadFirstTrendingBooksPage() {
        trendingBookPage = 1;
        oLAPI.trending(trendingBookPage).thenAccept(x -> {
            List<Media> updatedBooks = new ArrayList<>(trendingBooksLiveData.getValue());
            updatedBooks.addAll(x);
            trendingBooksLiveData.postValue(updatedBooks);
        });
    }

    public void LoadNextTrendingBooksPage() {
        trendingBookPage += 1;
        oLAPI.trending(trendingBookPage).thenAccept(x -> {
            List<Media> updatedBooks = new ArrayList<>(trendingBooksLiveData.getValue());
            updatedBooks.addAll(x);
            trendingBooksLiveData.postValue(updatedBooks);
        });
    }
}
