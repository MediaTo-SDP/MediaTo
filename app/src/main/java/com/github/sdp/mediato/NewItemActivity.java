package com.github.sdp.mediato;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

/**
 * New item activity page, used to add a rating to an object as well as a review using a slide bar
 * and edit text
 */
public class NewItemActivity extends AppCompatActivity {

    // The maximum allowed length for review field
    public final static int MAX_REVIEW_LENGTH = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // set the item title, description, etc
        // for now, only mock values before linking to database
        setItemInformation("OSS 117", "OSS 117 : Alerte rouge en Afrique noire ou" +
                " OSS 117: Bons Baisers d'Afrique au Québec est une comédie d'espionnage" +
                " française réalisé par Nicolas Bedos et sorti en 2021.", R.drawable.oss117_item_test);

        setProgressBarIndicator();

        TextView errorTextView = findViewById(R.id.new_item_review_error_msg);
        EditText review = findViewById(R.id.item_review_edittext);

        // Clear the error message (if there is one) once the user edits the text
        review.setOnClickListener(v -> {
            if (errorTextView.getText().length() > 0) {
                errorTextView.setText("");
            }
        });

    }

    /**
     * Adds a listener to the progress bar in order to attach the indicator above (displays the rating)
     */
    private void setProgressBarIndicator() {
        SeekBar ratingSlider = findViewById(R.id.item_rating_slider);
        TextView ratingIndicator = findViewById(R.id.item_rating_slider_progress);

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
                int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
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
     * @param image_res:   the item image resource link
     */
    private void setItemInformation(String title, String description, int image_res) {

        ((TextView) findViewById(R.id.item_title)).setText(title);

        ((TextView) findViewById(R.id.item_description_text)).setText(description);

        ((ImageView) findViewById(R.id.item_image)).setImageResource(image_res);

    }

    /**
     * Called by "add" button onclick, displays an error when character limit is exceeded
     * @param view: the activity view
     */
    public void addItem(View view) {

        TextView errorTextView = findViewById(R.id.new_item_review_error_msg);
        EditText review = findViewById(R.id.item_review_edittext);

        if (review.getText().length() > MAX_REVIEW_LENGTH) {
            runOnUiThread(  ()->          errorTextView.setText(
                    String.format(Locale.ENGLISH, "Exceeded character limit: %d", MAX_REVIEW_LENGTH)));
;
        }
    }
}