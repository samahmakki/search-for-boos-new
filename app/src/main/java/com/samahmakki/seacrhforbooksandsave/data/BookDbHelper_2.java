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

    private static final int DATABASE_VERSION = 8;

    public static long newRowId;

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

    //insert data into the Books Table

    final public long insertBook (String bookName, String authorName, Bitmap image,
                                  String date, String link, String description,
                                  String saleability, String buyLink, String webReaderLink) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        newRowId = 0;

        if (image != null) {
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);

            byte[] byteArray = stream.toByteArray();

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(AuthorEntry.COLUMN_Book_NAME, bookName);
            values.put(AuthorEntry.COLUMN_AUTHOR_NAME, authorName);
            values.put(AuthorEntry.COLUMN_BOOK_IMAGE, byteArray);
            values.put(AuthorEntry.COLUMN_PUBLISHED_DATE, date);
            values.put(AuthorEntry.COLUMN_BOOK_LINK, link);
            values.put(AuthorEntry.COLUMN_BOOK_DESCRIPTION, description);
            values.put(AuthorEntry.COLUMN_BOOK_SALEABILITY, saleability);
            values.put(AuthorEntry.COLUMN_BOOK_BUY_LINK, buyLink);
            values.put(AuthorEntry.COLUMN_BOOK_WEB_READER_LINK, webReaderLink);

            newRowId = db.insert(AuthorEntry.TABLE_NAME, null, values);

            db.close();
        }

        if (image == null){
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(AuthorEntry.COLUMN_Book_NAME, bookName);
            values.put(AuthorEntry.COLUMN_AUTHOR_NAME, authorName);
            values.put(AuthorEntry.COLUMN_PUBLISHED_DATE, date);
            values.put(AuthorEntry.COLUMN_BOOK_LINK, link);
            values.put(AuthorEntry.COLUMN_BOOK_DESCRIPTION, description);
            values.put(AuthorEntry.COLUMN_BOOK_SALEABILITY, saleability);
            values.put(AuthorEntry.COLUMN_BOOK_BUY_LINK, buyLink);
            values.put(AuthorEntry.COLUMN_BOOK_WEB_READER_LINK, webReaderLink);

            newRowId = db.insert(AuthorEntry.TABLE_NAME, null, values);

            db.close();
        }

        return newRowId;
    }

    final public void deleteBook (String name){

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + AuthorEntry.TABLE_NAME + " WHERE " +
                AuthorEntry.COLUMN_Book_NAME + " = '" + name + "'";
        db.execSQL(query);
    }
}
