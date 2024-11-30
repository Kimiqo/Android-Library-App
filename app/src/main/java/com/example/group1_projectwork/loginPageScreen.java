package com.example.group1_projectwork;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class loginPageScreen extends AppCompatActivity {

    private SQLiteHelper dbHelper;
    private EditText userNameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.login_page);

        dbHelper = new SQLiteHelper(this); // Initialize SQLiteHelper

        userNameEditText = findViewById(R.id.editTextLoginUsername);
        passwordEditText = findViewById(R.id.editTextLoginPassword);

        AppCompatButton loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            String userName = userNameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (userName.isEmpty() || password.isEmpty()) {
                Toast.makeText(loginPageScreen.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Authenticate with SQLite
            if (authenticateUser(userName, password)) {
                // If login is successful, navigate to the main screen
                Intent intent = new Intent(loginPageScreen.this, MainScreen.class);
                startActivity(intent);
                finish();
            } else {
                // If login fails, display a message to the user
                Toast.makeText(loginPageScreen.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
            }
        });

        // Link to sign-up page
        TextView signUpLink = findViewById(R.id.singInLink);
        signUpLink.setOnClickListener(v -> {
            Intent intent = new Intent(loginPageScreen.this, signUpPageScreen.class);
            startActivity(intent);
        });
    }

    private boolean authenticateUser(String username, String password) {
        // Check if the user exists in the SQLite database
        return dbHelper.checkUserCredentials(username, password, this);
    }

    public void signUpPage(View view) {
        Intent intent = new Intent(this, signUpPageScreen.class);
        startActivity(intent);
    }
}
