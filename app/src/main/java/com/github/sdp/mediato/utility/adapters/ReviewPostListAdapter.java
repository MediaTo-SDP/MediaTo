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
import java.util.concurrent.CompletableFuture;

/*
* An adapter (that can be used in recycler views) for the review posts
*/
public class ReviewPostListAdapter extends ListAdapter<ReviewPost, ReviewPostListAdapter.MyViewHolder> {
    /**
     * Used by the adapter to compare review posts
     */
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
        return new ReviewPostListAdapter.MyViewHolder(binding);
    }

    /**
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ReviewPostListAdapter.MyViewHolder holder, int position) {
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
                .placeholder(R.drawable.movie)
                .into(holder.binding.mediaCover);
        displayProfilePic(holder, position);
    }

    /**
     * Helper function that displays the profile pic of the user that posted the review
     * @param holder
     * @param position
     */
    private void displayProfilePic(ReviewPostListAdapter.MyViewHolder holder, int position) {
        CompletableFuture<byte[]> fetchingProfilePic = UserDatabase.getProfilePic(getItem(position).getUsername());
        fetchingProfilePic.exceptionally(
                        exception -> {
                            System.out.println("Couldn't fetch pic for " + getItem(position).getUsername());
                            Glide.with(holder.itemView.getContext())
                                    .load(R.drawable.profile_picture_default)
                                    .into(holder.binding.profilePic);
                            return new byte[0];
                        })
                .thenAccept(
                        profilePicBytes -> {
                            if(!fetchingProfilePic.isCompletedExceptionally() && profilePicBytes.length > 0) {
                                System.out.println("Fetched pic for " + getItem(position).getUsername());
                                Glide.with(holder.itemView.getContext())
                                        .asBitmap()
                                        .load(profilePicBytes)
                                        .placeholder(R.drawable.profile_picture_default)
                                        .into(holder.binding.profilePic);
                            }
                        }
                );

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final LayoutReviewPostItemBinding binding;

        public MyViewHolder(@NonNull LayoutReviewPostItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

