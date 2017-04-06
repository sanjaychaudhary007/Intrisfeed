package com.archi.intrisfeed.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.archi.intrisfeed.R;
import com.archi.intrisfeed.fragment.GroupChat;
import com.archi.intrisfeed.fragment.PrivateChat;
import com.quickblox.chat.model.QBChatDialog;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.archi.intrisfeed.MainActivity.groupName;
import static com.archi.intrisfeed.MainActivity.occupantIdsList;

/**
 * Created by archi_info on 12/13/2016.
 */

public class ChatListQBAdapter extends BaseAdapter {
    public Context context;
    public ArrayList<QBChatDialog> arrayList;
    public LayoutInflater inflater;
    ImageView ivAddFav;
    static CheckBox cbGroupChat;
    ChatListQBAdapter adapter = this;
//    ArrayList<String> arrayUpdate;

    public ChatListQBAdapter(Context context, ArrayList<QBChatDialog> users) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.arrayList = users;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        if (arrayList.get(position).getType().toString().equalsIgnoreCase("GROUP")) {
            view = inflater.inflate(R.layout.adapter_chat_group, null);
            CircleImageView ivProfilePic = (CircleImageView) view.findViewById(R.id.ivProfilePicGroup);
            TextView usernameTv = (TextView) view.findViewById(R.id.adapter_chat_group_usernametv);
            TextView timeTv = (TextView) view.findViewById(R.id.adapter_chat_group_timetv);
//            timeTv.setText(arrayList.get(position).getLastMessage());
            usernameTv.setText("" + arrayList.get(position).getName());
//            adapter_chat_group_timetv
        } else {
            view = inflater.inflate(R.layout.adapter_chat_user, null);
            CircleImageView ivProfilePic = (CircleImageView) view.findViewById(R.id.ivProfilePicPrivate);
            TextView usernameTv = (TextView) view.findViewById(R.id.adapter_chat_user_usernametv);
            TextView timeTv = (TextView) view.findViewById(R.id.adapter_chat_user_last_msg);
            cbGroupChat = (CheckBox) view.findViewById(R.id.checkbox_adapter_chat_user);

//            Picasso.with(context).load(arrayList.get(position).get)
            usernameTv.setText(arrayList.get(position).getName());
            if (arrayList.get(position).getLastMessage() != null) {
                timeTv.setText(arrayList.get(position).getLastMessage());
            }


            cbGroupChat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (occupantIdsList.size() <= 15) {
                            occupantIdsList.add(arrayList.get(position).getId());
                            Log.e("LIST ", ">> " + arrayList.get(position).getId());
                            groupName.add(arrayList.get(position).getName());
                        } else {
                            Toast.makeText(context, "You can select Maximum 15 user in group", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        occupantIdsList.remove(arrayList.get(position).getId());
                    }
                }
            });
        }


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayList.get(position).getType().toString().equalsIgnoreCase("GROUP")) {
                    Intent in = new Intent(context, GroupChat.class);
                    in.putExtra("user_id", "" + arrayList.get(position).getUserId());
                    in.putExtra("dialog_id", "" + arrayList.get(position).getDialogId());
                    in.putExtra("group_name", "" + arrayList.get(position).getName());
                    in.putExtra("oponantID", "" + arrayList.get(position).getRecipientId());
                    in.putExtra("occupant_id", "" + arrayList.get(position).getOccupants());
                    in.putExtra("jID", arrayList.get(position).getRoomJid());
                    Log.e("==== ", "----------------GROUP-----------------");
                    Log.e("user_id", "" + arrayList.get(position).getUserId());
                    Log.e("dialog_id", "" + arrayList.get(position).getDialogId());
                    Log.e("group_name", "" + arrayList.get(position).getName());
                    Log.e("occupant_id", "" + arrayList.get(position).getOccupants());
                    Log.e("jID", "" + arrayList.get(position).getRoomJid());

                    context.startActivity(in);
                } else {
                    Intent in = new Intent(context, PrivateChat.class);
                    in.putExtra("dialog_id", "" + arrayList.get(position).getDialogId());
                    in.putExtra("fullname", arrayList.get(position).getName());
                    in.putExtra("oponantID", arrayList.get(position).getRecipientId());
                    in.putExtra("user_id", arrayList.get(position).getUserId());
                    Log.e("==== ", "-------------PRIVATE--------------------");
                    Log.e("user_id", "" + arrayList.get(position).getUserId());
                    Log.e("oponantID", "" + arrayList.get(position).getRecipientId());
                    Log.e("dialog_id",""+ arrayList.get(position).getDialogId());
                    context.startActivity(in);
                }

            }
        });


        return view;
    }
}
