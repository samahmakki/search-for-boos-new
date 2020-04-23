package com.samahmakki.seacrhforbooksandsave.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import com.samahmakki.seacrhforbooksandsave.data.BookContract.BookEntry;

import java.io.ByteArrayOutputStream;

public class BookDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "book.db";

    private static final int DATABASE_VERSION = 3;


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
                + BookEntry.COLUMN_BOOK_LINK + " TEXT);";

        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + BookEntry.TABLE_NAME);
        onCreate(db);
    }

    //insert data into the Books Table

    final public long insertBook (String bookName, String authorName, Bitmap image,
                                  String date, String link) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(BookEntry.COLUMN_Book_NAME, bookName);
        values.put(BookEntry.COLUMN_AUTHOR_NAME, authorName);
        values.put(BookEntry.COLUMN_BOOK_IMAGE, byteArray);
        values.put(BookEntry.COLUMN_PUBLISHED_DATE, date);
        values.put(BookEntry.COLUMN_BOOK_LINK, link);

        long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);

        db.close();
        return newRowId;
    }

    final public void deleteBook (String name){

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + BookEntry.TABLE_NAME + " WHERE " +
                BookEntry.COLUMN_Book_NAME + " = '" + name + "'";
        db.execSQL(query);
    }
}
