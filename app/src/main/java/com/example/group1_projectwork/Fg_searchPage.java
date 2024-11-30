package com.example.group1_projectwork;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class Fg_searchPage extends Fragment {

    private SQLiteHelper dbHelper;
    private BookGridAdapter bookGridAdapter;
    private GridView bookGridView;
    private SearchView searchView;
    private List<Book> allBooks;

    public Fg_searchPage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_search_page, container, false);

        // Initialize UI components
        searchView = view.findViewById(R.id.searchView);
        bookGridView = view.findViewById(R.id.bookGridView);

        // Initialize database and fetch all books
        dbHelper = new SQLiteHelper(requireContext());
        allBooks = dbHelper.getAllBooks(requireContext()); // Load all books

        // Set up the adapter
        bookGridAdapter = new BookGridAdapter(requireContext(), new ArrayList<>(allBooks)); // Initially show all books
        bookGridView.setAdapter(bookGridAdapter);

        // Add listener for real-time filtering
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterBooks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText);
                return true;
            }
        });

        return view;
    }

    /**
     * Filters books based on the search query and updates the GridView.
     */
    private void filterBooks(String query) {
        List<Book> filteredBooks = new ArrayList<>();

        if (!TextUtils.isEmpty(query)) {
            for (Book book : allBooks) {
                if (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        book.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                    filteredBooks.add(book);
                }
            }
        } else {
            filteredBooks.addAll(allBooks); // Show all books if the query is empty
        }

        // Update the adapter with the filtered list
        bookGridAdapter.updateBooks(filteredBooks);
    }
}
