package com.example.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
public class DatabaseViewActivity extends AppCompatActivity {
    private TextView textViewData;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_view);

        textViewData = findViewById(R.id.textViewD);
        dbHelper = new DatabaseHelper(this);

        displayDatabaseData();
    }

    private void displayDatabaseData() {
        List<String> dataList = getAllData();

        StringBuilder stringBuilder = new StringBuilder();
        for (String data : dataList) {
            stringBuilder.append(data).append("\n");
        }

        textViewData.setText(stringBuilder.toString());
    }

    private List<String> getAllData() {
        List<String> dataList = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DatabaseHelper.COLUMN_NUMBER};
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_REGISTER,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NUMBER));
            dataList.add(phoneNumber);
        }

        cursor.close();
        db.close();

        return dataList;
    }

}
