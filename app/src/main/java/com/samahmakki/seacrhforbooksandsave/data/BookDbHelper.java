package com.samahmakki.seacrhforbooksandsave.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import com.samahmakki.seacrhforbooksandsave.data.BookContract.BookEntry;

import java.io.ByteArrayOutputStream;

public class BookDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "book.db";

    private static final int DATABASE_VERSION = 9;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + BookEntry.TABLE_NAME + " ("
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_Book_NAME + " TEXT, "
                + BookEntry.COLUMN_AUTHOR_NAME + " TEXT, "
                + BookEntry.COLUMN_BOOK_IMAGE + " BLOB, "
                + BookEntry.COLUMN_PUBLISHED_DATE + " TEXT, "
                + BookEntry.COLUMN_BOOK_DESCRIPTION + " TEXT, "
                + BookEntry.COLUMN_BOOK_SALEABILITY + " TEXT, "
                + BookEntry.COLUMN_BOOK_BUY_LINK + " TEXT, "
                + BookEntry.COLUMN_BOOK_WEB_READER_LINK + " TEXT, "
                + BookEntry.COLUMN_BOOK_LINK + " TEXT, "
                + BookEntry.COLUMN_BOOK_DOWNLOAD_LINK + " TEXT);";
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + BookEntry.TABLE_NAME);
        onCreate(db);
    }
}
