package com.samahmakki.seacrhforbooksandsave;

import android.app.Activity;
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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.google.android.material.navigation.NavigationView;
import com.samahmakki.seacrhforbooksandsave.classes.Book;
import com.samahmakki.seacrhforbooksandsave.classes.BookAdapter;
import com.samahmakki.seacrhforbooksandsave.classes.BookCursorAdapter;
import com.samahmakki.seacrhforbooksandsave.classes.SharedPref;
import com.samahmakki.seacrhforbooksandsave.data.BookContract.BookEntry;
import com.samahmakki.seacrhforbooksandsave.data.BookDbHelper;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class SavedBooksActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, NavigationView.OnNavigationItemSelectedListener {
    ListView savedBookListView;
    BookAdapter mAdapter;
    private TextView mEmptyStateTextView2;
    int selectedItem;
    DrawerLayout drawer;
    SharedPref sharedpref;
    BookCursorAdapter mCursorAdapter;
    private static final int Book_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLocale();
        setTitle(R.string.saved_books);

        sharedpref = new SharedPref(this);//load night mode setting
        if (sharedpref.loadNightModeState() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer_saved_books);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        savedBookListView = findViewById(R.id.saved_list);
        mEmptyStateTextView2 = findViewById(R.id.saved_empty_view);
        savedBookListView.setEmptyView(mEmptyStateTextView2);
        mEmptyStateTextView2.setText(getResources().getString(R.string.no_Saved_Books_Yet));
        mCursorAdapter = new BookCursorAdapter(this, null);
        savedBookListView.setAdapter(mCursorAdapter);
        // Kick off the loader
        getSupportLoaderManager().initLoader(Book_LOADER, null, this);

        ///////////////////////////////////////////////////////////

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ////////////////////////////////////////////////////////////

        savedBookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                Intent intent = new Intent(SavedBooksActivity.this, BookInfoActivity.class);

                Uri contentUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                intent.setData(contentUri);
                intent.putExtra("Unique", "from_saved_fragment");
                startActivity(intent);
            }
        });

        savedBookListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                final PopupMenu popupMenu = new PopupMenu(SavedBooksActivity.this, view);
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
        savedBookListView.setEmptyView(mEmptyStateTextView2);
        mEmptyStateTextView2.setText(getResources().getString(R.string.no_Saved_Books_Yet));
    }


    private void showDialogDelete(final long i) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(Objects.requireNonNull(SavedBooksActivity.this));
        dialogDelete.setTitle(getResources().getString(R.string.delete_book));
        dialogDelete.setMessage(getResources().getString(R.string.delete_book_msg));
        dialogDelete.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri contentUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, i);
                int rowsDeleted = getContentResolver().delete(contentUri, null, null);
                // Show a toast message depending on whether or not the delete was successful.
                if (rowsDeleted == 0) {
                    // If no rows were deleted, then there was an error with the delete.
                    Toast.makeText(SavedBooksActivity.this, getResources().getString(R.string.not_deleted)
                            , Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the delete was successful and we can display a toast.
                    Toast.makeText(SavedBooksActivity.this, getResources().getString(R.string.deleted)
                            , Toast.LENGTH_SHORT).show();
                }
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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        final String appUrl = "https://play.google.com/store/apps/details?id=com.samahmakki.seacrhforbooksandsave";
        if (id == R.id.home) {
            Intent it = new Intent(SavedBooksActivity.this, MainActivity.class);
            startActivity(it);
        } else if (id == R.id.saved_books) {
            Intent it = new Intent(SavedBooksActivity.this, SavedBooksActivity.class);
            startActivity(it);
        } else if (id == R.id.favorite_authors) {
            Intent it = new Intent(SavedBooksActivity.this, FavoriteAuthorsActivity.class);
            startActivity(it);
        } else if (id == R.id.favorite_topics) {
            Intent it = new Intent(SavedBooksActivity.this, FavoriteTopicsActivity.class);
            startActivity(it);
        } else if (id == R.id.language) {
            showChangeLanguageDialog();

        } else if (id == R.id.night_mode) {
            showNightModeDialog();
        } else if (id == R.id.nav_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, appUrl);
            startActivity(Intent.createChooser(shareIntent, "Share using"));

        } else if (id == R.id.nav_rate) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SavedBooksActivity.this);
            dialogBuilder.setTitle(R.string.rate_s);
            dialogBuilder.setMessage(R.string.if_you);
            dialogBuilder.setPositiveButton(R.string.rate_s, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(appUrl));
                    startActivity(i);
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();

        } else if (id == R.id.exit) {
            // Toast.makeText(appContext, "BAck", Toast.LENGTH_LONG).show();
            AlertDialog.Builder alert = new AlertDialog.Builder(SavedBooksActivity.this);
            alert.setTitle(getString(R.string.app_name));
            // alert.setIcon(R.drawable.ic_logout);
            alert.setMessage(getString(R.string.Quit));
            alert.setPositiveButton(getString(R.string.Yes),
                    new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {

                           /* finish();
                            System.exit(0);*/
                            finishAffinity();
                        }
                    });
            alert.setNegativeButton(getString(R.string.No),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }

                    });

            alert.show();
            return true;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    private void showChangeLanguageDialog() {
        //Array of language to display in alert dialog
        final String[] listItems = {"English", "عربي"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SavedBooksActivity.this);
        mBuilder.setTitle(R.string.choose_language);
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 1) {
                    //Arabic
                    Locale locale = new Locale("ar");
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources()
                            .getDisplayMetrics());
                    SharedPreferences.Editor editor = getSharedPreferences("CommonPrefs",
                            MODE_PRIVATE).edit();
                    editor.putString("Language", "ar");
                    editor.apply();
                    recreate();
                    Toast.makeText(SavedBooksActivity.this, "تم إختيار اللغة العربية", Toast.LENGTH_LONG).show();

                } else if (which == 0) {

                    //English
                    Locale locale = new Locale("en");
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources()
                            .getDisplayMetrics());
                    SharedPreferences.Editor editor = getSharedPreferences("CommonPrefs",
                            MODE_PRIVATE).edit();
                    editor.putString("Language", "en");
                    editor.apply();
                    recreate();
                    Toast.makeText(SavedBooksActivity.this, "English Language Selected", Toast.LENGTH_LONG).show();
                }
                //dismiss BillAlert dialog when language selected
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        //show BillAlert Dialog
        mDialog.show();
    }

    private void showNightModeDialog() {
        //Array of language to display in alert dialog
        final String[] listItems = {getResources().getString(R.string.night_mode_on),
                getResources().getString(R.string.night_mode_off)};

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SavedBooksActivity.this);
        mBuilder.setTitle(R.string.choose_nightMode_state);
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    sharedpref.setNightModeState(true);
                    restartApp();
                    Toast.makeText(SavedBooksActivity.this, getResources().getString(R.string.night_modeOn),
                            Toast.LENGTH_LONG).show();

                } else if (which == 1) {
                    sharedpref.setNightModeState(false);
                    restartApp();
                    Toast.makeText(SavedBooksActivity.this, getResources().getString(R.string.night_modeOff),
                            Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public void restartApp() {
        Intent i = new Intent(getApplicationContext(), SavedBooksActivity.class);
        startActivity(i);
        finish();
    }

    private void showDialogDeleteAll() {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(Objects.requireNonNull(SavedBooksActivity.this));
        dialogDelete.setTitle(getResources().getString(R.string.delete_all_books));
        dialogDelete.setMessage(getResources().getString(R.string.delete_all_books_msg));
        dialogDelete.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Respond to a click on the "Insert dummy data" menu option
                int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
                // Show a toast message depending on whether or not the delete was successful.
                if (rowsDeleted == 0) {
                    // If no rows were deleted, then there was an error with the delete.
                    Toast.makeText(SavedBooksActivity.this, getResources().getString(R.string.not_deleted_all_books)
                            , Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the delete was successful and we can display a toast.
                    Toast.makeText(SavedBooksActivity.this, getResources().getString(R.string.deleted_all_books)
                            , Toast.LENGTH_SHORT).show();
                }
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

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_Book_NAME,
                BookEntry.COLUMN_AUTHOR_NAME,
                BookEntry.COLUMN_BOOK_IMAGE,
                BookEntry.COLUMN_PUBLISHED_DATE,
        };

        return new CursorLoader(this,
                BookEntry.CONTENT_URI,
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
