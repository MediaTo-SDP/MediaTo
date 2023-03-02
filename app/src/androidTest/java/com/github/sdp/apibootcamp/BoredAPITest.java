package com.github.sdp.apibootcamp;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class BoredAPITest {
    private MockWebServer mockServer = new MockWebServer();

    @Before
    private void setup(){
        mockServer.enqueue(new MockResponse().setBody("{\"activity\":\"Hold a video game tournament with some friends\"," +
                "\"type\":\"social\"," +
                "\"participants\":4," +
                "\"price\":0," +
                "\"link\":\"\"," +
                "\"key\":\"2300257\"," +
                "\"accessibility\":0.1}"));
        try {
            mockServer.start(4578);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    private void WorkingServerWithJSONOutput() throws IOException {
        BoredAPI api = BoredAPI.createAPI(String.format("https://%s:%d", mockServer.getHostName(), mockServer.getPort()));
        BoredActivity activity = api.getActivity().execute().body();
        assertThat(activity.getActivity(), is("Hold a video game tournament with some friends"));
    }

    @After
    private void takeDown(){
        try {
            mockServer.shutdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    private void ServerWithoutErrorReturnRightObject(){

    }

}