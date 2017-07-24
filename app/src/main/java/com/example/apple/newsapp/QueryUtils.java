package com.example.apple.newsapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by justin on 2017/7/21.
 */

public final class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static MyApplication application;

    public QueryUtils() {
    }

    public static List<News> fetchNews(String requestUrl) {

        // Create URL object
        //URL url = null;
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        List<News> newsList = extractNewsFromJson(jsonResponse);
        return newsList;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
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
            Log.e(LOG_TAG, "Problem retrieving the News JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
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

    /**
     * Return an {@link News} object by parsing out information
     * about the first earthquake from the input earthquakeJSON string.
     */
    private static List<News> extractNewsFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        List<News> newsList = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject responseObject = baseJsonResponse.getJSONObject(Constant.RESPONSE);
            JSONArray resultsArray = responseObject.getJSONArray(Constant.RESULTS);

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject currentNews = resultsArray.getJSONObject(i);
                //Get News title.
                String title = currentNews.getString(Constant.TITLE);

                //Get News publish date and format.
                String date = currentNews.getString(Constant.DATE);
                date = formatDate(date);

                //Get News url.
                String webUrl = currentNews.getString(Constant.WEB_URL);

                //Get News author.
                JSONArray tagsArray = currentNews.getJSONArray(Constant.TAGS);
                String author = "";

                if (tagsArray.length() == 0) {
                    author = application.getContext().getString(R.string.unknown_author);
                } else {
                    for (int j = 0; j < tagsArray.length(); j++) {
                        JSONObject currentTag = tagsArray.getJSONObject(j);
                        author += currentTag.getString(Constant.AUTHOR);
                    }
                }

                News news = new News(title, author, date, webUrl);
                newsList.add(news);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the News JSON results", e);
        }
        return newsList;
    }

    private static String formatDate(String date) {
        SimpleDateFormat jsonFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            Date parsedDate = jsonFormatter.parse(date);
            SimpleDateFormat parsedDateFormat = new SimpleDateFormat("LLL dd, yyyy");
            return parsedDateFormat.format(parsedDate);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error parsing JSON date: ", e);
            return "";
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connMgr
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
