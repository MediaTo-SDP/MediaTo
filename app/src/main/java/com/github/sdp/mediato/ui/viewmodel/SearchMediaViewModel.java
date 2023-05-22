package com.github.sdp.mediato.ui.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.github.sdp.mediato.R;
import com.github.sdp.mediato.api.API;
import com.github.sdp.mediato.api.openlibrary.OLAPI;
import com.github.sdp.mediato.api.themoviedb.TheMovieDBAPI;
import com.github.sdp.mediato.data.GenreMovies;
import com.github.sdp.mediato.cache.dao.MediaDao;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.MediaType;
import com.github.sdp.mediato.ui.SearchFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    private MediaDao mediaDao;
    private final MutableLiveData<List<Media>> liveData = new MutableLiveData<>(new ArrayList<>());

    public SearchMediaViewModel(Application application) {
        super(application);
        theMovieDBAPI = new TheMovieDBAPI(application.getString(R.string.tmdb_url), application.getString(R.string.TMDBAPIKEY));
        oLAPI = new OLAPI(application.getString(R.string.openlibrary_url));
    }

    public void loadFirstSearchPage(String title, MediaType type) {
        if (type != MediaType.BOOK && type != MediaType.MOVIE) return;
        titleSearch = title;
        searchBooksPage = 1;
        loadFirstSearchPage(searchBooksPage, titleSearch, type);
    }

    public void loadNextSearchPage(MediaType type) {
        if (type != MediaType.BOOK && type != MediaType.MOVIE) return;
        searchMoviesPage += 1;
        loadNextSearchPage(searchMoviesPage, titleSearch, type);
    }

    public void loadFirstTrendingPage(MediaType type) {
        if (type != MediaType.BOOK && type != MediaType.MOVIE) return;
        trendingMoviesPage = 1;
        loadFirstTrendingPage(trendingMoviesPage, type);
    }

    public void loadNextTrendingPage(MediaType type) {
        if (type != MediaType.BOOK && type != MediaType.MOVIE) return;
        trendingMoviesPage += 1;
        loadNextTrendingPage(trendingMoviesPage, type);
    }


    private void loadFirstSearchPage(int page, String title, MediaType type) {
        API<Media> api = (type == MediaType.BOOK) ? oLAPI: theMovieDBAPI;

        liveData.setValue(new ArrayList<>());
        api.searchItems(title, page)
            .handle(((medias, throwable) -> {
                // try catch to avoid silencing error with the handle function
                try{
                    if (throwable == null){
                        mediaDao.insertAll(medias);
                        liveData.postValue(medias);
                    } else {
                        liveData.postValue(mediaDao.searchInTitle(type, title));
                    }
                } catch (Exception e) {
                    printException(e);
                }
                return null;
            }));
    }

    private void loadNextSearchPage(int page, String title, MediaType type) {
        API<Media> api = (type == MediaType.BOOK) ? oLAPI: theMovieDBAPI;
        api.searchItems(title, page).handle((medias, throwable) -> {
            try{
                if (throwable == null){
                    addToLiveData(medias);
                } else{
                    liveData.postValue(mediaDao.searchInTitle(type, title));
                }
            } catch (Exception e) {
                printException(e);
            }
            return null;
        });
    }

    private void loadFirstTrendingPage(int page, MediaType type) {
        API<Media> api = (type == MediaType.BOOK) ? oLAPI: theMovieDBAPI;
        liveData.setValue(new ArrayList<>());
        api.trending(year, genre, page).handle((x, throwable) -> {
            try {
                if (throwable == null) {
                    List<Media> updatedMedia = new ArrayList<>(x);
                    liveData.postValue(updatedMedia);
                    mediaDao.insertAll(x);
                } else {
                    liveData.postValue(mediaDao.getAllMediaFromType(type));
                }
            } catch (Exception e){
                printException(e);
            }
            return null;
        });
    }

    private void loadNextTrendingPage(int page, MediaType type) {
        API<Media> api = (type == MediaType.BOOK) ? oLAPI: theMovieDBAPI;
        api.trending(page).handle((medias, throwable) -> {
            try{
                if (throwable == null){
                    addToLiveData(medias);
                } else {
                    liveData.postValue(mediaDao.getAllMediaFromType(type));
                }
            } catch (Exception e){
                printException(e);
            }
            return null;
        });
    }

    public MutableLiveData<List<Media>> getLiveData() {
        return liveData;
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

    public void setMediaDao(MediaDao mediaDao) {
        this.mediaDao = mediaDao;
    }
    private void printException(Exception e) {
        System.out.println(e.getMessage());
        System.out.println(Arrays.toString(e.getStackTrace()));
    }

    private void addToLiveData(List<Media> medias){
        List<Media> updatedMedia = new ArrayList<>(Objects.requireNonNull(liveData.getValue()));
        updatedMedia.addAll(medias);
        liveData.postValue(updatedMedia);
        mediaDao.insertAll(medias);
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
