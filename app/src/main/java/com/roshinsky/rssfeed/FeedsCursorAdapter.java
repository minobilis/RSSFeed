package com.roshinsky.rssfeed;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class FeedsCursorAdapter extends CursorAdapter {
    public FeedsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.card_view_rss_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewID = (TextView) view.findViewById(R.id.rss_item_id);
        TextView textViewTitle = (TextView) view.findViewById(R.id.rss_item_title);
        TextView textViewDescription = (TextView) view.findViewById(R.id.rss_item_description);
        ImageView imageViewImage = (ImageView) view.findViewById(R.id.rss_item_image);

        String id = cursor.getString(cursor.getColumnIndexOrThrow(FeedCacheProvider.FEED_ITEM_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(FeedCacheProvider.FEED_ITEM_TITLE));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(FeedCacheProvider.FEED_ITEM_DESCRIPTION));
        String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(FeedCacheProvider.FEED_ITEM_IMAGE_URL));

        textViewID.setText(id);
        textViewTitle.setText(title);
        textViewDescription.setText(description);

        try {
            Picasso.with(context)
                    .load(imageUrl)
                    .into(imageViewImage);

        } catch (Exception e) {
            Log.e("Feed", "Error while opening image");
        }
    }
}
