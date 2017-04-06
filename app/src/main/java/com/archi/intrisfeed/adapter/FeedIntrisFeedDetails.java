package com.archi.intrisfeed.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.archi.intrisfeed.R;
import com.archi.intrisfeed.model.IntrisfeedDetail;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by archi on 12/19/2016.
 */

public class FeedIntrisFeedDetails extends BaseAdapter {
    Context context;
    ArrayList<IntrisfeedDetail> arrayList;
    LayoutInflater inflater;
    public FeedIntrisFeedDetails(FragmentActivity activity, ArrayList<IntrisfeedDetail> arrayListFieldDetail) {
        this.context = activity;
        this.arrayList = arrayListFieldDetail;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.adapter_feed_detail,null);
        if (arrayList.get(position).getVideoFeedList() != null) {
            convertView = inflater.inflate(R.layout.adapter_feed_video, null);
            HashMap<String, String> hashMap;
            TextView tvCategoryName = (TextView) convertView.findViewById(R.id.tvCategoryNameVideo);
            VideoView videoViewFeed = (VideoView) convertView.findViewById(R.id.vwVideo_VideoFeedAdapter);
            ImageView ivSendMail = (ImageView) convertView.findViewById(R.id.ivSendMail_VideoAdapter);
            TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitleNameVideo);
            for (int i = 0; i < arrayList.get(position).getVideoFeedList().size(); i++)
            {

                hashMap = arrayList.get(position).getVideoFeedList().get(0);
            tvCategoryName.setText(hashMap.get("content_title").toString());
            tvTitle.setText(hashMap.get("content_title").toString());
            videoViewFeed.setVideoURI(Uri.parse(hashMap.get("video")));
            videoViewFeed.seekTo(100);

                final HashMap<String, String> finalHashMap = hashMap;
                videoViewFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                          Toast.makeText(context, "click Video", Toast.LENGTH_SHORT).show();
                          Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalHashMap.get("content_link")));
                          context.startActivity(browserIntent);
//                if (!isFileExits(hashMap.get("video"), viewHolder)) {
//                    downloadFile(hashMap.get("video"), viewHolder);
//                } else {
//                    String path= getpath(hashMap.get("video"));
//                    openImage(path);
//                    Toast.makeText(mContext, "File alreday exits", Toast.LENGTH_SHORT).show();
//                }
                    }
                });
            }}
        else if (arrayList.get(position).getAllFeedList()!=null)
        {
            convertView = inflater.inflate(R.layout.adapter_feed_all,null);
            TextView tvCategoryNameAll = (TextView) convertView.findViewById(R.id.tvCategory_AllFeedAdapter);
            TextView tvVideoAll = (TextView) convertView.findViewById(R.id.tvVideo_AllFeedAdapter);
            TextView tvDocAll = (TextView) convertView.findViewById(R.id.tvDoc_AllFeedAdapter);
            TextView tvDescAll = (TextView) convertView.findViewById(R.id.tvDesc_AllFeedAdapter);
            ImageView ivCatImage = (ImageView) convertView.findViewById(R.id.ivCatImageAll);
            ImageView ivSendMail = (ImageView) convertView.findViewById(R.id.ivSendMail_AllAdapter);
        }
        else if (arrayList.get(position).getImageFeedList() != null)
        {
            convertView = inflater.inflate(R.layout.adapter_feed_image,null);
            TextView tvCategoryName = (TextView) convertView.findViewById(R.id.tvCategoryName);
            ImageView imageViewFeed = (ImageView) convertView.findViewById(R.id.ivImage_ImageFeedAdapter);
        }
        else if (arrayList.get(position).getBlogsFeedList() != null)
        {
            convertView = inflater.inflate(R.layout.adapter_feed_blogs,null);
            TextView tvCategoryName = (TextView) convertView.findViewById(R.id.tvCategoryName_AdapterBlogsFeed);
            TextView tvFeedLink = (TextView) convertView.findViewById(R.id.tvBlogLink_AdapterBlogsFeed);
            ImageView ivBlogsSendMail = (ImageView) convertView.findViewById(R.id.ivSendMail_BlogsAdapter);
        }
        else
        {
            Toast.makeText(context,"No Data available",Toast.LENGTH_SHORT).show();


        }
        return convertView;
    }
}
