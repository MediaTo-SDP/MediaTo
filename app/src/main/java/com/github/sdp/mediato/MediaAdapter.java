package com.github.sdp.mediato;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.sdp.mediato.model.media.Media;

import java.util.List;

public class MediaAdapter extends BaseAdapter {

    private final Context context;
    private final List<Media> mediaItems;

    private final LayoutInflater inflater;

    public MediaAdapter(Context context, List<Media> mediaItems) {
        this.context = context;
        this.mediaItems = mediaItems;
        this.inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Media getItem(int position) {
        return mediaItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.adapter_item, null);

        Media currentItem = getItem(position);
        String name = currentItem.getTitle();
        String src = currentItem.getImageURL();

        // to do change the image, fetched from url
        ImageView moviePoster = convertView.findViewById(R.id.item_icon);
        
        // toDO : fetch from the dataBase
        int resId = context.getResources().getIdentifier(src, "drawable", context.getPackageName());
        moviePoster.setImageResource(resId);

        TextView movieName = convertView.findViewById(R.id.item_name);
        movieName.setText(name);

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // do the job
            }
        });
        return convertView;
    }
}
