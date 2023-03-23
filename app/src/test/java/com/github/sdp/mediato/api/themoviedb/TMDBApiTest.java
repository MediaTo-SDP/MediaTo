package com.github.sdp.mediato.api.themoviedb;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
    public void TestSearchingSingleItem() {
        TMDBMovie movie = db.searchItem(SEARCHTERM).join();
        assertThat(movie.getId(), is(10674));
        assertThat(movie.getTitle(), is("Mulan"));
        assertThat(movie.getPoster_path(), is("https://image.tmdb.org/t/p/original/5TYgKxYhnhRNNwqnRAKHkgfqi2G.jpg"));
        assertThat(movie.getRelease_date(), is("1998-06-18"));
    }

    @Test
    public void TestSearchingMultipleSingleItems() {
        db.searchItem(SEARCHTERM).join();
        TMDBMovie movie = db.searchItem(SEARCHTERM).join();
        assertThat(movie.getId(), is(337401));
    }

    @Test
    public void TestSearchingList() {
        List<TMDBMovie> movies = db.searchItems(SEARCHTERM, 30).join();
        assertThat(movies.get(0).getId(), is(10674));
        assertThat(movies.get(19).getId(), is(86705));
    }

    @Test
    public void TestSearchingMultipleList() {
        db.searchItems(SEARCHTERM, 30).join();
        List<TMDBMovie> movies = db.searchItems(SEARCHTERM, 30).join();
        assertThat(movies.get(0).getId(), is(420564));
        assertThat(movies.get(19).getId(), is(1077647));
    }

    @Test
    public void TestSearchCache() {
        db.searchItems(SEARCHTERM, 10).join();
        List<TMDBMovie> movies = db.searchItems(SEARCHTERM, 20).join();
        assertThat(movies.get(0).getId(), is(221166));
        assertThat(movies.get(19).getId(), is(316876));
    }

    @Test
    public void TestTrendingList() {
        List<TMDBMovie> movies = db.trending(30).join();
        assertThat(movies.get(0).getId(), is(937278));
        assertThat(movies.get(19).getId(), is(646389));
    }

    @Test
    public void TestTrendingMultipleList() {
        db.trending(30).join();
        List<TMDBMovie> movies = db.trending(30).join();
        assertThat(movies.get(0).getId(), is(850871));
        assertThat(movies.get(19).getId(), is(1067282));
    }

    @Test
    public void TestTrendingCache() {
        db.trending(10).join();
        List<TMDBMovie> movies = db.trending(20).join();
        assertThat(movies.get(0).getId(), is(631842));
        assertThat(movies.get(19).getId(), is(1081291));
    }

    @Test
    public void TestClearingTheCache() {
        List<TMDBMovie> movies = db.searchItems(SEARCHTERM, 5).join();
        db.clearCache();
        TMDBMovie movie = db.searchItem(SEARCHTERM).join();
        assertThat(movie.getId(), is(movies.get(0).getId()));
        assertThat(movie.getId(), is(10674));

    }

    @After
    public void clearEverything() throws IOException {
        mockApi.shutdown();
    }

}
