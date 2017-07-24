package com.example.apple.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by justin on 2017/7/21.
 */

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    private static final String LOG_TAG = NewsLoader.class.getName();
    private String mUrl;

    public NewsLoader(Context context, String mUrl) {
        super(context);
        this.mUrl = mUrl;
    }

    @Override
    protected void onStartLoading() {
        Log.d(LOG_TAG, "on start loading.");
        forceLoad();
    }

    @Override
    public List<News> loadInBackground(){
        Log.d(LOG_TAG, "load in background.");
        if (mUrl == null) {
            return null;
        }
        List<News> newsList = QueryUtils.fetchNews(mUrl);
        return newsList;
    }

}
