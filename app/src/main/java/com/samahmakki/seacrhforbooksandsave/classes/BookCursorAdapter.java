package com.samahmakki.seacrhforbooksandsave.classes;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.samahmakki.seacrhforbooksandsave.R;
import com.samahmakki.seacrhforbooksandsave.data.BookContract;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView bookNameView = view.findViewById(R.id.book_name);
        ImageView bookImageView = view.findViewById(R.id.book_image);
        TextView authorNameView = view.findViewById(R.id.author_name);
        TextView publishedDateView = view.findViewById(R.id.published_date);

        int bookNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_Book_NAME);
        int authorNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_AUTHOR_NAME);
        int imageColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_IMAGE);
        int dateColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PUBLISHED_DATE);

        String currentBookName = cursor.getString(bookNameColumnIndex);
        String currentAuthorName = cursor.getString(authorNameColumnIndex);
        byte[] currentImage = cursor.getBlob(imageColumnIndex);
        Bitmap bitmap = null;
        if (currentImage != null) {
            bitmap = BitmapFactory.decodeByteArray(currentImage, 0, currentImage.length);
        } else {
            bitmap = null;
        }
        String currentDate = cursor.getString(dateColumnIndex);

        bookNameView.setText(currentBookName);
        bookImageView.setImageBitmap(bitmap);
        authorNameView.setText(currentAuthorName);
        publishedDateView.setText(currentDate);
    }
}
