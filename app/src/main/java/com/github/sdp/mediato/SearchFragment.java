package com.github.sdp.mediato;

import static com.github.sdp.mediato.data.Database.getUser;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sdp.mediato.api.themoviedb.TheMovieDB;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.Movie;
import com.github.sdp.mediato.ui.viewmodel.SearchUserViewModel;
import com.github.sdp.mediato.utility.adapters.MediaListAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchFragment extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener {

    private enum SearchCategory {
        PEOPLE,
        MOVIES,
        BOOKS,
        MUSIC
    }

    private Snackbar failedUserSearch;
    private SearchUserViewModel searchUserViewModel;

    private SearchView searchView;
    private TextView textView;
    private GridView gridView;

    private RecyclerView recyclerView;

    private Button peopleButton;
    private Button booksButton;
    private Button filmButton;
    private Button musicButton;

    private SearchFragment.SearchCategory currentCategory;
    private Button currentHighlightedButton;

    private static String USERNAME;

    private TheMovieDB theMovieDB;

    private final MutableLiveData<List<Media>> searchMediaResults = new MutableLiveData<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        USERNAME = getArguments().getString("username");

        theMovieDB = new TheMovieDB(getString(R.string.tmdb_url), getString(R.string.TMDBAPIKEY));

    }

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

        // Create and init the Search User ViewModel
        searchUserViewModel = new ViewModelProvider(this).get(SearchUserViewModel.class);
        searchUserViewModel.setUserName(USERNAME);

        // Set the Search User RecyclerView with its adapter
        recyclerView = searchView.findViewById(R.id.searchactivity_recyclerView);

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
            //this.mTextView.setText("Results");
            searchAndDisplayResult(s);
            //gridView.setAdapter(new MediaAdapter(this.getContext(), searchResults));

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

    private void searchAndDisplayResult(String toBeSearched) {
        if (this.currentCategory == SearchCategory.PEOPLE) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
            recyclerView.setAdapter(new SearchUserAdapter(searchUserViewModel));
            searchUser(toBeSearched);
        } else {
            switch (this.currentCategory) {
                case MOVIES:
                    // fetch from API
                    theMovieDB.searchItems(toBeSearched, 40).thenAccept(list -> {
                        searchMediaResults.setValue(list.stream().map(Movie::new).collect(Collectors.toList()));
                    });
                    break;
                case BOOKS:
                    break;
                case MUSIC:
                    break;
            }
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            MediaListAdapter mla = new MediaListAdapter(getActivity());
            recyclerView.setAdapter(mla);
            searchMediaResults.observe(getViewLifecycleOwner(), mla::submitList);
        }
    }

    private void searchUser(String toBeSearched) {
        //TODO Use a search engine
        getUser(toBeSearched).thenAccept(user -> {
            List<User> users = new ArrayList<>();
            users.add(user);
            searchUserViewModel.setUserList(users);
        }).exceptionally(throwable -> {
            searchUserViewModel.clearUserList();
            displaySnackbar(R.string.searchUserFailed);
            return null;
        });
    }

    private void displaySnackbar(int msg) {
        Snackbar snackbar = Snackbar.make(
                getActivity().getWindow().getDecorView().findViewById(android.R.id.content),
                msg,
                Snackbar.LENGTH_SHORT
        );
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}