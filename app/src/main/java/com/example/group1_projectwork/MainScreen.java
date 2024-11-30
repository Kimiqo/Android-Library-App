package com.example.group1_projectwork;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import java.util.List;

public class MainScreen extends AppCompatActivity implements AddBookDialogFragment.OnBookAddedListener {

    private ImageView homeIcon, searchIcon, settingsIcon, headerImage;
    private TextView headerTitle;
    private Fragment homeFragment, searchFragment, settingsFragment;
    private SharedViewModel viewModel;
    private SQLiteHelper dbHelper; // SQLite Helper instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_main);

        // Load books into SharedViewModel
        dbHelper = new SQLiteHelper(this);
        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        viewModel.setBooks(dbHelper.getAllBooks(this));

        // Initialize views first
        headerImage = findViewById(R.id.profileIcon);
        headerTitle = findViewById(R.id.headerText);
        homeIcon = findViewById(R.id.homeIcon);
        searchIcon = findViewById(R.id.searchIcon);
        settingsIcon = findViewById(R.id.settingIcon);

        // Initialize SQLite helper
        dbHelper = new SQLiteHelper(this);

        // Initialize fragments
        homeFragment = new Fg_mainPage();
        searchFragment = new Fg_searchPage();
        settingsFragment = new Fg_settingsPage();

        // Load the home fragment by default
        if (savedInstanceState == null) {
            loadFragment(homeFragment);
        }

        // Setup navigation
        setupNavigation();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (!fragmentManager.getFragments().contains(fragment)) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null) // Optional, if you want back navigation
                    .commit();
        }
    }

    @Override
    public void onBookAdded(Book book) {
        if (book.getTitle().isEmpty() || book.getAuthor().isEmpty() || book.getPdfUri() == null) {
            Toast.makeText(this, "Please provide valid book details", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dbHelper.addBook(book, this); // Save to SQLite
        if (success) {
            Toast.makeText(this, "Book added successfully!", Toast.LENGTH_SHORT).show();
            ((Fg_mainPage) homeFragment).reloadBooks(); // Refresh books
        } else {
            Toast.makeText(this, "Failed to add book", Toast.LENGTH_SHORT).show();
        }
    }

    // SharedPreferences setup for storing and retrieving the header image
    public void changeProfileImage(Uri newImageUri) {
        // Save URI to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("header_image_uri", newImageUri.toString()); // Save URI as a String
        editor.apply();

        // Update the image view
        headerImage.setImageURI(newImageUri);
    }


    private void setupNavigation() {
        homeIcon.setOnClickListener(v -> {
            loadFragment(homeFragment);
            headerTitle.setText("Home");
        });

        searchIcon.setOnClickListener(v -> {
            loadFragment(searchFragment);
            headerTitle.setText("Search");
        });

        settingsIcon.setOnClickListener(v -> {
            loadFragment(settingsFragment);
            headerTitle.setText("Settings");
        });
    }

}
