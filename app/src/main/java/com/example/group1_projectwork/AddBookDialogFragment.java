package com.example.group1_projectwork;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.UUID;

public class AddBookDialogFragment extends DialogFragment {

    private OnDismissListener onDismissListener;

    // Call this method to set the listener from Fg_mainPage
    public void setOnDismissListener(OnDismissListener listener) {
        this.onDismissListener = listener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    public interface OnDismissListener {
        void onDismiss();
    }

    // Define the listener interface
    public interface OnBookAddedListener {
        void onBookAdded(Book book);  // This will be called when a book is added
    }

    private OnBookAddedListener listener;  // Declare the listener variable

    private EditText editTextTitle, editTextAuthor;
    private Uri pdfUri;
    private SQLiteHelper dbHelper;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Make sure the activity implements the listener interface
        if (context instanceof OnBookAddedListener) {
            listener = (OnBookAddedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnBookAddedListener");
        }
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_book, null);

        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextAuthor = view.findViewById(R.id.editTextAuthor);

        dbHelper = new SQLiteHelper(requireContext());

        Button btnSelectPdf = view.findViewById(R.id.btnSelectPdf);
        Button btnAddBook = view.findViewById(R.id.btnAddBook);

        btnSelectPdf.setOnClickListener(v -> selectPdf());
        btnAddBook.setOnClickListener(v -> addBook());

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton("Cancel", (dialog, which) -> dismiss())
                .create();
    }

    private void selectPdf() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, 1001);  // Request code 1001 for PDF selection
    }

    private void addBook() {
        String title = editTextTitle.getText().toString().trim();
        String author = editTextAuthor.getText().toString().trim();

        if (title.isEmpty() || author.isEmpty() || pdfUri == null) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Book book = new Book(UUID.randomUUID().toString(), title, author, pdfUri.toString(), "1"); // Mock userId
        boolean success = dbHelper.addBook(book);
        if (success) {
            // Update the ViewModel with the new book
            SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
            viewModel.addBook(book); // Notify ViewModel about the new book

            dismiss();
            Toast.makeText(getContext(), "Book added successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to add book", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle the result of the PDF selection
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == Activity.RESULT_OK && data != null) {
            pdfUri = data.getData();  // Get the URI of the selected PDF
            Toast.makeText(getContext(), "PDF selected: " + pdfUri, Toast.LENGTH_SHORT).show();
        }
    }
}
