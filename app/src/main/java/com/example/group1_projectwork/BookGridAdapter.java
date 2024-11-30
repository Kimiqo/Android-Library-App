package com.example.group1_projectwork;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class BookGridAdapter extends BaseAdapter {
    private Context context;
    private List<Book> books;
    private SQLiteHelper dbHelper;

    public BookGridAdapter(Context context, List<Book> books) {
        this.context = context;
        this.books = books;
        this.dbHelper = new SQLiteHelper(context);
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int position) {
        return books.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.book_item, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = convertView.findViewById(R.id.bookTitle);
            holder.authorTextView = convertView.findViewById(R.id.bookAuthor);
            holder.deleteButton = convertView.findViewById(R.id.deleteButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Get the book for the current position
        Book book = books.get(position);

        // Set the book details to the TextViews
        holder.titleTextView.setText(book.getTitle());
        holder.authorTextView.setText(book.getAuthor());

        // Set up the delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            boolean isDeleted = dbHelper.deleteBook(book.getId());
            if (isDeleted) {
                Toast.makeText(context, "Book deleted successfully", Toast.LENGTH_SHORT).show();
                books.remove(position); // Remove the book from the list
                notifyDataSetChanged(); // Refresh the GridView
            } else {
                Toast.makeText(context, "Failed to delete book", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    public void updateBooks(List<Book> newBooks) {
        books.clear();
        books.addAll(newBooks);
        notifyDataSetChanged();
    }

    // ViewHolder pattern for better performance
    private static class ViewHolder {
        TextView titleTextView;
        TextView authorTextView;
        ImageView deleteButton;
    }
}
