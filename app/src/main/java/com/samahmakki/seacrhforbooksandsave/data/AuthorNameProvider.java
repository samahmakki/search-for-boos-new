package com.samahmakki.seacrhforbooksandsave.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.samahmakki.seacrhforbooksandsave.data.BookContract.AuthorName;

public class AuthorNameProvider extends ContentProvider {

    /**
     * URI matcher code for the content URI for the books table
     */
    private static final int AUTHORS = 200;

    /**
     * URI matcher code for the content URI for a single book in the books table
     */
    private static final int AUTHOR_ID = 201;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // TODO: Add 2 content URIs to URI matcher
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY_AUTHOR_NAME, BookContract.PATH_AUTHOR_NAME, AUTHORS);

        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY_AUTHOR_NAME, BookContract.PATH_AUTHOR_NAME + "/#", AUTHOR_ID);
    }

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = AuthorNameProvider.class.getSimpleName();
    private AuthorNameDbHelper mAuthorNameDbHelper;

    @Override
    public boolean onCreate() {
        // TODO: Create and initialize a BookDbHelper object to gain access to the pets database.
        mAuthorNameDbHelper = new AuthorNameDbHelper(getContext());
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Get readable database
        SQLiteDatabase database = mAuthorNameDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case AUTHORS:
                // For the BOOKS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the books table.
                // TODO: Perform database query on pets table
                cursor = database.query(AuthorName.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case AUTHOR_ID:
                // For the BOOK_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.samahmakki.seacrhforbooksandsave/books/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = AuthorName._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(AuthorName.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case AUTHORS:
                return AuthorName.CONTENT_LIST_TYPE;
            case AUTHOR_ID:
                return AuthorName.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case AUTHORS:
                return insertBook(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {

        // TODO: Insert a new pet into the books database table with the given ContentValues
        // Get writeable database
        SQLiteDatabase database = mAuthorNameDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(AuthorName.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Track the number of rows that were deleted
        int rowsDeleted;

        // Get writeable database
        SQLiteDatabase database = mAuthorNameDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case AUTHORS:
                // Delete all rows that match the selection and selection args
                // For  case AUTHORS:
                rowsDeleted = database.delete(AuthorName.TABLE_NAME, selection, selectionArgs);
                // If 1 or more rows were deleted, then notify all listeners that the data at the
                // given URI has changed
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
            case AUTHOR_ID:
                // For case PET_ID:
                // Delete a single row given by the ID in the URI
                selection = AuthorName._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(AuthorName.TABLE_NAME, selection, selectionArgs);
                // If 1 or more rows were deleted, then notify all listeners that the data at the
                // given URI has changed
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case AUTHORS:
                return updatePet(uri, values, selection, selectionArgs);
            case AUTHOR_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = AuthorName._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    /**
     * Update authors in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(AuthorName.COLUMN_AUTHOR_NAME_2)) {
            String name = values.getAsString(AuthorName.COLUMN_AUTHOR_NAME_2);
            if (name == null) {
                throw new IllegalArgumentException("Author requires a name");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }
        // TODO: Update the selected pets in the pets database table with the given ContentValues
        // Get writeable database
        SQLiteDatabase database = mAuthorNameDbHelper.getWritableDatabase();

        // If the ID is -1, then the insertion failed. Log an error and return null.
       /* if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + id);
            return Integer.parseInt(null);
        }*/

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(AuthorName.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        // TODO: Return the number of rows that were affected
        return rowsUpdated;
    }
}
