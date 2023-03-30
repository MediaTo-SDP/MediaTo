package com.github.sdp.mediato;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sdp.mediato.model.media.Media;

import java.util.List;

public class MediaAdapter2 extends RecyclerView.Adapter<MediaAdapter2.ViewHolder> {

    private List<Media> medias;
    private Context context;

    private FragmentSwitcher fs;

    public MediaAdapter2(List<Media> medias, Context context, FragmentSwitcher fs) {
        this.medias = medias;
        this.context = context;
        this.fs = fs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Media currentMedia = medias.get(position);

        // toDO : display the image properly
        /*
        Glide.with(context)
                .load(currentMedia.getImageURL())
                .into(holder.imageView);
         */

        holder.getTitleView().setText(currentMedia.getTitle());

        holder.getEntireElement().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fg = new NewItemFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("media", currentMedia);
                fg.setArguments(bundle);
                fs.switchCurrentFragmentWithChildFragment(fg);
            }
        });
    }

    @Override
    public int getItemCount() {
        return medias.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titleView;
        private ImageView imageView;
        private LinearLayout entireElement;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.item_icon);
            this.titleView = itemView.findViewById(R.id.item_name);
            this.entireElement = itemView.findViewById(R.id.entire_media_item);
        }

        public TextView getTitleView() {
            return titleView;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public LinearLayout getEntireElement() {
            return entireElement;
        }
    }
}
