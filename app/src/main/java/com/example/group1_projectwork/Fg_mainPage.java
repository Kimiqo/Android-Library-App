package com.example.group1_projectwork;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class Fg_mainPage extends Fragment {

    private GridView bookGridView;
    private BookGridAdapter bookGridAdapter;
    private SQLiteHelper dbHelper;
    private SharedViewModel sharedViewModel;
    private List<Book> books; // Initialize the books list

    public Fg_mainPage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_main_page, container, false);

        dbHelper = new SQLiteHelper(requireContext());
        books = new ArrayList<>(); // Initialize the books list

        bookGridView = view.findViewById(R.id.bookGridView);

        // Initialize the SharedViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observe the book list from the ViewModel
        sharedViewModel.getBooksLiveData().observe(getViewLifecycleOwner(), books -> {
            if (bookGridAdapter == null) {
                bookGridAdapter = new BookGridAdapter(requireContext(), books);
                bookGridView.setAdapter(bookGridAdapter);
            } else {
                bookGridAdapter.updateBooks(books);
            }
        });

        // FAB to add a new book
        FloatingActionButton fab = view.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> {
            AddBookDialogFragment dialog = new AddBookDialogFragment();
            dialog.show(getParentFragmentManager(), "AddBookDialogFragment");
        });

        return view;
    }


    public void loadBooksFromSQLite() {
        books.clear(); // Clear the current list
        if (dbHelper != null) {
            List<Book> fetchedBooks = dbHelper.getAllBooks(requireContext()); // Fetch books from SQLite
            Log.d("Fg_mainPage", "Fetched books: " + fetchedBooks.size());
            books.addAll(fetchedBooks); // Add fetched books to the list
        } else {
            Log.e("Fg_mainPage", "SQLiteHelper is not initialized.");
        }

        if (bookGridAdapter == null) {
            bookGridAdapter = new BookGridAdapter(requireContext(), books);
            bookGridView.setAdapter(bookGridAdapter);
        } else {
            bookGridAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBooksFromSQLite(); // Reload books when the fragment is resumed
    }

    public void reloadBooks() {
        if (dbHelper != null) {
            List<Book> fetchedBooks = dbHelper.getAllBooks(requireContext());
            bookGridAdapter.updateBooks(fetchedBooks);
        } else {
            Log.e("Fg_mainPage", "SQLiteHelper is not initialized.");
        }
    }
}
