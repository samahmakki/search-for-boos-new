package com.samahmakki.seacrhforbooksandsave;

import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.samahmakki.seacrhforbooksandsave.classes.SharedPref;
import com.samahmakki.seacrhforbooksandsave.data.BookDbHelper;
import com.samahmakki.seacrhforbooksandsave.data.BookDbHelper_2;
import com.samahmakki.seacrhforbooksandsave.fragments.SavedBooksFragment;
import com.samahmakki.seacrhforbooksandsave.fragments.SearchBooksFragment;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class BookInfoActivity extends AppCompatActivity {

    ImageView bookImageView;
    TextView bookNameTextView;
    TextView authorNameTextView;
    TextView publishedDateTextView;
    TextView descriptionTextView;
    Button readButton, buyButton, previewButton;

    Bitmap bookImage;
    String bookName;
    String authorName;
    String publishedDate;
    String description;

    String saleability;
    String buyLink;
    static String webReaderLink;
    URL webReaderUrl;

    String previewLink;

    SharedPref sharedpref;
    Intent receivedIntent;
    Intent intent_2;
    public static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(R.string.book_info);

        sharedpref = new SharedPref(this);//load night mode setting
        if (sharedpref.loadNightModeState() == true) {

            setTheme(R.style.darktheme2);
        } else {
            setTheme(R.style.AppTheme2);
        }

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

        receivedIntent = getIntent();

        bookImage = receivedIntent.getParcelableExtra("bookImage");

        if (bookImage != null) {
            bookImageView.setImageBitmap(bookImage);
        }
        if (bookImage == null) {
            Bitmap bitmap = null;
            bookImageView.setImageBitmap(bitmap);
            bookImageView.setVisibility(View.GONE);
        }
        bookName = receivedIntent.getStringExtra("bookName");
        bookNameTextView.setText(bookName);
        authorName = receivedIntent.getStringExtra("authorName");
        authorNameTextView.setText(authorName);
        publishedDate = receivedIntent.getStringExtra("publishedDate");
        publishedDateTextView.setText(publishedDate);
        description = receivedIntent.getStringExtra("description");
        descriptionTextView.setText(description);

        saleability = receivedIntent.getStringExtra("saleability");
        buyLink = receivedIntent.getStringExtra("buyLink");
        webReaderLink = receivedIntent.getStringExtra("webReaderLink");
        previewLink = receivedIntent.getStringExtra("previewLink");

        try {
            webReaderUrl = new URL(webReaderLink);
        } catch (MalformedURLException e) {
            e.printStackTrace();
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
        }
    }

    @Override
    public void onPrepareNavigateUpTaskStack(TaskStackBuilder builder) {
        super.onPrepareNavigateUpTaskStack(builder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        if (receivedIntent != null) {
            String strdata = receivedIntent.getExtras().getString("Unique");
            if (strdata.equals("from_search_fragment")) {
                getMenuInflater().inflate(R.menu.menu_book_info, menu);
            }
            if (strdata.equals("from_saved_fragment")) {
                getMenuInflater().inflate(R.menu.menu_book_info_saved, menu);
            }
           /* if (strdata.equals("from_authors_list_activity")) {
                getMenuInflater().inflate(R.menu.menu_book_saved_list, menu);
            }*/
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.save_only:
                BookDbHelper bookDbHelper = new BookDbHelper(BookInfoActivity.this);
                long newRowId = bookDbHelper.insertBook(bookName, authorName,
                        bookImage, publishedDate, previewLink, description, saleability, buyLink, webReaderLink);
                if (newRowId == -1) {
                    Toast.makeText(BookInfoActivity.this, getResources().getString(R.string.save_it_again)
                            , Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(BookInfoActivity.this, getResources().getString(R.string.successfully_saved)
                            , Toast.LENGTH_SHORT).show();
                }
                return true;

            /*case R.id.save_with_specific_author:
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
                startActivityForResult(intent_2, RC_SIGN_IN);
                return true;
            case R.id.save_to_specific_topic:
                // Do nothing for now
                return true;*/
            case R.id.delete:
                showDialogDelete();
                return true;
            /*case R.id.delete_2:
                showDialogDelete_2();
                return true;*/
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
                try {
                    BookDbHelper bookDbHelper = new BookDbHelper(BookInfoActivity.this);
                    bookDbHelper.deleteBook(bookName);
                    Intent intent = new Intent(BookInfoActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(BookInfoActivity.this, getResources().getString(R.string.deleted)
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


    private void showDialogDelete_2() {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(Objects.requireNonNull(BookInfoActivity.this));
        dialogDelete.setTitle(getResources().getString(R.string.delete_book));
        dialogDelete.setMessage(getResources().getString(R.string.delete_book_msg));
        dialogDelete.setPositiveButton(getResources().getString(R.string.ok)
                , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    BookDbHelper_2 bookDbHelper = new BookDbHelper_2(BookInfoActivity.this);
                    bookDbHelper.deleteBook(bookName);
                    Intent intent = new Intent(BookInfoActivity.this, SavedBooksActivity.class);
                    startActivity(intent);
                    Toast.makeText(BookInfoActivity.this, getResources().getString(R.string.deleted)
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

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }*/

}
