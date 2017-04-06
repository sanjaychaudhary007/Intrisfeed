package com.archi.intrisfeed.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.archi.intrisfeed.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by archi_info on 12/9/2016.
 */

public class ContentAutherDetailAdapter extends BaseAdapter {

    public Context context;
    public LayoutInflater inflater;
    public ArrayList<HashMap<String,String>> arrayList;
    public ContentAutherDetailAdapter(Context context, ArrayList<HashMap<String, String>> arrayContent)
    {
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.arrayList = arrayContent;
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
        convertView = inflater.inflate(R.layout.adapter_content_auther_detail,null);
        RelativeLayout rvCategory = (RelativeLayout)convertView.findViewById(R.id.adapter_content_auther_detail_category);
        RelativeLayout rvTitle = (RelativeLayout)convertView.findViewById(R.id.adapter_content_auther_detail_rv_title);

        if (position % 2 ==0)
        {
            rvCategory.setBackgroundColor(Color.parseColor("#d3d3d3"));
            rvTitle.setBackgroundColor(Color.parseColor("#dcdcdc"));
        }

        final TextView tvCategory = (TextView)convertView.findViewById(R.id.adpter_content_auther_detail_category);
        TextView tvTitle = (TextView)convertView.findViewById(R.id.adpter_content_auther_detail_tital);
        tvCategory.setText(arrayList.get(position).get("category"));
        tvTitle.setText(arrayList.get(position).get("content_title"));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_auther_content_detail);

                if (dialog != null)
                {
                    int width = ViewGroup.LayoutParams.MATCH_PARENT;
                    int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    dialog.getWindow().setLayout(width, height);
                    Button btnCancel = (Button)dialog.findViewById(R.id.dialog_auther_content_detail_cancel);
                    TextView tvDialogCategoty = (TextView)dialog.findViewById(R.id.dialog_auther_content_detail_category);
                    TextView tvDialogTitle = (TextView)dialog.findViewById(R.id.dialog_auther_content_title);
                    TextView tvDialogDetail = (TextView)dialog.findViewById(R.id.dialog_auther_content_detail);
                    TextView tvDialogContentLink = (TextView)dialog.findViewById(R.id.dialog_auther_content_link);
                    TextView tvDialogEmail = (TextView)dialog.findViewById(R.id.dialog_auther_content_email);
                    TextView tvImage = (TextView) dialog.findViewById(R.id.dialog_auther_content_detail_tv_image);
                    TextView tvVideo = (TextView)dialog.findViewById(R.id.dialg_auther_content_detail_tv_video);
                    TextView tvDoc = (TextView)dialog.findViewById(R.id.dialog_auther_content_detail_tv_doc);
                    tvDialogCategoty.setText(arrayList.get(position).get("content_details"));
                    tvDialogTitle.setText(arrayList.get(position).get("content_title"));
                    tvDialogDetail.setText(arrayList.get(position).get("content_details"));
                    tvDialogContentLink.setText(arrayList.get(position).get("content_link"));
                    tvDialogEmail.setText(arrayList.get(position).get("content_author_email"));

                    tvDialogEmail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("plain/text");
                            intent.putExtra(Intent.EXTRA_EMAIL, new String[] {arrayList.get(position).get("content_author_email")});
                            intent.putExtra(Intent.EXTRA_SUBJECT, "intrisfeed");
                            intent.putExtra(Intent.EXTRA_TEXT, "mail body");
                            context.startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                        }
                    });


                    tvVideo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(arrayList.get(position).get("video")));
                            context.startActivity(myIntent);
                        }
                    });

                    tvImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(arrayList.get(position).get("image")));
                            context.startActivity(myIntent);
                        }
                    });

                    tvDoc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(arrayList.get(position).get("doc")));
                            context.startActivity(myIntent);
                        }
                    });


                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }

                }

        });

        return convertView;
    }
}
