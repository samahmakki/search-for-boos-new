package com.samahmakki.seacrhforbooksandsave.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import com.samahmakki.seacrhforbooksandsave.data.BookContract.AuthorEntry;

import java.io.ByteArrayOutputStream;

public class BookDbHelper_2 extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "author.db";

    private static final int DATABASE_VERSION = 9;

    public BookDbHelper_2(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + AuthorEntry.TABLE_NAME + " ("
                + AuthorEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AuthorEntry.COLUMN_Book_NAME + " TEXT, "
                + AuthorEntry.COLUMN_AUTHOR_NAME + " TEXT, "
                + AuthorEntry.COLUMN_BOOK_IMAGE + " BLOB, "
                + AuthorEntry.COLUMN_PUBLISHED_DATE + " TEXT, "
                + AuthorEntry.COLUMN_BOOK_DESCRIPTION + " TEXT, "
                + AuthorEntry.COLUMN_BOOK_SALEABILITY + " TEXT, "
                + AuthorEntry.COLUMN_BOOK_BUY_LINK + " TEXT, "
                + AuthorEntry.COLUMN_BOOK_WEB_READER_LINK + " TEXT, "
                + AuthorEntry.COLUMN_BOOK_LINK + " TEXT);";

        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + AuthorEntry.TABLE_NAME);
        onCreate(db);
    }
}
