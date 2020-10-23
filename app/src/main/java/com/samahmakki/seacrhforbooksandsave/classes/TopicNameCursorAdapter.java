package com.samahmakki.seacrhforbooksandsave.classes;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.samahmakki.seacrhforbooksandsave.R;
import com.samahmakki.seacrhforbooksandsave.data.BookContract.TopicName;

public class TopicNameCursorAdapter extends CursorAdapter {

    public TopicNameCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.simple_list_item_2, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView topicNameView = view.findViewById(R.id.topic_name);

        int topicNameColumnIndex = cursor.getColumnIndex(TopicName.COLUMN_TOPIC_NAME_2);

        String currentTopicName = cursor.getString(topicNameColumnIndex);

        topicNameView.setText(currentTopicName);
    }
}
