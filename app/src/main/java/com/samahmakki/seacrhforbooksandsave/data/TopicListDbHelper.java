package com.samahmakki.seacrhforbooksandsave.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.samahmakki.seacrhforbooksandsave.data.BookContract.TopicEntry;

public class TopicListDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "topic_list.db";

    private static final int DATABASE_VERSION = 11;

    public TopicListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + TopicEntry.TABLE_NAME + " ("
                + TopicEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TopicEntry.TOPIC_NUMBER + " INTEGER, "
                + TopicEntry.COLUMN_Book_NAME + " TEXT, "
                + TopicEntry.COLUMN_AUTHOR_NAME + " TEXT, "
                + TopicEntry.COLUMN_BOOK_IMAGE + " BLOB, "
                + TopicEntry.COLUMN_PUBLISHED_DATE + " TEXT, "
                + TopicEntry.COLUMN_BOOK_DESCRIPTION + " TEXT, "
                + TopicEntry.COLUMN_BOOK_SALEABILITY + " TEXT, "
                + TopicEntry.COLUMN_BOOK_BUY_LINK + " TEXT, "
                + TopicEntry.COLUMN_BOOK_WEB_READER_LINK + " TEXT, "
                + BookContract.BookEntry.COLUMN_BOOK_LINK + " TEXT, "
                + BookContract.BookEntry.COLUMN_BOOK_DOWNLOAD_LINK + " TEXT);";

        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TopicEntry.TABLE_NAME);
        onCreate(db);
    }
}
