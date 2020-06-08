package com.samahmakki.seacrhforbooksandsave.rubbish;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.samahmakki.seacrhforbooksandsave.R;
import com.samahmakki.seacrhforbooksandsave.classes.SharedPref;
import com.samahmakki.seacrhforbooksandsave.data.AuthorNameDbHelper;
import com.samahmakki.seacrhforbooksandsave.data.BookContract;
import com.samahmakki.seacrhforbooksandsave.data.BookDbHelper;

import java.util.ArrayList;
import java.util.Locale;

public class AuthorsActivity extends AppCompatActivity {

    ListView authors_name_list;
    ArrayAdapter<String> adapter;
    SharedPref sharedpref;
    public static String currentAuthor;
    EditText authorNameEditText;
    String authorName;
    AuthorNameDbHelper authorNameDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedpref = new SharedPref(this);//load night mode setting
        if (sharedpref.loadNightModeState() == true) {

            setTheme(R.style.darktheme2);
        } else {
            setTheme(R.style.AppTheme2);
        }
        loadLocale();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authors);

        Read();

        final ArrayList<String> NameItem = new ArrayList<String>();
        authors_name_list = findViewById(R.id.auth_list);
        adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_3, NameItem);
        authors_name_list.setAdapter(adapter);


        Button add_author_btn = findViewById(R.id.add_auth_btn);
        add_author_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // custom dialog
                final Dialog dialog = new Dialog(AuthorsActivity.this);
                dialog.setContentView(R.layout.add_author);
                dialog.setTitle("Add Author");

                // set the custom dialog components - text, image and button
                authorNameEditText = dialog.findViewById(R.id.add_auth_name);

                Button dialogButton = dialog.findViewById(R.id.ok_btn);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            authorName = authorNameEditText.getText().toString();
                            authorNameDbHelper = new AuthorNameDbHelper(AuthorsActivity.this);
                            long newRowId = authorNameDbHelper.insertName(authorName);

                            adapter.add(authorName);

                            if (newRowId == -1) {
                                Toast.makeText(AuthorsActivity.this, "Type author name again", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AuthorsActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("error", e.getMessage());
                        }
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });


        authors_name_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentAuthor = adapter.getItem(position);

                BookDbHelper authorListDbHelper = new BookDbHelper(AuthorsActivity.this);

                Toast.makeText(AuthorsActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Read();
    }

    private void Read() {
        AuthorNameDbHelper authorNameDbHelper = new AuthorNameDbHelper(AuthorsActivity.this);

        final SQLiteDatabase db = authorNameDbHelper.getReadableDatabase();
        String[] projection = {
                BookContract.AuthorName.COLUMN_AUTHOR_NAME,
        };

        Cursor cursor = db.query(
                BookContract.AuthorName.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        final ArrayList<String> NameItem = new ArrayList<String>();
        authors_name_list = findViewById(R.id.auth_list);
        adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_3, NameItem);
        authors_name_list.setAdapter(adapter);

        try {
            // Figure out the index of each column
            int authorNameColumnIndex = cursor.getColumnIndex(BookContract.AuthorName.COLUMN_AUTHOR_NAME);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                String currentAuthorName = cursor.getString(authorNameColumnIndex);
                NameItem.add(currentAuthorName);
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
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
}
