package com.example.group1_projectwork;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class signUpPageScreen extends AppCompatActivity {

    private EditText userNameEditText, passwordEditText;
    private SQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        dbHelper = new SQLiteHelper(this);

        userNameEditText = findViewById(R.id.editTextSignUsername);
        passwordEditText = findViewById(R.id.editTextSignPassword);

        AppCompatButton signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String userName = userNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (userName.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a User object with userName and password
        User newUser = new User(userName, password);

        // Try to add the user to the database
        boolean success = dbHelper.addUser(newUser, this);

        if (success) {
            Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainScreen.class));
            finish();
        } else {
            Toast.makeText(this, "User registration failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void loginPage(View view) {
        Intent intent = new Intent(this, loginPageScreen.class);
        startActivity(intent);
    }


}
