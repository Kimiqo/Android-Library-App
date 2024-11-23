package com.example.group1_projectwork;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<Book>> booksLiveData = new MutableLiveData<>(new ArrayList<>());

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
}
