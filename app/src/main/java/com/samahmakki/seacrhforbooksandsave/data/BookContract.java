package com.samahmakki.seacrhforbooksandsave.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class BookContract {

    private BookContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY_BOOKS = "com.samahmakki.seacrhforbooksandsave";
    public static final String CONTENT_AUTHORITY_AUTHOR_NAME = "com.samahmakki.seacrhforbooksandsave.data";
    public static final String CONTENT_AUTHORITY_AUTHOR_ENTRY = "com.samahmakki.seacrhforbooksandsave.data.BookContract.AuthorEntry";
    public static final String CONTENT_AUTHORITY_TOPIC_NAME = "com.samahmakki.seacrhforbooksandsave.data.BookContract.TopicName";
    public static final String CONTENT_AUTHORITY_TOPIC_ENTRY = "com.samahmakki.seacrhforbooksandsave.data.BookContract.TopicEntry";


    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY_BOOKS);
    public static final Uri AUTHOR_NAME_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY_AUTHOR_NAME);
    public static final Uri AUTHOR_ENTRY_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY_AUTHOR_ENTRY);
    public static final Uri TOPIC_NAME_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY_TOPIC_NAME);
    public static final Uri TOPIC_ENTRY_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY_TOPIC_ENTRY);


    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.pets/pets/ is a valid path for
     * looking at pet data. content://com.example.android.pets/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_BOOKS = "books";
    public static final String PATH_AUTHOR_NAME = "author_name";
    public static final String PATH_AUTHOR_ENTRY = "author_list";
    public static final String PATH_TOPIC_NAME = "topic_name";
    public static final String PATH_TOPIC_ENTRY = "topic_list";


    public static final class BookEntry implements BaseColumns {

        /** The content URI to access the book data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY_BOOKS + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single book.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY_BOOKS + "/" + PATH_BOOKS;

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
        public final static String COLUMN_BOOK_DOWNLOAD_LINK = "downloadLink";
    }

    public static final class AuthorName implements BaseColumns {

        /** The content URI to access the book data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHOR_NAME_CONTENT_URI, PATH_AUTHOR_NAME);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY_AUTHOR_NAME + "/" + PATH_AUTHOR_NAME;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY_AUTHOR_NAME + "/" + PATH_AUTHOR_NAME;

        public final static String TABLE_NAME = "authors_name";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_AUTHOR_NAME_2 = "author_name";
    }

    public static final class AuthorEntry implements BaseColumns {

        /** The content URI to access the book data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHOR_ENTRY_CONTENT_URI, PATH_AUTHOR_ENTRY);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY_AUTHOR_ENTRY + "/" + PATH_AUTHOR_ENTRY;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY_AUTHOR_ENTRY + "/" + PATH_AUTHOR_ENTRY;

        public final static String TABLE_NAME = "author_list";

        public final static String _ID = BaseColumns._ID;

        public final static String AUTHOR_NUMBER = "position";

        public final static String COLUMN_Book_NAME = "book_name";
        public final static String COLUMN_AUTHOR_NAME = "author_name";
        public final static String COLUMN_BOOK_IMAGE = "image";
        public final static String COLUMN_PUBLISHED_DATE = "date";
        public final static String COLUMN_BOOK_LINK = "link";
        public final static String COLUMN_BOOK_DESCRIPTION = "description";
        public final static String COLUMN_BOOK_SALEABILITY = "saleability";
        public final static String COLUMN_BOOK_BUY_LINK = "buyLink";
        public final static String COLUMN_BOOK_WEB_READER_LINK = "webReaderLink";
        public final static String COLUMN_BOOK_DOWNLOAD_LINK = "downloadLink";
    }

    public static final class TopicName implements BaseColumns {
        /** The content URI to access the book data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(TOPIC_NAME_CONTENT_URI, PATH_TOPIC_NAME);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY_TOPIC_NAME + "/" + PATH_TOPIC_NAME;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY_TOPIC_NAME + "/" + PATH_TOPIC_NAME;

        public final static String TABLE_NAME = "topics_name";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_TOPIC_NAME_2 = "topic_name";
    }

    public static final class TopicEntry implements BaseColumns {

        /** The content URI to access the book data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(TOPIC_ENTRY_CONTENT_URI, PATH_TOPIC_ENTRY);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY_TOPIC_ENTRY + "/" + PATH_TOPIC_ENTRY;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY_TOPIC_ENTRY + "/" + PATH_TOPIC_ENTRY;

        public final static String TABLE_NAME = "topic_list";

        public final static String _ID = BaseColumns._ID;

        public final static String TOPIC_NUMBER = "position";

        public final static String COLUMN_Book_NAME = "book_name";
        public final static String COLUMN_AUTHOR_NAME = "author_name";
        public final static String COLUMN_BOOK_IMAGE = "image";
        public final static String COLUMN_PUBLISHED_DATE = "date";
        public final static String COLUMN_BOOK_LINK = "link";
        public final static String COLUMN_BOOK_DESCRIPTION = "description";
        public final static String COLUMN_BOOK_SALEABILITY = "saleability";
        public final static String COLUMN_BOOK_BUY_LINK = "buyLink";
        public final static String COLUMN_BOOK_WEB_READER_LINK = "webReaderLink";
        public final static String COLUMN_BOOK_DOWNLOAD_LINK = "downloadLink";
    }
}