package com.archi.intrisfeed.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.archi.intrisfeed.R;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import static com.archi.intrisfeed.R.id.rlReceiverImg;

/**
 * Created by archi_info on 10/29/2016.
 */

public class PrivateChatMsgListAdapter extends BaseAdapter {
    /**
     * Created by archi_info on 10/29/2016.
     */

    public Context context;
    public ArrayList<HashMap<String, String>> arrayList;
    public HashMap<String, String> hashMap;
    public LayoutInflater inflater;
    RelativeLayout llImageReceiver, rlSendImg, rlReceiveImg;
    TextView tvSendImageTime, tvReceiveImgTime, tvReceiveMsg, tvSendMsg, tvReceiveTime, tvTimeSend;
    ImageView ivRecivedImage, ivSentImage;

    public PrivateChatMsgListAdapter(Context context, ArrayList<HashMap<String, String>> msgs) {
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
        view = inflater.inflate(R.layout.adapter_msg_list, parent, false);
        // well set up the ViewHolder
//            viewHolder = new ViewHolderItem();
        tvReceiveMsg = (TextView) view.findViewById(R.id.tvreceivedMsg);
        tvReceiveTime = (TextView) view.findViewById(R.id.adapter_received_time);
        tvSendMsg = (TextView) view.findViewById(R.id.tvSendMsg);

        tvTimeSend = (TextView) view.findViewById(R.id.adapter_send_time);
        LinearLayout llSender = (LinearLayout) view.findViewById(R.id.llSender);
        LinearLayout llReceiver = (LinearLayout) view.findViewById(R.id.llReceiver);
        llImageReceiver = (RelativeLayout) view.findViewById(R.id.llIVReceiver);
        tvSendImageTime = (TextView) view.findViewById(R.id.adapter_send_Imgtime);
        tvReceiveImgTime = (TextView) view.findViewById(R.id.adapter_receive_Imgtime);
        rlSendImg = (RelativeLayout) view.findViewById(R.id.rlSenderImg);
        rlReceiveImg = (RelativeLayout) view.findViewById(rlReceiverImg);

        ivRecivedImage = (ImageView) view.findViewById(R.id.adpter_iv_image_recived);
        ivSentImage = (ImageView) view.findViewById(R.id.adpter_iv_image_sender);

//        ivDownloadImage = (ImageView) view.findViewById(R.id.adp_iv_download_icon);
        llImageReceiver.setVisibility(View.GONE);
        hashMap = arrayList.get(position);
        Log.e("", "***********************************");
        Log.e("ADPTR MSG", "" + hashMap.get("msg"));
        Log.e("Sender_id", "" + hashMap.get("sender_id"));
        Log.e("recipient_id", "" + hashMap.get("recipient_id"));
        Log.e("APP SENDER", "" + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_USER_CHAT_ID));
        Log.e("LOGIN USER", "" + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_QB_USERID));
//        Log.e("url", "" + hashMap.get("url"));

        if (Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_QB_USERID).equalsIgnoreCase(hashMap.get("recipient_id"))) {
            if(!hashMap.get("msg").equalsIgnoreCase("") && hashMap.get("msg") != null && !hashMap.get("msg").equalsIgnoreCase("null")){
                tvSendMsg.setText(hashMap.get("msg"));
                tvTimeSend.setText(hashMap.get("updated_at"));
                llSender.setVisibility(View.VISIBLE);
                llReceiver.setVisibility(View.GONE);
                llImageReceiver.setVisibility(View.GONE);
            }else{
                if (hashMap.get("url").contains("https://")  ) {
                    tvSendImageTime.setText(hashMap.get("updated_at"));
                    if (!hashMap.get("url").equalsIgnoreCase("")) {
                        Picasso.with(context).load(hashMap.get("url")).placeholder(R.drawable.ic_default).into(ivSentImage);
                    } else {
                        Picasso.with(context).load(R.drawable.ic_default).placeholder(R.drawable.default_img).into(ivSentImage);
                    }
                    llSender.setVisibility(View.GONE);
                    llReceiver.setVisibility(View.GONE);
                    llImageReceiver.setVisibility(View.VISIBLE);
                    rlReceiveImg.setVisibility(View.GONE);
                    rlSendImg.setVisibility(View.VISIBLE);
                }
            }
        } else {

            if(!hashMap.get("msg").equalsIgnoreCase("") && hashMap.get("msg") != null && !hashMap.get("msg").equalsIgnoreCase("null")){
                tvReceiveMsg.setText(hashMap.get("msg"));
                tvReceiveTime.setText(hashMap.get("updated_at"));
                llSender.setVisibility(View.GONE);
                llReceiver.setVisibility(View.VISIBLE);
                llImageReceiver.setVisibility(View.GONE);
            }else{
                if (hashMap.get("url").contains("https://") ) {
                    tvReceiveImgTime.setText(hashMap.get("updated_at"));
                    if (!hashMap.get("url").equalsIgnoreCase("")) {
                        Picasso.with(context).load(hashMap.get("url")).error(R.drawable.ic_default).placeholder(R.drawable.default_img).into(ivRecivedImage);
                    } else {
                        Picasso.with(context).load(R.drawable.ic_default).placeholder(R.drawable.default_img).into(ivRecivedImage);
                    }
                    llSender.setVisibility(View.GONE);
                    llReceiver.setVisibility(View.GONE);
                    llImageReceiver.setVisibility(View.VISIBLE);
                    rlReceiveImg.setVisibility(View.VISIBLE);
                    rlSendImg.setVisibility(View.GONE);
                }
            }
        }
        return view;
    }
}

