package com.github.sdp.mediato.utility.adapters;

import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.data.ReviewInteractionDatabase;
import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.databinding.LayoutMovieItemBinding;
import com.github.sdp.mediato.databinding.LayoutReviewPostItemBinding;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.post.ReviewPost;
import com.github.sdp.mediato.ui.ExploreFragment;
import com.github.sdp.mediato.ui.viewmodel.ExploreViewModel;
import com.github.sdp.mediato.ui.viewmodel.FeedViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.common.base.Converter;
import com.google.protobuf.Internal;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/*
* An adapter (that can be used in recycler views) for the review posts
*/
public class ReviewPostListAdapter extends ListAdapter<ReviewPost, ReviewPostListAdapter.MyViewHolder> {
    public enum CallerFragment {
        EXPLORE,
        FEED
    }
    private CallerFragment callerFragment;
    private String username;
    private ExploreViewModel exploreViewModel;
    private FeedViewModel feedViewModel;

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
        MaterialButton followButton = holder.binding.exploreFollowButton;
        holder.binding.textTitle.setText(getItem(position).getTitle());
        holder.binding.textComment.setText(getItem(position).getComment());
        if (getItem(position).getGrade() > 0) {
            holder.binding.rating.setText(String.valueOf(getItem(position).getGrade()));
        } else {
            holder.binding.textRating.setVisibility(View.GONE);
        }
        holder.binding.username.setText(getItem(position).getUsername());
        holder.binding.likeCount.setText(String.valueOf(getItem(position).getLikeCount()));
        holder.binding.dislikeCount.setText(String.valueOf(getItem(position).getDislikeCount()));

        Glide.with(holder.itemView.getContext())
                .load(getItem(position).getMediaIconUrl())
                .placeholder(R.drawable.movie)
                .into(holder.binding.mediaCover);
        displayProfilePic(holder, position);

        holder.binding.dislikeButton.setOnClickListener(
                v -> {
                    System.out.println("Disliking review");
                    ReviewInteractionDatabase.dislikeReview(username, getItem(position).getUsername(), getItem(position).getCollectionName(), getItem(position).getTitle());
                    int dislikeCount = getItem(position).getDislikeCount();
                    holder.binding.dislikeCount.setText(String.valueOf(dislikeCount + 1));
                }
        );

        holder.binding.likeButton.setOnClickListener(
                v -> {
                    System.out.println("Liking review");
                    ReviewInteractionDatabase.likeReview(username, getItem(position).getUsername(), getItem(position).getCollectionName(), getItem(position).getTitle());
                    int likeCount = getItem(position).getLikeCount();
                    holder.binding.likeCount.setText(String.valueOf(likeCount + 1));
                }
        );

        switch (callerFragment) {
            case EXPLORE:
                setExploreSpecifics(getItem(position).getUsername(), followButton);
                break;
            case FEED:
                setFeedSpecifics(getItem(position).getUsername(), followButton);
                break;
        }
    }

    public void setFeedSpecifics(String reviewPostUsername, MaterialButton followButton) {
        followButton.setVisibility(View.GONE);
    }

    public void setExploreSpecifics(String reviewPostUsername, MaterialButton followButton) {
        System.out.println("Setting explore specifics for " + reviewPostUsername);
        if(exploreViewModel.getFollowedUsers().getValue().contains(reviewPostUsername)) {
            System.out.println("User " + reviewPostUsername + " is followed");
            followButton.setText(R.string.following);
            followButton.setClickable(false);
        } else {
            System.out.println("User " + reviewPostUsername + " is not followed");
            followButton.setText("Follow");
            followButton.setVisibility(View.VISIBLE);
            followButton.setClickable(true);
            followButton.setOnClickListener(
                    v -> {
                        UserDatabase.followUser(username, reviewPostUsername);
                        exploreViewModel.updateFollows(reviewPostUsername);
                    }
            );
        }
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCallerFragment(ViewModel viewModel, CallerFragment callerFragment) {
        this.callerFragment = callerFragment;
        switch (callerFragment) {
            case EXPLORE:
                this.exploreViewModel = (ExploreViewModel) viewModel;
                break;
            case FEED:
                this.feedViewModel = (FeedViewModel) viewModel;
                break;
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final LayoutReviewPostItemBinding binding;

        public MyViewHolder(@NonNull LayoutReviewPostItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

