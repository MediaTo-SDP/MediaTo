package com.github.sdp.mediato.utility.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.sdp.mediato.databinding.LayoutMovieItemBinding;
import com.github.sdp.mediato.model.media.Media;

/**
 * A general ListAdapter (that can be used in recycler views) for any Media type
 */
public class MediaListAdapter extends ListAdapter<Media, MediaListAdapter.MyViewHolder> {
    /**
     * Used by the adapter to differentiate medias and updated medias from different ones
     */
    private static final DiffUtil.ItemCallback<Media> MEDIA_COMPARATOR = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Media oldItem, @NonNull Media newItem) {
            return oldItem.getId() == newItem.getId() && oldItem.getMediaType() == newItem.getMediaType();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Media oldItem, @NonNull Media newItem) {
            return areItemsTheSame(oldItem, newItem);
        }
    };

    /**
     * Default constructor
     */
    public MediaListAdapter() {
        super(MEDIA_COMPARATOR);
    }

    /**
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @NonNull
    @Override
    public MediaListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LayoutMovieItemBinding binding = LayoutMovieItemBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    /**
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Todo add placeholder
        holder.binding.textTitle.setText(getItem(position).getTitle());
        Glide.with(holder.itemView.getContext())
                .load(getItem(position).getIconUrl())
                .into(holder.binding.mediaCover);
        if (position + 6 == getItemCount()) {
            // TODO: Download more data through a callback function
        }
    }

    // Todo can extend View.OnclickListener to implement the OnClickCallback
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final LayoutMovieItemBinding binding;

        public MyViewHolder(@NonNull LayoutMovieItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
