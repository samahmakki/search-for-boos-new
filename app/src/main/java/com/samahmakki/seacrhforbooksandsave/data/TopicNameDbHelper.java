package com.samahmakki.seacrhforbooksandsave.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.samahmakki.seacrhforbooksandsave.data.BookContract.TopicName;


public class TopicNameDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "topic_name.db";

    private static final int DATABASE_VERSION = 2;

    public TopicNameDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + TopicName.TABLE_NAME + " ("
                + TopicName._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TopicName.COLUMN_TOPIC_NAME_2 + " TEXT);";

        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TopicName.TABLE_NAME);
        onCreate(db);
    }
}
