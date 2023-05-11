package com.github.sdp.mediato.utility.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.sdp.mediato.ui.MyProfileFragment.OnAddMediaButtonClickListener;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.media.Collection;
import com.github.sdp.mediato.utility.SampleReviews;
import java.util.List;

/**
 * This adapter displays a list of collections of Media.
 */
public class CollectionListAdapter extends RecyclerView.Adapter<CollectionListAdapter.ViewHolder> {

  private Context context;

  // Can be used to execute some code in the profilePage, such as updating the database
  private OnAddMediaButtonClickListener onAddMediaButtonClickListener;

  private List<Collection> collections;

  public CollectionListAdapter(Context context, List<Collection> collections,
      @Nullable OnAddMediaButtonClickListener onAddMediaButtonClickListener) {
    this.context = context;
    this.collections = collections;
    this.onAddMediaButtonClickListener = onAddMediaButtonClickListener;
  }

  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.layout_collection, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Collection collection = collections.get(position);
    holder.collectionTitle.setText(collection.getCollectionName());

    // Set up an inner horizontal RecyclerView for the new collection
    CollectionAdapter collectionAdapter = new CollectionAdapter(context, collection);
    holder.collectionRecyclerView.setAdapter(collectionAdapter);
    holder.collectionRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

    SampleReviews s = new SampleReviews();

    // Set up the add button for the current collection
    if(onAddMediaButtonClickListener == null){
      // if no click listener is passed, don't show the button
      holder.addMediaButton.setVisibility(View.INVISIBLE);
    }else{
      holder.addMediaButton.setOnClickListener(v -> {
        // Can be used to execute some code in the profilePage, such as updating the database
        if (onAddMediaButtonClickListener != null) {
          onAddMediaButtonClickListener.onAddMediaButtonClick(collection);
        }
        collectionAdapter.notifyItemInserted(collectionAdapter.getItemCount());
      });
    }

  }

  @Override
  public int getItemCount() {
    return collections.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    TextView collectionTitle;
    ImageButton addMediaButton;
    RecyclerView collectionRecyclerView;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);

      collectionTitle = itemView.findViewById(R.id.collection_title);
      addMediaButton = itemView.findViewById(R.id.add_media_button);
      collectionRecyclerView = itemView.findViewById(R.id.collection_recycler_view);
    }
  }
}



