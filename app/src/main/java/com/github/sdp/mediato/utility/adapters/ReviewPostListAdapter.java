package com.github.sdp.mediato.utility.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.databinding.LayoutMovieItemBinding;
import com.github.sdp.mediato.databinding.LayoutReviewPostItemBinding;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.post.ReviewPost;
import com.google.common.base.Converter;
import com.google.protobuf.Internal;

import java.util.List;

public class ReviewPostListAdapter extends ListAdapter<ReviewPost, ReviewPostListAdapter.MyViewHolder> {
    private static final DiffUtil.ItemCallback<ReviewPost> REVIEWPOST_COMPARATOR = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull ReviewPost oldItem, @NonNull ReviewPost newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ReviewPost oldItem, @NonNull ReviewPost newItem) {
            return oldItem.equals(newItem);
        }
    };

    public ReviewPostListAdapter() {
        super(REVIEWPOST_COMPARATOR);
    }

    /**
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return the View Holder
     */
    @NonNull
    @Override
    public ReviewPostListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LayoutReviewPostItemBinding binding = LayoutReviewPostItemBinding.inflate(inflater, parent, false);
        //REMOVE
        return new ReviewPostListAdapter.MyViewHolder(binding);
    }

    /**
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ReviewPostListAdapter.MyViewHolder holder, int position) {
        // Todo add placeholder
        holder.binding.textTitle.setText(getItem(position).getTitle());
        holder.binding.textComment.setText(getItem(position).getComment());
        if (getItem(position).getGrade() > 0) {
            holder.binding.rating.setText(String.valueOf(getItem(position).getGrade()));
        } else {
            holder.binding.textRating.setVisibility(View.GONE);
        }
        holder.binding.username.setText(getItem(position).getUsername());

        Glide.with(holder.itemView.getContext())
                .load(getItem(position).getMediaIconUrl())
                .placeholder(R.drawable.octopussy)
                .into(holder.binding.mediaCover);

        UserDatabase.getProfilePic(getItem(position).getUsername()).thenAccept(
                profilePicBytes -> {
                    Glide.with(holder.itemView.getContext())
                            .asBitmap()
                            .load(profilePicBytes)
                            .placeholder(R.drawable.profile_picture_default)
                            .into(holder.binding.profilePic);
                }
        );

        if (position + 6 == getItemCount()) {
            // TODO: Download more data through a callback function
        }
    }

    // Todo can extend View.OnclickListener to implement the OnClickCallback
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final LayoutReviewPostItemBinding binding;

        public MyViewHolder(@NonNull LayoutReviewPostItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

