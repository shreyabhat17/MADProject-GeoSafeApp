package com.example.myapplication;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.DatabaseHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    private EditText etUsername, etPassword , etConfirmPassword;
    private Button btnSignup;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsername = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btSignup);
        etConfirmPassword= findViewById(R.id.etConfirmPassword);

        etPassword.setTransformationMethod(new PasswordTransformationMethod());
        etConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());


        databaseHelper = new DatabaseHelper(this);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (validateInput(username, password)) {
                    if (signup(username, password)) {
                        Toast.makeText(SignupActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(SignupActivity.this, "Signup failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean validateInput(String username, String password) {
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please enter all required fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isStrongPassword(password)) {
            Toast.makeText(this, "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private boolean isStrongPassword(String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private boolean signup(String username, String password) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_USERNAME, username);
        values.put(DatabaseHelper.KEY_PASSWORD, password);

        long result = db.insert(DatabaseHelper.TABLE_USERS, null, values);
        db.close();

        return result != -1;
    }
}
