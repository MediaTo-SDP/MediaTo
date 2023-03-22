package com.github.sdp.mediato.ui;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.sdp.mediato.R;

public class SearchBar extends AppCompatActivity implements View.OnClickListener,
    SearchView.OnQueryTextListener {

  private SearchView searchView;
  private TextView textView;
  private ListView listView;

  private Button peopleButton;
  private Button booksButton;
  private Button filmButton;
  private Button musicButton;

  private SearchCategory currentCategory;
  private Button currentHighlightedButton;

  @Override
  public boolean onQueryTextSubmit(String s) {

    if (s.length() > 0) {
      // this.mTextView.setText("Results");
      search(s);
      // toDO : call the search function
      // toDO : update the ViewList
    }
    return false;
  }

  @Override
  public boolean onQueryTextChange(String s) {
    if (s.length() > 0) {
      // this.mTextView.setText("Suggested");
      search(s);
      // toDO : call the search function
      // toDO : update the ViewList
    }
    return false;
  }

  @Override
  public void onClick(View view) {

    if (view == peopleButton) {
      this.currentCategory = SearchCategory.PEOPLE;
    } else if (view == booksButton) {
      this.currentCategory = SearchCategory.BOOKS;
    } else if (view == filmButton) {
      this.currentCategory = SearchCategory.MOVIES;
    } else if (view == musicButton) {
      this.currentCategory = SearchCategory.MUSIC;
    }

    // toDO : implement a try catch version of this (in case hte currentHighLightedButton is null for example)
    this.currentHighlightedButton.setTypeface(null, Typeface.NORMAL);
    this.currentHighlightedButton.setPaintFlags(
        this.currentHighlightedButton.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
    this.currentHighlightedButton = (Button) view;
    this.currentHighlightedButton.setTypeface(null, Typeface.BOLD);
    this.currentHighlightedButton.setPaintFlags(
        this.currentHighlightedButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
  }

  /* attributes */
  private enum SearchCategory {
    PEOPLE,
    MOVIES,
    BOOKS,
    MUSIC
  }

  /* methods */
  @Override
  protected void onCreate(Bundle savedInstanceState) {

    // render the activity
    super.onCreate(savedInstanceState);
    setContentView(R.layout.search_menu);

    // get the categorie buttons and associate a listener to all of them
    this.peopleButton = findViewById(R.id.searchactivity_setCategorie_people);
    peopleButton.setOnClickListener(this);

    this.booksButton = findViewById(R.id.searchactivity_setCategorie_books);
    booksButton.setOnClickListener(this);

    this.filmButton = findViewById(R.id.searchactivity_setCategorie_film);
    filmButton.setOnClickListener(this);

    this.musicButton = findViewById(R.id.searchactivity_setCategorie_music);
    musicButton.setOnClickListener(this);

    // get the text view
    this.textView = findViewById(R.id.searchactivity_textView_textDuringAfterSearch);

    // get the list view
    this.listView = findViewById(R.id.searchactivity_listview_searchresults);

    // get the search view and bind it to a listener
    this.searchView = findViewById(R.id.searchactivity_searchview_searchbar);
    this.searchView.setOnQueryTextListener(this);

    // finally warmup the system, the activity starts by searching for peoples
    this.currentHighlightedButton = peopleButton;
    this.currentHighlightedButton.setPaintFlags(
        this.currentHighlightedButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    this.currentCategory = SearchCategory.PEOPLE;
  }

  /**
   * not implemented yet
   */
  private void search(String toBeSearched) {
    switch (this.currentCategory) {
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

