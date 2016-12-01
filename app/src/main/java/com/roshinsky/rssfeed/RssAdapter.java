package com.roshinsky.rssfeed;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import rejasupotaro.asyncrssclient.RssItem;

public class RssAdapter extends RecyclerView.Adapter<RssAdapter.ViewHolder>{
    private ArrayList<RssItem> mDataSet;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvDescription;
        private ImageView imageView;

        public ViewHolder(final View view) {
            super(view);
            tvTitle = (TextView) itemView.findViewById(R.id.rss_item_title);
            tvDescription = (TextView) itemView.findViewById(R.id.rss_item_description);
            imageView = (ImageView) itemView.findViewById(R.id.rss_item_image);
        }
    }

    public RssAdapter(ArrayList<RssItem> myDataSet, Context context) {
        mDataSet = myDataSet;
        this.context = context;
    }

    @Override
    public RssAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_rss_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Uri linkURI = mDataSet.get(holder.getAdapterPosition()).getLink();
        holder.tvTitle.setTag(linkURI);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = mDataSet.get(position).getLink();

                if (uri != null) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                    view.getContext().startActivity(browserIntent);

                } else {
                    Toast.makeText(view.getContext(), "Cannot open empty Link", Toast.LENGTH_SHORT).show();
                }
            }
        };

        holder.tvTitle.setOnClickListener(listener);
        holder.tvDescription.setOnClickListener(listener);
        holder.imageView.setOnClickListener(listener);

        holder.tvTitle.setText(mDataSet.get(position).getTitle());
        holder.tvDescription.setText(mDataSet.get(position).getDescription());

        try {
            String imageURL = mDataSet.get(position).getMediaThumbnails().get(0).getUrl().toString();
            Picasso.with(context).load(imageURL).into(holder.imageView);
        } catch (Exception e) {
            Log.e("Feed", "Error while opening image");
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
