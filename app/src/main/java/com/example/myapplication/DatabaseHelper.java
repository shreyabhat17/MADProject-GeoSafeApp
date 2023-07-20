package com.example.myapplication;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "myapp.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_REGISTER = "register"; // New table for storing registered numbers
    public static final String TABLE_USERS = "users";

    public static final String KEY_ID = "id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    public static final String COLUMN_NUMBER = "number"; // Column for storing phone numbers
    public static final String COLUMN_USER_ID = "user_id"; // New column for user ID

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS +
            "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_USERNAME + " TEXT UNIQUE," +
            KEY_PASSWORD + " TEXT)";

    private static final String CREATE_TABLE_REGISTER = "CREATE TABLE " + TABLE_REGISTER +
            "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_NUMBER + " TEXT," +
            COLUMN_USER_ID + " INTEGER," +
            KEY_USERNAME + " TEXT," + // Add the username column definition
            "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + "))";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_REGISTER); // Create the register table

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGISTER); // Drop the register table
        onCreate(db);
    }

}
