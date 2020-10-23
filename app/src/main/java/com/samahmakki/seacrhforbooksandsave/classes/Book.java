package com.samahmakki.seacrhforbooksandsave.classes;

import android.graphics.Bitmap;

public class Book {
    private String mBookName;
    private String mAuthorName;
    private Bitmap mImage;
    private String mPublishedDate;
    private String mLink;
    private String mDescription;
    private String mSaleability;
    private String mBuyLink;
    private String mWebReaderLink;
    private String mDownloadLink;

    public Book(String bookName, Bitmap image, String authorName, String publishedDate, String link, String description,
                String saleability, String buyLink, String webReaderLink, String downloadLink){
        mBookName = bookName;
        mAuthorName = authorName;
        mPublishedDate = publishedDate;
        mLink = link;
        mImage = image;
        mDescription = description;
        mSaleability = saleability;
        mBuyLink = buyLink;
        mWebReaderLink = webReaderLink;
        mDownloadLink = downloadLink;
    }

    public String getBookName(){
        return mBookName;
    }

    public String getAuthorName(){
        return mAuthorName;
    }

    public Bitmap getBitmap(){
        return mImage;
    }

    public String getPublishedDate(){
        return mPublishedDate;
    }

    public String getLink(){
        return mLink;
    }

    public String getDescription(){
        return mDescription;
    }

    public String getSaleability(){
        return mSaleability;
    }

    public String getBuyLink(){
        return mBuyLink;
    }

    public String getWebReaderLink(){
        return mWebReaderLink;
    }

    public String getDownloadLink(){
        return mDownloadLink;
    }
}
