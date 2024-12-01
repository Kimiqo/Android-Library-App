package com.example.group1_projectwork;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Fg_searchPage extends Fragment {

    private SQLiteHelper dbHelper;
    private BookGridAdapter bookGridAdapter;
    private GridView bookGridView;
    private SearchView searchView;
    private List<Book> books;

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
        books = dbHelper.getAllBooks(requireContext()); // Load all books

        // Set up the adapter
        bookGridAdapter = new BookGridAdapter(requireContext(), new ArrayList<>(books)); // Initially show all books
        bookGridView.setAdapter(bookGridAdapter);

        // Handle book click to open PDF
        bookGridView.setOnItemClickListener((parent, view1, position, id) -> {
            Book clickedBook = books.get(position);
            String pdfUri = clickedBook.getPdfUri(); // Get the existing URI from the database

            // Open the PDF using the existing URI
            openPDF(pdfUri);
        });

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
            for (Book book : books) {
                if (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        book.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                    filteredBooks.add(book);
                }
            }
        } else {
            filteredBooks.addAll(books); // Show all books if the query is empty
        }

        // Update the adapter with the filtered list
        bookGridAdapter.updateBooks(filteredBooks);
    }

    public void openPDF(String pdfUri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(pdfUri), "application/pdf");
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY & Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setPackage("com.gappstudios.autowifi3gdataswitch.san.basicpdfviewer"); // Drive PDF Viewer

            startActivity(intent);
        } catch (Exception e) {
            // Prompt user to install a PDF Viewer
            Toast.makeText(requireContext(), "Please install a PDF viewer to open this file.", Toast.LENGTH_SHORT).show();
        }
    }
}
