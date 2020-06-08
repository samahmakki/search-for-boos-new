package com.samahmakki.seacrhforbooksandsave.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.samahmakki.seacrhforbooksandsave.data.BookContract.Topic;


public class TopicDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "topics.db";

    private static final int DATABASE_VERSION = 1;


    public TopicDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + Topic.TABLE_NAME + " ("
                + Topic._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Topic.COLUMN_TOPIC + " TEXT);";

        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + Topic.TABLE_NAME);
        onCreate(db);
    }

    //insert data into the Books Table

    final public long insertName (String authorName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Topic.COLUMN_TOPIC, authorName);

        long newRowId = db.insert(Topic.TABLE_NAME, null, values);

        db.close();
        return newRowId;
    }

    final public void deleteName (String name){

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + Topic.TABLE_NAME + " WHERE " +
                Topic.COLUMN_TOPIC + " = '" + name + "'";
        db.execSQL(query);
    }
}
