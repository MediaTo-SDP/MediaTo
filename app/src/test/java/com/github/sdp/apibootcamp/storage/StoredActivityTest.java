package com.github.sdp.apibootcamp.storage;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.github.sdp.apibootcamp.Storage.StoredActivity;

import org.junit.Test;


public class StoredActivityTest {
    @Test
    public void checkUIDsCoherence(){
        for (int i = 0; i < 10; i++) {
            StoredActivity newActivity = new StoredActivity("Activity " + i);
            assertThat(newActivity.uid, is(i));
            assertThat(newActivity.activity, is("Activity " + i));
        }
    }


}
