package com.samahmakki.seacrhforbooksandsave.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.samahmakki.seacrhforbooksandsave.data.BookContract.AuthorName;


public class AuthorNameDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "author_name.db";

    private static final int DATABASE_VERSION = 2;

    public AuthorNameDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + AuthorName.TABLE_NAME + " ("
                + AuthorName._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AuthorName.COLUMN_AUTHOR_NAME_2 + " TEXT);";

        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + AuthorName.TABLE_NAME);
        onCreate(db);
    }
}
