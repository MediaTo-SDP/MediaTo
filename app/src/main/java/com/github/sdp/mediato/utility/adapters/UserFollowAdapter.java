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
import com.github.sdp.mediato.utility.adapters.UserAdapter.UserViewHolder;
import java.util.concurrent.CompletableFuture;


public class UserFollowAdapter extends UserAdapter {

    public UserFollowAdapter(UserViewModel userViewModel) {
        super(userViewModel);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_searchuser_item, parent, false);
        return new UserFollowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        User user = userViewModel.getUserListLiveData().getValue().get(position);
        UserFollowViewHolder followViewHolder = (UserFollowViewHolder) holder;

        // Decide which button to display
        if (userViewModel.getUser().getFollowing().contains(user.getUsername())) {
            followViewHolder.followButton.setVisibility(View.GONE);
            followViewHolder.unfollowButton.setVisibility(View.VISIBLE);
        } else {
            followViewHolder.unfollowButton.setVisibility(View.GONE);
            followViewHolder.followButton.setVisibility(View.VISIBLE);
        }

        followViewHolder.followButton.setOnClickListener(v -> {
                followUser(userViewModel.getUserName(), user.getUsername());
                userViewModel.reloadUser();
            }
        );

        followViewHolder.unfollowButton.setOnClickListener(v -> {
                unfollowUser(userViewModel.getUserName(), user.getUsername());
                userViewModel.reloadUser();
            }
        );
    }

    public static class UserFollowViewHolder extends UserViewHolder {

        Button followButton;
        Button unfollowButton;

        public UserFollowViewHolder(@NonNull View itemView) {
            super(itemView);
            followButton = itemView.findViewById(R.id.searchUserAdapter_followButton);
            unfollowButton = itemView.findViewById(R.id.searchUserAdapter_unfollowButton);
        }
    }
}

