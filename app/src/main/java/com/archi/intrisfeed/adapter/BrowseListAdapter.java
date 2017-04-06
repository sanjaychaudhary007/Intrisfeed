package com.archi.intrisfeed.adapter;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.archi.intrisfeed.R;
import com.archi.intrisfeed.fragment.BrowseDetailScreenFragment;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by archi_info on 10/5/2016.
 */
public class BrowseListAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<HashMap<String, String>> userList;
    private LayoutInflater inflater = null;
    private String receiverId;
    HashMap<String, String> hashMap;
    FragmentManager fm;

    public BrowseListAdapter(Context mCtx, ArrayList<HashMap<String, String>> list, FragmentManager fm) {
        this.mContext = mCtx;
        this.userList = list;
        this.fm = fm;
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolderItem viewHolder;
        if (view == null) {
            // inflate the layout
            view = inflater.inflate(R.layout.item_userlist, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.tvName = (TextView) view.findViewById(R.id.tvName_LinkRequestFragment);
//            viewHolder.tvEmail = (TextView) view.findViewById(R.id.tvEmail_LinkRequestFragment);
            viewHolder.tvPhone = (TextView) view.findViewById(R.id.tvPhone_LinkRequestFragment);
            viewHolder.tvCategory = (TextView) view.findViewById(R.id.tvCategory_LinkRequestFragment);
            viewHolder.ivUserImage = (ImageView) view.findViewById(R.id.ivUserImage_LinkRequestFragment);
            viewHolder.ivSendRequest = (ImageView) view.findViewById(R.id.ivSendRequest_LinkRequestFragment);
            // store the holder with the view.
            view.setTag(viewHolder);
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolderItem) view.getTag();
        }
        ;

        hashMap = userList.get(position);
        Picasso.with(mContext).load(hashMap.get("image")).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(viewHolder.ivUserImage);
        Log.e("IMAGE" + position, ">> " + hashMap.get("image"));
        viewHolder.tvName.setText(hashMap.get("name"));
//        viewHolder.tvEmail.setText(hashMap.get("email"));
        viewHolder.tvPhone.setText("Approved Category:" + hashMap.get("aproved_category"));

        String category = hashMap.get("categories");
        List<String> elephantList = Arrays.asList(category.split(","));
        String catty = "";
        String cat1 = "", cat2 = "", cat3 = "";
        switch (elephantList.size()) {

            case 1:
                cat1 = elephantList.get(0);
                catty = cat1;
                viewHolder.tvCategory.setText("Interest: " + catty);
                break;
            case 2:
                cat1 = elephantList.get(0);
                cat2 = elephantList.get(1);
                catty = cat1 + "," + cat2;
                viewHolder.tvCategory.setText("Interest: " + catty);
                break;
            case 3:
                cat1 = elephantList.get(0);
                cat2 = elephantList.get(1);
                cat3 = elephantList.get(2);
                catty = cat1 + "," + cat2 + "," + cat3;
                viewHolder.tvCategory.setText("Interest: " + catty);
                break;

        }


        viewHolder.ivSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiverId = userList.get(position).get("id");
//                Toast.makeText(mContext,"POSITIOn "+position,Toast.LENGTH_LONG).show();
                Log.e("UIDDD", ">> " + receiverId);
                showSendMailPopup(receiverId);

            }
        });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hashMap = userList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("list", "list");
                bundle.putSerializable("hashmap", hashMap);

                Fragment fragment = new BrowseDetailScreenFragment();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.commit();
            }
        });
        return view;
    }


    static class ViewHolderItem {
        TextView tvName, tvEmail, tvPhone, tvCategory;
        ImageView ivUserImage, ivSendRequest;
    }


    public void showSendMailPopup(final String rcvId) {
        // custom dialog
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.popup_friend_request_msg);
        dialog.setTitle("Send Request");
        dialog.setCancelable(false);

        // set the custom dialog components - text, image and button
        final EditText shortMsg = (EditText) dialog.findViewById(R.id.etRequest_msg_BrowseAdapter);

        TextView sendMail = (TextView) dialog.findViewById(R.id.tvSend_BrowseAdapter);
        TextView cancel = (TextView) dialog.findViewById(R.id.tvCancel_BrowseAdapter);
        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isOnline(mContext)) {
                    String msg = shortMsg.getText().toString();
                    if (!msg.equalsIgnoreCase("")) {
                        new sendFriendRequest(rcvId, msg).execute();
                    }else{
                        Toast.makeText(mContext, "Please Enter Invitation Message", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "" + Constant.network_error, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public class sendFriendRequest extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        String id;
        String message;

        public sendFriendRequest(String receiverId, String msg) {
            this.id = receiverId;
            this.message = msg;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(mContext);
            pd.setMessage("Loading");
            pd.setCancelable(false);
            pd.show();
            userList = new ArrayList<HashMap<String, String>>();
            userList.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            HashMap<String, String> hashmap = new HashMap<>();
            hashmap.put("frd_from", Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID));
            hashmap.put("frd_to", id);
            hashmap.put("message", message);
            Log.e("FROM_TO", "FRM >> " + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID) + " To:" + id);

            response = Util.getResponseofPost(Constant.BASE_URL + "send_request.php", hashmap);
            Log.e("RESULT", ">>" + response);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
//            pd.dismiss();
            super.onPostExecute(s);
            pd.dismiss();
            Log.d("Response", "" + s);
            try {
                JSONObject object = new JSONObject(s.toString());
                if (object.getString("successful").equalsIgnoreCase("true")) {
                    Toast.makeText(mContext, "" + object.getString("msg"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "" + object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
