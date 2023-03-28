package com.github.sdp.mediato;

import static com.github.sdp.mediato.data.Database.followUser;
import static com.github.sdp.mediato.data.Database.unfollowUser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.ui.viewmodel.SearchUserViewModel;


public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.UserViewHolder> {

    private final SearchUserViewModel searchUserViewModel;

    public SearchUserAdapter(SearchUserViewModel searchUserViewModel) {
        this.searchUserViewModel = searchUserViewModel;

        // Refresh when follow or unfollow
        searchUserViewModel.getUserLiveData().observeForever(user -> notifyDataSetChanged());

        // Refresh when new search
        searchUserViewModel.getUserListLiveData().observeForever(users -> notifyDataSetChanged());
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_searchuser_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = searchUserViewModel.getUserListLiveData().getValue().get(position);
        holder.userNameTextView.setText(user.getUsername());

        // Decide which button to display
        if(searchUserViewModel.getUser().getFollowing().contains(user.getUsername())) {
            holder.followButton.setVisibility(View.GONE);
            holder.unfollowButton.setVisibility(View.VISIBLE);
        } else {
            holder.unfollowButton.setVisibility(View.GONE);
            holder.followButton.setVisibility(View.VISIBLE);
        }

        holder.followButton.setOnClickListener(v -> {
                    followUser(searchUserViewModel.getUserName(), user.getUsername());
                    searchUserViewModel.reloadUser();
                }
        );

        holder.unfollowButton.setOnClickListener(v -> {
                    unfollowUser(searchUserViewModel.getUserName(), user.getUsername());
                    searchUserViewModel.reloadUser();
                }
        );
    }

    @Override
    public int getItemCount() {
        return searchUserViewModel.getUserList().size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        ImageView userProfileImageView;
        TextView userNameTextView;
        Button followButton;
        Button unfollowButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImageView = itemView.findViewById(R.id.searchUserAdapter_imageView);
            userNameTextView = itemView.findViewById(R.id.searchUserAdapter_userName);
            followButton = itemView.findViewById(R.id.searchUserAdapter_followButton);
            unfollowButton = itemView.findViewById(R.id.searchUserAdapter_unfollowButton);
        }
    }
}

