package com.github.sdp.mediato.api.openlibrary;

import com.github.sdp.mediato.api.API;
import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.model.media.Book;
import com.github.sdp.mediato.model.media.Media;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OLAPI implements API<Media> {

    private final OLAPIInterface api;

    private static final String TAG = "OLAPI";
    /**
     * Default constructor
     *
     * @param serverUrl domain name of the api (used to inject tests)
     */
    public OLAPI(String serverUrl) {
        Preconditions.checkNullOrEmptyString(serverUrl, "serverUrl");

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(String.class, new DescriptionDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        this.api = retrofit.create(OLAPIInterface.class);
    }

    @Override
    public CompletableFuture<List<Media>> searchItems(String title, int page) {
        return api.getSearchBook(title.replaceAll(" ","+"),page)
                .thenApply(olTrendingBooks -> olTrendingBooks.getBooks().stream()
                        .map(Book::new)
                        .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<List<Media>> trending(int page) {
        return api.getTrendingBooks(page)
                .thenApply(olTrendingBooks -> olTrendingBooks.getWorks().stream()
                        .map(Book::new)
                        .collect(Collectors.toList()));
    }

    public CompletableFuture<String> getDescription(String key) {
        return api.getBookDetails(key)
                .thenApply(olBookDetails -> {
                    String description = olBookDetails.getDescription();
                    description = description == null ? "No description :(" : description;
                    return description;
                });
    }

    private static class DescriptionDeserializer implements JsonDeserializer<String> {
        @Override
        public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return json.getAsString();
            } else if (json.isJsonObject()) {
                JsonObject descriptionObject = json.getAsJsonObject();
                return descriptionObject.get("value").getAsString();
            }
            return null;
        }
    }
}
