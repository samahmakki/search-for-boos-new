package com.samahmakki.seacrhforbooksandsave.data;

import android.provider.BaseColumns;

public class BookContract {

    private BookContract() {
    }

    public static final class BookEntry implements BaseColumns {

        public final static String TABLE_NAME = "books";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_Book_NAME = "book_name";
        public final static String COLUMN_AUTHOR_NAME = "author_name";
        public final static String COLUMN_BOOK_IMAGE = "image";
        public final static String COLUMN_PUBLISHED_DATE = "date";
        public final static String COLUMN_BOOK_LINK = "link";
        public final static String COLUMN_BOOK_DESCRIPTION = "description";
        public final static String COLUMN_BOOK_SALEABILITY = "saleability";
        public final static String COLUMN_BOOK_BUY_LINK = "buyLink";
        public final static String COLUMN_BOOK_WEB_READER_LINK = "webReaderLink";
    }

    public static final class AuthorName implements BaseColumns {

        public final static String TABLE_NAME = "author_name";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_AUTHOR_NAME = "author_name";
    }

    public static final class Topic implements BaseColumns {

        public final static String TABLE_NAME = "topics";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_TOPIC = "topic";
    }

    public static final class AuthorEntry implements BaseColumns {

        public final static String TABLE_NAME = "authors";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_Book_NAME = "book_name";
        public final static String COLUMN_AUTHOR_NAME = "author_name";
        public final static String COLUMN_BOOK_IMAGE = "image";
        public final static String COLUMN_PUBLISHED_DATE = "date";
        public final static String COLUMN_BOOK_LINK = "link";
        public final static String COLUMN_BOOK_DESCRIPTION = "description";
        public final static String COLUMN_BOOK_SALEABILITY = "saleability";
        public final static String COLUMN_BOOK_BUY_LINK = "buyLink";
        public final static String COLUMN_BOOK_WEB_READER_LINK = "webReaderLink";
    }
}
