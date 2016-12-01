package com.roshinsky.rssfeed;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.apache.http.Header;

import java.util.ArrayList;

import rejasupotaro.asyncrssclient.AsyncRssClient;
import rejasupotaro.asyncrssclient.AsyncRssResponseHandler;
import rejasupotaro.asyncrssclient.RssFeed;
import rejasupotaro.asyncrssclient.RssItem;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFeed;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<RssItem> rssDataSet = new ArrayList<>();
    private RssAdapter rssAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewFeed = (RecyclerView) findViewById(R.id.recycler_view_feed);
        recyclerViewFeed.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerViewFeed.setLayoutManager(mLayoutManager);
        rssAdapter = new RssAdapter(rssDataSet, this);
        recyclerViewFeed.setAdapter(rssAdapter);
        recyclerViewFeed.setItemAnimator(new DefaultItemAnimator());

        AsyncRssClient client = new AsyncRssClient();
        client.read("http://feeds.abcnews.com/abcnews/topstories", new AsyncRssResponseHandler() {
            @Override
            public void onSuccess(RssFeed rssFeed) {
                rssDataSet.addAll(rssFeed.getRssItems());
                rssAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }
}
