package com.samahmakki.seacrhforbooksandsave.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.samahmakki.seacrhforbooksandsave.BookInfoActivity;
import com.samahmakki.seacrhforbooksandsave.R;
import com.samahmakki.seacrhforbooksandsave.classes.Book;
import com.samahmakki.seacrhforbooksandsave.classes.BookAdapter;
import com.samahmakki.seacrhforbooksandsave.classes.BookLoader;
import com.samahmakki.seacrhforbooksandsave.classes.QueryUtils;
import com.samahmakki.seacrhforbooksandsave.classes.SharedPref;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchBooksFragment_2 extends Fragment implements LoaderManager.LoaderCallbacks<List<Book>> {
    Button searchButton;
    EditText keywordEditText;
    String keyword;
    private BookAdapter mAdapter;
    private TextView mEmptyStateTextView;
    ProgressBar loadingIndicator;
    ListView bookListView;
    private String GOOGLE_BOOKS_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes";
    ImageView imageView;
    SharedPref sharedpref;
    Button clear_btn;
    byte[] byteArray;
    Bitmap bookImage;

    private static final int BOOK_LOADER_ID = 1;

    public SearchBooksFragment_2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        sharedpref = new SharedPref(getContext());//load night mode setting

        final View rootView = inflater.inflate(R.layout.fragment_search_books, container, false);

       /* AdView mAdView = rootView.findViewById(R.id.ad_View);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
*/
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View rootView, @Nullable Bundle savedInstanceState) {

        imageView = rootView.findViewById(R.id.image_view);

        mEmptyStateTextView = rootView.findViewById(R.id.empty_view);

        searchButton = rootView.findViewById(R.id.search_button);
        keywordEditText = rootView.findViewById(R.id.keyword);
        clear_btn = rootView.findViewById(R.id.clear_txt);

        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keywordEditText.setText("");
                if (mAdapter != null){
                    mAdapter.clear();
                    mEmptyStateTextView.setText(R.string.no_books_found);
                }
            }
        });

        keywordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //performSearch();
                    Search();
                    keywordEditText.clearFocus();
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(keywordEditText.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        keywordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    searchButton.setEnabled(true);
                } else {
                    searchButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = keywordEditText.getText().length();
                if (length > 0){
                     clear_btn.setVisibility(View.VISIBLE);
                     clear_btn.setBackgroundResource(R.drawable.ic_clear_black_24dp);
                }
                else {
                    clear_btn.setVisibility(View.GONE);
                }
            }
        });


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Search();
            }
        });
    }

    private void Search() {
        mEmptyStateTextView = getActivity().findViewById(R.id.empty_view);
        loadingIndicator = getActivity().findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);
        keyword = keywordEditText.getText().toString();
        bookListView = getActivity().findViewById(R.id.list);
        mAdapter = new BookAdapter(getContext(), new ArrayList<Book>());

        bookListView.setAdapter(mAdapter);

        bookListView.setEmptyView(mEmptyStateTextView);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, final long id) {

                Book book = (Book) bookListView.getItemAtPosition(position);
                bookImage = book.getBitmap();
                String bookName = book.getBookName();
                String authorName = book.getAuthorName();
                String publishedDate = book.getPublishedDate();
                String description = book.getDescription();
                String saleability = book.getSaleability();
                String buyLink = book.getBuyLink();
                String webReaderLink = book.getWebReaderLink();
                String previewLink = book.getLink();
                String downloadLink = book.getDownloadLink();

                Intent intent = new Intent(getContext(), BookInfoActivity.class);
                if (bookImage != null){
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bookImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byteArray = stream.toByteArray();
                } else {
                    byteArray = null;
                    /*Bitmap bookImage_2 = BitmapFactory.decodeResource(getResources(), R.drawable.no_cover_thumb);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bookImage_2.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byteArray = stream.toByteArray();*/
                }

                intent.putExtra("bookImage", byteArray);
                intent.putExtra("bookName", bookName);
                intent.putExtra("authorName", authorName);
                intent.putExtra("publishedDate", publishedDate);
                intent.putExtra("description", description);
                intent.putExtra("saleability", saleability);
                intent.putExtra("buyLink", buyLink);
                intent.putExtra("webReaderLink", webReaderLink);
                intent.putExtra("previewLink", previewLink);
                intent.putExtra("downloadLink", downloadLink);
                intent.putExtra("Unique","from_search_fragment");
                startActivity(intent);
            }
        });

        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        //(connMgr.getActiveNetwork()!= null && connMgr.isDefaultNetworkActive()
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {
            loadingIndicator = getActivity().findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
      //  cursor.setNotificationUri(getContext().getContentResolver(), uri);
    }

    @NonNull
    @Override
    public Loader<List<Book>> onCreateLoader(int id, @Nullable Bundle args) {
        // Create a new loader for the given URL
        Uri baseUri = Uri.parse(GOOGLE_BOOKS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("q", keyword)
                .appendQueryParameter("maxResults", "40");
        return new BookLoader(getContext(), uriBuilder.build().toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Book>> loader, List<Book> data) {
        loadingIndicator = getActivity().findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mAdapter.clear();

        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }

        mEmptyStateTextView.setText(R.string.no_books_found);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Book>> loader) {
        mAdapter.clear();
    }
}
