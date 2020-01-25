package com.samahmakki.seacrhforbooksandsave.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.samahmakki.seacrhforbooksandsave.R;
import com.samahmakki.seacrhforbooksandsave.classes.Book;
import com.samahmakki.seacrhforbooksandsave.classes.BookAdapter;
import com.samahmakki.seacrhforbooksandsave.data.BookDbHelper;
import com.samahmakki.seacrhforbooksandsave.data.BookContract.BookEntry;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class SavedBooksFragment extends Fragment {
    ListView savedBookListView;
    private BookAdapter mAdapter;
    private TextView mEmptyStateTextView2;
    int selectedItem;
    Intent intent;

    public SavedBooksFragment() {
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Refresh your fragment here
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_saved_books, container, false);

        AdView mAdView = rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View rootView, @Nullable Bundle savedInstanceState) {

        mEmptyStateTextView2 = rootView.findViewById(R.id.saved_empty_view);
        BookDbHelper mBillHelper = new BookDbHelper(rootView.getContext());


        final SQLiteDatabase db = mBillHelper.getReadableDatabase();
        String[] projection = {
                BookEntry.COLUMN_Book_NAME,
                BookEntry.COLUMN_AUTHOR_NAME,
                BookEntry.COLUMN_BOOK_IMAGE,
                BookEntry.COLUMN_PUBLISHED_DATE,
                BookEntry.COLUMN_BOOK_LINK
        };

        Cursor cursor = db.query(
                BookEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        final ArrayList<Book> BillsItem = new ArrayList<Book>();
        savedBookListView = rootView.findViewById(R.id.saved_list);
        mAdapter = new BookAdapter(getContext(), BillsItem);
        savedBookListView.setAdapter(mAdapter);


        try {
            // Figure out the index of each column
            int bookNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_Book_NAME);
            int authorNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_AUTHOR_NAME);
            int imageColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_IMAGE);
            int dateColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PUBLISHED_DATE);
            int linkColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_LINK);


            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                String currentBookName = cursor.getString(bookNameColumnIndex);
                String currentAuthorName = cursor.getString(authorNameColumnIndex);
                byte[] currentImage = cursor.getBlob(imageColumnIndex);

                Bitmap bitmap = BitmapFactory.decodeByteArray(currentImage, 0, currentImage.length);

                String currentDate = cursor.getString(dateColumnIndex);
                String currentLink = cursor.getString(linkColumnIndex);

                BillsItem.add(new Book(currentBookName, bitmap, currentAuthorName, currentDate, currentLink));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }


        savedBookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final PopupMenu popupMenu = new PopupMenu(rootView.getContext(), view);
                popupMenu.inflate(R.menu.pop_up_menu_saved);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        selectedItem = item.getItemId();
                        if (selectedItem == R.id.read) {
                            Book currentBook = mAdapter.getItem(position);
                            Uri bookUri = Uri.parse(currentBook.getLink());
                            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                            startActivity(websiteIntent);

                        } else if (selectedItem == R.id.delete) {
                            showDialogDelete(position);
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        savedBookListView.setEmptyView(mEmptyStateTextView2);
        mEmptyStateTextView2.setText("No Saved Books Yet...");

        super.onViewCreated(rootView, savedInstanceState);
    }

    private void showDialogDelete(final int i) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        dialogDelete.setTitle("Delete Book?");
        dialogDelete.setMessage("Are you sure you want delete this Book?");
        dialogDelete.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Book currentBook = mAdapter.getItem(i);
                    BookDbHelper bookDbHelper = new BookDbHelper(getContext());
                    bookDbHelper.deleteBook(currentBook.getBookName());
                    mAdapter.remove(currentBook);
                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Log.e("error",e.getMessage());
                }

            }
        });
        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }


}
