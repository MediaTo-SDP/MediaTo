package com.github.sdp.apibootcamp;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.io.IOException;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Response;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class BoredAPITest {
    public MockWebServer mockServer = new MockWebServer();

    @Before
    public void setup(){
        final Dispatcher dispatcher = new Dispatcher() {
            @NonNull
            @Override
            public MockResponse dispatch(@NonNull RecordedRequest request) throws InterruptedException {
                switch (request.getPath()){
                    case "/activity":
                        return new MockResponse().setResponseCode(200).setBody("{\"activity\":\"Hold a video game tournament with some friends\"," +
                                "\"type\":\"social\"," +
                                "\"participants\":4," +
                                "\"price\":0," +
                                "\"link\":\"\"," +
                                "\"key\":\"2300257\"," +
                                "\"accessibility\":0.1}");
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };
        try {
            mockServer.setDispatcher(dispatcher);
            mockServer.start(4578);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void WorkingServerWithJSONOutput() throws IOException {
        System.out.println(mockServer.getHostName());
        BoredAPI api = BoredAPI.createAPI(String.format("http://%s:%d", mockServer.getHostName(), mockServer.getPort()));
        Response<BoredActivity> response = api.getActivity().execute();
        if (!response.isSuccessful()){
            assertThat(1+1, is(3));
        }
        BoredActivity activity = response.body();
        assertThat(activity.getActivity(), is("Hold a video game tournament with some friends"));
    }

    @After
    public void takeDown(){
        try {
            mockServer.shutdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}