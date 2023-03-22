package com.github.sdp.mediato.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.data.Database;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.media.Collection;
import com.github.sdp.mediato.model.media.CollectionType;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.Movie;
import com.github.sdp.mediato.utility.PhotoPicker;
import com.github.sdp.mediato.utility.SampleReviews;
import com.github.sdp.mediato.utility.adapters.CollectionAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A fragment that displays the user's profile information, including their profile picture,
 * username, and collections of their favorite media types. The profile picture and collections can
 * be edited by the user.
 */
public class ProfileFragment extends Fragment {

  private PhotoPicker photoPicker;

  // this string is used to access the user's data in the database
  private String username = "Username";

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_profile, container, false);

    // Get all UI components
    Button edit_button = view.findViewById(R.id.edit_button);
    ConstraintLayout collection = view.findViewById(R.id.layout_collection);
    TextView username_view = view.findViewById(R.id.username_text);
    ImageView profileImage = view.findViewById(R.id.profile_image);
    RecyclerView collectionRecyclerView = view.findViewById(R.id.collectionRecyclerView);

    setUsername(username_view);
    setProfileImage(profileImage);

    SampleReviews s = new SampleReviews();

    Collection sampleCollection = new Collection("Recently watched");
    CollectionAdapter collectionAdapter = setupCollection(collectionRecyclerView, sampleCollection);

    // On click on the edit button, open a photo picker to choose the profile image
    photoPicker = new PhotoPicker(this, profileImage);
    edit_button.setOnClickListener(v ->
        photoPicker.getOnClickListener(requireActivity().getActivityResultRegistry()).onClick(v)
    );

    ImageButton add_movie_button = view.findViewById(R.id.add_button);

    // On click on the add movie button, open a search window
    add_movie_button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        /*replaceFragment(new SearchFragment());*/
        sampleCollection.addReview(s.getMovieReview());
        collectionAdapter.notifyDataSetChanged();
      }
    });

    return view;
  }

  private void replaceFragment(Fragment fragment) {
    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.main_container, fragment);
    fragmentTransaction.commit();
  }

  private void setProfileImage(ImageView profileImage) {
    CompletableFuture<byte[]> imageFuture = Database.getProfilePic(username);

    imageFuture.thenAccept(imageBytes -> {
      // Create a Bitmap object from the byte array
      Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

      // Set the bitmap to the ImageView
      profileImage.setImageBitmap(bitmap);
    });
  }


  /**
   * For now this gets the username set by the profile creation page and forwarded by the
   * MainActivity, but this might be changed in the future when the user is already logged in.
   */
  private void setUsername(TextView username) {
    String arg = getArguments().getString("username");
    username.setText(arg);
  }

  private CollectionAdapter setupCollection(RecyclerView recyclerView, Collection collection) {
    CollectionAdapter collectionAdapter = new CollectionAdapter(getContext(),
        collection);
    recyclerView.setAdapter(collectionAdapter);
    recyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    return collectionAdapter;
  }


  public Collection sampleCollection() {
    List<Review> reviews = new ArrayList<>();

    Media movie1 = new Movie("The Godfather",
        "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.",
        "https://example.com/the-godfather.jpg");
    Review review1 = new Review("Alice", movie1, 9, "One of the best movies I've ever seen.");
    reviews.add(review1);

    Media movie2 = new Movie("The Shawshank Redemption",
        "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
        "https://example.com/the-shawshank-redemption.jpg");
    Review review2 = new Review("Alice", movie2, 10, "Simply amazing. The best movie ever made.");
    reviews.add(review2);

    Media movie3 = new Movie("The Dark Knight",
        "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
        "https://example.com/the-dark-knight.jpg");
    Review review3 = new Review("Alice", movie3, 8);
    reviews.add(review3);

    Media movie4 = new Movie("Inception",
        "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
        "https://example.com/inception.jpg");
    Review review4 = new Review("Alice", movie4, 9);
    reviews.add(review4);

    Media movie5 = new Movie("The Lord of the Rings: The Fellowship of the Ring",
        "A young hobbit named Frodo Baggins inherits a powerful ring from his uncle and must embark on a perilous journey to destroy it before it falls into the wrong hands.",
        "https://example.com/fellowship-of-the-ring.jpg");
    Review review5 = new Review("John", movie5, 10);
    reviews.add(review5);

    Media movie6 = new Movie("Pirates of the Caribbean: Dead Man's Chest",
        "Captain Jack Sparrow races to recover the heart of Davy Jones to avoid enslaving his soul to Jones' service, as other friends and foes seek the heart for their own agenda as well.",
        "https://example.com/dead-mans-chest.jpg");
    Review review6 = new Review("Jane", movie6, 8);
    reviews.add(review6);

    Collection movieCollection = new Collection(CollectionType.RECENTLY_WATCHED, reviews);

    return movieCollection;
  }


  private Review newReview() {
    Media movie6 = new Movie("Pirates of the Caribbean: Dead Man's Chest",
        "Captain Jack Sparrow races to recover the heart of Davy Jones to avoid enslaving his soul to Jones' service, as other friends and foes seek the heart for their own agenda as well.",
        "https://example.com/dead-mans-chest.jpg");
    return new Review("Jane", movie6, 8);
  }


}