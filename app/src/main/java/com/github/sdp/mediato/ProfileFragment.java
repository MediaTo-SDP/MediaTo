package com.github.sdp.mediato;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.sdp.mediato.data.Database;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.media.Collection;
import com.github.sdp.mediato.model.media.CollectionType;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.Movie;
import com.github.sdp.mediato.utility.PhotoPicker;
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
  private RecyclerView collectionRecyclerView;
  private CollectionAdapter collectionAdapter;

  // this string is used to access the user's data in the database
  private String username = "Username";

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_profile, container, false);

    // Get all UI components
    Button edit_button = view.findViewById(R.id.edit_button);
    AppCompatImageButton add_movie_button = view.findViewById(R.id.add_movie_button);
    TextView username_view = view.findViewById(R.id.username_text);
    ImageView profileImage = view.findViewById(R.id.profile_image);

    setUsername(username_view);
    // This does not work yet
    //setProfileImage(profileImage);

    Collection sampleCollection = sampleCollection();

    // On click on the edit button, open a photo picker to choose the profile image
    photoPicker = new PhotoPicker(this, profileImage);
    edit_button.setOnClickListener(v ->
        photoPicker.getOnClickListener(requireActivity().getActivityResultRegistry()).onClick(v)
    );

    // On click on the add movie button, open a search window
    add_movie_button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        replaceFragment(new SearchFragment());
      }
    });

    // Set up RecyclerView and CollectionAdapter to display sampleCollection
    RecyclerView collectionRecyclerView = view.findViewById(R.id.collectionRecyclerView);
    CollectionAdapter collectionAdapter = new CollectionAdapter(getContext(),
        sampleCollection.getReviews());
    collectionRecyclerView.setAdapter(collectionAdapter);
    collectionRecyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
        collectionRecyclerView.getContext(),
        LinearLayoutManager.HORIZONTAL);
    dividerItemDecoration.setDrawable(
        ContextCompat.getDrawable(getContext(), R.drawable.recycler_view_divider));
    collectionRecyclerView.addItemDecoration(dividerItemDecoration);*/

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
/*
  public void addItemToScrollView(String itemText, LinearLayout scrollViewContent) {
    // Inflate the layout
    LayoutInflater inflater = LayoutInflater.from(getContext());
    FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.layout_movie_item,
        scrollViewContent, false);

    // Set the title
    TextView titleView = layout.findViewById(R.id.text_title);
    titleView.setText(itemText);

    // Add the layout to the scroll view
    scrollViewContent.addView(layout);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    // Set the RecyclerView layout manager and adapter
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    recyclerView.setAdapter(new CollectionAdapter(getActivity(), getSampleCollections()));
  }

  // Helper method to create sample collections for testing
  private List<Collection> getSampleCollections() {
    List<Collection> collections = new ArrayList<>();
    collections.add(new Collection("Collection 1", getSampleReviews()));
    collections.add(new Collection("Collection 2", getSampleReviews()));
    collections.add(new Collection("Collection 3", getSampleReviews()));
    return collections;
  }

  // Helper method to create sample reviews for testing
  private List<Review> getSampleReviews() {
    Media movie = new Movie("Movie 1", "Summary 1", "http://example.com/image1.jpg");
    Review review = new Review("User1", movie, 5);
    List<Review> reviews = new ArrayList<>();
    reviews.add(review);
    return reviews;
  }*/

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
    Review review2 = new Review("Bob", movie2, 10, "Simply amazing. The best movie ever made.");
    reviews.add(review2);

    Media movie3 = new Movie("The Dark Knight",
        "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
        "https://example.com/the-dark-knight.jpg");
    Review review3 = new Review("Charlie", movie3, 8);
    reviews.add(review3);

    Media movie4 = new Movie("The Dark Knight",
        "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
        "https://example.com/the-dark-knight.jpg");
    Review review4 = new Review("Charlie", movie4, 8);
    reviews.add(review4);

    Media movie5 = new Movie("The Dark Knight",
        "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
        "https://example.com/the-dark-knight.jpg");
    Review review5 = new Review("Charlie", movie5, 8);
    reviews.add(review5);

    Media movie6 = new Movie("The Dark Knight",
        "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
        "https://example.com/the-dark-knight.jpg");
    Review review6 = new Review("Charlie", movie6, 8);
    reviews.add(review6);

    Collection movieCollection = new Collection(CollectionType.RECENTLY_WATCHED, reviews);

    return movieCollection;
  }

}