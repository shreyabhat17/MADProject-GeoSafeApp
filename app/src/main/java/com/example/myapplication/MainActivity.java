package com.example.myapplication;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button btnLogin, btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if the user is already logged in
        if (isLoggedIn()) {
            startHomeActivity();
            return; // Skip the rest of the code in onCreate
        }
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, VerificationActivity.class));
            }
        });
    }
    // Method to check if the user is logged in
    private boolean isLoggedIn() {
        SharedPreferences sharedPref = getSharedPreferences("loginPref", MODE_PRIVATE);
        return sharedPref.contains("username"); // Check if the "username" key exists
    }

    // Method to start the HomeActivity
    private void startHomeActivity() {
        SharedPreferences sharedPref = getSharedPreferences("loginPref", MODE_PRIVATE);
        String username = sharedPref.getString("username", "");

        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish(); // Optional: Finish the MainActivity so the user can't navigate back to it
    }
}
