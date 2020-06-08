package com.samahmakki.seacrhforbooksandsave;

import android.app.Activity;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.samahmakki.seacrhforbooksandsave.classes.Book;
import com.samahmakki.seacrhforbooksandsave.classes.BookAdapter;
import com.samahmakki.seacrhforbooksandsave.classes.SharedPref;
import com.samahmakki.seacrhforbooksandsave.data.BookContract;
import com.samahmakki.seacrhforbooksandsave.data.BookDbHelper;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class SavedBooksActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    ListView savedBookListView;
    private BookAdapter mAdapter;
    private TextView mEmptyStateTextView2;
    int selectedItem;
    DrawerLayout drawer;
    SharedPref sharedpref;

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

        mEmptyStateTextView2 = findViewById(R.id.saved_empty_view);
        BookDbHelper mBillHelper = new BookDbHelper(SavedBooksActivity.this);


        final SQLiteDatabase db = mBillHelper.getReadableDatabase();
        String[] projection = {
                BookContract.BookEntry.COLUMN_Book_NAME,
                BookContract.BookEntry.COLUMN_AUTHOR_NAME,
                BookContract.BookEntry.COLUMN_BOOK_IMAGE,
                BookContract.BookEntry.COLUMN_PUBLISHED_DATE,
                BookContract.BookEntry.COLUMN_BOOK_LINK,
                BookContract.BookEntry.COLUMN_BOOK_DESCRIPTION,
                BookContract.BookEntry.COLUMN_BOOK_SALEABILITY,
                BookContract.BookEntry.COLUMN_BOOK_BUY_LINK,
                BookContract.BookEntry.COLUMN_BOOK_WEB_READER_LINK
        };

        Cursor cursor = db.query(
                BookContract.BookEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        final ArrayList<Book> BillsItem = new ArrayList<Book>();
        savedBookListView = findViewById(R.id.saved_list);
        mAdapter = new BookAdapter(SavedBooksActivity.this, BillsItem);
        savedBookListView.setAdapter(mAdapter);


        try {
            // Figure out the index of each column
            int bookNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_Book_NAME);
            int authorNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_AUTHOR_NAME);
            int imageColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_IMAGE);
            int dateColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PUBLISHED_DATE);
            int linkColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_LINK);
            int descriptionColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_DESCRIPTION);
            int saleabilityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SALEABILITY);
            int buyLinkColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_BUY_LINK);
            int webReaderLinkColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_WEB_READER_LINK);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                String currentBookName = cursor.getString(bookNameColumnIndex);
                String currentAuthorName = cursor.getString(authorNameColumnIndex);
                byte[] currentImage = cursor.getBlob(imageColumnIndex);
                String currentDate = cursor.getString(dateColumnIndex);
                String currentLink = cursor.getString(linkColumnIndex);
                String currentDescription = cursor.getString(descriptionColumnIndex);
                String currentSaleability = cursor.getString(saleabilityColumnIndex);
                String currentBuyLink = cursor.getString(buyLinkColumnIndex);
                String currentWebReaderLink = cursor.getString(webReaderLinkColumnIndex);

                if (currentImage != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(currentImage, 0, currentImage.length);
                    //Bitmap bitmap1 = ImageDecoder.decodeBitmap(ImageDecoder.createSource(ByteBuffer.wrap(currentImage)));
                    BillsItem.add(new Book(currentBookName, bitmap, currentAuthorName, currentDate, currentLink,
                            currentDescription, currentSaleability, currentBuyLink, currentWebReaderLink));
                }

                if (currentImage == null){
                    Bitmap bitmap = null;
                    BillsItem.add(new Book(currentBookName, bitmap, currentAuthorName, currentDate, currentLink,
                            currentDescription, currentSaleability, currentBuyLink, currentWebReaderLink));
                }
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }

        savedBookListView.setEmptyView(mEmptyStateTextView2);
        mEmptyStateTextView2.setText("No Saved Books Yet...");

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

                Book book = (Book) savedBookListView.getItemAtPosition(position);
                Bitmap bookImage = book.getBitmap();
                String bookName = book.getBookName();
                String authorName = book.getAuthorName();
                String publishedDate = book.getPublishedDate();
                String description = book.getDescription();
                String saleability = book.getSaleability();
                String buyLink = book.getBuyLink();
                String webReaderLink = book.getWebReaderLink();
                String previewLink = book.getLink();

                Intent intent = new Intent(SavedBooksActivity.this, BookInfoActivity.class);
                intent.putExtra("bookImage", bookImage);
                intent.putExtra("bookName", bookName);
                intent.putExtra("authorName", authorName);
                intent.putExtra("publishedDate", publishedDate);
                intent.putExtra("description", description);
                intent.putExtra("saleability", saleability);
                intent.putExtra("buyLink", buyLink);
                intent.putExtra("webReaderLink", webReaderLink);
                intent.putExtra("previewLink", previewLink);
                intent.putExtra("Unique", "from_saved_fragment");
                startActivity(intent);
            }
        });

        savedBookListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final PopupMenu popupMenu = new PopupMenu(SavedBooksActivity.this, view);
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
        });
        savedBookListView.setEmptyView(mEmptyStateTextView2);
        mEmptyStateTextView2.setText(getResources().getString(R.string.no_Saved_Books_Yet));
    }


    private void showDialogDelete(final int i) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(Objects.requireNonNull(SavedBooksActivity.this));
        dialogDelete.setTitle(getResources().getString(R.string.delete_book));
        dialogDelete.setMessage(getResources().getString(R.string.delete_book_msg));
        dialogDelete.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Book currentBook = mAdapter.getItem(i);
                    BookDbHelper bookDbHelper = new BookDbHelper(SavedBooksActivity.this);
                    bookDbHelper.deleteBook(currentBook.getBookName());
                    mAdapter.remove(currentBook);
                    Toast.makeText(SavedBooksActivity.this, getResources().getString(R.string.deleted)
                            , Toast.LENGTH_SHORT).show();
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
            Intent it = new Intent(SavedBooksActivity.this, MainActivity.class);
            startActivity(it);
        }
        else if (id == R.id.saved_books) {
            Intent it = new Intent(SavedBooksActivity.this, SavedBooksActivity.class);
            startActivity(it);
        }
        /*else if (id == R.id.favorite_authors) {
            Intent it = new Intent(SavedBooksActivity.this, FavoriteAuthorsActivity.class);
            startActivity(it);
        }
        else if (id == R.id.favorite_topics) {
            Intent it = new Intent(SavedBooksActivity.this, FavoriteTopicsActivity.class);
            startActivity(it);
        }*/
        else if (id == R.id.language) {
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

        }
        else if (id == R.id.exit) {
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
        final String[] listItems = {"English","عربي"};
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
}
