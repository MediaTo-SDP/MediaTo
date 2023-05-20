package com.github.sdp.mediato.utility.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.media.Collection;
import com.github.sdp.mediato.model.media.Media;


/**
 * This adapter displays a single collection of Media.
 */
public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {

  private final Context context;
  private final Collection collection;


  public CollectionAdapter(Context context, Collection collection) {
    this.context = context;
    this.collection = collection;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context)
        .inflate(R.layout.layout_profile_movie_item, parent, false);

    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Review review = collection.getReviewsList().get(position);
    Media media = review.getMedia();

    // Set the media title
    holder.mediaTitle.setText(media.getTitle());

    // Set the media rating
    int grade = review.getGrade();
    String ratingString = "";
    if (grade > 0) {
      ratingString = getStarString(grade);
    }
    holder.mediaRating.setText(ratingString);

    Glide.with(holder.itemView.getContext())
        .load(media.getIconUrl())
        .placeholder(R.drawable.movie)
        .into(holder.mediaImage);
  }

  @Override
  public int getItemCount() {
    return collection.getReviewsList().size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    ImageView mediaImage;
    TextView mediaTitle;
    TextView mediaRating;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);

      mediaImage = itemView.findViewById(R.id.movie_image);
      mediaTitle = itemView.findViewById(R.id.movie_title);
      mediaRating = itemView.findViewById(R.id.movie_rating);
    }
  }

  private static String getStarString(int numDarkStars) {
    Preconditions.checkGrade(numDarkStars);
    StringBuilder sb = new StringBuilder();
    for (int i = 1; i <= Review.MAX_GRADE; i++) {
      if (i <= numDarkStars) {
        sb.append("●");
      } else {
        sb.append("○");
      }
    }
    return sb.toString();
  }

}

