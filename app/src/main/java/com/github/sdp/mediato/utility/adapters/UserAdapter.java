package com.github.sdp.mediato.utility.adapters;

import static com.github.sdp.mediato.data.UserDatabase.followUser;
import static com.github.sdp.mediato.data.UserDatabase.unfollowUser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sdp.mediato.R;
import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.ui.viewmodel.UserViewModel;

import java.util.concurrent.CompletableFuture;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final UserViewModel userViewModel;

    public UserAdapter(UserViewModel userViewModel) {
        this.userViewModel = userViewModel;

        // Refresh when follow or unfollow
        this.userViewModel.getUserLiveData().observeForever(user -> notifyDataSetChanged());

        // Refresh when new search
        this.userViewModel.getUserListLiveData().observeForever(users -> notifyDataSetChanged());
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userViewModel.getUserListLiveData().getValue().get(position);
        holder.userNameTextView.setText(user.getUsername());

        downloadProfilePicWithRetry(holder.userProfileImageView, user.getUsername());

        // Decide which button to display
        if(userViewModel.getUser().getFollowing().contains(user.getUsername())) {
            holder.followButton.setVisibility(View.GONE);
            holder.unfollowButton.setVisibility(View.VISIBLE);
        } else {
            holder.unfollowButton.setVisibility(View.GONE);
            holder.followButton.setVisibility(View.VISIBLE);
        }

        holder.followButton.setOnClickListener(v -> {
                    followUser(userViewModel.getUserName(), user.getUsername());
                    userViewModel.reloadUser();
                }
        );

        holder.unfollowButton.setOnClickListener(v -> {
                    unfollowUser(userViewModel.getUserName(), user.getUsername());
                    userViewModel.reloadUser();
                }
        );
    }

    @Override
    public int getItemCount() {
        return userViewModel.getUserList().size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        ImageView userProfileImageView;
        TextView userNameTextView;
        Button followButton;
        Button unfollowButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImageView = itemView.findViewById(R.id.userAdapter_imageView);
            userNameTextView = itemView.findViewById(R.id.userAdapter_userName);
            followButton = itemView.findViewById(R.id.userAdapter_followButton);
            unfollowButton = itemView.findViewById(R.id.userAdapter_unfollowButton);
        }
    }

    // TODO: Should be improved so it does not need to use the hardcoded retry
    private void downloadProfilePicWithRetry(ImageView userProfileImageView, String userName) {

        CompletableFuture<byte[]> imageFuture = UserDatabase.getProfilePic(userName);

        // It would probably be better to do this directly in the database class
        // (getProfilePic would return CompletableFuture<Bitmap>)
        CompletableFuture<Bitmap> future = imageFuture.thenApply(imageBytes -> {
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        });

        // Try to set the profile pic
        future.thenAccept(userProfileImageView::setImageBitmap)
                .exceptionally(throwable -> {
                    // Could not download image, try again in 1 second
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        downloadProfilePicWithRetry(userProfileImageView, userName);
                    }, 1000);

                    return null;
                });
    }
}