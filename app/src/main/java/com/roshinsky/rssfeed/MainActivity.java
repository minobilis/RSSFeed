package com.roshinsky.rssfeed;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.Header;

import rejasupotaro.asyncrssclient.AsyncRssClient;
import rejasupotaro.asyncrssclient.AsyncRssResponseHandler;
import rejasupotaro.asyncrssclient.RssFeed;
import rejasupotaro.asyncrssclient.RssItem;

import static com.roshinsky.rssfeed.FeedCacheProvider.FEED_ITEM_CONTENT_URI;
import static com.roshinsky.rssfeed.FeedCacheProvider.FEED_ITEM_DESCRIPTION;
import static com.roshinsky.rssfeed.FeedCacheProvider.FEED_ITEM_IMAGE_URL;
import static com.roshinsky.rssfeed.FeedCacheProvider.FEED_ITEM_LINK;
import static com.roshinsky.rssfeed.FeedCacheProvider.FEED_ITEM_TITLE;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener {
    public static final String LAST_TIME_UPDATE_KEY = "last_time_update";
    private static final int LOADER_ID = 0x01;
    private FeedsCursorAdapter feedCursorAdapter;
    private long lastUpdateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        feedCursorAdapter = new FeedsCursorAdapter(this, null, 0);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        ListViewCompat lvData = (ListViewCompat) findViewById(R.id.listView);
        lvData.setAdapter(feedCursorAdapter);
        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    TextView textViewLink = (TextView) view.findViewById(R.id.rss_item_link);
                    String link = textViewLink.getText().toString();


                    Uri uri = Uri.parse(link);

                    if (uri != null) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                        view.getContext().startActivity(browserIntent);

                    } else {
                        Toast.makeText(view.getContext(), "Cannot open empty Link", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        lastUpdateTime = sharedPref.getLong(LAST_TIME_UPDATE_KEY, 0);

        if (System.currentTimeMillis() - lastUpdateTime > 24 * 60 * 60 * 1000) { // more than a day
            updateCache();
        } else {
            Toast.makeText(this, "Cache is recently updated. No update required", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                FEED_ITEM_CONTENT_URI,
                new String[]{
                        FeedCacheProvider.FEED_ITEM_ID,
                        FEED_ITEM_TITLE,
                        FEED_ITEM_DESCRIPTION,
                        FEED_ITEM_IMAGE_URL,
                        FEED_ITEM_LINK
                },
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        feedCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        feedCursorAdapter.swapCursor(null);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void updateCache() {
        AsyncRssClient client = new AsyncRssClient();
        client.read("http://feeds.abcnews.com/abcnews/topstories", new AsyncRssResponseHandler() {
            @Override
            public void onSuccess(RssFeed rssFeed) {
                clearCache();
                insertFeedItems(rssFeed);
                lastUpdateTime = System.currentTimeMillis();
                saveLastTimeUpdateToPrefrences(lastUpdateTime);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    private void saveLastTimeUpdateToPrefrences(long lastUpdateTime) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(LAST_TIME_UPDATE_KEY, lastUpdateTime);
        editor.commit();
    }

    private void clearCache() {
        getContentResolver().delete(FEED_ITEM_CONTENT_URI, null, null);
    }

    private void insertFeedItems(RssFeed rssFeed) {
        for (RssItem item : rssFeed.getRssItems()) {
            ContentValues values = new ContentValues();
            values.put(FEED_ITEM_TITLE, item.getTitle());
            values.put(FEED_ITEM_DESCRIPTION, item.getDescription());
            values.put(FEED_ITEM_IMAGE_URL, item.getMediaThumbnails().get(0).getUrl().toString());
            values.put(FEED_ITEM_LINK, item.getLink().toString());
            getContentResolver().insert(FEED_ITEM_CONTENT_URI, values);
        }

        Toast.makeText(this, "Cache updated", Toast.LENGTH_SHORT).show();
    }
}
