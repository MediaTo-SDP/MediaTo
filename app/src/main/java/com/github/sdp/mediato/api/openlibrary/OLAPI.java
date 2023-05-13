package com.github.sdp.mediato.api.openlibrary;

import com.github.sdp.mediato.api.API;
import com.github.sdp.mediato.api.openlibrary.models.OLBookDetails;
import com.github.sdp.mediato.api.openlibrary.models.OLTrendingBook;
import com.github.sdp.mediato.api.openlibrary.models.OLTrendingBooks;
import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.model.media.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OLAPI implements API {

    private final OLAPIInterface api;

    private static final String TAG = "OLAPI";
    /**
     * Default constructor
     *
     * @param serverUrl domain name of the api (used to inject tests)
     */
    public OLAPI(String serverUrl) {
        Preconditions.checkNullOrEmptyString(serverUrl, "serverUrl");

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.BODY); // Use Level.BASIC for just the request method and URL.

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        this.api = retrofit.create(OLAPIInterface.class);
    }
    @Override
    public CompletableFuture searchItem(String s) {
        return null;
    }

    @Override
    public CompletableFuture<List<String>> searchItems(String s, int count) {
        return null;
    }


//    public CompletableFuture<List<Book>> trending(int page) {
//        return api.getTrendingBooks(page)
//                .thenApply(olTrendingBooks -> olTrendingBooks.getWorks().stream()
//                        .map(book -> new Book(
//                                        book.getKey(),
//                                        book.getTitle(),
//                                        "",
//                                        book.getCoverI())
//                        )
//                        .collect(Collectors.toList()));
//    }

    public CompletableFuture<List<Book>> trending(int page) {
        return api.getTrendingBooks(page)
                .thenApply(olTrendingBooks -> olTrendingBooks.getWorks().stream()
                        .map(Book::new)
                        .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture get(String id) {
        return null;
    }
}
