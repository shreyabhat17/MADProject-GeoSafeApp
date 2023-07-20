package com.example.myapplication;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private TextView tvWelcome;

    EditText number;
    Button bt1;
    DatabaseHelper dbHelper;
    ListView listView;
    ArrayAdapter<String> adapter;
    List<String> numberList;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId==R.id.action_about){
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);

        }

         else if (itemId == R.id.action_sign_out) {

            SharedPreferences sharedPref = getSharedPreferences("loginPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("username");
            editor.apply();

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Redirect to the login activity
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the activity stack
            startActivity(intent);
            finish();

            return true;
        }



        return super.onOptionsItemSelected(item);
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        tvWelcome = findViewById(R.id.tvWelcome);
        number = findViewById(R.id.t1);
        bt1 = findViewById(R.id.b1);
        listView = findViewById(R.id.listView);

        dbHelper = new DatabaseHelper(HomeActivity.this);


        SharedPreferences sharedPref = getSharedPreferences("loginPref", MODE_PRIVATE);
        String username = sharedPref.getString("username", "");
        tvWelcome.setText("                 Welcome                " +
                "\n "+username + "!");
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numberString = number.getText().toString();
                if (numberString.length() == 10) {
                    addNumberInDatabase(numberString);
                    Toast.makeText(HomeActivity.this, "Number stored successfully!", Toast.LENGTH_SHORT).show();

                    number.setText("");
                    displayRegisteredNumbers();
                } else {
                    Toast.makeText(HomeActivity.this, "Enter a valid number!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        displayRegisteredNumbers();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String phoneNumber = numberList.get(position);
                sendLocationAsSMS(phoneNumber);
                Intent intent = new Intent(HomeActivity.this, MyLocation.class);
                intent.putExtra("ENUM", phoneNumber);
                startActivity(intent);
                finish();
            }
        });
    }

    private void addNumberInDatabase(String numberString) {
        SharedPreferences sharedPref = getSharedPreferences("loginPref", MODE_PRIVATE);
        String username = sharedPref.getString("username", "");

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_USERNAME, username);
        values.put(DatabaseHelper.COLUMN_NUMBER, numberString);

        db.insert(DatabaseHelper.TABLE_REGISTER, null, values);
        db.close();
    }


    private void sendLocationAsSMS(String phoneNumber) {

    }

    private void displayRegisteredNumbers() {
        numberList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        SharedPreferences sharedPref = getSharedPreferences("loginPref", MODE_PRIVATE);
        String username = sharedPref.getString("username", "");

        String[] projection = {DatabaseHelper.COLUMN_NUMBER};
        String selection = DatabaseHelper.KEY_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_REGISTER,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NUMBER));
            numberList.add(phoneNumber);
        }

        cursor.close();
        db.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, numberList);
        listView.setAdapter(adapter);
    }

}

