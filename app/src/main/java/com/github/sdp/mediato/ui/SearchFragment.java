package com.github.sdp.mediato.ui;

import static com.github.sdp.mediato.data.UserDatabase.followUser;
import static com.github.sdp.mediato.data.UserDatabase.getAllUser;
import static com.github.sdp.mediato.data.UserDatabase.unfollowUser;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.data.GenreMovies;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.ui.viewmodel.SearchMediaViewModel;
import com.github.sdp.mediato.ui.viewmodel.UserViewModel;
import com.github.sdp.mediato.utility.adapters.MediaAdapter;
import com.github.sdp.mediato.utility.adapters.UserAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SearchFragment extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener, UserAdapter.OnUserInteractionListener {

    private static String COLLECTION_NAME;
    private static String USERNAME;
    private UserViewModel userViewModel;
    private SearchMediaViewModel searchMediaViewModel;

    private UserAdapter userAdapter;
    private MediaAdapter movieSearchAdapter;
    private MediaAdapter movieTrendingAdapter;
    private MediaAdapter bookSearchAdapter;
    private MediaAdapter bookTrendingAdapter;

    private RecyclerView userSearchRecyclerView;
    private RecyclerView movieSearchRecyclerView;
    private RecyclerView bookSearchRecyclerView;
    private RecyclerView movieTrendingRecyclerView;
    private RecyclerView bookTrendingRecyclerView;

    private SearchView searchBar;

    private Button peopleButton;
    private Button booksButton;
    private Button filmButton;
    private Button currentHighlightedButton;

    private Spinner year_filter;
    private Spinner genre_filter;

    private LinearLayout filter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        USERNAME = ((MainActivity)getActivity()).getMyProfileViewModel().getUsername();
        COLLECTION_NAME = (String) getArguments().get("collection");

        // Create and init the Search User ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userAdapter = new UserAdapter(
                getActivity(),
                ((MainActivity)getActivity()).getMyProfileViewModel().getUser(),
                new ArrayList<>()
        );
        userAdapter.setOnUserInteractionListener(this);
        userViewModel.setConnectedUsername(USERNAME);
        userViewModel.getUserListLiveData().observe(this, userAdapter::updateUserList);
        userViewModel.getConnectedUserLiveData().observe(this, userAdapter::updateConnectedUser);

        searchMediaViewModel = new ViewModelProvider(this).get(SearchMediaViewModel.class);

        setMediaAdapter();

        String isGeneralSearch = (String) getArguments().get("general_search");
        if (isGeneralSearch != null && !isGeneralSearch.equals("true")) {
            this.searchMediaViewModel.setCurrentCategory(SearchCategory.MOVIES);
        } else {
            this.searchMediaViewModel.setCurrentCategory(SearchCategory.PEOPLE);
        }
    }

    private void setMediaAdapter() {
        movieSearchAdapter = new MediaAdapter(getActivity(), COLLECTION_NAME);
        searchMediaViewModel.getSearchMoviesLiveData().observe(this, movieSearchAdapter::update);

        movieTrendingAdapter = new MediaAdapter(getActivity(), COLLECTION_NAME);
        searchMediaViewModel.getTrendingMoviesLiveData().observe(this, movieTrendingAdapter::update);

        bookSearchAdapter = new MediaAdapter(getActivity(), COLLECTION_NAME);
        searchMediaViewModel.getSearchBooksLiveData().observe(this, bookSearchAdapter::update);

        bookTrendingAdapter = new MediaAdapter(getActivity(), COLLECTION_NAME);
        searchMediaViewModel.getTrendingBooksLiveData().observe(this, bookTrendingAdapter::update);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for the search fragment
        View searchView = inflater.inflate(R.layout.fragment_search, container, false);

        this.filter = searchView.findViewById(R.id.top_movies_filters);

        this.peopleButton = searchView.findViewById(R.id.search_category_people);
        peopleButton.setOnClickListener(this);

        this.booksButton = searchView.findViewById(R.id.search_category_books);
        booksButton.setOnClickListener(this);

        this.filmButton = searchView.findViewById(R.id.search_category_movie);
        filmButton.setOnClickListener(this);

        // Set the Search User RecyclerView with its adapter
        userSearchRecyclerView = searchView.findViewById(R.id.userSearch_recyclerView);
        userSearchRecyclerView.setAdapter(userAdapter);

        setMediaRecyclerView(searchView);

        // get the search view and bind it to a listener
        searchBar = searchView.findViewById(R.id.searchbar);
        searchBar.setOnQueryTextListener(this);
        searchBar.setQuery(this.searchMediaViewModel.getSearchQuery(), false);

        setUpYearFilter(searchView);

        // Get reference to the spinner
        setUpGenreFilter(searchView);

        Button resetFilter = searchView.findViewById(R.id.reset_filter);
        resetFilter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                genre_filter.setSelection(0);
                year_filter.setSelection(0);
                searchMediaViewModel.setYear_filter("Year");
                searchMediaViewModel.setGenre_filter("Genre");
                searchMediaViewModel.loadFirstMovieTrendingPage();
            }
        });


        setDisplayComponent();

        userViewModel.reloadUser();

        return searchView;
    }

    private void setUpGenreFilter(View searchView) {
        genre_filter = searchView.findViewById(R.id.genre_spinner);

        // Create an instance of GenreMovies and get the list of genres
        List<String> genres = GenreMovies.getGenreName();

        // Create an ArrayAdapter using the genre list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, genres);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        genre_filter.setAdapter(adapter);

        int position = adapter.getPosition(searchMediaViewModel.getGenre_filter());
        genre_filter.setSelection(position);

        genre_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchMediaViewModel.setGenre_filter((String) parent.getItemAtPosition(position));
                searchMediaViewModel.loadFirstMovieTrendingPage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setUpYearFilter(View searchView) {
        // Get reference to the spinner
        year_filter = searchView.findViewById(R.id.years_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.years_array, R.layout.spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        year_filter.setAdapter(adapter);

        int position = adapter.getPosition(searchMediaViewModel.getYear_filter());
        year_filter.setSelection(position);

        year_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchMediaViewModel.setYear_filter((String) parent.getItemAtPosition(position));
                searchMediaViewModel.loadFirstMovieTrendingPage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setMediaRecyclerView(View searchView) {
        movieSearchRecyclerView = searchView.findViewById(R.id.movieSearch_recyclerView);
        movieSearchRecyclerView.setAdapter(movieSearchAdapter);

        movieTrendingRecyclerView = searchView.findViewById(R.id.movieTrending_recyclerView);
        movieTrendingRecyclerView.setAdapter(movieTrendingAdapter);

        bookSearchRecyclerView = searchView.findViewById(R.id.bookSearch_recyclerView);
        bookSearchRecyclerView.setAdapter(bookSearchAdapter);

        bookTrendingRecyclerView= searchView.findViewById(R.id.bookTrending_recyclerView);
        bookTrendingRecyclerView.setAdapter(bookTrendingAdapter);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        if (s.length() > 0) {
            this.searchMediaViewModel.setSearchQuery(s);
            search(s);
        }
        return false;
    }

    private void search(String s) {
        if (this.searchMediaViewModel.getCurrentCategory() == SearchCategory.PEOPLE) {
            searchUser(s);
        } else {
            searchMediaViewModel.loadFirstMovieBookSearchPage(s);
            if (this.searchMediaViewModel.getCurrentCategory() == SearchCategory.MOVIES) {
                this.movieSearchRecyclerView.setVisibility(View.VISIBLE);
                this.movieTrendingRecyclerView.setVisibility(View.GONE);
                this.filter.setVisibility(View.GONE);
            } else {
                this.bookSearchRecyclerView.setVisibility(View.VISIBLE);
                this.bookTrendingRecyclerView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (s.length() < 1) {
            this.searchMediaViewModel.setSearchQuery(s);
            switch (this.searchMediaViewModel.getCurrentCategory()) {
                case MOVIES:
                    this.movieSearchRecyclerView.setVisibility(View.GONE);
                    this.movieTrendingRecyclerView.setVisibility(View.VISIBLE);
                    this.filter.setVisibility(View.VISIBLE);
                    break;
                case BOOKS:
                    this.bookSearchRecyclerView.setVisibility(View.GONE);
                    this.bookTrendingRecyclerView.setVisibility(View.VISIBLE);
                    break;
                case PEOPLE:
                    this.userViewModel.clearUserList();
                    break;
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {

        this.currentHighlightedButton.setTypeface(null, Typeface.NORMAL);
        this.currentHighlightedButton.setPaintFlags(this.currentHighlightedButton.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));

        if (view == peopleButton) {
            this.searchMediaViewModel.setCurrentCategory(SearchFragment.SearchCategory.PEOPLE);
        } else if (view == booksButton) {
            this.searchMediaViewModel.setCurrentCategory(SearchFragment.SearchCategory.BOOKS);
        } else if (view == filmButton) {
            this.searchMediaViewModel.setCurrentCategory(SearchFragment.SearchCategory.MOVIES);
        }

        setDisplayComponent();
    }

    public void setDisplayComponent() {
        hideAllRecyclerView();

        switch (this.searchMediaViewModel.getCurrentCategory()) {
            case PEOPLE:
                this.currentHighlightedButton = peopleButton;
                this.userSearchRecyclerView.setVisibility(View.VISIBLE);
                break;
            case BOOKS:
                this.currentHighlightedButton = booksButton;
                setMediaComponents(
                        null,
                        bookSearchRecyclerView,
                        bookTrendingRecyclerView,
                        searchMediaViewModel.getTitleSearch()
                );
                break;
            case MOVIES:
                this.currentHighlightedButton = filmButton;
                setMediaComponents(
                        filter,
                        movieSearchRecyclerView,
                        movieTrendingRecyclerView,
                        searchMediaViewModel.getTitleSearch()
                );
                break;
        }


        this.currentHighlightedButton.setTypeface(null, Typeface.BOLD);
        this.currentHighlightedButton.setPaintFlags(this.currentHighlightedButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    private void hideAllRecyclerView() {
        this.movieTrendingRecyclerView.setVisibility(View.GONE);
        this.movieSearchRecyclerView.setVisibility(View.GONE);
        this.bookTrendingRecyclerView.setVisibility(View.GONE);
        this.bookSearchRecyclerView.setVisibility(View.GONE);
        this.userSearchRecyclerView.setVisibility(View.GONE);
        this.filter.setVisibility(View.GONE);
    }

    private void setMediaComponents(LinearLayout filter, RecyclerView searchRecyclerView, RecyclerView trendingRecyclerView, String oldTitle) {
        if (this.searchBar.getQuery().length() > 0) {
            if (!this.searchBar.getQuery().toString().equals(oldTitle)) {
                searchMediaViewModel.loadFirstMovieBookSearchPage(this.searchBar.getQuery().toString());
            }
            searchRecyclerView.setVisibility(View.VISIBLE);
        } else {
            trendingRecyclerView.setVisibility(View.VISIBLE);
            if (filter != null) {
                filter.setVisibility(View.VISIBLE);
            }
        }
    }

    private void searchUser(String toBeSearched) {
        getAllUser(USERNAME).thenAccept(users -> {
            List<User> filteredUser = users.stream()
                .filter(user -> user.getUsername().toLowerCase().startsWith(toBeSearched.toLowerCase()))
                .collect(Collectors.toList());
            sortUsersByName(filteredUser);
            userViewModel.setUserList(filteredUser);
        });
    }

    private static void sortUsersByName(List<User> userList) {
        userList.sort(Comparator.comparing(u -> u.getUsername().toLowerCase()));
    }

    @Override
    public void onFollowClick(User user) {
        followUser(userViewModel.getConnectedUser().getUsername(), user.getUsername());
        userViewModel.reloadUser();
    }

    @Override
    public void onUnfollowClick(User user) {
        unfollowUser(userViewModel.getConnectedUser().getUsername(), user.getUsername());
        userViewModel.reloadUser();
    }

    public enum SearchCategory {
        PEOPLE,
        MOVIES,
        BOOKS
    }
}