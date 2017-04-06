package com.archi.intrisfeed.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.archi.intrisfeed.OpenGroupLinkActivity;
import com.archi.intrisfeed.R;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import static com.archi.intrisfeed.R.id.rlReceiverImg_Group;

/**
 * Created by archi_info on 12/5/2016.
 */

public class GroupChatMessageListAdapter extends BaseAdapter {
    /**
     * Created by archi_info on 10/29/2016.
     */

    public Context context;
    public ArrayList<HashMap<String, String>> arrayList;
    public HashMap<String, String> hashMap;
    public LayoutInflater inflater;
    RelativeLayout llImageReceiver, rlSendImg, rlReceiveImg;
    TextView tvSendImageTime, tvReceiveImgTime, tvReceivedMsg, tvSendMsg;

    public GroupChatMessageListAdapter(Context context, ArrayList<HashMap<String, String>> msgs) {
        this.context = context;
        this.arrayList = msgs;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
//        final ViewHolderItem viewHolder;

        // inflate the layout
        view = inflater.inflate(R.layout.adapter_group_msg_list, parent, false);
        // well set up the ViewHolder
//            viewHolder = new ViewHolderItem();
        tvReceivedMsg = (TextView) view.findViewById(R.id.tvreceivedMsgGroup);
        TextView timeRecive = (TextView) view.findViewById(R.id.adapter_received_timeGroup);
        tvSendMsg = (TextView) view.findViewById(R.id.tvSendMsgGroup);
        TextView timeSend = (TextView) view.findViewById(R.id.adapter_send_time_Group);
        LinearLayout llSender = (LinearLayout) view.findViewById(R.id.llSenderGroup);
        LinearLayout llReceiver = (LinearLayout) view.findViewById(R.id.llReceiverGroup);
        llImageReceiver = (RelativeLayout) view.findViewById(R.id.llIVReceiver_Group);
        tvSendImageTime = (TextView) view.findViewById(R.id.adapter_send_Imgtime_Group);
        tvReceiveImgTime = (TextView) view.findViewById(R.id.adapter_receive_Imgtime_Group);
        rlSendImg = (RelativeLayout) view.findViewById(R.id.rlSenderImg_Group);
        rlReceiveImg = (RelativeLayout) view.findViewById(rlReceiverImg_Group);

        ImageView ivRecivedImage = (ImageView) view.findViewById(R.id.adpter_iv_image_recived_Group);

//        ivDownloadImage = (ImageView) view.findViewById(R.id.adp_iv_download_icon);
        llImageReceiver.setVisibility(View.GONE);
        hashMap = arrayList.get(position);
        Log.e("---------", "--------------------------------");
        Log.e("ADPTR MSG", "" + hashMap.get("msg"));
        Log.e("Sender_id", "" + hashMap.get("sender_id"));
        Log.e("recipient_id", "" + hashMap.get("recipient_id"));
        Log.e("APP SENDER", "" + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_USER_CHAT_ID));
        Log.e("LOGIN USER", "" + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_QB_USERID));
//        Log.e("url", "URL " + hashMap.get("url").toString());
        if (!Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_QB_USERID).equalsIgnoreCase(hashMap.get("sender_id"))) {
            if (!hashMap.get("msg").equalsIgnoreCase("") && hashMap.get("msg") != null && !hashMap.get("msg").equalsIgnoreCase("null")) {
                tvReceivedMsg.setText(hashMap.get("msg"));
                timeRecive.setText(hashMap.get("updated_at"));
                llSender.setVisibility(View.GONE);
                llReceiver.setVisibility(View.VISIBLE);
                llImageReceiver.setVisibility(View.GONE);
            } else {
                String url = hashMap.get("url").toString();

                if (url.contains("https://")) {
                    tvSendImageTime.setText(hashMap.get("updated_at"));
                    Picasso.with(context).load(hashMap.get("url")).error(R.drawable.ic_default).into(ivRecivedImage);
                    llSender.setVisibility(View.GONE);
                    llReceiver.setVisibility(View.GONE);
                    llImageReceiver.setVisibility(View.VISIBLE);
                    rlReceiveImg.setVisibility(View.VISIBLE);
                    rlSendImg.setVisibility(View.GONE);
                }
            }
        } else {
            if (!hashMap.get("msg").equalsIgnoreCase("") && hashMap.get("msg") != null && !hashMap.get("msg").equalsIgnoreCase("null")) {
                tvSendMsg.setText(hashMap.get("msg"));
                timeSend.setText(hashMap.get("updated_at"));
                llSender.setVisibility(View.VISIBLE);
                llReceiver.setVisibility(View.GONE);
                llImageReceiver.setVisibility(View.GONE);
            } else {
                String url = hashMap.get("url").toString();

                if (url.contains("https://")) {
                    tvSendImageTime.setText(hashMap.get("updated_at"));
                    Picasso.with(context).load(hashMap.get("url")).error(R.drawable.ic_default).into(ivRecivedImage);
                    llSender.setVisibility(View.GONE);
                    llReceiver.setVisibility(View.GONE);
                    llImageReceiver.setVisibility(View.VISIBLE);
                    rlReceiveImg.setVisibility(View.GONE);
                    rlSendImg.setVisibility(View.VISIBLE);
                }
            }
        }


        llImageReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openLink = new Intent(context,OpenGroupLinkActivity.class);
                openLink.putExtra("url",arrayList.get(position).get("url"));
                openLink.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(openLink);

            }
        });



        return view;
    }
}