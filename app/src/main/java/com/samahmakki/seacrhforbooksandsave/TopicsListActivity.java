package com.samahmakki.seacrhforbooksandsave;

import android.app.Activity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.samahmakki.seacrhforbooksandsave.classes.TopicListCursorAdapter;
import com.samahmakki.seacrhforbooksandsave.classes.SharedPref;
import com.samahmakki.seacrhforbooksandsave.data.BookContract.TopicEntry;

import java.util.Locale;
import java.util.Objects;

public class TopicsListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    ListView savedTopicBookListView;
    private TextView mEmptyStateTextView2;
    SharedPref sharedpref;
    Intent receivedIntent;
    int p;
    int selectedItem;
    TopicListCursorAdapter mCursorAdapter;
    private static final int TOPIC_LIST_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLocale();
        setTitle(R.string.books_list);

        sharedpref = new SharedPref(this);//load night mode setting
        if (sharedpref.loadNightModeState() == true) {

            setTheme(R.style.darktheme2);
        } else {
            setTheme(R.style.AppTheme2);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics_list);

        receivedIntent = getIntent();
        p = receivedIntent.getIntExtra("position", 0);

        savedTopicBookListView = findViewById(R.id.saved_authors_list);

        mEmptyStateTextView2 = findViewById(R.id.saved_empty_view);
        savedTopicBookListView.setEmptyView(mEmptyStateTextView2);
        mEmptyStateTextView2.setText(getResources().getString(R.string.no_Saved_Books_Yet));

        mCursorAdapter = new TopicListCursorAdapter(this, null);
        savedTopicBookListView.setAdapter(mCursorAdapter);

        savedTopicBookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                Intent intent = new Intent(TopicsListActivity.this, BookInfoActivity.class);

                Uri contentUri = ContentUris.withAppendedId(TopicEntry.CONTENT_URI, id);
                intent.setData(contentUri);
                intent.putExtra("Unique", "from_saved_fragment");
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(TOPIC_LIST_LOADER, null, this);

        savedTopicBookListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                final PopupMenu popupMenu = new PopupMenu(TopicsListActivity.this, view);
                popupMenu.inflate(R.menu.menu_book_saved_list);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        selectedItem = item.getItemId();
                        if (selectedItem == R.id.delete_2) {
                            showDialogDelete(id);
                        }
                        return true;
                    }
                });
                popupMenu.show();
                return true;
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

    private void showDialogDelete(final long i) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(Objects.requireNonNull(TopicsListActivity.this));
        dialogDelete.setTitle(getResources().getString(R.string.delete_book));
        dialogDelete.setMessage(getResources().getString(R.string.delete_book_msg));
        dialogDelete.setPositiveButton(getResources().getString(R.string.ok)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri contentUri = ContentUris.withAppendedId(TopicEntry.CONTENT_URI, i);
                        int rowsDeleted = getContentResolver().delete(contentUri, null, null);
                        // Show a toast message depending on whether or not the delete was successful.
                        if (rowsDeleted == 0) {
                            // If no rows were deleted, then there was an error with the delete.
                            Toast.makeText(TopicsListActivity.this, getResources().getString(R.string.not_deleted)
                                    , Toast.LENGTH_SHORT).show();
                        } else {
                            // Otherwise, the delete was successful and we can display a toast.
                            Toast.makeText(TopicsListActivity.this, getResources().getString(R.string.deleted)
                                    , Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        dialogDelete.setNegativeButton(getResources().getString(R.string.cancel)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
        dialogDelete.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete_all, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.delete_all:
                showDialogDeleteAll();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialogDeleteAll() {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(Objects.requireNonNull(TopicsListActivity.this));
        dialogDelete.setTitle(getResources().getString(R.string.delete_all_books));
        dialogDelete.setMessage(getResources().getString(R.string.delete_all_books_msg));
        dialogDelete.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selection = TopicEntry.TOPIC_NUMBER + " = ?";
                String[] selectionArgs = new String[]{String.valueOf(p)};

                // Respond to a click on the "Insert dummy data" menu option
                int rowsDeleted = getContentResolver().delete(TopicEntry.CONTENT_URI, selection, selectionArgs);
                // Show a toast message depending on whether or not the delete was successful.
                if (rowsDeleted == 0) {
                    // If no rows were deleted, then there was an error with the delete.
                    Toast.makeText(TopicsListActivity.this, getResources().getString(R.string.not_deleted_all_books)
                            , Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the delete was successful and we can display a toast.
                    Toast.makeText(TopicsListActivity.this, getResources().getString(R.string.deleted_all_books)
                            , Toast.LENGTH_SHORT).show();
                }
                //Intent intent = new Intent(AuthorsListActivity.this, AuthorsListActivity.class);
                //startActivity(intent);
            }
        });
        dialogDelete.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                TopicEntry._ID,
                TopicEntry.COLUMN_Book_NAME,
                TopicEntry.COLUMN_AUTHOR_NAME,
                TopicEntry.COLUMN_BOOK_IMAGE,
                TopicEntry.COLUMN_PUBLISHED_DATE,
        };

        String selection = TopicEntry.TOPIC_NUMBER + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(p)};

        return new CursorLoader(this,
                TopicEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
            mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
