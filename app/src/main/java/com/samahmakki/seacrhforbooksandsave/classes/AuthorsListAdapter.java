package com.samahmakki.seacrhforbooksandsave.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.samahmakki.seacrhforbooksandsave.R;

import java.util.List;

public class AuthorsListAdapter extends ArrayAdapter<Book> {

    public AuthorsListAdapter(Context context, List<Book> books) {
        super(context, 0, books);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.author_list_item, parent, false);
        }

        Book currentBook = getItem(position);

        TextView bookNameView = listItemView.findViewById(R.id.book_name);
        bookNameView.setText(currentBook.getBookName());

        ImageView bookImageView = listItemView.findViewById(R.id.book_image);
        bookImageView.setImageBitmap(currentBook.getBitmap());

        TextView publishedDateView = listItemView.findViewById(R.id.published_date);
        publishedDateView.setText(currentBook.getPublishedDate());

        return listItemView;
    }

}
