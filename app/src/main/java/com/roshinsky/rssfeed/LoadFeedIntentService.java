package com.roshinsky.rssfeed;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.widget.Toast;

import org.apache.http.Header;

import rejasupotaro.asyncrssclient.AsyncRssClient;
import rejasupotaro.asyncrssclient.AsyncRssResponseHandler;
import rejasupotaro.asyncrssclient.RssFeed;
import rejasupotaro.asyncrssclient.RssItem;

import static com.roshinsky.rssfeed.FeedCacheProvider.FEED_ITEM_CONTENT_URI;
import static com.roshinsky.rssfeed.FeedCacheProvider.FEED_ITEM_DESCRIPTION;
import static com.roshinsky.rssfeed.FeedCacheProvider.FEED_ITEM_IMAGE_URL;
import static com.roshinsky.rssfeed.FeedCacheProvider.FEED_ITEM_TITLE;

public class LoadFeedIntentService extends IntentService {
    public LoadFeedIntentService() {
        super("Load Feed Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AsyncRssClient client = new AsyncRssClient();
        client.read("http://feeds.abcnews.com/abcnews/topstories", new AsyncRssResponseHandler() {
            @Override
            public void onSuccess(RssFeed rssFeed) {
                clearCache();
                insertFeedItems(rssFeed);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    private void clearCache(){
        getContentResolver().delete(FEED_ITEM_CONTENT_URI, null, null);
    }

    private void insertFeedItems(RssFeed rssFeed) {
        for (RssItem item : rssFeed.getRssItems()) {
            ContentValues values = new ContentValues();
            values.put(FEED_ITEM_TITLE, item.getTitle());
            values.put(FEED_ITEM_DESCRIPTION, item.getDescription());
            values.put(FEED_ITEM_IMAGE_URL, item.getMediaThumbnails().get(0).getUrl().toString());
            getContentResolver().insert(FEED_ITEM_CONTENT_URI, values);
        }

        Toast.makeText(this, "Cache updated", Toast.LENGTH_SHORT).show();
    }
}

