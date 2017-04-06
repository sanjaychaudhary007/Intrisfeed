package com.archi.intrisfeed.adapter;

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
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.archi.intrisfeed.R;
import com.archi.intrisfeed.fragment.InboxDetailFragment;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by archi_info on 11/22/2016.
 */

public class InboxReceivedMailAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<HashMap<String, String>> receivedMailList;
    private LayoutInflater inflater = null;
    HashMap<String, String> hashMap;
    FragmentManager fm;
    InboxReceivedMailAdapter adapter = this;

    public InboxReceivedMailAdapter(Context mCtx, ArrayList<HashMap<String, String>> list, FragmentManager fm) {
        this.mContext = mCtx;
        this.receivedMailList = list;
        this.fm = fm;
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return receivedMailList.size();
    }

    @Override
    public Object getItem(int position) {
        return receivedMailList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolderItem viewHolder;
        if (view == null) {
            // inflate the layout
            view = inflater.inflate(R.layout.inbox_received_mail, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.tvTitleReceive);
            viewHolder.tvFrom = (TextView) view.findViewById(R.id.tvFromReceived);
            viewHolder.tvDesc = (TextView) view.findViewById(R.id.tvDetailsReceived);
            viewHolder.flRemove = (FrameLayout) view.findViewById(R.id.flRemoveReceivedMail);
            view.setTag(viewHolder);
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolderItem) view.getTag();
        }
        hashMap = receivedMailList.get(position);
        viewHolder.tvTitle.setText(hashMap.get("content_title"));
        viewHolder.tvFrom.setText(hashMap.get("recevie_mailid"));
        viewHolder.tvDesc.setText(hashMap.get("content_details"));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.WriteSharePrefrence(mContext,Constant.SHRED_PR.KEY_IS_SENT_OR_RECEIVE,"1");
                Bundle bundle = new Bundle();
                bundle.putString("content_title",""+receivedMailList.get(position).get("content_title"));
                bundle.putString("recevie_mailid",""+receivedMailList.get(position).get("recevie_mailid"));
                bundle.putString("content_link",""+receivedMailList.get(position).get("content_link"));
                bundle.putString("content_details",""+receivedMailList.get(position).get("content_details"));
                bundle.putString("image",""+receivedMailList.get(position).get("image"));
                bundle.putString("video",""+receivedMailList.get(position).get("video"));

//                bundle.putString("response", ""+receivedMailList.get(position));
                Fragment fragment = new InboxDetailFragment();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.commit();

            }
        });

        viewHolder.flRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new removeReceivedMail(receivedMailList.get(position).get("id"), position).execute();
            }
        });

        return view;
    }

    static class ViewHolderItem {
        TextView tvTitle, tvFrom, tvDesc;
        FrameLayout flRemove;
    }

    //    http://181.224.157.105/~hirepeop/host1/intrisfeed/api/delete_mail_post.php?id=10&user_id=25
    public class removeReceivedMail extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        String id;
        int pos;

        public removeReceivedMail(String id, int position) {
            this.id = id;
            this.pos = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(mContext);
            pd.setMessage("Loading");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
//            http://181.224.157.105/~hirepeop/host1/intrisfeed/api/get_sendmail_data.php?user_id=25
            Log.d("jai","deleted url :"+ "Received deleted mail id :"+id);
            HashMap<String, String> hashmap = new HashMap<String, String>();
            hashmap.put("id", id);
            hashmap.put("user_id", "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID));
            response = Util.getResponseofPost(Constant.BASE_URL + "delete_mail_post.php", hashmap);
//                urlString = Constant.BASE_URL + "login.php?email=" + usernamestr + "&password=" + passwordstr;
            Log.e("RESULT", ">>" + response);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
//            pd.dismiss();
            super.onPostExecute(s);
            pd.dismiss();
            Log.d("SENT MAIL", "" + s);
            try {
                JSONObject object = new JSONObject(s.toString());
                if (object.getString("status").equalsIgnoreCase("true")) {
//                    JSONArray arry = object.getJSONArray("data");
//                    JSONObject obj = object.getJSONObject("data");
                    receivedMailList.remove(pos);
                    adapter.notifyDataSetChanged();
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
