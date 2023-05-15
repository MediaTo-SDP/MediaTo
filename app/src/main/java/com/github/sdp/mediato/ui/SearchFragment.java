package com.github.sdp.mediato.ui;

import static com.github.sdp.mediato.data.UserDatabase.getAllUser;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.ui.viewmodel.SearchMediaViewModel;
import com.github.sdp.mediato.ui.viewmodel.SearchUserViewModel;
import com.github.sdp.mediato.utility.adapters.MediaAdapter;
import com.github.sdp.mediato.utility.adapters.MediaListAdapter;
import com.github.sdp.mediato.utility.adapters.UserAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SearchFragment extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener {

    private static String COLLECTION_NAME;
    private static String USERNAME;
    private SearchUserViewModel searchUserViewModel;
    private SearchMediaViewModel searchMediaViewModel;

    private RecyclerView userSearchRecyclerView;
    private RecyclerView movieSearchRecyclerView;
    private RecyclerView bookSearchRecyclerView;
    private RecyclerView movieTrendingRecyclerView;
    private RecyclerView bookTrendingRecyclerView;

    private Button peopleButton;
    private Button booksButton;
    private Button filmButton;
    private SearchFragment.SearchCategory currentCategory;
    private Button currentHighlightedButton;

    private final MutableLiveData<List<Media>> searchMediaResults = new MutableLiveData<>(new ArrayList<>());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        USERNAME = ((MainActivity)getActivity()).getMyProfileViewModel().getUsername();
        COLLECTION_NAME = (String) getArguments().get("collection");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for the search fragment
        View searchView = inflater.inflate(R.layout.fragment_search, container, false);

        this.peopleButton = searchView.findViewById(R.id.search_category_people);
        peopleButton.setOnClickListener(this);

        this.booksButton = searchView.findViewById(R.id.search_category_books);
        booksButton.setOnClickListener(this);

        this.filmButton = searchView.findViewById(R.id.search_category_movie);
        filmButton.setOnClickListener(this);

        String isGeneralSearch = (String) getArguments().get("general_search");
        if (isGeneralSearch != null && !isGeneralSearch.equals("true")) {
            peopleButton.setVisibility(View.GONE);
        }

        // Create and init the Search User ViewModel
        searchUserViewModel = new ViewModelProvider(this).get(SearchUserViewModel.class);
        searchUserViewModel.setUserName(USERNAME);
        searchUserViewModel.setMainActivity((MainActivity) getActivity());

        searchMediaViewModel = new ViewModelProvider(this).get(SearchMediaViewModel.class);

        // Set the Search User RecyclerView with its adapter
        userSearchRecyclerView = searchView.findViewById(R.id.userSearch_recyclerView);
        userSearchRecyclerView.setAdapter(new UserAdapter(searchUserViewModel));

        movieSearchRecyclerView = searchView.findViewById(R.id.movieSearch_recyclerView);
        MediaAdapter movieSearchAdapter = new MediaAdapter(getActivity(), COLLECTION_NAME);
        searchMediaViewModel.getSearchMoviesLiveData().observe(getViewLifecycleOwner(), movieSearchAdapter::update);
        movieSearchRecyclerView.setAdapter(movieSearchAdapter);

        movieTrendingRecyclerView = searchView.findViewById(R.id.movieTrending_recyclerView);
        MediaAdapter movieTrendingAdapter = new MediaAdapter(getActivity(), COLLECTION_NAME);
        searchMediaViewModel.getTrendingMoviesLiveData().observe(getViewLifecycleOwner(), movieTrendingAdapter::update);
        movieTrendingRecyclerView.setAdapter(movieTrendingAdapter);

        bookSearchRecyclerView = searchView.findViewById(R.id.bookSearch_recyclerView);
        MediaAdapter bookSearchAdapter = new MediaAdapter(getActivity(), COLLECTION_NAME);
        searchMediaViewModel.getSearchBooksLiveData().observe(getViewLifecycleOwner(), bookSearchAdapter::update);
        bookSearchRecyclerView.setAdapter(bookSearchAdapter);

        bookTrendingRecyclerView= searchView.findViewById(R.id.bookTrending_recyclerView);
        MediaAdapter bookTrendingAdapter = new MediaAdapter(getActivity(), COLLECTION_NAME);
        searchMediaViewModel.getTrendingBooksLiveData().observeForever(books -> {
            bookTrendingAdapter.update(books);
            books.forEach(x -> System.out.println(x.getTitle()));
        });
        bookTrendingRecyclerView.setAdapter(bookTrendingAdapter);

        bookTrendingRecyclerView.setVisibility(View.VISIBLE);

        // get the search view and bind it to a listener
        SearchView searchView1 = searchView.findViewById(R.id.searchbar);
        searchView1.setOnQueryTextListener(this);

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
        }

        this.currentHighlightedButton.setTypeface(null, Typeface.NORMAL);
        this.currentHighlightedButton.setPaintFlags(this.currentHighlightedButton.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        this.currentHighlightedButton = (Button) view;
        this.currentHighlightedButton.setTypeface(null, Typeface.BOLD);
        this.currentHighlightedButton.setPaintFlags(this.currentHighlightedButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    private void searchAndDisplayResult(String toBeSearched) {
        if (this.currentCategory == SearchCategory.PEOPLE) {
            userSearchRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
            userSearchRecyclerView.setAdapter(new UserAdapter(searchUserViewModel));
            searchUser(toBeSearched);
        } else {
           searchMedia(toBeSearched);
        }
    }

    private void searchMedia(String toBeSearched){
        switch (this.currentCategory) {
            case MOVIES:
                if (!toBeSearched.isEmpty()) {

                } else {

                }
                break;
            case BOOKS:
                if (!toBeSearched.isEmpty()) {

                } else {

                }
                break;
        }
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
        userList.sort(Comparator.comparing(u -> u.getUsername().toLowerCase()));
    }

    private enum SearchCategory {
        PEOPLE,
        MOVIES,
        BOOKS
    }
}