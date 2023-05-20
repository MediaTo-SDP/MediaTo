package com.github.sdp.mediato.utility.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.sdp.mediato.utility.FragmentSwitcher;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.databinding.AdapterMediaItemBinding;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.ui.NewItemFragment;

import java.util.ArrayList;
import java.util.List;


public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {

    final private FragmentSwitcher fragmentSwitcher;
    final private String collectionName;
    private final List<Media> mediaList = new ArrayList<>();

    public MediaAdapter(Activity activity, String collectionName) {
        this.fragmentSwitcher = (FragmentSwitcher) activity;
        this.collectionName = collectionName;
    }

    public void update(List<Media> newMedias) {
        this.mediaList.clear();
        this.mediaList.addAll(newMedias);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdapterMediaItemBinding binding = AdapterMediaItemBinding.inflate(inflater, parent, false);
        MediaViewHolder holder = new MediaViewHolder(binding);

        holder.itemView.setOnClickListener(v -> {
            Fragment newItemFragment = new NewItemFragment();
            Bundle bundle = new Bundle();

            bundle.putSerializable("media", mediaList.get(holder.getAdapterPosition()));
            bundle.putString("collection", collectionName);
            newItemFragment.setArguments(bundle);

            fragmentSwitcher.switchCurrentFragmentWithChildFragment(newItemFragment);
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        Media media = mediaList.get(position);
        holder.binding.itemName.setText(media.getTitle());
        Glide.with(holder.itemView.getContext())
                .load(media.getIconUrl())
                .placeholder(R.drawable.movie)
                .into(holder.binding.itemIcon);
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public static class MediaViewHolder extends RecyclerView.ViewHolder {

        private final AdapterMediaItemBinding binding;

        public MediaViewHolder(@NonNull AdapterMediaItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}