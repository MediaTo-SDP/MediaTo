package com.github.sdp.mediato.ui.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.github.sdp.mediato.R;
import com.github.sdp.mediato.api.openlibrary.OLAPI;
import com.github.sdp.mediato.api.themoviedb.TheMovieDBAPI;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.Movie;
import com.github.sdp.mediato.ui.SearchFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchMediaViewModel extends AndroidViewModel {

    private SearchFragment.SearchCategory currentCategory = SearchFragment.SearchCategory.PEOPLE;
    private String searchQuery = "";
    private TheMovieDBAPI theMovieDB;
    private OLAPI oLAPI;
    private String titleSearchBook = "";
    private String titleSearchMovie = "";
    private int searchBooksPage = 1;
    private int trendingBooksPage = 1;
    private int searchMoviesPage = 1;
    private int trendingMoviesPage = 1;

    private final MutableLiveData<List<Media>> searchMoviesLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Media>> trendingMoviesLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Media>> searchBooksLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Media>> trendingBooksLiveData = new MutableLiveData<>(new ArrayList<>());

    public SearchMediaViewModel(Application application) {
        super(application);
        theMovieDB = new TheMovieDBAPI(application.getString(R.string.tmdb_url), application.getString(R.string.TMDBAPIKEY));
        oLAPI = new OLAPI("https://openlibrary.org/");

        LoadFirstTrendingBooksPage();
        LoadFirstTrendingMoviesPage();
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
        trendingBooksPage = 1;
        trendingBooksLiveData.setValue(new ArrayList<>());
        oLAPI.trending(trendingBooksPage).thenAccept(x -> {
            List<Media> updatedBooks = new ArrayList<>();
            updatedBooks.addAll(x);
            trendingBooksLiveData.postValue(updatedBooks);
        });
    }

    public void LoadNextTrendingBooksPage() {
        trendingBooksPage += 1;
        oLAPI.trending(trendingBooksPage).thenAccept(x -> {
            List<Media> updatedBooks = new ArrayList<>(trendingBooksLiveData.getValue());
            updatedBooks.addAll(x);
            trendingBooksLiveData.postValue(updatedBooks);
        });
    }

    public void LoadFirstSearchBooksPage(String titleSearchBook) {
        searchBooksPage = 1;
        this.titleSearchBook = titleSearchBook;
        this.searchBooksLiveData.setValue(new ArrayList<>());
        oLAPI.searchItems(this.titleSearchBook, searchBooksPage).thenAccept(x -> {
            List<Media> updatedBooks = new ArrayList<>();
            updatedBooks.addAll(x);
            searchBooksLiveData.postValue(updatedBooks);
        });
    }

    public void LoadNextSearchBooksPage() {
        searchBooksPage += 1;
        oLAPI.searchItems(this.titleSearchBook, searchBooksPage).thenAccept(x -> {
            List<Media> updatedBooks = new ArrayList<>(searchBooksLiveData.getValue());
            updatedBooks.addAll(x);
            searchBooksLiveData.postValue(updatedBooks);
        });
    }

    public void LoadFirstTrendingMoviesPage() {
        trendingMoviesPage = 1;
        trendingMoviesLiveData.setValue(new ArrayList<>());
        theMovieDB.trending(20).thenAccept(x -> {
            List<Media> updatedMovies = x.stream().map(Movie::new).collect(Collectors.toList());
            trendingMoviesLiveData.postValue(updatedMovies);
        });
    }

    public void LoadNextTrendingMoviesPage() {
        trendingMoviesPage += 1;
        theMovieDB.trending(trendingMoviesPage).thenAccept(x -> {
            List<Media> updatedBooks = new ArrayList<>(trendingBooksLiveData.getValue());
            updatedBooks.addAll(x.stream().map(Movie::new).collect(Collectors.toList()));
            trendingMoviesLiveData.postValue(updatedBooks);
        });
    }

    public void LoadFirstSearchMoviesPage(String titleSearchMovie) {
        searchMoviesPage = 1;
        this.titleSearchMovie = titleSearchMovie;
        this.searchMoviesLiveData.setValue(new ArrayList<>());
        theMovieDB.searchItems(this.titleSearchMovie, 20).thenAccept(x -> {
            List<Media> updatedMovies = x.stream().map(Movie::new).collect(Collectors.toList());
            searchMoviesLiveData.postValue(updatedMovies);
        });
    }

    public void LoadNextSearchMoviesPage() {
        searchMoviesPage += 1;
        theMovieDB.searchItems(this.titleSearchMovie, searchMoviesPage).thenAccept(x -> {
            List<Media> updatedMovies = new ArrayList<>(searchMoviesLiveData.getValue());
            updatedMovies.addAll(x.stream().map(Movie::new).collect(Collectors.toList()));
            searchMoviesLiveData.postValue(updatedMovies);
        });
    }

    public SearchFragment.SearchCategory getCurrentCategory() {
        return currentCategory;
    }

    public void setCurrentCategory(SearchFragment.SearchCategory currentCategory) {
        this.currentCategory = currentCategory;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getTitleSearchBook() {
        return titleSearchBook;
    }

    public String getTitleSearchMovie() {
        return titleSearchMovie;
    }
}
