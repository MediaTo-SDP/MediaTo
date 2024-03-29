package com.github.sdp.mediato.ui;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.sdp.mediato.utility.FragmentSwitcher;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.api.openlibrary.OLAPI;
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

public class NewItemFragment extends Fragment {
    public final static int MAX_REVIEW_LENGTH = 100;
    public final static int MAX_SUMMARY_LENGTH = 300;
    public WebView webView;
    public EditText reviewText;
    // The maximum allowed length for review field
    private OLAPI oLAPI = new OLAPI("https://openlibrary.org/");
    private View view;
    private Media media;
    private FragmentSwitcher fragmentSwitcher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_item, container, false);
        this.view = view;

        reviewText = view.findViewById(R.id.item_review_edittext);
        InputFilter[] filters = new InputFilter[] { new InputFilter.LengthFilter(MAX_REVIEW_LENGTH) };
        reviewText.setFilters(filters);

        fragmentSwitcher = (FragmentSwitcher) getActivity();

        Button addButton = view.findViewById(R.id.item_button_add);
        addButton.setOnClickListener(v -> addItem(view, reviewText));

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            media = (Media) bundle.get("media");
        }

        setItemInformation(media.getTitle(), media.getSummary(), media.getPosterUrl());
        setObserverOnKeyboard(view);
        loadDescriptionIfMissing();
        setProgressBarIndicator();
        searchTrailer();

        webView = view.findViewById(R.id.trailer_web_view);

        return view;
    }

    private void setObserverOnKeyboard(View view) {
        // Get references to the views
        ScrollView scrollView = view.findViewById(R.id.scrollView);

        // Set an OnLayoutChangeListener to the root view to detect layout changes
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Calculate the difference between the root view's height and the visible content height
                int heightDiff = scrollView.getRootView().getHeight() - scrollView.getHeight();

                // Check if the height difference is above a certain threshold to consider it as the keyboard being open
                int keyboardThreshold = 200;
                if (heightDiff > keyboardThreshold) {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }
        });
    }

    private void loadDescriptionIfMissing() {
        if (media.getMediaType() == MediaType.BOOK && media.getSummary().equals("Loading Description ...")) {
            oLAPI.getDescription(media.getId()).thenAccept(description -> {
                media.setSummary(description);
                getActivity().runOnUiThread(() -> {
                    setDescription(media.getSummary());
                });
            });
        }
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
                if (getActivity() != null && listResponse != null && !listResponse.getItems().isEmpty()) {
                    getActivity()
                            .runOnUiThread(() -> // we go back to ui thread (mandatory)
                                    handleTrailerResponse(listResponse.getItems().get(0)));
                }
            } catch (IOException e) {
                getActivity().runOnUiThread(() -> {
                    ImageView playButton = view.findViewById(R.id.item_play_button);
                    playButton.setVisibility(View.GONE);
                    webView.setVisibility(View.GONE);
                });
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
        ratingIndicator.setText(String.valueOf(progress));
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
        setDescription(description);
        ImageView img = view.findViewById(R.id.item_image);
        Glide.with(this).load(url).into(img);

    }

    private void setDescription(String description) {
        String shortDescription = description.length() > MAX_SUMMARY_LENGTH ? description.substring(0, MAX_SUMMARY_LENGTH) : description;

        ((TextView) this.view.findViewById(R.id.item_description_text)).setText(shortDescription);
    }

    /**
     * Called by "add" button onclick, displays an error when character limit is exceeded
     *
     * @param view: the activity view
     */
    public void addItem(View view, EditText reviewText) {

        TextView ratingIndicator = view.findViewById(R.id.item_rating_slider_progress);

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
