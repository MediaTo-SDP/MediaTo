package com.github.sdp.mediato.ui.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.github.sdp.mediato.R;
import com.github.sdp.mediato.api.API;
import com.github.sdp.mediato.api.openlibrary.OLAPI;
import com.github.sdp.mediato.api.themoviedb.TheMovieDBAPI;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.ui.SearchFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class SearchMediaViewModel extends AndroidViewModel {
    private SearchFragment.SearchCategory currentCategory = SearchFragment.SearchCategory.PEOPLE;
    private String searchQuery = "";
    private final API<Media> theMovieDBAPI;
    private final API<Media> oLAPI;
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
        theMovieDBAPI = new TheMovieDBAPI(application.getString(R.string.tmdb_url), application.getString(R.string.TMDBAPIKEY));
        oLAPI = new OLAPI(application.getString(R.string.openlibrary_url));

        loadFirstBookTrendingPage();
        loadFirstMovieTrendingPage();
    }

    public void loadFirstMovieSearchPage(String title) {
        titleSearchMovie = title;
        searchMoviesPage = 1;
        loadFirstSearchPage(searchMoviesLiveData, () -> searchMoviesPage, () -> titleSearchMovie, theMovieDBAPI);
    }

    public void loadNextMovieSearchPage() {
        searchMoviesPage += 1;
        loadNextSearchPage(searchMoviesLiveData, () -> searchMoviesPage, () -> titleSearchMovie, theMovieDBAPI);
    }

    public void loadFirstMovieTrendingPage() {
        trendingMoviesPage = 1;
        loadFirstTrendingPage(trendingMoviesLiveData, () -> trendingMoviesPage, theMovieDBAPI);
    }

    public void loadNextMovieTrendingPage() {
        trendingMoviesPage += 1;
        loadNextTrendingPage(trendingMoviesLiveData, () -> trendingMoviesPage, theMovieDBAPI);
    }

    public void loadFirstBookSearchPage(String title) {
        titleSearchBook = title;
        searchBooksPage = 1;
        loadFirstSearchPage(searchBooksLiveData, () -> searchBooksPage, () -> titleSearchBook, oLAPI);
    }

    public void loadNextBookSearchPage() {
        searchBooksPage += 1;
        loadNextSearchPage(searchBooksLiveData, () -> searchBooksPage, () -> titleSearchBook, oLAPI);
    }

    public void loadFirstBookTrendingPage() {
        trendingBooksPage = 1;
        loadFirstTrendingPage(trendingBooksLiveData, () -> trendingBooksPage, oLAPI);
    }

    public void loadNextBookTrendingPage() {
        trendingBooksPage += 1;
        loadNextTrendingPage(trendingBooksLiveData, () -> trendingBooksPage, oLAPI);
    }

    private void loadFirstSearchPage(MutableLiveData<List<Media>> liveData, IntSupplier pageSupplier, Supplier<String> titleSupplier, API<Media> api) {
        int page = pageSupplier.getAsInt();
        String title = titleSupplier.get();
        liveData.setValue(new ArrayList<>());
        api.searchItems(title, page).thenAccept(x -> {
            List<Media> updatedMedia = new ArrayList<>(x);
            liveData.postValue(updatedMedia);
        });
    }

    private void loadNextSearchPage(MutableLiveData<List<Media>> liveData, IntSupplier pageSupplier, Supplier<String> titleSupplier, API<Media> api) {
        int page = pageSupplier.getAsInt();
        String title = titleSupplier.get();
        api.searchItems(title, page).thenAccept(x -> {
            List<Media> updatedMedia = new ArrayList<>(Objects.requireNonNull(liveData.getValue()));
            updatedMedia.addAll(x);
            liveData.postValue(updatedMedia);
        });
    }

    private void loadFirstTrendingPage(MutableLiveData<List<Media>> liveData, IntSupplier pageSupplier, API<Media> api) {
        int page = pageSupplier.getAsInt();
        liveData.setValue(new ArrayList<>());
        api.trending(page).thenAccept(x -> {
            List<Media> updatedMedia = new ArrayList<>(x);
            liveData.postValue(updatedMedia);
        });
    }

    private void loadNextTrendingPage(MutableLiveData<List<Media>> liveData, IntSupplier pageSupplier, API<Media> api) {
        int page = pageSupplier.getAsInt();
        api.trending(page).thenAccept(x -> {
            List<Media> updatedMedia = new ArrayList<>(Objects.requireNonNull(liveData.getValue()));
            updatedMedia.addAll(x);
            liveData.postValue(updatedMedia);
        });
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
