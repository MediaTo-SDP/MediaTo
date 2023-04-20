package com.github.sdp.mediato.api.gbook;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import androidx.annotation.NonNull;

import com.github.sdp.mediato.api.gbook.models.GoogleBook;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import mockwebserver3.Dispatcher;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;

public class GbookApiTest {
    private final String SEARCHTERM = "searchTerm";
    // private final com.github.sdp.mediato.api.gbook.APITestStrings strings = new com.github.sdp.mediato.api.gbook.APITestStrings();
    private String search2;
    public final MockWebServer mockApi = new MockWebServer();
    private GBookAPI db;
    final Dispatcher DISPATCHER = new Dispatcher() {
        @NonNull
        @Override
        public MockResponse dispatch(@NonNull RecordedRequest recordedRequest) {
            switch (recordedRequest.getPath()) {
                case "/books/v1/volumes?q=searchTerm&langRestrict=en&startIndex=0&maxResults=40":
                    return new MockResponse().setResponseCode(200).setBody(APITestStrings.SEARCH1);
                case "/books/v1/volumes?q=searchTerm&langRestrict=en&startIndex=40&maxResults=40":
                    return new MockResponse().setResponseCode(200).setBody(APITestStrings.SEARCH2);
                case "/books/v1/volumes?q=searchTerm&langRestrict=en&startIndex=80&maxResults=40":
                    return new MockResponse().setResponseCode(200).setBody(APITestStrings.SEARCH3);
                case "/books/v1/volumes/ucSmAgAAQBAJ":
                    return new MockResponse().setResponseCode(200).setBody(APITestStrings.GET);
                default:
                    return new MockResponse().setResponseCode(404);
            }
        }
    };

    @Before
    public void setUp() throws IOException {
        mockApi.setBodyLimit(Long.MAX_VALUE);
        mockApi.setDispatcher(DISPATCHER);
        mockApi.start(8080);
        db = new GBookAPI(String.format("http://%s:8080", mockApi.getHostName()));
    }

    @Test
    // Search for a single item
    public void TestSearchingSingleItem() {
        GoogleBook book = db.searchItem(SEARCHTERM).join();
        assertThat(book.getId(), is("LanWAAAAMAAJ"));
        assertThat(book.getTitle(), is("The Maze and the Warrior"));
        assertThat(book.getIconURL(), is("http://books.google.com/books/content?id=LanWAAAAMAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api"));
        assertThat(book.getPublishedDate(), is("2004"));
        assertThat(book.getPosterURL(), is("https://books.google.com/books/content/images/frontcover/LanWAAAAMAAJ?fife=w3000-h6000"));
        assertThat(book.getOverview(), is("Craig Wright explores the complex symbolism of the labyrinth in architecture, religious thought, music, and dance from the Middle Ages to the present."));
        assertThat(book.getSubtitle(), is("Symbols in Architecture, Theology, and Music"));
        assertThat(book.getAuthors(), is(List.of("Craig Wright")));
    }

    @Test
    // Search multiple times one item does not return the same one
    public void TestSearchingMultipleSingleItems() {
        db.searchItem(SEARCHTERM).join();
        GoogleBook book = db.searchItem(SEARCHTERM).join();
        assertThat(book.getId(), is("Y98tAAAAYAAJ"));
    }

    @Test
    // Search multiple items at once returns a list
    public void TestSearchingList() {
        List<GoogleBook> books = db.searchItems(SEARCHTERM, 40).join();
        assertThat(books.get(0).getId(), is("LanWAAAAMAAJ"));
        assertThat(books.get(39).getId(), is("0LNaAAAAYAAJ"));
    }

    @Test
    // Searching multiple lists does not return the same list but the right list
    public void TestSearchingMultipleList() {
        List<GoogleBook> oldBooks = db.searchItems(SEARCHTERM, 40).join();
        List<GoogleBook> books = db.searchItems(SEARCHTERM, 40).join();
        assertThat(oldBooks.get(0).getId(), not(books.get(0).getId()));
        assertThat(books.get(0).getId(), is("njJSDwAAQBAJ"));
        assertThat(books.get(39).getId(), is("qTgLAAAAQBAJ"));
    }

    @Test
    // Subsequent request do not do request if data is available locally
    public void TestNoSearchingAdditionalRequest() {
        List<GoogleBook> books = db.searchItems(SEARCHTERM, 1).thenCompose((val) ->
                db.searchItems(SEARCHTERM,1)).thenCompose((val) ->
                db.searchItems(SEARCHTERM,100)).join();
        assertThat(books.size(), is(78));
    }


    @Test
    // The cache holds not returned values of precedent searches
    public void TestSearchCache() {
        db.searchItems(SEARCHTERM, 20).join();
        List<GoogleBook> books = db.searchItems(SEARCHTERM, 40).join();
        assertThat(books.get(0).getId(), is("2QY7AAAAIAAJ"));
        assertThat(books.get(39).getId(), is("qmmDDwAAQBAJ"));
    }

    @Test
    // Exceeding available data returns empty list
    public void TestEmptyListExcessOfSearching() {
        // We do three searches since we hard-coded only 2 return pages
        List<GoogleBook> books = db.searchItems(SEARCHTERM, 40).thenCompose((v) ->
                db.searchItems(SEARCHTERM, 40)).thenCompose(v ->
                db.searchItems(SEARCHTERM, 40)).join();
        assertThat(books.size(), is(0));
        // Takes the if empty
        assertThat(db.searchItems(SEARCHTERM, 40).join().size(), is(0));
    }


    @Test
    public void TestClearingTheCache() {
        List<GoogleBook> searchedMovies = db.searchItems(SEARCHTERM, 5).join();
        db.clearCache();
        GoogleBook searchedMovie = db.searchItem(SEARCHTERM).join();
        assertThat(searchedMovie.getId(), is(searchedMovies.get(0).getId()));
        assertThat(searchedMovie.getId(), is("LanWAAAAMAAJ"));

    }

    // Wrong URL throws ERROR
    @Test(expected = Exception.class)
    public void WrongURLWillThrowAnError() {
        // Throws an error, since the the mock server answer 404 to searches that do not use SEARCHTERM
        db.searchItems(" ", 1).join();
    }

    @Test
    public void GetReturnsTheMovieData(){
        GoogleBook book = db.get("ucSmAgAAQBAJ").join();
        assertThat(book.getTitle(), is("Lettres écarlates"));
        assertThat(book.getId(), is("ucSmAgAAQBAJ"));
        assertThat(book.getOverview(), is("<p>« L’un des meilleurs romans de bit-lit de tous les temps. » <b>All Things Urban Fantasy</b></p> <p>Meg Corbyn est une cassandra sangue, une prophétesse du sang, capable de prédire l’avenir lorsqu’elle s’incise la peau. Une malédiction qui lui a valu d’être traitée comme de la viande par des hommes sans scrupules prêts à la taillader pour s’enrichir. Mais aussi un don qui lui a permis de s’échapper et va la pousser à chercher refuge chez les Autres. Là où les lois humaines ne s’appliquent pas. Même si elle sait, grâce à cette vision, que Simon Wolfgard causera également sa perte. Car si le chef des loups est d’abord intrigué par cette humaine intrépide, peu de choses la séparent d’une simple proie à ses yeux... </p> <p>« Par moments brutalement réaliste, ce roman centré sur le combat d’une jeune femme en quête de liberté dégage aussi une certaine mélancolie. À ne surtout pas manquer ! » <br> <b>Romantic Times</b></p>"));
        assertThat(book.getPosterURL(), is("https://books.google.com/books/content/images/frontcover/ucSmAgAAQBAJ?fife=w3000-h6000"));
    }

    @After
    public void clearEverything() throws IOException {
        mockApi.shutdown();

    }


    public List<GoogleBook> getBooks() throws IOException {
        setUp();
        List<GoogleBook> data = db.searchItems(SEARCHTERM, 20).join();
        clearEverything();
        return data;

    }
}
