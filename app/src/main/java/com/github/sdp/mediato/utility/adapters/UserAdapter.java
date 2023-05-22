package com.github.sdp.mediato.utility.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.databinding.AdapterUserItemBinding;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.ui.MainActivity;
import com.github.sdp.mediato.ui.ReadOnlyProfileFragment;
import com.github.sdp.mediato.utility.FragmentSwitcher;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final FragmentSwitcher fragmentSwitcher;
    private User connectedUser;
    private List<User> userList;

    private OnUserInteractionListener userInteractionListener;

    public UserAdapter(Activity activity, User connectedUser, List<User> userList) {
        this.fragmentSwitcher = (FragmentSwitcher) activity;
        this.connectedUser = connectedUser;
        this.userList = new ArrayList<>(userList);
    }

    public void setOnUserInteractionListener(OnUserInteractionListener listener) {
        this.userInteractionListener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdapterUserItemBinding binding = AdapterUserItemBinding.inflate(inflater, parent, false);
        UserViewHolder holder = new UserViewHolder(binding);

        holder.itemView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                User user = userList.get(position);
                if (user.getUsername().equals(connectedUser.getUsername())){
                    fragmentSwitcher.switchCurrentFragmentWithChildFragment(((MainActivity) fragmentSwitcher).getMyProfileFragment());
                } else {
                    Bundle args = new Bundle();
                    args.putString("username", user.getUsername());
                    ReadOnlyProfileFragment readOnlyProfileFragment = new ReadOnlyProfileFragment();
                    readOnlyProfileFragment.setArguments(args);
                    fragmentSwitcher.switchCurrentFragmentWithChildFragment(readOnlyProfileFragment);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.binding.userAdapterUserName.setText(user.getUsername());

        downloadProfilePicWithRetry(user.getUsername(), 0, holder.binding.userAdapterImageView);

        Button follow = holder.binding.userAdapterFollowButton;
        Button unfollow = holder.binding.userAdapterUnfollowButton;

        // Decide which button to display
        setVisibilityFollowUnfollowButtons(follow, unfollow, user);

        follow.setOnClickListener(v -> {
            if (userInteractionListener != null) {
                userInteractionListener.onFollowClick(user);
            }
        });

        unfollow.setOnClickListener(v -> {
            if (userInteractionListener != null) {
                userInteractionListener.onUnfollowClick(user);
            }
        });
    }

    private void setVisibilityFollowUnfollowButtons(Button follow, Button unfollow, User user) {
        if(connectedUser.getUsername().equals(user.getUsername())) {
            follow.setVisibility(View.GONE);
            unfollow.setVisibility(View.GONE);
        }
        else if(connectedUser.getFollowing().contains(user.getUsername())) {
            follow.setVisibility(View.GONE);
            unfollow.setVisibility(View.VISIBLE);
        } else {
            follow.setVisibility(View.VISIBLE);
            unfollow.setVisibility(View.GONE);
        }
    }

    public void updateConnectedUser(User connectedUser) {
        this.connectedUser = connectedUser;
        notifyDataSetChanged();
    }

    public void updateUserList(List<User> userList) {
        this.userList = new ArrayList<>(userList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        private final AdapterUserItemBinding binding;

        public UserViewHolder(@NonNull AdapterUserItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    // TODO: Should be improved so it does not need to use the hardcoded retry
    private void downloadProfilePicWithRetry(String username, int count,  ImageView userProfileImageView) {

        CompletableFuture<byte[]> imageFuture = UserDatabase.getProfilePic(username);

        // It would probably be better to do this directly in the database class
        // (getProfilePic would return CompletableFuture<Bitmap>)
        CompletableFuture<Bitmap> future = imageFuture.thenApply(imageBytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            return bitmap;
        });

        future.thenAccept(userProfileImageView::setImageBitmap)
            .exceptionally(throwable -> {
                if (count < 5) {
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        downloadProfilePicWithRetry(username, count + 1, userProfileImageView);
                    }, 200);
                } else {
                    System.out.println("Couldn't fetch pic for " + username);
                    Bitmap profilePic = BitmapFactory.decodeResource(
                            ((Activity) fragmentSwitcher).getResources(),
                            R.drawable.profile_picture_default);
                    userProfileImageView.setImageBitmap(profilePic);
                }
                return null;
            });
    }

    public interface OnUserInteractionListener {
        void onFollowClick(User user);
        void onUnfollowClick(User user);
    }
}