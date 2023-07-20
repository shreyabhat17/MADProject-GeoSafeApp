package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if the user is already logged in
        if (isLoggedIn()) {
            startHomeActivity();
            return; // Skip the rest of the code in onCreate
        }
        // Proceed with the login screen
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etusername);
        etPassword = findViewById(R.id.etpassword);
        btnLogin = findViewById(R.id.btLogin);

        etPassword.setTransformationMethod(new PasswordTransformationMethod());

        databaseHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (validateInput(username, password)) {
                    if (login(username, password)) {
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        saveLoggedInUser(username); // Save the logged-in user
                        startHomeActivity(); // Start the HomeActivity
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private boolean validateInput(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean login(String username, String password) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String[] columns = {DatabaseHelper.KEY_USERNAME};

        String selection = DatabaseHelper.KEY_USERNAME + " = ? AND " + DatabaseHelper.KEY_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        boolean loggedIn = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return loggedIn;
    }

    private void saveLoggedInUser(String username) {
        SharedPreferences sharedPref = getSharedPreferences("loginPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", username);
        editor.apply();
    }
    // Method to check if the user is logged in
    private boolean isLoggedIn() {
        SharedPreferences sharedPref = getSharedPreferences("loginPref", MODE_PRIVATE);
        return sharedPref.contains("username"); // Check if the "username" key exists
    }


    //Method to start the HomeActivity
    private void startHomeActivity() {
        SharedPreferences sharedPref = getSharedPreferences("loginPref", MODE_PRIVATE);
        //String username = sharedPref.getString("username", "");

        Intent intent = new Intent(this, HomeActivity.class);
        //intent.putExtra("username", username);
        startActivity(intent);
        finish(); // Optional: Finish the LoginActivity so the user can't navigate back to it
    }



}
