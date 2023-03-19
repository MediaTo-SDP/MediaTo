package com.github.sdp.mediato;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.sdp.mediato.databinding.LayoutMovieItemBinding;
import com.github.sdp.mediato.model.media.Media;


public class MediaRecyclerViewAdapter extends ListAdapter<Media, MediaRecyclerViewAdapter.MyViewHolder> {
    private static DiffUtil.ItemCallback<Media> MEDIA_COMPARATOR = new DiffUtil.ItemCallback<Media>() {
        @Override
        public boolean areItemsTheSame(@NonNull Media oldItem, @NonNull Media newItem) {
            return oldItem.getId() == newItem.getId() && oldItem.getMediaType() == newItem.getMediaType();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Media oldItem, @NonNull Media newItem) {
            return areItemsTheSame(oldItem, newItem);
        }
    };

    protected MediaRecyclerViewAdapter() {
        super(MEDIA_COMPARATOR);
    }

    @NonNull
    @Override
    public MediaRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_movie_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Todo add placeholder
        holder.title.setText(getItem(position).getTitle());
        Glide.with(holder.itemView.getContext())
                .load(getItem(position).getImageURL())
                .into(holder.image);
    }

    // Todo can extend View.OnclickListener to implement the OnClickCallback
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.media_cover);
            title = itemView.findViewById(R.id.text_title);
        }
    }
}
