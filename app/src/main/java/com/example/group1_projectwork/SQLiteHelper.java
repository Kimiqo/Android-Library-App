package com.example.group1_projectwork;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app_library.db";
    private static final int DATABASE_VERSION = 1;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table with userID, username, and password
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)");
        db.execSQL("CREATE TABLE books (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, author TEXT, pdfUri TEXT, userId INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS books");
        onCreate(db);
    }

    public boolean addUser(User user) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("username", user.getUsername());
            values.put("password", user.getPassword());

            long result = db.insert("users", null, values);

            if (result != -1) {
                user.setUserID(String.valueOf(result));  // Set the generated userID in the User object
                return true;
            } else {
                Log.e("SQLiteHelper", "Failed to insert user");
            }
        } catch (Exception e) {
            Log.e("SQLiteHelper", "Database error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (db != null) db.close();
        }
        return false;
    }



    // Add a book (keeping userId as foreign key)
    public boolean addBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", book.getTitle());
        values.put("author", book.getAuthor());
        values.put("pdfUri", book.getPdfUri());
        values.put("userId", book.getUserId()); // Assuming UserId is passed when adding a book
        long result = db.insert("books", null, values);
        return result != -1;
    }

    // Get all books from the database
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM books", null);
        if (cursor.moveToFirst()) {
            do {
                books.add(new Book(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4) // assuming userId is an integer
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return books;
    }

    // Check user credentials using username and password
    public boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?",
                new String[]{username, password});
        boolean userExists = cursor.moveToFirst();  // If user exists, moveToFirst will return true
        cursor.close();
        return userExists;
    }

    // Method to check if a user exists in the database
    public boolean isUserRegistered() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users LIMIT 1", null);

        // If there is at least one user, it will return true
        boolean userExists = cursor.getCount() > 0;
        cursor.close();
        return userExists;
    }

    public User getUserByUserID(String userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"id", "username", "password"},
                "id=?", new String[]{userID}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id");
            int usernameIndex = cursor.getColumnIndex("username");
            int passwordIndex = cursor.getColumnIndex("password");

            // Check if the column indices are valid
            if (idIndex != -1 && usernameIndex != -1 && passwordIndex != -1) {
                String id = cursor.getString(idIndex);
                String username = cursor.getString(usernameIndex);
                String password = cursor.getString(passwordIndex);
                cursor.close();
                return new User(id, username, password);
            } else {
                cursor.close();
                return null; // Return null if any column index is invalid
            }
        }
        cursor.close();
        return null; // Return null if no user found
    }

}
