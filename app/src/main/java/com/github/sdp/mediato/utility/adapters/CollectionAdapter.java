package com.github.sdp.mediato.utility.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.media.Media;
import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {

  private Context context;
  private List<Review> reviews;

  public CollectionAdapter(Context context, List<Review> reviews) {
    this.context = context;
    this.reviews = reviews;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.layout_movie_item, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Review review = reviews.get(position);
    Media media = review.getMedia();

    holder.mediaImage.setImageResource(R.drawable.bg_movie2);

    holder.mediaTitle.setText(media.getTitle());
    /* holder.mediaRating.setText(review.getGrade());*/
    holder.mediaRating.setText("3");
  }

  @Override
  public int getItemCount() {
    return reviews.size();
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
}

