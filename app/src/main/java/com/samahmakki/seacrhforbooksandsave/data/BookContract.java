package com.samahmakki.seacrhforbooksandsave.data;

import android.provider.BaseColumns;

public class BookContract {

    private BookContract() {
    }

    public static final class BookEntry implements BaseColumns {

        public final static String TABLE_NAME = "books";


        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_Book_NAME ="book_name";
        public final static String COLUMN_AUTHOR_NAME ="author_name";
        public final static String COLUMN_BOOK_IMAGE ="image";
        public final static String COLUMN_PUBLISHED_DATE ="date";
        public final static String COLUMN_BOOK_LINK ="link";
    }
}
