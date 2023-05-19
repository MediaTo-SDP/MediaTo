package com.github.sdp.mediato.ui.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.github.sdp.mediato.R;
import com.github.sdp.mediato.api.API;
import com.github.sdp.mediato.api.openlibrary.OLAPI;
import com.github.sdp.mediato.api.themoviedb.TheMovieDBAPI;
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

    private MediaDao mediaDao;
    private final MutableLiveData<List<Media>> liveData = new MutableLiveData<>(new ArrayList<>());

    public SearchMediaViewModel(Application application) {
        super(application);
        theMovieDBAPI = new TheMovieDBAPI(application.getString(R.string.tmdb_url), application.getString(R.string.TMDBAPIKEY));
        oLAPI = new OLAPI(application.getString(R.string.openlibrary_url));
    }

    public void loadFirstBookSearchPage(String title) {
        titleSearch = title;
        searchBooksPage = 1;
        loadFirstSearchPage(searchBooksPage, titleSearch, MediaType.BOOK);
    }


    public void loadFirstMovieSearchPage(String title) {
        titleSearch = title;
        searchMoviesPage = 1;
        loadFirstSearchPage(searchMoviesPage, titleSearch, MediaType.MOVIE);
    }

    public void loadNextMovieSearchPage() {
        searchMoviesPage += 1;
        loadNextSearchPage(searchMoviesPage, titleSearch, MediaType.MOVIE);
    }

    public void loadFirstMovieTrendingPage() {
        trendingMoviesPage = 1;
        loadFirstTrendingPage(trendingMoviesPage, MediaType.MOVIE);
    }

    public void loadNextMovieTrendingPage() {
        trendingMoviesPage += 1;
        loadNextTrendingPage(trendingMoviesPage, MediaType.MOVIE);
    }

    public void loadNextBookSearchPage() {
        searchBooksPage += 1;
        loadNextSearchPage(searchBooksPage, titleSearch, MediaType.BOOK);
    }

    public void loadFirstBookTrendingPage() {
        trendingBooksPage = 1;
        loadFirstTrendingPage( trendingBooksPage, MediaType.BOOK);
    }

    public void loadNextBookTrendingPage() {

        trendingBooksPage += 1;
        loadNextTrendingPage(trendingBooksPage, MediaType.BOOK);
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
                    List<Media> updatedMedia = new ArrayList<>(Objects.requireNonNull(liveData.getValue()));
                    updatedMedia.addAll(medias);
                    liveData.postValue(updatedMedia);
                    mediaDao.insertAll(medias);
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
        api.trending(page).handle((x, throwable) -> {
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
        api.trending(page).handle((x, throwable) -> {
            try{
                if (throwable == null){
                    List<Media> updatedMedia = new ArrayList<>(Objects.requireNonNull(liveData.getValue()));
                    updatedMedia.addAll(x);
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
}
