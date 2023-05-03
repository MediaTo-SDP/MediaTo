package com.github.sdp.mediato.ui;

import static com.github.sdp.mediato.data.UserDatabase.getAllUser;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.sdp.mediato.MainActivity;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.api.gbook.GBookAPI;
import com.github.sdp.mediato.api.themoviedb.TheMovieDBAPI;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.model.media.Book;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.Movie;
import com.github.sdp.mediato.ui.viewmodel.SearchUserViewModel;
import com.github.sdp.mediato.utility.adapters.MediaListAdapter;
import com.github.sdp.mediato.utility.adapters.UserAdapter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SearchFragment extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener {

    private static String USERNAME;
    private SearchUserViewModel searchUserViewModel;

    private SearchView searchView;
    private TextView textView;

    private RecyclerView recyclerView;

    private Button peopleButton;
    private Button booksButton;
    private Button filmButton;
    private Button musicButton;

    private SearchFragment.SearchCategory currentCategory;
    private Button currentHighlightedButton;
    private TheMovieDBAPI theMovieDB;

    private GBookAPI gBookAPI;

    private boolean launchedByCollection;

    private final MutableLiveData<List<Media>> searchMediaResults = new MutableLiveData<>();

    public SearchFragment(){
        super();
        this.launchedByCollection = false;
    }

    public SearchFragment(boolean launchedByCollection){
        super();
        this.launchedByCollection = launchedByCollection;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        USERNAME = ((MainActivity)getActivity()).getMyProfileViewModel().getUsername();
        theMovieDB = new TheMovieDBAPI(getString(R.string.tmdb_url), getString(R.string.TMDBAPIKEY));
        gBookAPI = new GBookAPI(getString(R.string.gbook_url));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for the search fragment
        View searchView = inflater.inflate(R.layout.fragment_search, container, false);

        this.peopleButton = searchView.findViewById(R.id.search_category_people);
        peopleButton.setOnClickListener(this);
        if(launchedByCollection){
            peopleButton.setVisibility(View.GONE);
        }

        this.booksButton = searchView.findViewById(R.id.search_category_books);
        booksButton.setOnClickListener(this);

        this.filmButton = searchView.findViewById(R.id.search_category_movie);
        filmButton.setOnClickListener(this);

        this.musicButton = searchView.findViewById(R.id.search_category_music);
        musicButton.setOnClickListener(this);

        // get the text view
        this.textView = searchView.findViewById(R.id.searchactivity_textView_textDuringAfterSearch);

        // Create and init the Search User ViewModel
        searchUserViewModel = new ViewModelProvider(this).get(SearchUserViewModel.class);
        searchUserViewModel.setUserName(USERNAME);
        searchUserViewModel.setMainActivity((MainActivity) getActivity());

        // Set the Search User RecyclerView with its adapter
        recyclerView = searchView.findViewById(R.id.searchactivity_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(new UserAdapter(searchUserViewModel));

        // get the search view and bind it to a listener
        this.searchView = searchView.findViewById(R.id.searchbar);
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
            searchAndDisplayResult(s);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        searchAndDisplayResult(s);
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

    private void searchAndDisplayResult(String toBeSearched) {
        if (this.currentCategory == SearchCategory.PEOPLE) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
            recyclerView.setAdapter(new UserAdapter(searchUserViewModel));
            searchUser(toBeSearched);
        } else {
            searchMedia(toBeSearched);
        }
    }

    private void searchMedia(String toBeSearched){
        switch (this.currentCategory) {
            case MOVIES:
                if (!toBeSearched.isEmpty()) {
                    searchMediaResults.setValue(Collections.emptyList());
                    // fetch from API
                    theMovieDB.searchItems(toBeSearched, 40).thenAccept(list -> {
                        searchMediaResults.setValue(list.stream().map(Movie::new).collect(Collectors.toList()));
                    });
                } else {
                    searchMediaResults.setValue(Collections.emptyList());
                }
                break;
            case BOOKS:
                if (!toBeSearched.isEmpty()) {
                    searchMediaResults.setValue(Collections.emptyList());
                    // fetch from API
                    gBookAPI.searchItems(toBeSearched, 40).thenAccept(list -> {
                        searchMediaResults.setValue(list.stream().map(Book::new).collect(Collectors.toList()));
                    });
                } else {
                    searchMediaResults.setValue(Collections.emptyList());
                }
                break;
            case MUSIC:
                break;
        }
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        String collectionName = (String) getArguments().get("collection");
        MediaListAdapter mla = new MediaListAdapter(getActivity(), collectionName);
        recyclerView.setAdapter(mla);
        searchMediaResults.observe(getViewLifecycleOwner(), mla::submitList);
    }

    private void searchUser(String toBeSearched) {
        if (toBeSearched.length() > 0) {
            getAllUser(USERNAME).thenAccept(users -> {
                List<User> filteredUser = users.stream()
                        .filter(user -> user.getUsername().toLowerCase().startsWith(toBeSearched.toLowerCase()))
                        .collect(Collectors.toList());
                sortUsersByName(filteredUser);
                searchUserViewModel.setUserList(filteredUser);
            });
        } else {
            searchUserViewModel.clearUserList();
        }
    }

    private static void sortUsersByName(List<User> userList) {
        Collections.sort(userList, Comparator.comparing(u -> u.getUsername().toLowerCase()));
    }

    private enum SearchCategory {
        PEOPLE,
        MOVIES,
        BOOKS,
        MUSIC
    }
}