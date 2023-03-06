package com.github.sdp.mediato;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class NewItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        SeekBar ratingSlider = findViewById(R.id.item_rating_slider);
        TextView ratingIndicator = findViewById(R.id.item_rating_slider_progress);
        ratingSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                ratingIndicator.setText("" + progress);
                ratingIndicator.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);
                ratingIndicator.setY(seekBar.getY() - ratingIndicator.getTextSize()*1.5f);
            }
        });
    }
}