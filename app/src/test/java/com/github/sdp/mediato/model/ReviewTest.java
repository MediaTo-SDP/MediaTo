package com.github.sdp.mediato.model;

import org.junit.Assert;
import org.junit.Test;

public class ReviewTest {

  @Test
  public void testFormatRating() {
    Assert.assertEquals("○○○○○○○○○○", Review.formatRating(0));
    Assert.assertEquals("●●●●●○○○○○", Review.formatRating(5));
    Assert.assertEquals("●●●●●●●●●●", Review.formatRating(10));
  }

}
