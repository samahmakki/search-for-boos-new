package com.samahmakki.seacrhforbooksandsave.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.samahmakki.seacrhforbooksandsave.BookInfoActivity;
import com.samahmakki.seacrhforbooksandsave.R;
import com.samahmakki.seacrhforbooksandsave.classes.Book;
import com.samahmakki.seacrhforbooksandsave.classes.BookAdapter;
import com.samahmakki.seacrhforbooksandsave.classes.QueryUtils;
import com.samahmakki.seacrhforbooksandsave.classes.SharedPref;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchBooksFragment extends Fragment {
    Button searchButton;
    EditText keywordEditText;
    String keyword;
    private BookAdapter mAdapter;
    private TextView mEmptyStateTextView;
    ProgressBar loadingIndicator;
    ListView bookListView;
    private String GOOGLE_BOOKS_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes";
    int selectedItem;
    ImageView imageView;
    SharedPref sharedpref;
    Button clear_btn;

    public static Book currentBook;

    public SearchBooksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        sharedpref = new SharedPref(getContext());//load night mode setting

        // loadLocale();

        final View rootView = inflater.inflate(R.layout.fragment_search_books, container, false);

        AdView mAdView = rootView.findViewById(R.id.ad_View);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

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
                if (mAdapter != null)
                mAdapter.clear();
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
                intent.putExtra("Unique","from_search_fragment");
                startActivity(intent);
            }
        });

        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        //(connMgr.getActiveNetwork()!= null && connMgr.isDefaultNetworkActive()
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            BookAsyncTask task = new BookAsyncTask();
            Uri baseUri = Uri.parse(GOOGLE_BOOKS_REQUEST_URL);
            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendQueryParameter("q", keyword)
                    .appendQueryParameter("maxResults", "40")
                    /*.appendQueryParameter("key", getResources().getString(R.string.API_KEY))*/;
            task.execute(uriBuilder.build().toString());
        } else {
            loadingIndicator = getActivity().findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    private class BookAsyncTask extends AsyncTask<String, Void, List<Book>> {
        @Override
        protected List<Book> doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            List<Book> result = QueryUtils.fetchEarthquakeData(urls[0]);
            return result;
        }

        @Override
        protected void onPostExecute(List<Book> data) {
            loadingIndicator = getActivity().findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            mAdapter.clear();
            mEmptyStateTextView.setText(R.string.no_books_found);

            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }
        }
    }
}
