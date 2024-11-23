package com.example.group1_projectwork;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.splash_screen);

        dbHelper = new SQLiteHelper(this);

        // Check if the user is registered in SQLite database
        boolean isUserRegistered = dbHelper.isUserRegistered();
        if (isUserRegistered) {
            // User exists in the database, proceed to MainScreen
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, MainScreen.class);
                startActivity(intent);
                finish();
            }, 2000);
        } else {
            // No user in the database, proceed to Sign-Up screen
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, signUpPageScreen.class);
                startActivity(intent);
                finish();
            }, 2000);
        }
    }
}
