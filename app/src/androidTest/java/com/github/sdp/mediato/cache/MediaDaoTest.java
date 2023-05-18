package com.github.sdp.mediato.cache;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.sdp.mediato.cache.dao.MediaDao;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.MediaType;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

@RunWith(AndroidJUnit4.class)
public class MediaDaoTest {
    @Rule
    public final InstantTaskExecutorRule rule = new InstantTaskExecutorRule();
    private AppCache cache;
    private MediaDao mediaDao;
    private final Media[] medias1 = {
            new Media(MediaType.BOOK, "t1", "s1", "url1", "MediaDaoTestId1"),
            new Media(MediaType.MOVIE, "t2", "s2", "url2", "MediaDaoTestId2"),
            new Media(MediaType.BOOK, "t3", "s3", "url3", "MediaDaoTestId3"),
            new Media(MediaType.MOVIE, "t4", "s4", "url4", "MediaDaoTestId4")};
    private final Media[] medias2 = {
            new Media(MediaType.BOOK, "t5", "s5", "url5", "MediaDaoTestId1"),
            new Media(MediaType.MOVIE, "t6", "s6", "url6", "MediaDaoTestId2"),
            new Media(MediaType.BOOK, "t7", "s7", "url7", "MediaDaoTestId3"),
            new Media(MediaType.MOVIE, "t8", "s8", "url8", "MediaDaoTestId4")};

    @Before
    public void setup(){
        cache = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppCache.class)
            .allowMainThreadQueries()
            .build();
        mediaDao = cache.mediaDao();
    }

    /**
     * We can add data to cache with an array (vararg)
     */
    @Test
    public void addingDataWorks(){
        long[] rowIds = mediaDao.insert(medias1);
        assertThat(rowIds, is(new long[]{1, 2, 3, 4}));
    }

    /**
     * We can add data to the cache with a List
     */
    @Test
    public void addingListDataWorks(){
        long[] rowIds = mediaDao.insertAll(Arrays.asList(medias2));
        assertThat(rowIds, is(new long[]{1, 2, 3, 4}));
    }

    /**
     * We can retrieve data from the cache
     */
    @Test
    public void gettingDataWorks() {
        mediaDao.insert(medias1);
        testLiveData(mediaDao.getAllMedia(), (List<Media> data) -> {
            assertThat(data.get(0).getTitle(), is(medias1[0].getTitle()));
            assertThat(data.size(), is(4));
            return null;
        });
    }

    /**
     * We can get a specific media from the cache by its id and type
     */
    @Test
    public void gettingByIdWorks(){
        mediaDao.insert(medias1);
        Media media = mediaDao.getMediaFromTypeAndId(MediaType.BOOK, "MediaDaoTestId1");
        assertThat(media.getTitle(), is("t1"));
    }

    /**
     * We can update data in cache (No duplicate)
     */
    @Test
    public void updatingDataWorks(){
        mediaDao.insert(medias1);
        Media media = mediaDao.getMediaFromTypeAndId(MediaType.BOOK, "MediaDaoTestId1");
        assertThat(media.getTitle(), is("t1"));
        mediaDao.insert(medias2);
        media = mediaDao.getMediaFromTypeAndId(MediaType.BOOK, "MediaDaoTestId1");
        assertThat(media.getTitle(), is("t5"));
    }

    /**
     * We can search using terms from the summary
     */
    @Test
    public void searchingWorksWithSummaryTerms(){
        mediaDao.insert(medias1);
        testLiveData(mediaDao.search(MediaType.BOOK, "s"),
                (List<Media> data) -> {
                    assertThat(data.get(0).getTitle(), is("t1"));
                    assertThat(data.size(), is(2));
                    return null;
                });
        testLiveData(mediaDao.search(MediaType.BOOK, "s1"),
                (List<Media> data) -> {
                    assertThat(data.get(0).getTitle(), is("t1"));
                    assertThat(data.size(), is(1));
                    return null;
                });
    }

    /**
     * We can search using terms from the title
     */
    @Test
    public void searchingWorksWithTitleTerms(){
        mediaDao.insert(medias1);
        testLiveData(mediaDao.search(MediaType.BOOK, "t"),
                (List<Media> data) -> {
                    assertThat(data.get(0).getTitle(), is("t1"));
                    assertThat(data.size(), is(2));
                    return null;
        });
        testLiveData(mediaDao.search(MediaType.BOOK, "t1"),
                (List<Media> data) -> {
                    assertThat(data.get(0).getTitle(), is("t1"));
                    assertThat(data.size(), is(1));
                    return null;
                });
    }

    /**
     * We can search only in the title
     */
    @Test
    public void searchInTitleWorksWithTitleTerms(){
        mediaDao.insert(medias1);
        testLiveData(mediaDao.searchInTitle(MediaType.BOOK,"t1"),
                (List<Media> data)-> {
                    assertThat(data.get(0).getTitle(), is("t1"));
                    assertThat(data.size(), (is(1)));
                    return null;
                }
        );
        testLiveData(mediaDao.searchInTitle(MediaType.BOOK, "t"),
                (List<Media> data) -> {
                    assertThat(data.get(0).getTitle(), is("t1"));
                    assertThat(data.size(), is(2));
                    return null;
                });
    }

    /**
     * We can get all medias from a given type
     */
    @Test
    public void getAllMediaFromTypeWorks(){
        mediaDao.insert(medias1);
        testLiveData(mediaDao.getAllMediaFromType(MediaType.MOVIE),
                (List<Media> data) -> {
                assertThat(data.get(0).getTitle(), is("t2"));
                assertThat(data.size(), is(2));
            return null;
        });
    }

    /**
     * We can clean the media cache completely
     */
    @Test
    public void CleanMediasCompletelyCleansMediaCache(){
        mediaDao.insert(medias1);
        mediaDao.cleanMedias();
        testLiveData(mediaDao.getAllMedia(), (List<Media> data) -> {
            assertThat(data.size(), is(0));
            return null;
        });
    }


// Using function Void because we need to return the errors.
    public static void testLiveData(LiveData<List<Media>> liveData, Function<List<Media> , Void> asserts){
        CompletableFuture<Void> future = new CompletableFuture<>();
        Observer<List<Media>> observer = (List<Media> data) -> {
            try{
                asserts.apply(data);
                future.complete(null);
            } catch (Exception ignored) {}
        };
        liveData.observeForever(observer);
        try {
            future.get(1, TimeUnit.SECONDS);
        } catch (Exception ignored) {}
        liveData.removeObserver(observer);
    }
    @After
    public void teardown(){
        cache.close();
    }

}
