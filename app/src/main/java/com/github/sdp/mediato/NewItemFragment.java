package com.github.sdp.mediato;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class NewItemFragment extends Fragment {

    // The maximum allowed length for review field
    public final static int MAX_REVIEW_LENGTH = 100;

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_new_item, container, false);
        this.view = view;

        TextView errorTextView = view.findViewById(R.id.new_item_review_error_msg);
        EditText review = view.findViewById(R.id.item_review_edittext);



        /* ToDO 1 : with the given Bundle assign the view elements properly */
        String title = "";
        String description = "";
        String url = "";
        setItemInformation(title, description, url);

        TextView titleView = view.findViewById(R.id.item_title);
        titleView.setText(title);

        TextView descriptionView = (TextView) view.findViewById(R.id.item_description_text);
        descriptionView.setText(description);


        setProgressBarIndicator();

        review.setOnClickListener(v -> {
            if (errorTextView.getText().length() > 0) {
                errorTextView.setText("");
            }
        });

        return view;
    }

    /**
     * Adds a listener to the progress bar in order to attach the indicator above (displays the
     * rating)
     */
    private void setProgressBarIndicator() {
        SeekBar ratingSlider = view.findViewById(R.id.item_rating_slider);
        TextView ratingIndicator = view.findViewById(R.id.item_rating_slider_progress);

        // We add a listener to the slide bar to update the indicator text (displays the rating on
        // top of it)
        ratingSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                // get position of slide bar and set the text to match rating and position
                int val =
                        (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                ratingIndicator.setText(String.valueOf(progress));
                ratingIndicator.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2.f);
                ratingIndicator.setY(seekBar.getY() - ratingIndicator.getTextSize() * 1.5f);
            }
        });
    }

    /**
     * Sets the on-screen title and description to match those given as parameters
     *
     * @param title:       the item title
     * @param description: the item description
     * @param url:         the item image resource link
     */
    private void setItemInformation(String title, String description, String url) {

        ((TextView) this.view.findViewById(R.id.item_title)).setText(title);

        ((TextView) this.view.findViewById(R.id.item_description_text)).setText(description);

        /* ToDO 2 : fetch the image from url and display it */
        // ((ImageView) getView().findViewById(R.id.item_image)).setImageResource(image_res);

    }

    /**
     * Called by "add" button onclick, displays an error when character limit is exceeded
     *
     * @param view: the activity view
     */
    public void addItem(View view) {

        TextView errorTextView = view.findViewById(R.id.new_item_review_error_msg);
        EditText review = view.findViewById(R.id.item_review_edittext);

        if (review.getText().length() > MAX_REVIEW_LENGTH) {
            getActivity().runOnUiThread(() -> errorTextView.setText(
                    String.format(Locale.ENGLISH, "Exceeded character limit: %d", MAX_REVIEW_LENGTH)));
            ;
        }
    }
}
