package com.archi.intrisfeed.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.archi.intrisfeed.R;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

/**
 * Created by archi_info on 10/19/2016.
 */

public class ChatUserListAdapter1 extends BaseAdapter {
    public Context context;
    public ArrayList<QBUser> arrayList;
    public LayoutInflater inflater;

    public ChatUserListAdapter1(Context context, ArrayList<QBUser> users) {
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
        View view = inflater.inflate(R.layout.adapter_chat_user, null);
//        ImageView ivProfilePic = (ImageView) view.findViewById(R.id.ivProfilePic);
//        TextView usernameTv = (TextView) view.findViewById(R.id.adapter_chat_user_usernametv);
//        TextView timeTv = (TextView) view.findViewById(R.id.adapter_chat_user_timetv);
//
//        usernameTv.setText(arrayList.get(position).getFullName());
//        timeTv.setText(arrayList.get(position).getEmail());
//
////        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent in = new Intent(context, TestChat.class);
//                in.putExtra("id", "" + arrayList.get(position).getId());
//                in.putExtra("fullname", arrayList.get(position).getFullName());
//                in.putExtra("email", arrayList.get(position).getEmail());
//                context.startActivity(in);
//            }
//        });

        return view;
    }
}
