package com.example.group1_projectwork;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class BookGridAdapter extends BaseAdapter {
    private Context context;
    private List<Book> books;
    private SQLiteHelper dbHelper;

    public BookGridAdapter(Context context, List<Book> books) {
        this.context = context;
        this.books = books;
        dbHelper = new SQLiteHelper(context);
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.book_item, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.bookTitle);
        TextView authorTextView = convertView.findViewById(R.id.bookAuthor);

        Book book = books.get(position);
        titleTextView.setText(book.getTitle());
        authorTextView.setText(book.getAuthor());

        return convertView;
    }

    public void updateBooks(List<Book> newBooks) {
        this.books.clear();
        this.books.addAll(newBooks);
        notifyDataSetChanged();
    }

}
