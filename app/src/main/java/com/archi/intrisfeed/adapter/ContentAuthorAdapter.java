package com.archi.intrisfeed.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.archi.intrisfeed.ContentAuthorDetailActivity;
import com.archi.intrisfeed.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by archi_info on 12/9/2016.
 */

public class ContentAuthorAdapter extends BaseAdapter
{

    ArrayList<HashMap<String,String>> arrayList;
     LayoutInflater inflater;
     Context context;

    public ContentAuthorAdapter(ArrayList<HashMap<String,String>> arrayList, Context context) {
        this.arrayList = arrayList;
        inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.adapter_content_authors,null);

        TextView tvTitle = (TextView)convertView.findViewById(R.id.adapter_content_author_title);
        TextView tvAuthor = (TextView)convertView.findViewById(R.id.adapter_content_author_name);
        ImageView ivThumbnail = (ImageView)convertView.findViewById(R.id.adapter_content_author_profile);

        tvTitle.setText(arrayList.get(position).get("content_title"));
        tvAuthor.setText(arrayList.get(position).get("name"));
        Picasso.with(context).load(arrayList.get(position).get("profile_image")).into(ivThumbnail);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,ContentAuthorDetailActivity.class);
                i.putExtra("userId",arrayList.get(position).get("userId"));
                i.putExtra("email",arrayList.get(position).get("email"));
                i.putExtra("name",arrayList.get(position).get("name"));
                i.putExtra("image",arrayList.get(position).get("profile_image"));
                context.startActivity(i);
            }
        });
        return convertView;
    }
}
