package com.github.sdp.mediato.ui.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.sdp.mediato.R;
import com.github.sdp.mediato.api.API;
import com.github.sdp.mediato.api.openlibrary.OLAPI;
import com.github.sdp.mediato.api.themoviedb.TheMovieDBAPI;
import com.github.sdp.mediato.cache.dao.MediaDao;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.MediaType;
import com.github.sdp.mediato.ui.SearchFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

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
        loadFirstSearchPage(searchMoviesLiveData, searchMoviesPage, titleSearch, theMovieDBAPI, MediaType.MOVIE);
        loadFirstSearchPage(searchBooksLiveData, searchBooksPage, titleSearch, oLAPI, MediaType.BOOK);
    }

    public void loadNextMovieSearchPage() {
        searchMoviesPage += 1;
        loadNextSearchPage(searchMoviesLiveData, searchMoviesPage, titleSearch, theMovieDBAPI, MediaType.MOVIE);
    }

    public void loadFirstMovieTrendingPage() {
        trendingMoviesPage = 1;
        loadFirstTrendingPage(trendingMoviesLiveData, trendingMoviesPage, theMovieDBAPI, MediaType.MOVIE);
    }

    public void loadNextMovieTrendingPage() {
        trendingMoviesPage += 1;
        loadNextTrendingPage(trendingMoviesLiveData, trendingMoviesPage, theMovieDBAPI, MediaType.MOVIE);
    }

    public void loadNextBookSearchPage() {
        searchBooksPage += 1;
        loadNextSearchPage(searchBooksLiveData, searchBooksPage, titleSearch, oLAPI, MediaType.BOOK);
    }

    public void loadFirstBookTrendingPage() {
        trendingBooksPage = 1;
        loadFirstTrendingPage(trendingBooksLiveData, trendingBooksPage, oLAPI, MediaType.BOOK);
    }

    public void loadNextBookTrendingPage() {
        trendingBooksPage += 1;
        loadNextTrendingPage(trendingBooksLiveData, trendingBooksPage, oLAPI, MediaType.BOOK);
    }

    private void loadFirstSearchPage(MutableLiveData<List<Media>> liveData, int page, String title, API<Media> api, MediaType type) {
        liveData.setValue(new ArrayList<>());
        api.searchItems(title, page)
            .handle(((medias, throwable) -> {
                if (throwable == null){
                    try {
                        mediaDao.insertAll(medias);
                        liveData.postValue(medias);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                } else {
                    try {
                        LiveData<List<Media>> result = mediaDao.searchInTitle(type, title);
                        result.observeForever(liveData::postValue);
                    } catch (Exception e) {
                        System.out.println(e);
                    }

                }
                return null;
    }));
    }

    private void loadNextSearchPage(MutableLiveData<List<Media>> liveData, int page, String title, API<Media> api, MediaType type) {
        api.searchItems(title, page).handle((medias, throwable) -> {
            if (throwable == null){
                List<Media> updatedMedia = new ArrayList<>(Objects.requireNonNull(liveData.getValue()));
                updatedMedia.addAll(medias);
                liveData.postValue(updatedMedia);
                mediaDao.insertAll(medias);
            } else{
                LiveData<List<Media>> result = mediaDao.searchInTitle(type, title);
                result.observeForever(liveData::postValue);
            }
            return null;
        });
    }

    private void loadFirstTrendingPage(MutableLiveData<List<Media>> liveData, int page, API<Media> api, MediaType type) {
        liveData.setValue(new ArrayList<>());
        api.trending(page).thenApply(x -> {
            List<Media> updatedMedia = new ArrayList<>(x);
            liveData.postValue(updatedMedia);
            return updatedMedia;
        });
    }

    private void loadNextTrendingPage(MutableLiveData<List<Media>> liveData, int page, API<Media> api, MediaType type) {
        api.trending(page).thenApply(x -> {
            List<Media> updatedMedia = new ArrayList<>(Objects.requireNonNull(liveData.getValue()));
            updatedMedia.addAll(x);
            liveData.postValue(updatedMedia);
            return updatedMedia;
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

    public void setMediaDao(MediaDao mediaDao) {
        this.mediaDao = mediaDao;
    }


}
