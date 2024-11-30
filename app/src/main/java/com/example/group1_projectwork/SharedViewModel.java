package com.example.group1_projectwork;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<Book>> booksLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> usernameLiveData = new MutableLiveData<>();  // For username
    private final MutableLiveData<Uri> profileImageUriLiveData = new MutableLiveData<>();  // For profile image


    public LiveData<List<Book>> getBooksLiveData() {
        return booksLiveData;
    }

    public void setBooks(List<Book> books) {
        booksLiveData.setValue(books);
    }

    public void addBook(Book book) {
        if (booksLiveData.getValue() != null) {
            List<Book> currentBooks = new ArrayList<>(booksLiveData.getValue());
            currentBooks.add(book);
            booksLiveData.setValue(currentBooks);
        }
    }

    // Username related methods
    public LiveData<String> getUsernameLiveData() {
        return usernameLiveData;
    }

    public void setUsername(String username) {
        usernameLiveData.setValue(username);
    }

    // Profile Image related methods
    public LiveData<Uri> getProfileImageUriLiveData() {
        return profileImageUriLiveData;
    }

    public void setProfileImageUri(Uri uri) {
        profileImageUriLiveData.setValue(uri);
    }
}
