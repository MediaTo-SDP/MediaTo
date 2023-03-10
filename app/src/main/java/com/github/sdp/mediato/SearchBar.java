package com.github.sdp.mediato;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SearchBar extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener {

    @Override
    public boolean onQueryTextSubmit(String s) {

        if(s.length() > 0){
            // this.mTextView.setText("Results");
            search(s);
            // toDO : call the search function
            // toDO : update the ViewList
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if(s.length() > 0){
            // this.mTextView.setText("Suggested");
            search(s);
            // toDO : call the search function
            // toDO : update the ViewList
        }
        return false;
    }

    @Override
    public void onClick(View view) {

        if (view == mPeopleButton) {
            this.currentCategorie = SearchCategorie.PEOPLE;
        } else if (view == mBooksButton) {
            this.currentCategorie = SearchCategorie.BOOKS;
        } else if (view == mFilmButton) {
            this.currentCategorie = SearchCategorie.MOVIES;
        } else if (view == mMusicButton) {
            this.currentCategorie = SearchCategorie.MUSIC;
        }

        // toDO : implement a try catch version of this (in case hte currentHighLightedButton is null for example)
        this.currentHighlightedButton.setTypeface(null, Typeface.NORMAL);
        this.currentHighlightedButton.setPaintFlags(this.currentHighlightedButton.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        this.currentHighlightedButton = (Button) view;
        this.currentHighlightedButton.setTypeface(null, Typeface.BOLD);
        this.currentHighlightedButton.setPaintFlags(this.currentHighlightedButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    /* attributes */
    private enum SearchCategorie{
        PEOPLE,
        MOVIES,
        BOOKS,
        MUSIC
    }

    private SearchView mSearchView;
    private TextView mTextView;
    private ListView mListView;

    private Button mPeopleButton;
    private Button mBooksButton;
    private Button mFilmButton;
    private Button mMusicButton;

    private SearchCategorie currentCategorie;
    private Button currentHighlightedButton;

    /* methods */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // render the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_menu);

        // get the categorie buttons and associate a listener to all of them
        this.mPeopleButton = findViewById(R.id.searchactivity_setCategorie_people);
        mPeopleButton.setOnClickListener(this);

        this.mBooksButton = findViewById(R.id.searchactivity_setCategorie_books);
        mBooksButton.setOnClickListener(this);

        this.mFilmButton = findViewById(R.id.searchactivity_setCategorie_film);
        mFilmButton.setOnClickListener(this);

        this.mMusicButton = findViewById(R.id.searchactivity_setCategorie_music);
        mMusicButton.setOnClickListener(this);

        // get the text view
        this.mTextView = findViewById(R.id.searchactivity_textView_textDuringAfterSearch);

        // get the list view
        this.mListView = findViewById(R.id.searchactivity_listview_searchresults);

        // get the search view and bind it to a listener
        this.mSearchView = findViewById(R.id.searchactivity_searchview_searchbar);
        this.mSearchView.setOnQueryTextListener(this);

        // finally warmup the system, the activity starts by searching for peoples
        this.currentHighlightedButton = mPeopleButton;
        this.currentHighlightedButton.setPaintFlags(this.currentHighlightedButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        this.currentCategorie = SearchCategorie.PEOPLE;
    }

    /**
     * not implemented yet
     */
    private void search(String toBeSearched){
        switch(this.currentCategorie){
            case PEOPLE:
                break;
            case MOVIES:
                break;
            case BOOKS:
                break;
            case MUSIC:
                break;
        }
    }
}

