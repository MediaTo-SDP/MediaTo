package com.github.sdp.mediato.utility.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.data.ReviewInteractionDatabase;
import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.databinding.LayoutReviewPostItemBinding;
import com.github.sdp.mediato.model.Comment;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.post.ReviewPost;
import com.github.sdp.mediato.ui.viewmodel.ExploreViewModel;
import com.github.sdp.mediato.ui.viewmodel.FeedViewModel;
import com.google.android.material.button.MaterialButton;
import java.util.concurrent.CompletableFuture;

/**
* An adapter (that can be used in recycler views) for the review posts
*/
public class ReviewPostListAdapter extends ListAdapter<ReviewPost, ReviewPostListAdapter.MyViewHolder> {

    /** Orientation up along the x axis */
    public static final float ORIENTATION_UP = 0f;

    /** Orientation down along the x axis */
    public static final float ORIENTATION_DOWN = 180f;
    public static final float MAX_COMMENT_LENGTH= 140;
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
        CommentAdapter commentAdapter = new CommentAdapter();
        binding.commentList.setAdapter(commentAdapter);
        binding.commentList.setLayoutManager(new LinearLayoutManager(parent.getContext()));
        return new ReviewPostListAdapter.MyViewHolder(binding, commentAdapter);
    }

    /**
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ReviewPostListAdapter.MyViewHolder holder, int position) {

        MaterialButton followButton = holder.binding.exploreFollowButton;
        // Display the content of the review post
        displayReviewPostContent(holder, position);
        // Set the like and dislike buttons
        setLikeListener(holder, position);
        setDislikeListener(holder, position);

        setUpCommentSection(holder, position);

        // Set fragment specific details
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

    public void displayReviewPostContent(@NonNull ReviewPostListAdapter.MyViewHolder holder, int position) {
        holder.binding.textTitle.setText(getItem(position).getTitle());
        holder.binding.textComment.setText(getItem(position).getComment());
        if (getItem(position).getGrade() > 0) {
            String rating = Review.formatRating(getItem(position).getGrade());
            holder.binding.rating.setText(rating);
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
    }

    private void setLikeListener(ReviewPostListAdapter.MyViewHolder holder, int position) {
        ReviewPost reviewPost = getItem(position);
        holder.binding.likeButton.setOnClickListener(
                v -> {
                    ReviewInteractionDatabase.likes(username, getItem(position).getUsername(), getItem(position).getCollectionName(), getItem(position).getTitle())
                            .thenAccept(
                                    likes -> {
                                        if (!likes) {
                                            ReviewInteractionDatabase.likeReview(username, getItem(position).getUsername(), getItem(position).getCollectionName(), getItem(position).getTitle());
                                            ReviewInteractionDatabase.unDislikeReview(username, getItem(position).getUsername(), getItem(position).getCollectionName(), getItem(position).getTitle());
                                            reviewPost.like(username);
                                        } else {
                                            ReviewInteractionDatabase.unLikeReview(username, getItem(position).getUsername(), getItem(position).getCollectionName(), getItem(position).getTitle());
                                            reviewPost.unLike(username);
                                        }
                                        holder.binding.likeCount.setText(String.valueOf(reviewPost.getLikeCount()));
                                        holder.binding.dislikeCount.setText(String.valueOf(reviewPost.getDislikeCount()));

                                    });}
        );
    }

    private void setDislikeListener(ReviewPostListAdapter.MyViewHolder holder, int position) {
        ReviewPost reviewPost = getItem(position);
        holder.binding.dislikeButton.setOnClickListener(
                v -> {
                    ReviewInteractionDatabase.dislikes(username, getItem(position).getUsername(), getItem(position).getCollectionName(), getItem(position).getTitle())
                            .thenAccept(
                                    dislikes -> {
                                        if (!dislikes) {
                                            ReviewInteractionDatabase.dislikeReview(username, getItem(position).getUsername(), getItem(position).getCollectionName(), getItem(position).getTitle());
                                            ReviewInteractionDatabase.unLikeReview(username, getItem(position).getUsername(), getItem(position).getCollectionName(), getItem(position).getTitle());
                                            reviewPost.dislike(username);
                                        } else {
                                            ReviewInteractionDatabase.unDislikeReview(username, getItem(position).getUsername(), getItem(position).getCollectionName(), getItem(position).getTitle());
                                            reviewPost.unDislike(username);
                                        }
                                        holder.binding.likeCount.setText(String.valueOf(reviewPost.getLikeCount()));
                                        holder.binding.dislikeCount.setText(String.valueOf(reviewPost.getDislikeCount()));

                                    });
                }
        );
    }

    private void setUpCommentSection(ReviewPostListAdapter.MyViewHolder holder, int position){
        // Handle expanding the comment section
        holder.binding.commentsCard.setOnClickListener(v ->
            handleExpandArrow(holder.binding.expandArrow, holder.binding.commentSection));

        // Handle maximum comment length
        holder.binding.commentTextField.addTextChangedListener(
            maxLengthTextWatcher(holder.itemView.getContext(), MAX_COMMENT_LENGTH));

        // Handle entered comment text
        holder.binding.commentTextField.setOnEditorActionListener((textField, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String commentText = textField.getText().toString();
                handleComment(commentText, holder, position);
                return true;
            }
            return false;
        });
    }

    private void handleComment(String commentText, ReviewPostListAdapter.MyViewHolder holder, int position){
        if(commentText.isEmpty()){
            Toast.makeText(holder.itemView.getContext(), "Cannot add empty comment", Toast.LENGTH_LONG).show();
        }else{
            // Add the comment to the adapter
            ReviewPost reviewPost = getItem(position);
            Comment comment = new Comment(reviewPost.getCollectionName(), String.valueOf(reviewPost.getId()), commentText, username);
            holder.commentAdapter.addComment(comment);

            // TODO: Add comment to the database

            // Clear the comment text field for the next entry
            holder.binding.commentTextField.setText("");
        }
    }

    private static void handleExpandArrow(ImageView arrow, ConstraintLayout commentSection) {
        float newRotation;
        int visibility;
        // If the arrow is pointing up, then rotate down and make the comment section visible
        if (arrow.getRotation() == ORIENTATION_UP) {
            newRotation = ORIENTATION_DOWN;
            visibility = View.VISIBLE;
        } else { // Otherwise rotate up and hide the comment section
            newRotation = ORIENTATION_UP;
            visibility = View.GONE;
        }

        // Use an animation to rotate smoothly
        arrow.animate().rotation(newRotation).setDuration(300).start();
        commentSection.setVisibility(visibility);
    }

    private static TextWatcher maxLengthTextWatcher(Context context, float maxTextLength){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= maxTextLength) {
                    String message = String.format("Exceeded maximum comment length: %.0f characters", maxTextLength);
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public final LayoutReviewPostItemBinding binding;
        public final CommentAdapter commentAdapter;

        public MyViewHolder(@NonNull LayoutReviewPostItemBinding binding, CommentAdapter commentAdapter) {
            super(binding.getRoot());
            this.binding = binding;
            this.commentAdapter = commentAdapter;
        }
    }

}

