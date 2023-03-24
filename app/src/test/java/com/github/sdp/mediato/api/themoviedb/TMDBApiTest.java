package com.github.sdp.mediato.api.themoviedb;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import android.accounts.NetworkErrorException;

import androidx.annotation.NonNull;

import com.github.sdp.mediato.api.themoviedb.models.TMDBMovie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import mockwebserver3.Dispatcher;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;

public class TMDBApiTest {
    private final String APIKEY = "apiKey";
    private final String SEARCHTERM = "searchTerm";
    public final MockWebServer mockApi = new MockWebServer();
    private TheMovieDB db;
    final Dispatcher DISPATCHER = new Dispatcher() {
        @NonNull
        @Override
        public MockResponse dispatch(@NonNull RecordedRequest recordedRequest) {
            switch (recordedRequest.getPath()) {
                case "/3/search/movie?api_key=apiKey&query=searchTerm&language=en-US&page=1":
                    return new MockResponse()
                            .setResponseCode(200)
                            .setBody(APITestStrings.MULAN1);
                case "/3/search/movie?api_key=apiKey&query=searchTerm&language=en-US&page=2":
                    return new MockResponse()
                            .setResponseCode(200)
                            .setBody(APITestStrings.MULAN2);
                case "/3/trending/movie/week?api_key=apiKey&language=en-US&page=1":
                    return new MockResponse()
                            .setResponseCode(200)
                            .setBody(APITestStrings.TRENDING);
                case "/3/trending/movie/week?api_key=apiKey&language=en-US&page=2":
                    return new MockResponse()
                            .setResponseCode(200)
                            .setBody(APITestStrings.TRENDING2);
                default:
                    return new MockResponse().setResponseCode(404);
            }
        }
    };

    @Before
    public void setUp() throws IOException {
        mockApi.setDispatcher(DISPATCHER);
        mockApi.start(8080);
        db = new TheMovieDB(String.format("http://%s:8080/3/", mockApi.getHostName()), APIKEY);
    }

    @Test
    // Search for a single item
    public void TestSearchingSingleItem() {
        TMDBMovie movie = db.searchItem(SEARCHTERM).join();
        assertThat(movie.getId(), is(10674));
        assertThat(movie.getTitle(), is("Mulan"));
        assertThat(movie.getPoster_path(), is("https://image.tmdb.org/t/p/original/5TYgKxYhnhRNNwqnRAKHkgfqi2G.jpg"));
        assertThat(movie.getRelease_date(), is("1998-06-18"));
        assertThat(movie.getIcon_path(), is("https://image.tmdb.org/t/p/w154/5TYgKxYhnhRNNwqnRAKHkgfqi2G.jpg"));
        assertThat(movie.getOverview(), is("To save her father from certain death in the army, a young woman secretly enlists in his place and becomes one of China's greatest heroines in the process."));
    }

    @Test
    // Search multiple times one item does not return the same one
    public void TestSearchingMultipleSingleItems() {
        db.searchItem(SEARCHTERM).join();
        TMDBMovie movie = db.searchItem(SEARCHTERM).join();
        assertThat(movie.getId(), is(337401));
    }

    @Test
    // Search multiple items at once returns a list
    public void TestSearchingList() {
        List<TMDBMovie> movies = db.searchItems(SEARCHTERM, 30).join();
        assertThat(movies.get(0).getId(), is(10674));
        assertThat(movies.get(19).getId(), is(86705));
    }

    @Test
    // Searching multiple lists does not return the same list but the right list
    public void TestSearchingMultipleList() {
        List<TMDBMovie> oldMovies = db.searchItems(SEARCHTERM, 30).join();
        List<TMDBMovie> movies = db.searchItems(SEARCHTERM, 30).join();
        assertThat(oldMovies.get(0).getId(), not(movies.get(0).getId()));
        assertThat(movies.get(0).getId(), is(420564));
        assertThat(movies.get(19).getId(), is(1077647));
    }

    @Test
    // The cache holds not returned values of precedent searches
    public void TestSearchCache() {
        db.searchItems(SEARCHTERM, 10).join();
        List<TMDBMovie> movies = db.searchItems(SEARCHTERM, 20).join();
        assertThat(movies.get(0).getId(), is(221166));
        assertThat(movies.get(19).getId(), is(316876));
    }

    // Exceeding available data returns empty list
    public void TestEmptyListExcessOfSearching() {
        List<TMDBMovie> movies = db.searchItems(SEARCHTERM, 20).thenCompose((v) ->
                db.searchItems(SEARCHTERM, 20)).thenCompose(v ->
                db.searchItems(SEARCHTERM, 20)).join();
        assertThat(movies.size(), is(0));
    }

    @Test
    // The trending request returns a list
    public void TestTrendingList() {
        List<TMDBMovie> movies = db.trending(30).join();
        assertThat(movies.get(0).getId(), is(937278));
        assertThat(movies.get(19).getId(), is(646389));
    }

    @Test
    // Multiple trending requests does not returns the same list and the right ones
    public void TestTrendingMultipleList() {
        List<TMDBMovie> oldMovies = db.trending(30).join();
        List<TMDBMovie> movies = db.trending(30).join();
        assertThat(movies.get(0).getId(), not(oldMovies.get(0).getId()));
        assertThat(movies.get(0).getId(), is(850871));
        assertThat(movies.get(19).getId(), is(1067282));
    }

    @Test
    // Subsequent trending requests receive unused cached data
    public void TestTrendingCache() {
        db.trending(10).join();
        List<TMDBMovie> movies = db.trending(20).join();
        assertThat(movies.get(0).getId(), is(631842));
        assertThat(movies.get(19).getId(), is(1081291));
    }

    @Test
    // Subsequent request do not do request if the data is available
    public void TestNoAdditionalRequest() {
        List<TMDBMovie> movies = db.trending(1).thenCompose((val) ->
                db.trending(1)).thenCompose((val) ->
                db.trending(100)).join();
        assertThat(movies.size(), is(38));
    }

    // Exceeding available data returns empty list
    public void TestEmptyListExcessOfTrending() {
        List<TMDBMovie> movies = db.trending(20).thenCompose((v) ->
                db.trending(20)).thenCompose(v ->
                db.trending(20)).thenCompose(v ->
                db.trending(20)).join();
        assertThat(movies.size(), is(0));
    }

    @Test
    public void TestClearingTheCache() {
        List<TMDBMovie> searchedMovies = db.searchItems(SEARCHTERM, 5).join();
        List<TMDBMovie> trendingMovies = db.trending(5).join();
        db.clearCache();
        TMDBMovie searchedMovie = db.searchItem(SEARCHTERM).join();
        TMDBMovie trendingMovie = db.trending(1).join().get(0);
        assertThat(searchedMovie.getId(), is(searchedMovies.get(0).getId()));
        assertThat(searchedMovie.getId(), is(10674));
        assertThat(trendingMovie.getId(), is(937278));
        assertThat(trendingMovie.getId(), is(trendingMovies.get(0).getId()));

    }

    // Wrong URL throws ERROR
    @Test(expected = NetworkErrorException.class)
    public void WrongURLWillThrowAnError() {
        // Throws an error, since the the mock server answer 404 to searches that do not use SEARCHTERM
        db.searchItems(" ", 1).join();
    }

    @After
    public void clearEverything() throws IOException {
        mockApi.shutdown();
    }

}
