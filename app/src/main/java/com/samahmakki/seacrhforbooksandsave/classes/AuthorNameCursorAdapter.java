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
import com.samahmakki.seacrhforbooksandsave.data.BookContract.AuthorName;

public class AuthorNameCursorAdapter extends CursorAdapter {

    public AuthorNameCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.simple_list_item_3, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView authorNameView = view.findViewById(R.id.author_name);

        int authorNameColumnIndex = cursor.getColumnIndex(AuthorName.COLUMN_AUTHOR_NAME_2);

        String currentAuthorName = cursor.getString(authorNameColumnIndex);
     
        authorNameView.setText(currentAuthorName);
    }
}
