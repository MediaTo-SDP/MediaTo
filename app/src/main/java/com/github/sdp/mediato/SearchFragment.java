package com.github.sdp.mediato;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.sdp.mediato.model.LocalFilmDatabase;
import com.github.sdp.mediato.model.media.Media;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener {

    private enum SearchCategory {
        PEOPLE,
        MOVIES,
        BOOKS,
        MUSIC
    }

    private SearchView searchView;
    private TextView textView;
    private GridView gridView;

    private Button peopleButton;
    private Button booksButton;
    private Button filmButton;
    private Button musicButton;

    private SearchFragment.SearchCategory currentCategory;
    private Button currentHighlightedButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for the search fragment
        View searchView = inflater.inflate(R.layout.search_menu, container, false);

        this.peopleButton = searchView.findViewById(R.id.searchactivity_setCategorie_people);
        peopleButton.setOnClickListener(this);

        this.booksButton = searchView.findViewById(R.id.searchactivity_setCategorie_books);
        booksButton.setOnClickListener(this);

        this.filmButton = searchView.findViewById(R.id.searchactivity_setCategorie_film);
        filmButton.setOnClickListener(this);

        this.musicButton = searchView.findViewById(R.id.searchactivity_setCategorie_music);
        musicButton.setOnClickListener(this);

        // get the text view
        this.textView = searchView.findViewById(R.id.searchactivity_textView_textDuringAfterSearch);

        // get the list view
        this.gridView = searchView.findViewById(R.id.searchactivity_gridview_searchresults);

        // get the search view and bind it to a listener
        this.searchView = searchView.findViewById(R.id.searchactivity_searchview_searchbar);
        this.searchView.setOnQueryTextListener(this);

        // finally warmup the system, the activity starts by searching for peoples
        this.currentHighlightedButton = peopleButton;
        this.currentHighlightedButton.setPaintFlags(this.currentHighlightedButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        this.currentCategory = SearchFragment.SearchCategory.PEOPLE;

        return searchView;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        if (s.length() > 0) {
            // this.mTextView.setText("Results");
            // List<Media> searchResults = search(s);
            // gridView.setAdapter(new MediaAdapter(this.getContext(), searchResults));

            // toDO : call the search function
            // toDO : update the GridView
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (s.length() > 0) {
            // this.mTextView.setText("Suggested");
            //search(s);
            // toDO : call the search function
            // toDO : update the GridView
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view == peopleButton) {
            this.currentCategory = SearchFragment.SearchCategory.PEOPLE;
        } else if (view == booksButton) {
            this.currentCategory = SearchFragment.SearchCategory.BOOKS;
        } else if (view == filmButton) {
            this.currentCategory = SearchFragment.SearchCategory.MOVIES;
        } else if (view == musicButton) {
            this.currentCategory = SearchFragment.SearchCategory.MUSIC;
        }

        this.currentHighlightedButton.setTypeface(null, Typeface.NORMAL);
        this.currentHighlightedButton.setPaintFlags(this.currentHighlightedButton.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        this.currentHighlightedButton = (Button) view;
        this.currentHighlightedButton.setTypeface(null, Typeface.BOLD);
        this.currentHighlightedButton.setPaintFlags(this.currentHighlightedButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    private List<Media> search(String toBeSearched) {
        List<Media> searchMediaResults = new ArrayList<Media>();
        switch (this.currentCategory) {
            case PEOPLE:
                break;
            case MOVIES:

                // toDO : fetch from firebase 
                // for now fetches from a local database
                if (toBeSearched.equals("james bond")) {
                    searchMediaResults = new LocalFilmDatabase().getMovieItems();
                }
                break;
            case BOOKS:
                break;
            case MUSIC:
                break;
        }
        return searchMediaResults;
    }
}