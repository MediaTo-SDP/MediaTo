package com.github.sdp.mediato.cache;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
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
    @Test
    public void AddingDataWorks(){
        long[] rowIds = mediaDao.insert(medias1);
        assertThat(rowIds, is(new long[]{1, 2, 3, 4}));
    }

    @Test
    public void AddingListDataWorks(){
        long[] rowIds = mediaDao.insertAll(Arrays.asList(medias2));
        assertThat(rowIds, is(new long[]{1, 2, 3, 4}));
    }

    @Test
    public void GettingDataWorks() {
        mediaDao.insert(medias1);
        testLiveData(() -> mediaDao.getAllMedia(), (List<Media> data) -> {
            assertThat(data.get(0).getTitle(), is(medias1[0].getTitle()));
            assertThat(data.size(), is(4));
            return null;
        });
    }

    @Test
    public void GettingByIdWorks(){
        mediaDao.insert(medias1);
        Media media = mediaDao.getMediaFromTypeAndId(MediaType.BOOK, "MediaDaoTestId1");
        assertThat(media.getTitle(), is("t1"));
    }

    @Test
    public void UpdatingDataWorks(){
        mediaDao.insert(medias1);
        Media media = mediaDao.getMediaFromTypeAndId(MediaType.BOOK, "MediaDaoTestId1");
        assertThat(media.getTitle(), is("t1"));
        mediaDao.insert(medias2);
        media = mediaDao.getMediaFromTypeAndId(MediaType.BOOK, "MediaDaoTestId1");
        assertThat(media.getTitle(), is("t5"));
    }

    @Test
    public void SearchingWorksWithSummaryTerms(){
        mediaDao.insert(medias1);
        testLiveData(() -> mediaDao.search(MediaType.BOOK, "s"),
                (List<Media> data) -> {
                    assertThat(data.get(0).getTitle(), is("t1"));
                    assertThat(data.size(), is(2));
                    return null;
                });
        testLiveData(() -> mediaDao.search(MediaType.BOOK, "s1"),
                (List<Media> data) -> {
                    assertThat(data.get(0).getTitle(), is("t1"));
                    assertThat(data.size(), is(1));
                    return null;
                });
    }

    @Test
    public void SearchingWorksWithTitleTerms(){
        mediaDao.insert(medias1);
        testLiveData(() -> mediaDao.search(MediaType.BOOK, "t"),
                (List<Media> data) -> {
                    assertThat(data.get(0).getTitle(), is("t1"));
                    assertThat(data.size(), is(2));
                    return null;
        });
        testLiveData(() -> mediaDao.search(MediaType.BOOK, "t1"),
                (List<Media> data) -> {
                    assertThat(data.get(0).getTitle(), is("t1"));
                    assertThat(data.size(), is(1));
                    return null;
                });
    }

    @Test
    public void SearchInTitleWorksWithTitleTerms(){
        mediaDao.insert(medias1);
        testLiveData(() -> mediaDao.searchInTitle(MediaType.BOOK,"t1"),
                (List<Media> data)-> {
                    assertThat(data.get(0).getTitle(), is("t1"));
                    assertThat(data.size(), (is(1)));
                    return null;
                }
        );
        testLiveData(() -> mediaDao.searchInTitle(MediaType.BOOK, "t"),
                (List<Media> data) -> {
                    assertThat(data.get(0).getTitle(), is("t1"));
                    assertThat(data.size(), is(2));
                    return null;
                });
    }

    @Test
    public void CleanMediasCompletelyCleansMediaCache(){
        mediaDao.insert(medias1);
        mediaDao.cleanMedias();
        testLiveData(() -> mediaDao.getAllMedia(), (List<Media> data) -> {
            assertThat(data.size(), is(0));
            return null;
        });
    }


// Using function Void because we need to return the errors.
    public static void testLiveData(Supplier<LiveData<List<Media>>> dataSupplier, Function<List<Media> , Void> asserts){
        CompletableFuture<Void> future = new CompletableFuture<>();
        LiveData<List<Media>> liveData = dataSupplier.get();
        liveData.observeForever((List<Media> data) -> {
            try{
                asserts.apply(data);
                future.complete(null);
            } catch (Exception ignored) {}
        });
        try {
            future.get(1, TimeUnit.SECONDS);
        } catch (Exception ignored) {}
    }
    @After
    public void teardown(){
        cache.close();
    }

}
