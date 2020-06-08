package com.samahmakki.seacrhforbooksandsave;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.samahmakki.seacrhforbooksandsave.classes.AuthorsListAdapter;
import com.samahmakki.seacrhforbooksandsave.classes.Book;
import com.samahmakki.seacrhforbooksandsave.classes.SharedPref;
import com.samahmakki.seacrhforbooksandsave.data.AuthorListDbHelper;
import com.samahmakki.seacrhforbooksandsave.data.AuthorNameDbHelper;
import com.samahmakki.seacrhforbooksandsave.data.BookContract.AuthorEntry;
import com.samahmakki.seacrhforbooksandsave.data.BookDbHelper;
import com.samahmakki.seacrhforbooksandsave.data.BookDbHelper_2;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class AuthorsListActivity extends AppCompatActivity {
    ListView savedAuthorBookListView;
    private AuthorsListAdapter authorAdapter;
    SharedPref sharedpref;
    Intent receivedIntent;
    // long id;
    int selectedItem;

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

        Read();

        savedAuthorBookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                Book book = (Book) savedAuthorBookListView.getItemAtPosition(position);
                Bitmap bookImage = book.getBitmap();
                String bookName = book.getBookName();
                String authorName = book.getAuthorName();
                String publishedDate = book.getPublishedDate();
                String description = book.getDescription();
                String saleability = book.getSaleability();
                String buyLink = book.getBuyLink();
                String webReaderLink = book.getWebReaderLink();
                String previewLink = book.getLink();

                Intent intent = new Intent(AuthorsListActivity.this, BookInfoActivity.class);
                intent.putExtra("bookImage", bookImage);
                intent.putExtra("bookName", bookName);
                intent.putExtra("authorName", authorName);
                intent.putExtra("publishedDate", publishedDate);
                intent.putExtra("description", description);
                intent.putExtra("saleability", saleability);
                intent.putExtra("buyLink", buyLink);
                intent.putExtra("webReaderLink", webReaderLink);
                intent.putExtra("previewLink", previewLink);
                intent.putExtra("Unique", "from_authors_list_activity");
                startActivity(intent);
            }
        });

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

    public void Read() {
        BookDbHelper_2 authorListDbHelper = new BookDbHelper_2(AuthorsListActivity.this);

        final SQLiteDatabase db = authorListDbHelper.getReadableDatabase();
        String[] projection = {
                AuthorEntry.COLUMN_Book_NAME,
                AuthorEntry.COLUMN_AUTHOR_NAME,
                AuthorEntry.COLUMN_BOOK_IMAGE,
                AuthorEntry.COLUMN_PUBLISHED_DATE,
                AuthorEntry.COLUMN_BOOK_LINK,
                AuthorEntry.COLUMN_BOOK_DESCRIPTION,
                AuthorEntry.COLUMN_BOOK_SALEABILITY,
                AuthorEntry.COLUMN_BOOK_BUY_LINK,
                AuthorEntry.COLUMN_BOOK_WEB_READER_LINK
        };

        /*String selection = AuthorEntry._ID + "=?";
        String[] selectionArgs = {String.valueOf(id)};*/

        Cursor cursor = db.query(
                AuthorEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        final ArrayList<Book> BooksItem = new ArrayList<Book>();
        savedAuthorBookListView = findViewById(R.id.saved_authors_list);
        authorAdapter = new AuthorsListAdapter(AuthorsListActivity.this, BooksItem);
        savedAuthorBookListView.setAdapter(authorAdapter);

        try {
            // Figure out the index of each column
            int bookNameColumnIndex = cursor.getColumnIndex(AuthorEntry.COLUMN_Book_NAME);
            int authorNameColumnIndex = cursor.getColumnIndex(AuthorEntry.COLUMN_AUTHOR_NAME);
            int imageColumnIndex = cursor.getColumnIndex(AuthorEntry.COLUMN_BOOK_IMAGE);
            int dateColumnIndex = cursor.getColumnIndex(AuthorEntry.COLUMN_PUBLISHED_DATE);
            int linkColumnIndex = cursor.getColumnIndex(AuthorEntry.COLUMN_BOOK_LINK);
            int descriptionColumnIndex = cursor.getColumnIndex(AuthorEntry.COLUMN_BOOK_DESCRIPTION);
            int saleabilityColumnIndex = cursor.getColumnIndex(AuthorEntry.COLUMN_BOOK_SALEABILITY);
            int buyLinkColumnIndex = cursor.getColumnIndex(AuthorEntry.COLUMN_BOOK_BUY_LINK);
            int webReaderLinkColumnIndex = cursor.getColumnIndex(AuthorEntry.COLUMN_BOOK_WEB_READER_LINK);


            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                String currentBookName = cursor.getString(bookNameColumnIndex);
                String currentAuthorName = cursor.getString(authorNameColumnIndex);
                String currentDate = cursor.getString(dateColumnIndex);
                String currentLink = cursor.getString(linkColumnIndex);
                String currentDescription = cursor.getString(descriptionColumnIndex);
                String currentSaleability = cursor.getString(saleabilityColumnIndex);
                String currentBuyLink = cursor.getString(buyLinkColumnIndex);
                String currentWebReaderLink = cursor.getString(webReaderLinkColumnIndex);
                byte[] currentImage = cursor.getBlob(imageColumnIndex);
                Bitmap bitmap = BitmapFactory.decodeByteArray(currentImage, 0, currentImage.length);
                //Bitmap bitmap1 = ImageDecoder.decodeBitmap(ImageDecoder.createSource(ByteBuffer.wrap(currentImage)));
                BooksItem.add(new Book(currentBookName, bitmap, currentAuthorName, currentDate, currentLink,
                        currentDescription, currentSaleability, currentBuyLink, currentWebReaderLink));

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

}
