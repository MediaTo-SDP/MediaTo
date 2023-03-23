package com.github.sdp.mediato.utility;

import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.Movie;
import java.util.List;


/**
 * Utility class to return some random movie reviews. For now it is used for demo purposes, but it
 * can be deleted or used for tests later.
 */
public class SampleReviews {

  Media movie1 = new Movie("The Godfather",
      "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.",
      "https://example.com/the-godfather.jpg");
  Review review1 = new Review("Alice", movie1, 8, "One of the best movies I've ever seen.");


  Media movie2 = new Movie("The Shawshank Redemption",
      "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
      "https://example.com/the-shawshank-redemption.jpg");
  Review review2 = new Review("Alice", movie2, 7, "Simply amazing. The best movie ever made.");


  Media movie3 = new Movie("The Dark Knight",
      "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
      "https://example.com/the-dark-knight.jpg");
  Review review3 = new Review("Alice", movie3, 6);


  Media movie4 = new Movie("Inception",
      "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
      "https://example.com/inception.jpg");
  Review review4 = new Review("Alice", movie4, 9);


  Media movie5 = new Movie("One Flew Over the Cuckoo's Nest",
      "A criminal who feigns insanity is admitted to a mental institution where he rebels against the oppressive nurse and rallies up the scared patients.",
      "https://example.com/one-flew-over-the-cuckoos-nest.jpg");
  Review review5 = new Review("Alice", movie5, 10,
      "A thought-provoking and emotionally powerful movie that explores the human mind and its complexities.");


  Media movie6 = new Movie("Pirates of the Caribbean: Dead Man's Chest",
      "Captain Jack Sparrow races to recover the heart of Davy Jones to avoid enslaving his soul to Jones' service, as other friends and foes seek the heart for their own agenda as well.",
      "https://example.com/dead-mans-chest.jpg");
  Review review6 = new Review("Alice", movie6, 8);

  Media movie7 = new Movie("Forrest Gump",
      "Forrest Gump, a simple man with a low I.Q., who finds himself in extraordinary situations while trying to achieve his childhood sweetheart's dreams.",
      "https://example.com/forrest-gump.jpg");
  Review review7 = new Review("Alice", movie7, 7, "An all-time classic that never gets old.");

  Media movie8 = new Movie("The Matrix",
      "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.",
      "https://example.com/the-matrix.jpg");
  Review review8 = new Review("Alice", movie8, 10,
      "A groundbreaking movie that changed the course of sci-fi films forever.");

  Media movie9 = new Movie("The Silence of the Lambs",
      "A young F.B.I. cadet must receive the help of an incarcerated and manipulative cannibal killer to help catch another serial killer, a madman who skins his victims.",
      "https://example.com/the-silence-of-the-lambs.jpg");
  Review review9 = new Review("Alice", movie9, 9,
      "A thrilling masterpiece that keeps you on the edge of your seat.");

  List<Review> reviews = List.of(review1, review2, review3, review4, review5, review6, review7,
      review8, review9);
  int current = 0;

  public Review getMovieReview() {
    return reviews.get(current++ % reviews.size());
  }
}
