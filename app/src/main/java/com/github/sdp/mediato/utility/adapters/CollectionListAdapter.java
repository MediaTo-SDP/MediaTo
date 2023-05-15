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
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.model.media.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * This adapter displays a list of collections of Media.
 */
public class CollectionListAdapter extends RecyclerView.Adapter<CollectionListAdapter.ViewHolder> {

  private final Context context;
  private final Consumer<Collection> onAddMediaButtonClickListener;
  private final Consumer<Collection> onDeleteCollectionButtonClickListener;
  private final List<Collection> collections;

  public CollectionListAdapter(Context context, List<Collection> collections,
      @Nullable Consumer<Collection> onAddMediaButtonClickListener, @Nullable Consumer<Collection> onDeleteCollectionButtonClickListener) {
    this.context = context;
    this.collections = collections;
    this.onAddMediaButtonClickListener = onAddMediaButtonClickListener;
    this.onDeleteCollectionButtonClickListener = onDeleteCollectionButtonClickListener;
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

    // Set up the buttons for the new collection
    setUpButton(holder.deleteCollectionButton, collection, collectionAdapter,
        onDeleteCollectionButtonClickListener,
        adapter -> adapter.notifyItemRemoved(adapter.getItemCount()));

    setUpButton(holder.addMediaButton, collection, collectionAdapter,
        onAddMediaButtonClickListener,
        adapter -> adapter.notifyItemInserted(adapter.getItemCount()));
  }

  @Override
  public int getItemCount() {
    return collections.size();
  }

  private void setUpButton(ImageButton button, Collection collection,
      CollectionAdapter collectionAdapter,
      Consumer<Collection> clickListener,
      Consumer<CollectionAdapter> updateOperation) {
    if (clickListener == null) {
      // if no click listener is passed, don't show the button
      button.setVisibility(View.INVISIBLE);
    } else {
      button.setOnClickListener(v -> {
        clickListener.accept(collection);
        updateOperation.accept(collectionAdapter);
      });
    }
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    TextView collectionTitle;
    ImageButton addMediaButton;
    ImageButton deleteCollectionButton;
    RecyclerView collectionRecyclerView;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);

      collectionTitle = itemView.findViewById(R.id.collection_title);
      addMediaButton = itemView.findViewById(R.id.add_media_button);
      deleteCollectionButton = itemView.findViewById(R.id.delete_collection_button);
      collectionRecyclerView = itemView.findViewById(R.id.collection_recycler_view);
    }
  }
}



