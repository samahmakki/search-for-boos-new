package com.samahmakki.seacrhforbooksandsave.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class AuthorListDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "authorList.db";

    private static final int DATABASE_VERSION = 1;


    public AuthorListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE = "CREATE TABLE " + BookContract.BookEntry.TABLE_NAME + " ("
                + BookContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookContract.BookEntry.COLUMN_Book_NAME + " TEXT, "
                + BookContract.BookEntry.COLUMN_BOOK_IMAGE + " BLOB, "
                + BookContract.BookEntry.COLUMN_PUBLISHED_DATE + " TEXT, "
                + BookContract.BookEntry.COLUMN_BOOK_LINK + " TEXT);";

        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + BookContract.BookEntry.TABLE_NAME);
        onCreate(db);
    }

    //insert data into the Books Table

    final public long insertBook(String bookName, Bitmap image,
                                 String date, String link) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        long newRowId = 0;

        if (image != null) {
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(BookContract.BookEntry.COLUMN_Book_NAME, bookName);
            values.put(BookContract.BookEntry.COLUMN_BOOK_IMAGE, byteArray);
            values.put(BookContract.BookEntry.COLUMN_PUBLISHED_DATE, date);
            values.put(BookContract.BookEntry.COLUMN_BOOK_LINK, link);

            newRowId = db.insert(BookContract.BookEntry.TABLE_NAME, null, values);

            db.close();
        }

        if (image == null) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(BookContract.BookEntry.COLUMN_Book_NAME, bookName);
            values.put(BookContract.BookEntry.COLUMN_PUBLISHED_DATE, date);
            values.put(BookContract.BookEntry.COLUMN_BOOK_LINK, link);

            newRowId = db.insert(BookContract.BookEntry.TABLE_NAME, null, values);

            db.close();
        }

        return newRowId;
    }

    final public void deleteBook(String name) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + BookContract.BookEntry.TABLE_NAME + " WHERE " +
                BookContract.BookEntry.COLUMN_Book_NAME + " = '" + name + "'";
        db.execSQL(query);
    }
}
