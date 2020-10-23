package com.samahmakki.seacrhforbooksandsave;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.samahmakki.seacrhforbooksandsave.classes.SharedPref;
import com.samahmakki.seacrhforbooksandsave.data.BookContract.TopicName;

import java.util.Locale;

public class AddTopicActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    EditText topicNameEditText;
    String topicName;
    SharedPref sharedpref;

    /**
     * Content URI for the existing author name (null if it's a new author name)
     */
    private Uri mCurrentTopicUri;
    private static final int EXISTING_topic_LOADER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();

        setContentView(R.layout.add_topic);

        Intent intent = getIntent();
        mCurrentTopicUri = intent.getData();

        if (mCurrentTopicUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_topic));
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_topic));
            getSupportLoaderManager().initLoader(EXISTING_topic_LOADER, null, this);

        }
        topicNameEditText = findViewById(R.id.add_topic_name);

        Button okButton = findViewById(R.id.ok_btn);
        Button cancelButton = findViewById(R.id.cancel_btn);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topicName = topicNameEditText.getText().toString().trim();

                if (mCurrentTopicUri == null && TextUtils.isEmpty(topicName)) {
                    return;
                }
                // Create a ContentValues object where column names are the keys,
                // and pet attributes from the editor are the values.
                ContentValues values = new ContentValues();
                values.put(TopicName.COLUMN_TOPIC_NAME_2, topicName);

                if (mCurrentTopicUri == null) {
                    // Insert a new row for pet in the database, returning the ID of that new row.
                    Uri newUri = getContentResolver().insert(TopicName.CONTENT_URI, values);
                    // Show a toast message depending on whether or not the insertion was successful
                    if (newUri == null) {
                        // If the new content URI is null, then there was an error with insertion.
                        Toast.makeText(AddTopicActivity.this, getResources().getString(R.string.type_author_name_again)
                                , Toast.LENGTH_SHORT).show();
                    } else {
                        // Otherwise, the insertion was successful and we can display a toast.
                        Toast.makeText(AddTopicActivity.this, getResources().getString(R.string.successfully_added)
                                , Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
                    // and pass in the new ContentValues. Pass in null for the selection and selection args
                    // because mCurrentPetUri will already identify the correct row in the database that
                    // we want to modify.
                    int rowsAffected = getContentResolver().update(mCurrentTopicUri, values, null, null);
                    // Show a toast message depending on whether or not the update was successful.
                    if (rowsAffected == 0) {
                        // If no rows were affected, then there was an error with the update.
                        Toast.makeText(AddTopicActivity.this, getString(R.string.editor_update_author_failed),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Otherwise, the update was successful and we can display a toast.
                        Toast.makeText(AddTopicActivity.this, getString(R.string.editor_update_author_successful),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if (overrideConfiguration != null) {
            int uiMode = overrideConfiguration.uiMode;
            overrideConfiguration.setTo(getBaseContext().getResources().getConfiguration());
            overrideConfiguration.uiMode = uiMode;
        }
        super.applyOverrideConfiguration(overrideConfiguration);
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("CommonPrefs", MODE_PRIVATE).edit();
        editor.putString("Language", lang);
        editor.apply();
    }

    public void loadLocale() {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        setLocale(language);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                TopicName._ID,
                TopicName.COLUMN_TOPIC_NAME_2
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                mCurrentTopicUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(TopicName.COLUMN_TOPIC_NAME_2);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);

            // Update the views on the screen with the values from the database
            topicNameEditText.setText(name);

        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}