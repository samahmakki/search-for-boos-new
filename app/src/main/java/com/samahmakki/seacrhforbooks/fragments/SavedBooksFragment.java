package com.samahmakki.seacrhforbooks.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.samahmakki.seacrhforbooks.R;
import com.samahmakki.seacrhforbooks.classes.Book;
import com.samahmakki.seacrhforbooks.classes.BookAdapter;
import com.samahmakki.seacrhforbooks.data.BookDbHelper;
import com.samahmakki.seacrhforbooks.data.BookContract.BookEntry;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SavedBooksFragment extends Fragment {
    ListView savedBookListView;
    private BookAdapter mAdapter;
    private TextView mEmptyStateTextView2;
    int selectedItem;


    public SavedBooksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_saved_books, container, false);
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


        savedBookListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final PopupMenu popupMenu = new PopupMenu(rootView.getContext(), view);
                popupMenu.inflate(R.menu.pop_up_menu_saved);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        selectedItem = item.getItemId();
                        if (selectedItem == R.id.read) {
                            Book currentBook = mAdapter.getItem(i);
                            Uri bookUri = Uri.parse(currentBook.getLink());
                            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                            startActivity(websiteIntent);

                        } else if (selectedItem == R.id.delete) {
                            Book currentBook = mAdapter.getItem(i);
                            BookDbHelper bookDbHelper = new BookDbHelper(getContext());
                            bookDbHelper.deleteBook(currentBook.getBookName());
                            mAdapter.remove(currentBook);
                            Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
                popupMenu.show();
                return true;
            }
        });
        savedBookListView.setEmptyView(mEmptyStateTextView2);
        mEmptyStateTextView2.setText("No Saved Books Yet...");

        return rootView;
    }

}
