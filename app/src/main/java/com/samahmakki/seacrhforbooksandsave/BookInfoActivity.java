package com.samahmakki.seacrhforbooksandsave;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.samahmakki.seacrhforbooksandsave.classes.SharedPref;
import com.samahmakki.seacrhforbooksandsave.data.BookContract.BookEntry;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;

public class BookInfoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ImageView bookImageView;
    TextView bookNameTextView;
    TextView authorNameTextView;
    TextView publishedDateTextView;
    TextView descriptionTextView;
    Button readButton, buyButton, previewButton, downloadButton;

    byte[] bookImage;
    String bookName;
    String authorName;
    String publishedDate;
    String description;
    String saleability;
    String buyLink;
    static String webReaderLink;
    String downloadLink;
    URL webReaderUrl;
    String previewLink;
    SharedPref sharedpref;
    Intent receivedIntent;
    Intent intent_2;
    Bitmap imageBitmap;

    private Uri mCurrentBookUri;
    private InterstitialAd mInterstitialAd;

    private static final int Book_INFO_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(R.string.book_info);

        sharedpref = new SharedPref(this);//load night mode setting
        if (sharedpref.loadNightModeState() == true) {

            setTheme(R.style.darktheme2);
        } else {
            setTheme(R.style.AppTheme2);
        }

        loadLocale();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        bookImageView = findViewById(R.id.book_image_2);
        bookNameTextView = findViewById(R.id.book_name);
        authorNameTextView = findViewById(R.id.author_name);
        publishedDateTextView = findViewById(R.id.published_date);
        descriptionTextView = findViewById(R.id.description);
        readButton = findViewById(R.id.read_button);
        buyButton = findViewById(R.id.buy_button);
        previewButton = findViewById(R.id.preview_button);
        downloadButton = findViewById(R.id.download_button);

        receivedIntent = getIntent();
        mCurrentBookUri = receivedIntent.getData();

        if (mCurrentBookUri == null) {
            bookName = receivedIntent.getStringExtra("bookName");
            bookImage = receivedIntent.getByteArrayExtra("bookImage");
            authorName = receivedIntent.getStringExtra("authorName");
            publishedDate = receivedIntent.getStringExtra("publishedDate");
            description = receivedIntent.getStringExtra("description");
            saleability = receivedIntent.getStringExtra("saleability");
            buyLink = receivedIntent.getStringExtra("buyLink");
            webReaderLink = receivedIntent.getStringExtra("webReaderLink");
            previewLink = receivedIntent.getStringExtra("previewLink");
            downloadLink = receivedIntent.getStringExtra("downloadLink");

            if (bookImage != null) {
                imageBitmap = BitmapFactory.decodeByteArray(bookImage, 0, bookImage.length);
            } else {
                imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_cover_thumb);
            }

            bookImageView.setImageBitmap(imageBitmap);
            bookNameTextView.setText(bookName);
            authorNameTextView.setText(authorName);
            publishedDateTextView.setText(publishedDate);
            descriptionTextView.setText(description);

            if (saleability.equals("FREE")) {
                readButton.setTextColor(getResources().getColor(R.color.black));
                buyButton.setTextColor(getResources().getColor(R.color.gray));
                previewButton.setTextColor(getResources().getColor(R.color.gray));
                downloadButton.setTextColor(getResources().getColor(R.color.black));
                readButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ((webReaderLink != null)) {
                            Uri bookUri = Uri.parse(webReaderLink);
                            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                            startActivity(websiteIntent);
                        } else {
                            Uri bookUri2 = Uri.parse(previewLink);
                            Intent websiteIntent2 = new Intent(Intent.ACTION_VIEW, bookUri2);
                            startActivity(websiteIntent2);
                        }
                    }
                });
                downloadButton.setVisibility(View.VISIBLE);
                downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openOptionsMenu();
                        Uri bookUri3 = Uri.parse(downloadLink);
                        Intent websiteIntent3 = new Intent(Intent.ACTION_VIEW, bookUri3);
                        startActivity(websiteIntent3);
                    }
                });
            } else if (saleability.equals("FOR_SALE")) {
                readButton.setTextColor(getResources().getColor(R.color.gray));
                buyButton.setTextColor(getResources().getColor(R.color.black));
                previewButton.setTextColor(getResources().getColor(R.color.black));
                buyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri bookUri = Uri.parse(buyLink);
                        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                        startActivity(websiteIntent);
                    }
                });

                previewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if ((webReaderLink != null)) {
                            Uri bookUri = Uri.parse(webReaderLink);
                            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                            startActivity(websiteIntent);
                        } else {
                            Uri bookUri = Uri.parse(previewLink);
                            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                            startActivity(websiteIntent);
                        }

                    }
                });
                downloadButton.setVisibility(View.GONE);
            } else if (saleability.equals("NOT_FOR_SALE")) {
                readButton.setTextColor(getResources().getColor(R.color.gray));
                buyButton.setTextColor(getResources().getColor(R.color.gray));
                previewButton.setTextColor(getResources().getColor(R.color.black));
                previewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri bookUri_2 = Uri.parse(previewLink);
                        Intent websiteIntent_2 = new Intent(Intent.ACTION_VIEW, bookUri_2);
                        startActivity(websiteIntent_2);
                    }
                });
                downloadButton.setVisibility(View.GONE);
            }
        } else {
            getSupportLoaderManager().initLoader(Book_INFO_LOADER, null, this);
        }

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

    @Override
    public void onPrepareNavigateUpTaskStack(TaskStackBuilder builder) {
        super.onPrepareNavigateUpTaskStack(builder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        if (mCurrentBookUri == null){
            getMenuInflater().inflate(R.menu.menu_book_info, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_book_info_saved, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.save_only:
                // Create a ContentValues object where column names are the keys,
                // and pet attributes from the editor are the values.
                ContentValues values = new ContentValues();
                values.put(BookEntry.COLUMN_Book_NAME, bookName);
                values.put(BookEntry.COLUMN_AUTHOR_NAME, authorName);
                values.put(BookEntry.COLUMN_BOOK_IMAGE, bookImage);
                values.put(BookEntry.COLUMN_PUBLISHED_DATE, publishedDate);
                values.put(BookEntry.COLUMN_BOOK_LINK, previewLink);
                values.put(BookEntry.COLUMN_BOOK_DESCRIPTION, description);
                values.put(BookEntry.COLUMN_BOOK_SALEABILITY, saleability);
                values.put(BookEntry.COLUMN_BOOK_BUY_LINK, buyLink);
                values.put(BookEntry.COLUMN_BOOK_WEB_READER_LINK, webReaderLink);
                values.put(BookEntry.COLUMN_BOOK_DOWNLOAD_LINK, downloadLink);

                // Insert a new row for pet in the database, returning the ID of that new row.
                Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
                // Show a toast message depending on whether or not the insertion was successful
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, getResources().getString(R.string.save_it_again),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, getResources().getString(R.string.successfully_saved),
                            Toast.LENGTH_SHORT).show();
                }
                return true;

           case R.id.save_with_specific_author:
                intent_2 = new Intent(BookInfoActivity.this, FavoriteAuthorsActivity.class);
                intent_2.putExtra("Tag", "from_book_info");
                intent_2.putExtra("bookImage", bookImage);
                intent_2.putExtra("bookName", bookName);
                intent_2.putExtra("authorName", authorName);
                intent_2.putExtra("publishedDate", publishedDate);
                intent_2.putExtra("description", description);
                intent_2.putExtra("saleability", saleability);
                intent_2.putExtra("buyLink", buyLink);
                intent_2.putExtra("webReaderLink", webReaderLink);
                intent_2.putExtra("previewLink", previewLink);
               intent_2.putExtra("downloadLink", downloadLink);
               startActivity(intent_2);
                return true;
            case R.id.save_to_specific_topic:
                Intent intent = new Intent(BookInfoActivity.this, FavoriteTopicsActivity.class);
                intent.putExtra("Tag", "from_book_info");
                intent.putExtra("bookImage", bookImage);
                intent.putExtra("bookName", bookName);
                intent.putExtra("authorName", authorName);
                intent.putExtra("publishedDate", publishedDate);
                intent.putExtra("description", description);
                intent.putExtra("saleability", saleability);
                intent.putExtra("buyLink", buyLink);
                intent.putExtra("webReaderLink", webReaderLink);
                intent.putExtra("previewLink", previewLink);
                intent.putExtra("downloadLink", downloadLink);
                startActivity(intent);
                return true;
            case R.id.delete:
                showDialogDelete();
                return true;
            case R.id.delete_2:
                showDialogDelete_2();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialogDelete() {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(Objects.requireNonNull(BookInfoActivity.this));
        dialogDelete.setTitle(getResources().getString(R.string.delete_book));
        dialogDelete.setMessage(getResources().getString(R.string.delete_book_msg));
        dialogDelete.setPositiveButton(getResources().getString(R.string.ok)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /**
                         * Perform the deletion of the pet in the database.
                         */
                        // Only perform the delete if this is an existing pet.
                        if (mCurrentBookUri != null) {
                            // Call the ContentResolver to delete the pet at the given content URI.
                            // Pass in null for the selection and selection args because the mCurrentPetUri
                            // content URI already identifies the pet that we want.
                            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
                            // Show a toast message depending on whether or not the delete was successful.
                            if (rowsDeleted == 0) {
                                // If no rows were deleted, then there was an error with the delete.
                                Toast.makeText(BookInfoActivity.this, getResources().getString(R.string.not_deleted)
                                        , Toast.LENGTH_SHORT).show();
                            } else {
                                // Otherwise, the delete was successful and we can display a toast.
                                Toast.makeText(BookInfoActivity.this, getResources().getString(R.string.deleted)
                                        , Toast.LENGTH_SHORT).show();
                                // Close the activity
                                finish();
                            }
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


    private void showDialogDelete_2() {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(Objects.requireNonNull(BookInfoActivity.this));
        dialogDelete.setTitle(getResources().getString(R.string.delete_book));
        dialogDelete.setMessage(getResources().getString(R.string.delete_book_msg));
        dialogDelete.setPositiveButton(getResources().getString(R.string.ok)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /**
                         * Perform the deletion of the pet in the database.
                         */
                        // Only perform the delete if this is an existing pet.
                        if (mCurrentBookUri != null) {
                            // Call the ContentResolver to delete the pet at the given content URI.
                            // Pass in null for the selection and selection args because the mCurrentPetUri
                            // content URI already identifies the pet that we want.
                            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
                            // Show a toast message depending on whether or not the delete was successful.
                            if (rowsDeleted == 0) {
                                // If no rows were deleted, then there was an error with the delete.
                                Toast.makeText(BookInfoActivity.this, getResources().getString(R.string.not_deleted)
                                        , Toast.LENGTH_SHORT).show();
                            } else {
                                // Otherwise, the delete was successful and we can display a toast.
                                Toast.makeText(BookInfoActivity.this, getResources().getString(R.string.deleted)
                                        , Toast.LENGTH_SHORT).show();
                                // Close the activity
                                finish();
                            }
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

        String[] projection = new String[]{
                BookEntry._ID,
                BookEntry.COLUMN_Book_NAME,
                BookEntry.COLUMN_AUTHOR_NAME,
                BookEntry.COLUMN_BOOK_IMAGE,
                BookEntry.COLUMN_PUBLISHED_DATE,
                BookEntry.COLUMN_BOOK_LINK,
                BookEntry.COLUMN_BOOK_DESCRIPTION,
                BookEntry.COLUMN_BOOK_SALEABILITY,
                BookEntry.COLUMN_BOOK_BUY_LINK,
                BookEntry.COLUMN_BOOK_WEB_READER_LINK,
                BookEntry.COLUMN_BOOK_DOWNLOAD_LINK,
        };
        return new CursorLoader(this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int bookNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_Book_NAME);
            int authorNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_AUTHOR_NAME);
            int bookImageColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_IMAGE);
            int publishedDateColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PUBLISHED_DATE);
            int previewLinkColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_LINK);
            int descriptionColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_DESCRIPTION);
            int saleabilityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SALEABILITY);
            int buyLinkColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_BUY_LINK);
            int webReaderLinkColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_WEB_READER_LINK);
            int downloadLinkColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_DOWNLOAD_LINK);

            // Extract out the value from the Cursor for the given column index
            bookName = cursor.getString(bookNameColumnIndex);
            authorName = cursor.getString(authorNameColumnIndex);
            bookImage = cursor.getBlob(bookImageColumnIndex);
            if (bookImage != null) {
                imageBitmap = BitmapFactory.decodeByteArray(bookImage, 0, bookImage.length);
            } else {
                imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_cover_thumb);
            }

            publishedDate = cursor.getString(publishedDateColumnIndex);
            previewLink = cursor.getString(previewLinkColumnIndex);
            description = cursor.getString(descriptionColumnIndex);
            saleability = cursor.getString(saleabilityColumnIndex);
            buyLink = cursor.getString(buyLinkColumnIndex);
            webReaderLink = cursor.getString(webReaderLinkColumnIndex);
            downloadLink = cursor.getString(downloadLinkColumnIndex);

            // Update the views on the screen with the values from the database
            bookNameTextView.setText(bookName);
            authorNameTextView.setText(authorName);
            bookImageView.setImageBitmap(imageBitmap);
            publishedDateTextView.setText(publishedDate);
            descriptionTextView.setText(description);
        }

        if (saleability.equals("FREE")) {
            readButton.setTextColor(getResources().getColor(R.color.black));
            buyButton.setTextColor(getResources().getColor(R.color.gray));
            previewButton.setTextColor(getResources().getColor(R.color.gray));
            readButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((webReaderLink != null)) {
                        Uri bookUri = Uri.parse(webReaderLink);
                        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                        startActivity(websiteIntent);
                    } else {
                        Uri bookUri2 = Uri.parse(previewLink);
                        Intent websiteIntent2 = new Intent(Intent.ACTION_VIEW, bookUri2);
                        startActivity(websiteIntent2);
                    }
                }
            });

            downloadButton.setVisibility(View.VISIBLE);
            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri bookUri3 = Uri.parse(downloadLink);
                    Intent websiteIntent3 = new Intent(Intent.ACTION_VIEW, bookUri3);
                    startActivity(websiteIntent3);
                }
            });

        } else if (saleability.equals("FOR_SALE")) {
            readButton.setTextColor(getResources().getColor(R.color.gray));
            buyButton.setTextColor(getResources().getColor(R.color.black));
            previewButton.setTextColor(getResources().getColor(R.color.black));
            buyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri bookUri = Uri.parse(buyLink);
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                    startActivity(websiteIntent);
                }
            });

            previewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if ((webReaderLink != null)) {
                        Uri bookUri = Uri.parse(webReaderLink);
                        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                        startActivity(websiteIntent);
                    } else {
                        Uri bookUri = Uri.parse(previewLink);
                        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                        startActivity(websiteIntent);
                    }

                }
            });
            downloadButton.setVisibility(View.GONE);

        } else if (saleability.equals("NOT_FOR_SALE")) {
            readButton.setTextColor(getResources().getColor(R.color.gray));
            buyButton.setTextColor(getResources().getColor(R.color.gray));
            previewButton.setTextColor(getResources().getColor(R.color.black));
            previewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri bookUri_2 = Uri.parse(previewLink);
                    Intent websiteIntent_2 = new Intent(Intent.ACTION_VIEW, bookUri_2);
                    startActivity(websiteIntent_2);
                }
            });
            downloadButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }
}
