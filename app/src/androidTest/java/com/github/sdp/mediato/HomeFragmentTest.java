package com.github.sdp.mediato;

import static org.junit.Assert.assertEquals;

import android.widget.TextView;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class HomeFragmentTest {

  @Test
  public void testHomeFragmentTextView() {
   /* try (FragmentScenario<HomeFragment> scenario = FragmentScenario.launchInContainer(
        HomeFragment.class)) {
      scenario.onFragment(fragment -> {
        TextView textView = fragment.getView().findViewById(R.id.text_home);
        assertEquals("Home", textView.getText().toString());
      });
    }*/
  }

}

