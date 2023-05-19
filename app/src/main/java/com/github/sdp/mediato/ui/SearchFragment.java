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
import androidx.recyclerview.widget.RecyclerView;
import com.github.sdp.mediato.MainActivity;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.cache.AppCache;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.MediaType;
import com.github.sdp.mediato.ui.viewmodel.SearchMediaViewModel;
import com.github.sdp.mediato.ui.viewmodel.SearchUserViewModel;
import com.github.sdp.mediato.utility.adapters.MediaAdapter;
import com.github.sdp.mediato.utility.adapters.UserAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SearchFragment extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener {

    private static String COLLECTION_NAME;
    private static String USERNAME;
    // Only one instance of the cache should be used
    public static AppCache OFFLINE_CACHE;
    private SearchUserViewModel searchUserViewModel;
    private SearchMediaViewModel searchMediaViewModel;

    private MediaAdapter mediaAdapter;

    private RecyclerView userSearchRecyclerView;
    private RecyclerView mediaRecyclerView;

    private SearchView searchBar;

    private Button peopleButton;
    private Button booksButton;
    private Button filmButton;
    private Button currentHighlightedButton;
    private SearchCategory displayedCategory;

    private final MutableLiveData<List<Media>> searchMediaResults = new MutableLiveData<>(new ArrayList<>());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        USERNAME = ((MainActivity)getActivity()).getMyProfileViewModel().getUsername();
        COLLECTION_NAME = (String) getArguments().get("collection");

        // Create and init the Search User ViewModel
        searchUserViewModel = new ViewModelProvider(this).get(SearchUserViewModel.class);
        searchUserViewModel.setUserName(USERNAME);
        searchUserViewModel.setMainActivity((MainActivity) getActivity());

        searchMediaViewModel = new ViewModelProvider(this).get(SearchMediaViewModel.class);
        searchMediaViewModel.setMediaDao(OFFLINE_CACHE.mediaDao());

        setMediaAdapter();

        String isGeneralSearch = (String) getArguments().get("general_search");
        if (isGeneralSearch != null && !isGeneralSearch.equals("true")) {
            this.searchMediaViewModel.setCurrentCategory(SearchCategory.MOVIES);
        } else {
            this.searchMediaViewModel.setCurrentCategory(SearchCategory.PEOPLE);
        }
    }

    private void setMediaAdapter() {
        mediaAdapter = new MediaAdapter(getActivity(), COLLECTION_NAME);
        searchMediaViewModel.getLiveData().observe(this, mediaAdapter::update);
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

        // Set the Search User RecyclerView with its adapter
        userSearchRecyclerView = searchView.findViewById(R.id.media_recyclerView);
        userSearchRecyclerView.setAdapter(new UserAdapter(searchUserViewModel));

        setMediaRecyclerView(searchView);

        // get the search view and bind it to a listener
        searchBar = searchView.findViewById(R.id.searchbar);
        searchBar.setOnQueryTextListener(this);
        searchBar.setQuery(this.searchMediaViewModel.getSearchQuery(), false);

        setDisplayComponent();

        return searchView;
    }

    private void setMediaRecyclerView(View searchView) {
        mediaRecyclerView = searchView.findViewById(R.id.media_recyclerView);
        mediaRecyclerView.setAdapter(mediaAdapter);
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
            if (this.searchMediaViewModel.getCurrentCategory() == SearchCategory.MOVIES) {
                this.searchMediaViewModel.loadFirstSearchPage(s, MediaType.MOVIE);
            } else {
                this.searchMediaViewModel.loadFirstSearchPage(s, MediaType.BOOK);
            }
        }
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (s.length() < 1) {
            this.searchMediaViewModel.setSearchQuery(s);
            switch (this.searchMediaViewModel.getCurrentCategory()){
                case PEOPLE:
                    this.searchUserViewModel.clearUserList();
                    break;
                case MOVIES:
                    this.searchMediaViewModel.loadFirstTrendingPage(MediaType.MOVIE);
                case BOOKS:
                    this.searchMediaViewModel.loadFirstTrendingPage(MediaType.BOOK);
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
            this.searchMediaViewModel.setCurrentCategory(SearchCategory.PEOPLE);
        } else if (view == booksButton) {
            this.searchMediaViewModel.setCurrentCategory(SearchCategory.BOOKS);
        } else if (view == filmButton) {
            this.searchMediaViewModel.setCurrentCategory(SearchCategory.MOVIES);
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
                setMediaComponents();
                this.mediaRecyclerView.setVisibility(View.VISIBLE);
                break;
            case MOVIES:
                this.currentHighlightedButton = filmButton;
                setMediaComponents();
                this.mediaRecyclerView.setVisibility(View.VISIBLE);
                break;
        }


        this.currentHighlightedButton.setTypeface(null, Typeface.BOLD);
        this.currentHighlightedButton.setPaintFlags(this.currentHighlightedButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    private void hideAllRecyclerView() {
        this.mediaRecyclerView.setVisibility(View.GONE);
        this.userSearchRecyclerView.setVisibility(View.GONE);
    }

    private void setMediaComponents() {
        if (this.searchBar.getQuery().length() > 0) {
            searchMedias();
        } else {
            getTrendingMedias();
        }
    }

    private void getTrendingMedias(){
        if (this.searchMediaViewModel.getCurrentCategory() == SearchCategory.MOVIES) {
            searchMediaViewModel.loadFirstTrendingPage(MediaType.MOVIE);
        } else {
            searchMediaViewModel.loadFirstTrendingPage(MediaType.BOOK);
        }
    }
    private void searchMedias(){
        if (this.searchMediaViewModel.getCurrentCategory() == SearchCategory.MOVIES) {
            searchMediaViewModel.loadFirstSearchPage(
                    this.searchBar.getQuery().toString(),
                    MediaType.MOVIE
            );
        } else {
            searchMediaViewModel.loadFirstSearchPage(
                    this.searchBar.getQuery().toString(),
                    MediaType.BOOK
            );
        }
    }

    private void searchUser(String toBeSearched) {
        getAllUser(USERNAME).thenAccept(users -> {
            List<User> filteredUser = users.stream()
                .filter(user -> user.getUsername().toLowerCase().startsWith(toBeSearched.toLowerCase()))
                .collect(Collectors.toList());
            sortUsersByName(filteredUser);
            searchUserViewModel.setUserList(filteredUser);
        });
    }

    private static void sortUsersByName(List<User> userList) {
        userList.sort(Comparator.comparing(u -> u.getUsername().toLowerCase()));
    }

    public enum SearchCategory {
        PEOPLE,
        MOVIES,
        BOOKS
    }
}