package com.samahmakki.seacrhforbooksandsave.classes;

import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PathDashPathEffect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.RelativeDateTimeFormatter;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.support.v4.media.RatingCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.BitmapCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.gms.common.internal.ResourceUtils;
import com.samahmakki.seacrhforbooksandsave.MainActivity;
import com.samahmakki.seacrhforbooksandsave.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public final class QueryUtils {
    public static final String LOG_TAG = MainActivity.class.getName();

    private static String authorName;
    static Bitmap image;
    private static String bookName;
    private static String publishedDate;
    private static String previewLink;
    private static String description;
    private static String saleability;
    private static String buyLink;
    private static String webReaderLink;
    private static String downloadLink;
    private String url = "https://photos.google.com/photo/AF1QipPe2HYMc948ZrWFq465IKaopV3i_6xbO3TLaUpz";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Book> extractFeatureFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<Book> books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of Earthquake objects with the corresponding data.
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            JSONArray itemsArray = null;
            if (baseJsonResponse.has("items")) {
                itemsArray = baseJsonResponse.getJSONArray("items");

                for (int i = 0; i < itemsArray.length(); i++) {

                    JSONObject currentBook = itemsArray.getJSONObject(i);

                    JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                    bookName = null;
                    if ((volumeInfo.has("title"))) {
                        bookName = volumeInfo.getString("title");
                    } else {
                        bookName = null;
                    }

                    description = null;
                    if (volumeInfo.has("description")) {
                        description = volumeInfo.getString("description");
                    } else {
                        // description = Resources.getSystem().getString(R.string.no_description_available);
                        description = "No description available.";
                    }

                    JSONObject bookImage;
                    if (volumeInfo.has("imageLinks")) {
                        bookImage = volumeInfo.getJSONObject("imageLinks");

                        String smallThumbnail;
                        if (bookImage.has("smallThumbnail")) {
                            smallThumbnail = bookImage.getString("smallThumbnail");
                            StringBuilder sb = new StringBuilder(smallThumbnail);
                            if (sb.charAt(4) != 's') {
                                sb.insert(4, 's');
                                String sb_2 = sb.toString();
                                URL url = new URL(sb_2);
                                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            }
                        }
                    } else {
                        // image = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.no_cover_thumb);

                        //URL url = new URL("https://photos.google.com/photo/AF1QipPe2HYMc948ZrWFq465IKaopV3i_6xbO3TLaUpz");
                        //image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        image = null;
                    }

                    JSONArray authorsArray = null;
                    if (volumeInfo.has("authors")) {
                        authorsArray = volumeInfo.getJSONArray("authors");
                        for (int j = 0; j < authorsArray.length(); j++) {
                            if (j == 0) {
                                authorName = authorsArray.getString(j);
                            } else
                                authorName = authorName + ", " + authorsArray.getString(j);
                        }
                    } else {
                        authorName = null;
                    }

                    publishedDate = null;
                    if (volumeInfo.has("publishedDate")) {
                        publishedDate = volumeInfo.getString("publishedDate");
                    } else {
                        publishedDate = "No specific date";
                    }

                    previewLink = null;
                    if (volumeInfo.has("previewLink")) {
                        previewLink = volumeInfo.getString("previewLink");
                        try {
                            StringBuilder pLink = new StringBuilder(previewLink);
                            if (pLink.charAt(4) != 's') {
                                pLink.insert(4, 's');
                                pLink.append("f=true");
                                previewLink = pLink.toString();
                            }
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    } else {
                        previewLink = null;
                    }

                    buyLink = null;
                    saleability = null;
                    if (currentBook.has("saleInfo")) {
                        JSONObject saleInfo = currentBook.getJSONObject("saleInfo");
                        if (saleInfo.has("saleability")) {
                            saleability = saleInfo.getString("saleability");
                        } else {
                            saleability = null;
                        }

                        if (saleInfo.has("buyLink")) {
                            try {
                                StringBuilder bLink = new StringBuilder(buyLink);
                                if (bLink.charAt(4) != 's') {
                                    bLink.insert(4, 's');
                                    buyLink = bLink.toString();
                                }
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                            buyLink = saleInfo.getString("buyLink");
                        } else {
                            buyLink = null;
                        }
                    } else {
                        buyLink = null;
                        saleability = null;
                    }

                    webReaderLink = null;
                    JSONObject pdf;
                    downloadLink = null;
                    if (currentBook.has("accessInfo")) {
                        JSONObject accessInfo = currentBook.getJSONObject("accessInfo");
                        if (accessInfo.has("webReaderLink")) {
                            webReaderLink = accessInfo.getString("webReaderLink");
                            try {
                                StringBuilder webLink = new StringBuilder(webReaderLink);
                                if (webLink.charAt(4) != 's') {
                                    webLink.insert(4, 's');
                                    webReaderLink = webLink.toString();
                                }
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        } else {
                            webReaderLink = previewLink;
                        }

                        if (accessInfo.has("pdf")) {
                             pdf = accessInfo.getJSONObject("pdf");
                            if (pdf.has("downloadLink")) {
                                downloadLink = pdf.getString("downloadLink");
                                try {
                                    StringBuilder downLink = new StringBuilder(downloadLink);
                                    if (downLink.charAt(4) != 's') {
                                        downLink.insert(4, 's');
                                        downloadLink = downLink.toString();
                                    }
                                } catch (Exception e) {
                                    System.out.println(e);
                                }
                            } else {
                                downloadLink = null;
                            }
                        } else {
                            pdf = null;
                            downloadLink = null;
                        }
                    } else {
                        webReaderLink = previewLink;
                        pdf = null;
                        downloadLink = null;
                    }
                    Book book = new Book(bookName, image, authorName, publishedDate, previewLink,
                            description, saleability, buyLink, webReaderLink, downloadLink);

                    // Add the new {@link Earthquake} to the list of earthquakes.
                    books.add(book);
                }
            }

        } catch (JSONException | IOException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return books;
    }

    public static String replaceLastFive(String s) {
        int length = s.length();
        //Check whether or not the string contains at least four characters; if not, this method is useless
        //if (length < 5) return "Error: The provided string is not greater than five characters long.";
        return s.substring(0, length - 5) + "true";
    }

    /**
     * Query the USGS dataset and return a list of {@link Book} objects.
     */
    public static List<Book> fetchEarthquakeData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Book> books = extractFeatureFromJson(jsonResponse);

        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}