package com.example.group1_projectwork;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app_library.db";
    private static final int DATABASE_VERSION = 1;
    private static final String USER_SESSION_PREF = "UserSession";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the users table
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "password TEXT)");

        // Create the books table with a foreign key reference to users
        db.execSQL("CREATE TABLE books (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "author TEXT, " +
                "pdfUri TEXT, " +
                "userId INTEGER, " +
                "FOREIGN KEY(userId) REFERENCES users(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS books");
        onCreate(db);
    }

    // Add a new user to the database
    public boolean addUser(User user, Context context) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("username", user.getUsername());
            values.put("password", user.getPassword());

            long result = db.insert("users", null, values);

            if (result != -1) {
                user.setUserID(String.valueOf(result));  // Set the generated userID in the User object

                // Save the logged-in user's ID in SharedPreferences
                SharedPreferences prefs = context.getSharedPreferences(USER_SESSION_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("loggedInUserID", String.valueOf(result));
                editor.apply();

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

    // Add a new book linked to the currently logged-in user
    public boolean addBook(Book book, Context context) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Retrieve the logged-in user's ID from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(USER_SESSION_PREF, Context.MODE_PRIVATE);
        String loggedInUserID = prefs.getString("loggedInUserID", null);

        if (loggedInUserID == null) {
            Log.e("SQLiteHelper", "No logged-in user found");
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("title", book.getTitle());
        values.put("author", book.getAuthor());
        values.put("pdfUri", book.getPdfUri());
        values.put("userId", loggedInUserID); // Link to the logged-in user

        long result = db.insert("books", null, values);
        return result != -1;
    }

    // Get all books added by the currently logged-in user
    public List<Book> getAllBooks(Context context) {
        List<Book> books = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Retrieve the logged-in user's ID from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(USER_SESSION_PREF, Context.MODE_PRIVATE);
        String loggedInUserID = prefs.getString("loggedInUserID", null);

        if (loggedInUserID == null) {
            Log.e("SQLiteHelper", "No logged-in user found");
            return books;
        }

        Cursor cursor = db.rawQuery("SELECT * FROM books WHERE userId = ?", new String[]{loggedInUserID});
        if (cursor.moveToFirst()) {
            do {
                books.add(new Book(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return books;
    }

    // Validate user credentials and log in the user
    public boolean checkUserCredentials(String username, String password, Context context) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?",
                new String[]{username, password});

        if (cursor.moveToFirst()) {
            // Save the logged-in user's ID in SharedPreferences
            String userID = cursor.getString(0);
            SharedPreferences prefs = context.getSharedPreferences(USER_SESSION_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("loggedInUserID", userID);
            editor.apply();

            cursor.close();
            return true;
        }

        cursor.close();
        return false;
    }

    // Check if there is any registered user in the database
    public boolean isUserRegistered() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users LIMIT 1", null);
        boolean userExists = cursor.getCount() > 0;
        cursor.close();
        return userExists;
    }

    // Delete a book by its ID
    public boolean deleteBook(String bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete("books", "id = ?", new String[]{bookId});
        db.close();
        return rowsDeleted > 0; // Return true if at least one row was deleted
    }

    // Get the username of the logged-in user
    public String getLoggedInUsername(Context context) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Retrieve the logged-in user's ID from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(USER_SESSION_PREF, Context.MODE_PRIVATE);
        String loggedInUserID = prefs.getString("loggedInUserID", null);

        if (loggedInUserID == null) {
            Log.e("SQLiteHelper", "No logged-in user found");
            return null;
        }

        // Query the database to get the username
        Cursor cursor = db.rawQuery("SELECT username FROM users WHERE id = ?", new String[]{loggedInUserID});
        String username = null;
        if (cursor.moveToFirst()) {
            username = cursor.getString(0); // Retrieve the username
        }
        cursor.close();
        return username;
    }

    public Cursor searchBooks(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String searchQuery = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
        String wildcardQuery = "%" + query + "%";
        return db.rawQuery(searchQuery, new String[]{wildcardQuery, wildcardQuery});
    }



}
