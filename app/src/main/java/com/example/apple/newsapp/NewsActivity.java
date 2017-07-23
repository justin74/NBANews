package com.example.apple.newsapp;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>
        , SwipeRefreshLayout.OnRefreshListener {

    public static final String LOG_TAG = NewsActivity.class.getName();

    private TextView mEmptyStateTextView;
    private NewsAdapter mNewsAdapter;
    private ListView mNewsListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreated");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNewsListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        mNewsListView.setEmptyView(mEmptyStateTextView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mNewsAdapter = new NewsAdapter(this, new ArrayList<News>());
        mNewsListView.setAdapter(mNewsAdapter);

        mNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News currentNews = mNewsAdapter.getItem(position);

                // 将字符串 URL 转换成 URI 对象（传递到 Intent 构造函数中）
                Uri newsUri = Uri.parse(currentNews.getWebUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // 发送 intent 以启动新活动
                startActivity(websiteIntent);
            }
        });

        // If there is a network connection, fetch data
        if (QueryUtils.isNetworkAvailable(this)) {
            Log.d(LOG_TAG, "Network available");

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            getLoaderManager().initLoader(Constant.NEWS_LOADER_ID, null, this);
        } else {
            Log.d(LOG_TAG, "Network not available");
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            //mLoadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            getLoaderManager().initLoader(Constant.NEWS_LOADER_ID, null, this);
            mEmptyStateTextView.setText(R.string.no_internet_connection);


        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader");
        Uri baseUri = Uri.parse(Constant.NEWS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("order-by", Constant.ORDER_BY);
        uriBuilder.appendQueryParameter("show-tags", Constant.SHOW_TAGS);
        uriBuilder.appendQueryParameter("q", Constant.NBA);
        uriBuilder.appendQueryParameter("api-key", Constant.API_KEY);
        Log.d(LOG_TAG, "uri = " + uriBuilder.toString());

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {
        Log.d(LOG_TAG, "onLoadFinished");

        mSwipeRefreshLayout.setRefreshing(false);
        // Set empty state text to display "No news found."
        if (QueryUtils.isNetworkAvailable(this)){
            mEmptyStateTextView.setText(R.string.no_news_found);
        } else {
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
        // 清除之前地震数据的适配器
        mNewsAdapter.clear();

        // 如果存在 {@link News} 的有效列表，则将其添加到适配器的
        // 数据集。这将触发 ListView 执行更新。
        if (newsList != null && !newsList.isEmpty()) {
            Log.d(LOG_TAG, "newsList != null");
            mNewsAdapter.addAll(newsList);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        Log.d(LOG_TAG, "onLoaderReset");
        // 重置 Loader，以便能够清除现有数据。
        mNewsAdapter.clear();
    }

    @Override
    public void onRefresh() {
        Log.d(LOG_TAG, "onRefresh");
        getLoaderManager().restartLoader(Constant.NEWS_LOADER_ID, null, this);
    }
}
