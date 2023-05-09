package com.github.sdp.mediato.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.sdp.mediato.FragmentSwitcher;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.MediaType;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Locale;

public class NewItemFragment extends Fragment {
    // The maximum allowed length for review field

    public final static int MAX_REVIEW_LENGTH = 100;
    public final static int MAX_SUMMARY_LENGTH = 300;
    public WebView webView;
    private View view;
    private Media media;
    private FragmentSwitcher fragmentSwitcher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_item, container, false);
        this.view = view;

        TextView errorTextView = view.findViewById(R.id.new_item_review_error_msg);
        EditText review = view.findViewById(R.id.item_review_edittext);
        fragmentSwitcher = (FragmentSwitcher) getActivity();

        Button addButton = view.findViewById(R.id.item_button_add);
        addButton.setOnClickListener(v -> addItem(view));

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            media = (Media) bundle.get("media");
        }
        String summary = media.getSummary();
        summary = summary.length() > MAX_SUMMARY_LENGTH ? summary.substring(0, MAX_SUMMARY_LENGTH) : summary;
        setItemInformation(media.getTitle(), summary, media.getPosterUrl());


        setProgressBarIndicator();

        review.setOnClickListener(v -> {
            if (errorTextView.getText().length() > 0) {
                errorTextView.setText("");
            }
        });

        searchTrailer();

        webView = view.findViewById(R.id.trailer_web_view);

        return view;
    }

    /**
     * The youtube search list creation with the API
     *
     * @return the search list
     */
    private YouTube.Search.List createYoutubeSearch() {

        // init the youtube api
        YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), null)
                .setApplicationName(getString(R.string.mt_app_name))
                .setGoogleClientRequestInitializer(request -> request.setDisableGZipContent(true))
                .build();
        YouTube.Search.List search;

        // launch the search
        try {
            search = youtube.search().list("id,snippet");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        search.setKey(getString(R.string.google_api_key));
        search.setQ(media.getTitle() + " trailer");
        search.setType("video");
        search.setMaxResults(1L);

        return search;
    }

    /**
     * Launches a youtube search via the API in order to get a trailer url for the item
     */
    private void searchTrailer() {
        // wrapper
        final YouTube.Search.List search = createYoutubeSearch();
        Thread searchThread = new Thread(() -> { // we need to use another thread to do web searchs
            SearchListResponse listResponse;
            try {
                listResponse = search.execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (getActivity() != null) {
                getActivity()
                        .runOnUiThread(() -> // we go back to ui thread (mandatory)
                                handleTrailerResponse(listResponse.getItems().get(0)));
            }
        });
        searchThread.start();
    }

    /**
     * Once the youtube search has generated results, handle the url given by the first result
     *
     * @param result: the first item of the search result
     */
    private void handleTrailerResponse(SearchResult result) {
        ImageView playButton = view.findViewById(R.id.item_play_button);
        webView.setVisibility(View.INVISIBLE);

        // set click listener if media is a movie or series
        if (media.getMediaType() != MediaType.BOOK) {
            view.findViewById(R.id.item_image).setOnClickListener(v -> {
                // get the url and load it
                String url = getString(R.string.ytb_url) + result.getId().getVideoId();
                webView.loadUrl(url);
            });
            // otherwise hide play button
        } else {
            playButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Adds a listener to the progress bar in order to attach the indicator above (displays the
     * rating)
     */
    private void setProgressBarIndicator() {
        SeekBar ratingSlider = view.findViewById(R.id.item_rating_slider);
        TextView ratingIndicator = view.findViewById(R.id.item_rating_slider_progress);

        ratingIndicator.setText("5");
        setIndicatorToSeekBarPosition(ratingSlider, ratingIndicator, 5);

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
                setIndicatorToSeekBarPosition(seekBar, ratingIndicator, progress);
            }
        });
    }

    /**
     * Private method used to set seek bar indicator to display the score
     *
     * @param seekBar:         the seek bar
     * @param ratingIndicator: the indicator
     * @param progress:        the progress of the seek bar
     */
    private void setIndicatorToSeekBarPosition(SeekBar seekBar, TextView ratingIndicator, int progress) {
        // get position of slide bar and set the text to match rating and position
        int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
        ratingIndicator.setText(String.valueOf(progress));
        ratingIndicator.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2.f);
        ratingIndicator.setY(seekBar.getY() - ratingIndicator.getTextSize() * 1.5f);
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

        ImageView img = view.findViewById(R.id.item_image);
        Glide.with(this).load(url).into(img);

    }

    /**
     * Called by "add" button onclick, displays an error when character limit is exceeded
     *
     * @param view: the activity view
     */
    public void addItem(View view) {

        TextView errorTextView = view.findViewById(R.id.new_item_review_error_msg);
        EditText reviewText = view.findViewById(R.id.item_review_edittext);
        TextView ratingIndicator = view.findViewById(R.id.item_rating_slider_progress);

        if (reviewText.getText().length() > MAX_REVIEW_LENGTH) {
            requireActivity().runOnUiThread(() -> errorTextView.setText(String.format(Locale.ENGLISH, "Exceeded character limit: %d", MAX_REVIEW_LENGTH)));
        } else {
            // Create the review to forward to the profile page
            String username = requireActivity().getIntent().getStringExtra("username");
            Review review = new Review(username, media,
                    Integer.parseInt(ratingIndicator.getText().toString()), reviewText.getText().toString());

            // Forward the current arguments (should be username and the name of the collection to add to) as well as the review to the profile page
            Bundle args = getArguments();
            assert args != null;
            args.putString("review", new Gson().toJson(review));

            // Switch back to the profile page
            MyProfileFragment myProfileFragment = new MyProfileFragment();
            myProfileFragment.setArguments(getArguments());
            fragmentSwitcher.switchCurrentFragmentWithChildFragment(myProfileFragment);
        }
    }
}
