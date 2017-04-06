package com.archi.intrisfeed.adapter;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.archi.intrisfeed.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by archi_info on 11/10/2016.
 */

public class FeedImageAdapter  extends BaseAdapter {
    Context mContext;
    ArrayList<HashMap<String, String>> imageList;
    private LayoutInflater inflater = null;
    HashMap<String, String> hashMap;
    FragmentManager fm;

    public FeedImageAdapter(Context mCtx, ArrayList<HashMap<String, String>> list, FragmentManager fm) {
        this.mContext = mCtx;
        this.imageList = list;
        this.fm = fm;
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolderItem viewHolder;
        if (view == null) {
            // inflate the layout
            view = inflater.inflate(R.layout.adapter_feed_image, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.tvCategoryName = (TextView) view.findViewById(R.id.tvCategoryName);
            viewHolder.imageViewFeed = (ImageView) view.findViewById(R.id.ivImage_ImageFeedAdapter);

            // store the holder with the view.
            view.setTag(viewHolder);
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolderItem) view.getTag();
        };
        hashMap = imageList.get(position);
        Log.e("CAT",">> "+hashMap.get("category").toString());
        viewHolder.tvCategoryName.setText(hashMap.get("category").toString());
        Picasso.with(mContext).load(hashMap.get("image")).error(R.drawable.default_img).into(viewHolder.imageViewFeed);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(hashMap.get("content_link")));
                mContext.startActivity(browserIntent);
            }
        });

        return view;
    }

    static class ViewHolderItem {
        TextView tvCategoryName;
        ImageView imageViewFeed;

    }
}
