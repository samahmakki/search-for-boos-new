package com.samahmakki.seacrhforbooksandsave.fragments;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.samahmakki.seacrhforbooksandsave.BookInfoActivity;
import com.samahmakki.seacrhforbooksandsave.R;
import com.samahmakki.seacrhforbooksandsave.SavedBooksActivity;
import com.samahmakki.seacrhforbooksandsave.classes.Book;
import com.samahmakki.seacrhforbooksandsave.classes.BookAdapter;
import com.samahmakki.seacrhforbooksandsave.classes.BookCursorAdapter;
import com.samahmakki.seacrhforbooksandsave.data.BookDbHelper;
import com.samahmakki.seacrhforbooksandsave.data.BookContract.BookEntry;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class SavedBooksFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    ListView savedBookListView;
    private BookAdapter mAdapter;
    private TextView mEmptyStateTextView2;
    int selectedItem;
    BookCursorAdapter mCursorAdapter;
    private static final int Book_LOADER = 0;

    public SavedBooksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_saved_books, container, false);

        getActivity().getSupportLoaderManager().initLoader(Book_LOADER, null, this);

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View rootView, @Nullable final Bundle savedInstanceState) {

        savedBookListView = rootView.findViewById(R.id.saved_list);

        mEmptyStateTextView2 = rootView.findViewById(R.id.saved_empty_view);
        savedBookListView.setEmptyView(mEmptyStateTextView2);
        mEmptyStateTextView2.setText(getResources().getString(R.string.no_Saved_Books_Yet));

        mCursorAdapter = new BookCursorAdapter(getContext(), null);
        savedBookListView.setAdapter(mCursorAdapter);

        savedBookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                Intent intent = new Intent(getContext(), BookInfoActivity.class);

                Uri contentUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                intent.setData(contentUri);
                intent.putExtra("Unique", "from_saved_fragment");
                startActivity(intent);
            }
        });

        savedBookListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                final PopupMenu popupMenu = new PopupMenu(getContext(), view);
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

        super.onViewCreated(rootView, savedInstanceState);
    }

    private void showDialogDelete(final long i) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        dialogDelete.setTitle(getResources().getString(R.string.delete_book));
        dialogDelete.setMessage(getResources().getString(R.string.delete_book_msg));
        dialogDelete.setPositiveButton(getResources().getString(R.string.ok)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Uri contentUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, i);
                            int rowsDeleted = getActivity().getContentResolver().delete(contentUri, null, null);
                            // Show a toast message depending on whether or not the delete was successful.
                            if (rowsDeleted == 0) {
                                // If no rows were deleted, then there was an error with the delete.
                                Toast.makeText(getContext(), getResources().getString(R.string.not_deleted)
                                        , Toast.LENGTH_SHORT).show();
                            } else {
                                // Otherwise, the delete was successful and we can display a toast.
                                Toast.makeText(getContext(), getResources().getString(R.string.deleted)
                                        , Toast.LENGTH_SHORT).show();
                            }
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

    private void showDialogDeleteAll() {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        dialogDelete.setTitle(getResources().getString(R.string.delete_all_books));
        dialogDelete.setMessage(getResources().getString(R.string.delete_all_books_msg));
        dialogDelete.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Respond to a click on the "Insert dummy data" menu option
                int rowsDeleted = getActivity().getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
                // Show a toast message depending on whether or not the delete was successful.
                if (rowsDeleted == 0) {
                    // If no rows were deleted, then there was an error with the delete.
                    Toast.makeText(getContext(), getResources().getString(R.string.not_deleted_all_books)
                            , Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the delete was successful and we can display a toast.
                    Toast.makeText(getContext(), getResources().getString(R.string.deleted_all_books)
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_delete_all, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
                BookEntry.COLUMN_BOOK_LINK,
                BookEntry.COLUMN_BOOK_DESCRIPTION,
                BookEntry.COLUMN_BOOK_SALEABILITY,
                BookEntry.COLUMN_BOOK_BUY_LINK,
                BookEntry.COLUMN_BOOK_WEB_READER_LINK,
        };

        return new CursorLoader(getContext(),
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
