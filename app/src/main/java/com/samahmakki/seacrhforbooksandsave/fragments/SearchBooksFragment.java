package com.samahmakki.seacrhforbooksandsave.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.samahmakki.seacrhforbooksandsave.R;
import com.samahmakki.seacrhforbooksandsave.classes.Book;
import com.samahmakki.seacrhforbooksandsave.classes.BookAdapter;
import com.samahmakki.seacrhforbooksandsave.classes.QueryUtils;
import com.samahmakki.seacrhforbooksandsave.data.BookDbHelper;

import java.util.ArrayList;
import java.util.List;

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

    public SearchBooksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_search_books, container, false);


        AdView mAdView = rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View rootView, @Nullable Bundle savedInstanceState) {

        mEmptyStateTextView = rootView.findViewById(R.id.empty_view);

        searchButton = rootView.findViewById(R.id.search_button);
        keywordEditText = rootView.findViewById(R.id.keyword);

        keywordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //performSearch();

                    loadingIndicator = rootView.findViewById(R.id.loading_indicator);
                    loadingIndicator.setVisibility(View.VISIBLE);
                    keyword = keywordEditText.getText().toString();
                    bookListView = rootView.findViewById(R.id.list);
                    mAdapter = new BookAdapter(getContext(), new ArrayList<Book>());

                    bookListView.setAdapter(mAdapter);

                    bookListView.setEmptyView(mEmptyStateTextView);

                    bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            final PopupMenu popupMenu = new PopupMenu(rootView.getContext(), view);
                            popupMenu.inflate(R.menu.pop_up_menu);
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    selectedItem = item.getItemId();
                                    if (selectedItem == R.id.read) {
                                        Book currentBook = mAdapter.getItem(position);
                                        Uri bookUri = Uri.parse(currentBook.getLink());
                                        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                                        startActivity(websiteIntent);

                                    } else if (selectedItem == R.id.save) {
                                        Book currentBook = mAdapter.getItem(position);
                                        BookDbHelper bookDbHelper = new BookDbHelper(getContext());
                                        long newRowId = bookDbHelper.insertBook(currentBook.getBookName(), currentBook.getAuthorName(),
                                                currentBook.getBitmap(), currentBook.getPublishedDate(), currentBook.getLink());
                                        if (newRowId == -1) {
                                            Toast.makeText(getContext(), "Save it again", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(getContext(), "Successfully saved", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    return true;
                                }
                            });
                            popupMenu.show();
                        }
                    });


                    ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        BookAsyncTask task = new BookAsyncTask();
                        Uri baseUri = Uri.parse(GOOGLE_BOOKS_REQUEST_URL);
                        Uri.Builder uriBuilder = baseUri.buildUpon();
                        uriBuilder.appendQueryParameter("q", keyword);
                        uriBuilder.appendQueryParameter("maxResults", "40");
                        task.execute(uriBuilder.toString());
                    } else {
                        loadingIndicator = rootView.findViewById(R.id.loading_indicator);
                        loadingIndicator.setVisibility(View.GONE);
                        mEmptyStateTextView.setText(R.string.no_internet_connection);
                    }

                    keywordEditText.clearFocus();
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(keywordEditText.getWindowToken(), 0);

                    return true;

                }
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmptyStateTextView = rootView.findViewById(R.id.empty_view);
                loadingIndicator = rootView.findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.VISIBLE);
                keyword = keywordEditText.getText().toString();
                bookListView = rootView.findViewById(R.id.list);
                mAdapter = new BookAdapter(getContext(), new ArrayList<Book>());

                bookListView.setAdapter(mAdapter);

                bookListView.setEmptyView(mEmptyStateTextView);

                bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        final PopupMenu popupMenu = new PopupMenu(rootView.getContext(), view);
                        popupMenu.inflate(R.menu.pop_up_menu);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                selectedItem = item.getItemId();
                                if (selectedItem == R.id.read) {
                                    Book currentBook = mAdapter.getItem(position);
                                    Uri bookUri = Uri.parse(currentBook.getLink());
                                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                                    startActivity(websiteIntent);

                                } else if (selectedItem == R.id.save) {
                                    Book currentBook = mAdapter.getItem(position);
                                    BookDbHelper bookDbHelper = new BookDbHelper(getContext());
                                    long newRowId = bookDbHelper.insertBook(currentBook.getBookName(), currentBook.getAuthorName(),
                                            currentBook.getBitmap(), currentBook.getPublishedDate(), currentBook.getLink());
                                    if (newRowId == -1) {
                                        Toast.makeText(getContext(), "Save it again", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(getContext(), "Successfully saved", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                    }
                });


                ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    BookAsyncTask task = new BookAsyncTask();
                    Uri baseUri = Uri.parse(GOOGLE_BOOKS_REQUEST_URL);
                    Uri.Builder uriBuilder = baseUri.buildUpon();
                    uriBuilder.appendQueryParameter("q", keyword);
                    uriBuilder.appendQueryParameter("maxResults", "40");
                    task.execute(uriBuilder.toString());
                } else {
                    loadingIndicator = rootView.findViewById(R.id.loading_indicator);
                    loadingIndicator.setVisibility(View.GONE);
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                }
            }
        });

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
