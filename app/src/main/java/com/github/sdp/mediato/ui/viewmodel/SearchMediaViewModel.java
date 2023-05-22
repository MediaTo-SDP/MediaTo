package com.github.sdp.mediato.ui.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.github.sdp.mediato.R;
import com.github.sdp.mediato.api.API;
import com.github.sdp.mediato.api.openlibrary.OLAPI;
import com.github.sdp.mediato.api.themoviedb.TheMovieDBAPI;
import com.github.sdp.mediato.data.GenreMovies;
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
    private String titleSearch = "";
    private int searchBooksPage = 1;
    private int trendingBooksPage = 1;
    private int searchMoviesPage = 1;
    private int trendingMoviesPage = 1;
    private String year_filter = "Year";
    private String genre_filter = "Genre";

    private Integer year;
    private Integer genre;

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

    public void loadFirstMovieBookSearchPage(String title) {
        titleSearch = title;
        searchBooksPage = 1;
        searchMoviesPage = 1;
        loadFirstSearchPage(searchMoviesLiveData, () -> searchMoviesPage, () -> titleSearch, theMovieDBAPI);
        loadFirstSearchPage(searchBooksLiveData, () -> searchBooksPage, () -> titleSearch, oLAPI);
    }

    public void loadNextMovieSearchPage() {
        searchMoviesPage += 1;
        loadNextSearchPage(searchMoviesLiveData, () -> searchMoviesPage, () -> titleSearch, theMovieDBAPI);
    }

    public void loadFirstMovieTrendingPage() {
        trendingMoviesPage = 1;
        loadFirstTrendingPage(trendingMoviesLiveData, () -> trendingMoviesPage, theMovieDBAPI);
    }

    public void loadNextMovieTrendingPage() {
        trendingMoviesPage += 1;
        loadNextTrendingPage(trendingMoviesLiveData, () -> trendingMoviesPage, theMovieDBAPI);
    }

    public void loadNextBookSearchPage() {
        searchBooksPage += 1;
        loadNextSearchPage(searchBooksLiveData, () -> searchBooksPage, () -> titleSearch, oLAPI);
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
        api.trending(year, genre, page).thenAccept(x -> {
            List<Media> updatedMedia = new ArrayList<>(x);
            liveData.postValue(updatedMedia);
        });
    }

    private void loadNextTrendingPage(MutableLiveData<List<Media>> liveData, IntSupplier pageSupplier, API<Media> api) {
        int page = pageSupplier.getAsInt();
        api.trending(year, genre, page).thenAccept(x -> {
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

    public String getTitleSearch() {
        return titleSearch;
    }

    public String getYear_filter() {
        return year_filter;
    }

    public void setYear_filter(String year_filter) {
        this.year_filter = year_filter;
        this.year = year_filter.equals("Year") ? null : Integer.parseInt(year_filter);
    }

    public String getGenre_filter() {
        return genre_filter;
    }

    public void setGenre_filter(String genre_filter) {
        this.genre_filter = genre_filter;
        this.genre = genre_filter.equals("Genre") ? null : GenreMovies.getGenreId(genre_filter);
    }
}
