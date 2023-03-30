package com.github.sdp.mediato.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

import com.github.sdp.mediato.model.media.Collection;
import com.github.sdp.mediato.model.media.CollectionType;
import com.github.sdp.mediato.model.media.Movie;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class CollectionTests {
    private static Map<String, Review> reviews = new HashMap<>();
    private static String SAMPLE_TITLE = "Harry Potter";
    private static String SAMPLE_DESCRIPTION = "Description";
    private static String SAMPLE_URL = "Url";
    private static int SAMPLE_ID = 1;

    private static String SAMPLE_USERNAME = "testUser";
    private static final Movie SAMPLE_MOVIE = new Movie(SAMPLE_TITLE , SAMPLE_DESCRIPTION, SAMPLE_URL, SAMPLE_ID);

    @BeforeClass
    public static void setUp(){
        reviews.put(SAMPLE_TITLE, new Review(SAMPLE_USERNAME, SAMPLE_MOVIE));
    }

    @Test
    //Tests that a custom collection is created properly
    public void createsCustomCollectionProperly(){
        Collection collection = new Collection("MyCustom", reviews);
        assertThat(collection.getCollectionType(), is(CollectionType.CUSTOM));
        assertThat(collection.getCollectionName(), is("MyCustom"));
        assertThat(CollectionType.CUSTOM.toString(), is("Custom"));
    }

    @Test
    //Tests that a favourites collection is created properly
    public void createsFavouriteCollectionProperly(){
        Collection collection = new Collection(CollectionType.FAVOURITES, reviews);
        assertThat(collection.getCollectionType(), is(CollectionType.FAVOURITES));
        assertThat(collection.getCollectionName(), is(CollectionType.FAVOURITES.toString()));
    }

    @Test
    //Tests that a recently watched collection is created properly
    public void createsRecentlyWatchedCollectionProperly(){
        Collection collection = new Collection(CollectionType.RECENTLY_WATCHED, reviews);
        assertThat(collection.getCollectionType(), is(CollectionType.RECENTLY_WATCHED));
        assertThat(collection.getCollectionName(), is(CollectionType.RECENTLY_WATCHED.toString()));
    }

    @Test
    //Tests that custom collection creation fails when the wrong constructor is used
    public void defaultCollectionConstructorFailsWhenTypeIsCustom(){
        assertThrows(
                IllegalArgumentException.class,
                () -> new Collection(CollectionType.CUSTOM, reviews)
        );
    }
}
