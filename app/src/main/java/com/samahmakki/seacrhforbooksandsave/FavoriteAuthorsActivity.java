package com.samahmakki.seacrhforbooksandsave;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
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
import com.samahmakki.seacrhforbooksandsave.classes.AuthorNameCursorAdapter;
import com.samahmakki.seacrhforbooksandsave.classes.SharedPref;
import com.samahmakki.seacrhforbooksandsave.data.AuthorNameDbHelper;
import com.samahmakki.seacrhforbooksandsave.data.BookContract.AuthorEntry;
import com.samahmakki.seacrhforbooksandsave.data.BookContract.AuthorName;
import com.samahmakki.seacrhforbooksandsave.data.BookDbHelper;
import com.samahmakki.seacrhforbooksandsave.data.BookDbHelper_2;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class FavoriteAuthorsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    ListView authors_name_list;
    ArrayAdapter<String> adapter;
    String authorName;
    public static String currentAuthor;
    public static int p;

    int selectedItem;
    SharedPref sharedpref;
    byte[] bookImage;
    String bookName;
    String publishedDate;
    String description;

    String saleability;
    String buyLink;
    String webReaderLink;
    String previewLink;

    AuthorNameCursorAdapter mAuthorNameCursorAdapter;
    private static final int AUTHOR_LOADER = 1;

    // public static long newRowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLocale();
        setTitle(R.string.favorite_authors);

        sharedpref = new SharedPref(this);//load night mode setting
        if (sharedpref.loadNightModeState() == true) {

            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer_fav_auth);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ///////////////////////////////////////////////////////////

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ////////////////////////////////////////////////////////////

        Button add_author_btn = findViewById(R.id.add_auth_btn);
        add_author_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(FavoriteAuthorsActivity.this, AddAuthorActivity.class);
                startActivity(intent);
            }
        });

        authors_name_list = findViewById(R.id.auth_list);
        mAuthorNameCursorAdapter = new AuthorNameCursorAdapter(this, null);
        authors_name_list.setAdapter(mAuthorNameCursorAdapter);

        authors_name_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,  int position, final long id) {
                final PopupMenu popupMenu = new PopupMenu(FavoriteAuthorsActivity.this, view);
                popupMenu.inflate(R.menu.pop_up_edit_author);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        selectedItem = item.getItemId();
                        if (selectedItem == R.id.update) {
                            Intent intent = new Intent(FavoriteAuthorsActivity.this, AddAuthorActivity.class);
                            Uri contentUri = ContentUris.withAppendedId(AuthorName.CONTENT_URI, id);
                            intent.setData(contentUri);
                            startActivity(intent);
                        }
                        if (selectedItem == R.id.delete) {
                            showDialogDelete(id);
                        }
                        return true;
                    }
                });
                popupMenu.show();
                return true;
            }
        });

        Intent receivedIntent = getIntent();
        if (receivedIntent == null) {
            return;
        } else if (receivedIntent != null) {
            String stringData = receivedIntent.getStringExtra("Tag");
            if (stringData != null && !stringData.isEmpty() && stringData.equals("from_book_info")) {
                bookName = receivedIntent.getStringExtra("bookName");
                authorName = receivedIntent.getStringExtra("authorName");
                publishedDate = receivedIntent.getStringExtra("publishedDate");
                description = receivedIntent.getStringExtra("description");
                bookImage = receivedIntent.getByteArrayExtra("bookImage");
                saleability = receivedIntent.getStringExtra("saleability");
                buyLink = receivedIntent.getStringExtra("buyLink");
                webReaderLink = receivedIntent.getStringExtra("webReaderLink");
                previewLink = receivedIntent.getStringExtra("previewLink");

                authors_name_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        // and pet attributes from the editor are the values.
                        ContentValues values = new ContentValues();
                        values.put(AuthorEntry.COLUMN_Book_NAME, bookName);
                        values.put(AuthorEntry.COLUMN_AUTHOR_NAME, authorName);
                        values.put(AuthorEntry.COLUMN_BOOK_IMAGE, bookImage);
                        values.put(AuthorEntry.COLUMN_PUBLISHED_DATE, publishedDate);
                        values.put(AuthorEntry.COLUMN_BOOK_LINK, previewLink);
                        values.put(AuthorEntry.COLUMN_BOOK_DESCRIPTION, description);
                        values.put(AuthorEntry.COLUMN_BOOK_SALEABILITY, saleability);
                        values.put(AuthorEntry.COLUMN_BOOK_BUY_LINK, buyLink);
                        values.put(AuthorEntry.COLUMN_BOOK_WEB_READER_LINK, webReaderLink);

                        // Insert a new row for pet in the database, returning the ID of that new row.
                        Uri newUri = getContentResolver().insert(AuthorEntry.CONTENT_URI, values);
                        // Show a toast message depending on whether or not the insertion was successful
                        if (newUri == null) {
                            // If the new content URI is null, then there was an error with insertion.
                            Toast.makeText(FavoriteAuthorsActivity.this, getResources().getString(R.string.save_it_again)
                                    , Toast.LENGTH_SHORT).show();
                        } else {
                            // Otherwise, the insertion was successful and we can display a toast.
                            Toast.makeText(FavoriteAuthorsActivity.this, getResources().getString(R.string.successfully_saved)
                                    , Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                });
            } else {
                authors_name_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(FavoriteAuthorsActivity.this, AuthorsListActivity.class);
                        // intent.putExtra("row_id", id);
                        startActivity(intent);
                        //Toast.makeText(FavoriteAuthorsActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                        //finish();
                    }
                });
            }
        }

        authors_name_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getSupportLoaderManager().initLoader(AUTHOR_LOADER, null, this);

    }

    private void showDialogDelete(final long i) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(Objects.requireNonNull(FavoriteAuthorsActivity.this));
        dialogDelete.setTitle(getResources().getString(R.string.Delete_Author));
        dialogDelete.setMessage(getResources().getString(R.string.Delete_Author_msg));
        dialogDelete.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Uri contentUri = ContentUris.withAppendedId(AuthorName.CONTENT_URI, i);
                    int rowsDeleted = getContentResolver().delete(contentUri, null, null);
                    // Show a toast message depending on whether or not the delete was successful.
                    if (rowsDeleted == 0) {
                        // If no rows were deleted, then there was an error with the delete.
                        Toast.makeText(FavoriteAuthorsActivity.this, getResources().getString(R.string.not_deleted)
                                , Toast.LENGTH_SHORT).show();
                    } else {
                        // Otherwise, the delete was successful and we can display a toast.
                        Toast.makeText(FavoriteAuthorsActivity.this, getResources().getString(R.string.deleted)
                                , Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
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
            Intent it = new Intent(FavoriteAuthorsActivity.this, MainActivity.class);
            startActivity(it);
        } else if (id == R.id.saved_books) {
            Intent it = new Intent(FavoriteAuthorsActivity.this, SavedBooksActivity.class);
            startActivity(it);
        } else if (id == R.id.favorite_authors) {
            Intent it = new Intent(FavoriteAuthorsActivity.this, FavoriteAuthorsActivity.class);
            startActivity(it);
        } else if (id == R.id.favorite_topics) {
            Intent it = new Intent(FavoriteAuthorsActivity.this, FavoriteTopicsActivity.class);
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
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(FavoriteAuthorsActivity.this);
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
            AlertDialog.Builder alert = new AlertDialog.Builder(
                    FavoriteAuthorsActivity.this);
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
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
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

    private void showChangeLanguageDialog() {
        //Array of language to display in alert dialog
        final String[] listItems = {"English", "عربي"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(FavoriteAuthorsActivity.this);
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
                    Toast.makeText(FavoriteAuthorsActivity.this, "تم إختيار اللغة العربية", Toast.LENGTH_LONG).show();

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
                    Toast.makeText(FavoriteAuthorsActivity.this, "English Language Selected", Toast.LENGTH_LONG).show();
                }
                //dismiss BillAlert dialog when language selected
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        //show BillAlert Dialog
        mDialog.show();
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

    public void restartApp() {
        Intent i = new Intent(getApplicationContext(), FavoriteAuthorsActivity.class);
        startActivity(i);
        // finish();
    }

    private void showNightModeDialog() {
        //Array of language to display in alert dialog
        final String[] listItems = {getResources().getString(R.string.night_mode_on),
                getResources().getString(R.string.night_mode_off)};

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(FavoriteAuthorsActivity.this);
        mBuilder.setTitle(R.string.choose_nightMode_state);
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    sharedpref.setNightModeState(true);
                    restartApp();
                    Toast.makeText(FavoriteAuthorsActivity.this, getResources().getString(R.string.night_modeOn),
                            Toast.LENGTH_LONG).show();

                } else if (which == 1) {
                    sharedpref.setNightModeState(false);
                    restartApp();
                    Toast.makeText(FavoriteAuthorsActivity.this, getResources().getString(R.string.night_modeOff),
                            Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                AuthorName._ID,
                AuthorName.COLUMN_AUTHOR_NAME_2,
        };

        return new CursorLoader(this,
                AuthorName.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAuthorNameCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAuthorNameCursorAdapter.swapCursor(null);

    }
}
