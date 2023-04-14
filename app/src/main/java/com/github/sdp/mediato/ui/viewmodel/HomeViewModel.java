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
import com.github.sdp.mediato.model.media.MediaType;
import com.github.sdp.mediato.model.media.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * The ViewModel for the {@link com.github.sdp.mediato.ui.HomeFragment}
 */
public class HomeViewModel extends AndroidViewModel {

    Application application;
    private final MutableLiveData<List<Media>> medias = new MutableLiveData<>(new ArrayList<>());
    private final TheMovieDBAPI movieApi;
    private final GBookAPI bookApi;
    private MediaType currentType;

    /**
     * Default constructor
     *
     * @param application the application that holds this ViewModel
     */
    public HomeViewModel(Application application) {
        super(application);
        this.application = application;
        movieApi = new TheMovieDBAPI(application.getApplicationContext().getString(R.string.tmdb_url),
                application.getApplicationContext().getString(R.string.TMDBAPIKEY));
        bookApi = new GBookAPI(application.getApplicationContext().getString(R.string.gbook_url));
        currentType = MediaType.MOVIE;
    }

    /**
     * Get new movies from the API (20 each call)
     */
    public void getMovies() {
        movieApi.trending(20)
                .thenApply(downloadedData ->
                        downloadedData.stream().map(Movie::new).collect(Collectors.toList()))
                .thenAccept(movies -> updateMediaList(movies, MediaType.MOVIE));
    }

    /**
     * Get new books from the API (40 each call)
     */
    public void getBooks(){
        bookApi.searchItems("potter", 40)
                .thenApply(list ->
                    list.stream().map(Book::new).collect(Collectors.toList()))
                .thenAccept(books -> updateMediaList(books, MediaType.BOOK));
    }

    /**
     * Getter for the LiveData containing the currently displayed medias
     * @return the medias LiveData
     */
    public LiveData<List<Media>> getMedias() {
        return medias;
    }

    /**
     * Deletes the internal cache of the API.
     * The API will now return the data that has already been returned
     */
    public void wipeOldData(){
        bookApi.clearCache();
        movieApi.clearCache();
    }

    private void updateMediaList(List<? extends Media> downloadedData, MediaType type){
            List<Media> completedList = new ArrayList<>();
            if (currentType == type) {
                completedList = medias.getValue();
            } else {
                wipeOldData();
            }
            completedList.addAll(downloadedData);
            medias.setValue(completedList);
            currentType = type;
    }

}
