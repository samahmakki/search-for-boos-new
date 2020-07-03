package com.samahmakki.seacrhforbooksandsave;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.samahmakki.seacrhforbooksandsave.classes.AuthorListCursorAdapter;
import com.samahmakki.seacrhforbooksandsave.classes.AuthorsListAdapter;
import com.samahmakki.seacrhforbooksandsave.classes.Book;
import com.samahmakki.seacrhforbooksandsave.classes.BookCursorAdapter;
import com.samahmakki.seacrhforbooksandsave.classes.SharedPref;
import com.samahmakki.seacrhforbooksandsave.data.AuthorListDbHelper;
import com.samahmakki.seacrhforbooksandsave.data.AuthorNameDbHelper;
import com.samahmakki.seacrhforbooksandsave.data.BookContract;
import com.samahmakki.seacrhforbooksandsave.data.BookContract.AuthorEntry;
import com.samahmakki.seacrhforbooksandsave.data.BookDbHelper;
import com.samahmakki.seacrhforbooksandsave.data.BookDbHelper_2;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class AuthorsListActivity extends AppCompatActivity implements  LoaderManager.LoaderCallbacks<Cursor>{
    ListView savedAuthorBookListView;
    private AuthorsListAdapter authorAdapter;
    SharedPref sharedpref;
    Intent receivedIntent;
    // long id;
    int selectedItem;
    AuthorListCursorAdapter mCursorAdapter;
    private static final int AUTHOR_LIST_LOADER = 0;

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
        setContentView(R.layout.activity_authors_list);

       /* receivedIntent = getIntent();
        id = receivedIntent.getLongExtra("row_id", id);*/

        savedAuthorBookListView = findViewById(R.id.saved_authors_list);
        mCursorAdapter = new AuthorListCursorAdapter(this, null);
        savedAuthorBookListView.setAdapter(mCursorAdapter);

        savedAuthorBookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                Intent intent = new Intent(AuthorsListActivity.this, BookInfoActivity.class);

                Uri contentUri = ContentUris.withAppendedId(AuthorEntry.CONTENT_URI, id);
                intent.setData(contentUri);
                intent.putExtra("Unique","from_saved_fragment");
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(AUTHOR_LIST_LOADER, null, this);

       /* savedAuthorBookListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final PopupMenu popupMenu = new PopupMenu(AuthorsListActivity.this, view);
                popupMenu.inflate(R.menu.menu_book_saved_list);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        selectedItem = item.getItemId();
                        if (selectedItem == R.id.delete_2) {
                            showDialogDelete(position);
                        }
                        return true;
                    }
                });
                popupMenu.show();
                return true;
            }
        });*/
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

    private void showDialogDelete(final int i) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(Objects.requireNonNull(AuthorsListActivity.this));
        dialogDelete.setTitle(getResources().getString(R.string.delete_book));
        dialogDelete.setMessage(getResources().getString(R.string.delete_book_msg));
        dialogDelete.setPositiveButton(getResources().getString(R.string.ok)
                , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Book currentBook = authorAdapter.getItem(i);
                    AuthorListDbHelper bookDbHelper = new AuthorListDbHelper(AuthorsListActivity.this);
                    bookDbHelper.deleteBook(currentBook.getBookName());
                    authorAdapter.remove(currentBook);
                    Toast.makeText(AuthorsListActivity.this, getResources().getString(R.string.deleted)
                            , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
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

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                AuthorEntry._ID,
                AuthorEntry.COLUMN_Book_NAME,
                AuthorEntry.COLUMN_BOOK_IMAGE,
                AuthorEntry.COLUMN_PUBLISHED_DATE,
        };

        return new CursorLoader(this,
                AuthorEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
