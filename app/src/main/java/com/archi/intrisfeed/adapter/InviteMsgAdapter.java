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
import android.widget.Toast;

import com.archi.intrisfeed.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by archi_info on 9/28/2016.
 */
public class InviteMsgAdapter extends BaseAdapter implements View.OnClickListener {
    Context mContext;
    ArrayList<HashMap<String, String>> contactList;
    private LayoutInflater inflater = null;
    private String receiverId;
    HashMap<String, String> hashMap;
    FragmentManager fm;


    public InviteMsgAdapter(Context mCtx, ArrayList<HashMap<String, String>> list, FragmentManager fm) {
        this.mContext = mCtx;
        this.contactList = list;
        this.fm = fm;
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolderItem viewHolder;
        if (view == null) {
            // inflate the layout
            view = inflater.inflate(R.layout.item_invite_msg_request, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.tvName = (TextView) view.findViewById(R.id.tvName_InviteMsgFragment);
//            viewHolder.tvEmail = (TextView) view.findViewById(R.id.tvEmail_LinkRequestFragment);
            viewHolder.tvPhone = (TextView) view.findViewById(R.id.tvPhone_InviteMsgFragment);
            viewHolder.ivUserImage = (ImageView) view.findViewById(R.id.ivUserImage_InviteMsgFragment);
            viewHolder.ivSendRequest = (ImageView) view.findViewById(R.id.ivSendRequest_InviteMsgFragment);
            // store the holder with the view.
            view.setTag(viewHolder);
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolderItem) view.getTag();
        }
        ;

        hashMap = contactList.get(position);
        if (!hashMap.get("member_image").equalsIgnoreCase("")) {
            Picasso.with(mContext).load(hashMap.get("member_image")).placeholder(R.drawable.default_img).into(viewHolder.ivUserImage);
        } else {
            Picasso.with(mContext).load(R.drawable.default_img).into(viewHolder.ivUserImage);
        }
        Log.e("IMAGE" + position, ">> " + hashMap.get("image"));
        viewHolder.tvName.setText(hashMap.get("member_name"));
//        viewHolder.tvEmail.setText(hashMap.get("email"));
        viewHolder.tvPhone.setText(hashMap.get("member_number"));
        viewHolder.ivSendRequest.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivSendRequest_InviteMsgFragment:
                try {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.putExtra("sms_body", "Check out IntrinsFeed, Download it today from "+ Uri.parse("https://play.google.com/store/apps/details?id=com.archi.intrisfeed"));
                    sendIntent.putExtra("address", "" + hashMap.get("member_number"));
                    sendIntent.setType("vnd.android-dir/mms-sms");
                    mContext.startActivity(sendIntent);
                } catch (Exception e) {
                    Toast.makeText(mContext,
                            "SMS failed, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                break;
        }
    }


    static class ViewHolderItem {
        TextView tvName, tvPhone;
        ImageView ivUserImage, ivSendRequest;
    }
}
