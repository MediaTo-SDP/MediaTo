package com.github.sdp.mediato.utility;

import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.Movie;
import java.util.List;

public class SampleReviews {

  Media movie1 = new Movie("The Godfather",
      "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.",
      "https://example.com/the-godfather.jpg");
  Review review1 = new Review("Alice", movie1, 9, "One of the best movies I've ever seen.");


  Media movie2 = new Movie("The Shawshank Redemption",
      "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
      "https://example.com/the-shawshank-redemption.jpg");
  Review review2 = new Review("Alice", movie2, 10, "Simply amazing. The best movie ever made.");


  Media movie3 = new Movie("The Dark Knight",
      "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
      "https://example.com/the-dark-knight.jpg");
  Review review3 = new Review("Alice", movie3, 8);


  Media movie4 = new Movie("Inception",
      "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
      "https://example.com/inception.jpg");
  Review review4 = new Review("Alice", movie4, 9);


  Media movie5 = new Movie("The Lord of the Rings: The Fellowship of the Ring",
      "A young hobbit named Frodo Baggins inherits a powerful ring from his uncle and must embark on a perilous journey to destroy it before it falls into the wrong hands.",
      "https://example.com/fellowship-of-the-ring.jpg");
  Review review5 = new Review("John", movie5, 10);


  Media movie6 = new Movie("Pirates of the Caribbean: Dead Man's Chest",
      "Captain Jack Sparrow races to recover the heart of Davy Jones to avoid enslaving his soul to Jones' service, as other friends and foes seek the heart for their own agenda as well.",
      "https://example.com/dead-mans-chest.jpg");
  Review review6 = new Review("Jane", movie6, 8);

  List<Review> reviews = List.of(review1, review2, review3, review4, review5, review6);
  int current = 0;

  public Review getMovieReview() {
    return reviews.get(current++ % reviews.size());
  }
}
