package com.github.sdp.mediato.ui;

import static com.github.sdp.mediato.data.UserDatabase.getAllUser;
import static com.github.sdp.mediato.data.UserDatabase.getUser;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.LocalFilmDatabase;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.ui.viewmodel.SearchUserViewModel;
import com.github.sdp.mediato.utility.adapters.UserAdapter;
import com.github.sdp.mediato.utility.adapters.UserFollowAdapter;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        USERNAME = getArguments().getString("username");
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

        // get the list view
        this.gridView = searchView.findViewById(R.id.searchactivity_gridview_searchresults);

        // Create and init the Search User ViewModel
        searchUserViewModel = new ViewModelProvider(this).get(SearchUserViewModel.class);
        searchUserViewModel.setUserName(USERNAME);

        // Set the Search User RecyclerView with its adapter
        recyclerView = searchView.findViewById(R.id.searchactivity_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(new UserFollowAdapter(searchUserViewModel));

        // get the search view and bind it to a listener
        this.searchView = searchView.findViewById(R.id.searchactivity_searchview_searchbar);
        this.searchView.setOnQueryTextListener(this);

        // finally warmup the system, the activity starts by searching for peoples
        this.currentHighlightedButton = peopleButton;
        this.currentHighlightedButton.setPaintFlags(this.currentHighlightedButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        this.currentCategory = SearchFragment.SearchCategory.PEOPLE;
        this.gridView.setVisibility(View.GONE);

        return searchView;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        search(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        search(s);
        return false;
    }

    @Override
    public void onClick(View view) {
        SwitchGridOrRecyclerView(view);

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

    private void SwitchGridOrRecyclerView(View view) {
        if (view == peopleButton) {
            this.gridView.setVisibility(View.GONE);
            this.recyclerView.setVisibility(View.VISIBLE);
        } else {
            this.recyclerView.setVisibility(View.GONE);
            this.gridView.setVisibility(View.VISIBLE);
        }
    }

    private List<Media> search(String toBeSearched) {
        List<Media> searchMediaResults = new ArrayList<Media>();
        switch (this.currentCategory) {
            case PEOPLE:
                searchUser(toBeSearched);
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

    private void searchUser(String toBeSearched) {
        if (toBeSearched.length() > 0) {
            getAllUser(USERNAME).thenAccept(users -> {
                List<User> filteredUser = users.stream()
                    .filter(user -> user.getUsername().toLowerCase().startsWith(toBeSearched.toLowerCase()))
                    .collect(Collectors.toList());
                sortUsersByName(filteredUser);
                searchUserViewModel.setUserList(filteredUser);
            }).exceptionally(throwable -> {
                searchUserViewModel.clearUserList();
                displaySnackbar(R.string.searchUserFailed);
                return null;
            });
        } else {
            searchUserViewModel.clearUserList();
        }
    }

    private static void sortUsersByName(List<User> userList) {
        Collections.sort(userList, Comparator.comparing(u -> u.getUsername().toLowerCase()));
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