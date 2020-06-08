package com.samahmakki.seacrhforbooksandsave.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.samahmakki.seacrhforbooksandsave.BookInfoActivity;
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

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View rootView, @Nullable final Bundle savedInstanceState) {

        mEmptyStateTextView2 = rootView.findViewById(R.id.saved_empty_view);
        BookDbHelper mBillHelper = new BookDbHelper(rootView.getContext());
        final SQLiteDatabase db = mBillHelper.getReadableDatabase();
        String[] projection = {
                BookEntry.COLUMN_Book_NAME,
                BookEntry.COLUMN_AUTHOR_NAME,
                BookEntry.COLUMN_BOOK_IMAGE,
                BookEntry.COLUMN_PUBLISHED_DATE,
                BookEntry.COLUMN_BOOK_LINK,
                BookEntry.COLUMN_BOOK_DESCRIPTION,
                BookEntry.COLUMN_BOOK_SALEABILITY,
                BookEntry.COLUMN_BOOK_BUY_LINK,
                BookEntry.COLUMN_BOOK_WEB_READER_LINK
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
            int descriptionColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_DESCRIPTION);
            int saleabilityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SALEABILITY);
            int buyLinkColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_BUY_LINK);
            int webReaderLinkColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_WEB_READER_LINK);


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

                if (currentImage != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(currentImage, 0, currentImage.length);
                    //Bitmap bitmap1 = ImageDecoder.decodeBitmap(ImageDecoder.createSource(ByteBuffer.wrap(currentImage)));
                    BillsItem.add(new Book(currentBookName, bitmap, currentAuthorName, currentDate, currentLink,
                            currentDescription, currentSaleability, currentBuyLink, currentWebReaderLink));
                }

                if (currentImage == null) {
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

                Intent intent = new Intent(getContext(), BookInfoActivity.class);
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
                final PopupMenu popupMenu = new PopupMenu(getContext(), view);
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

        super.onViewCreated(rootView, savedInstanceState);
    }

    private void showDialogDelete(final int i) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        dialogDelete.setTitle(getResources().getString(R.string.delete_book));
        dialogDelete.setMessage(getResources().getString(R.string.delete_book_msg));
        dialogDelete.setPositiveButton(getResources().getString(R.string.ok)
                , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Book currentBook = mAdapter.getItem(i);
                    BookDbHelper bookDbHelper = new BookDbHelper(getContext());
                    bookDbHelper.deleteBook(currentBook.getBookName());
                    mAdapter.remove(currentBook);
                    Toast.makeText(getContext(), getResources().getString(R.string.deleted)
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
