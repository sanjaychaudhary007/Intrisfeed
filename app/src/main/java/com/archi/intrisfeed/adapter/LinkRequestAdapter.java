package com.archi.intrisfeed.adapter;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.archi.intrisfeed.R;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by archi_info on 9/26/2016.
 * Accept USER REQUEST
 * http://web-medico.com/web2/intrisfeed/api/accept_request.php?frd_from=1&frd_to=3
 *
 * REJECT USER REQUEST
 * http://web-medico.com/web2/intrisfeed/api/reject_friend_request.php?frd_from=1&frd_to=3
 */
public class LinkRequestAdapter extends BaseAdapter{
    Context mContext;
    ArrayList<HashMap<String, String>> userList;
    private LayoutInflater inflater = null;
    private String receiverId;
    HashMap<String, String> hashMap;
    FragmentManager fm;
    LinkRequestAdapter adapter = this;

    public LinkRequestAdapter(Context mCtx, ArrayList<HashMap<String, String>> list, FragmentManager fm) {
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
            view = inflater.inflate(R.layout.item_linkrequest, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.tvName = (TextView) view.findViewById(R.id.tvName_LinkRequestFragment);
//            viewHolder.tvEmail = (TextView) view.findViewById(R.id.tvEmail_LinkRequestFragment);
            viewHolder.tvCategory = (TextView) view.findViewById(R.id.tvCategory_LinkRequestFragment);
            viewHolder.ivUserImage = (ImageView) view.findViewById(R.id.ivUserImage_LinkRequestFragment);
            viewHolder.tvRejectRequest = (TextView) view.findViewById(R.id.tvRejectRequest_LinkRequestFragment);
            viewHolder.tvAcceptRequest = (TextView) view.findViewById(R.id.tvAcceptRequest_LinkRequestFragment);
            // store the holder with the view.
            view.setTag(viewHolder);
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolderItem) view.getTag();
        };

        hashMap = userList.get(position);
        Picasso.with(mContext).load(hashMap.get("image")).error(R.drawable.com_facebook_profile_picture_blank_portrait).into(viewHolder.ivUserImage);
        Log.e("IMAGE" + position, ">> " + hashMap.get("image"));
        viewHolder.tvName.setText(hashMap.get("name"));
//        viewHolder.tvEmail.setText(hashMap.get("email"));
//        viewHolder.tvPhone.setText(hashMap.get("phone"));
        viewHolder.tvCategory.setText(hashMap.get("message"));

        viewHolder.tvAcceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiverId =  userList.get(position).get("id");
                if(Util.isOnline(mContext)){
                    new acceptFriendRequest(receiverId, position).execute();
                }else{
                    Toast.makeText(mContext, ""+ Constant.network_error, Toast.LENGTH_SHORT).show();
                }
            }
        });


        viewHolder.tvRejectRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiverId =  userList.get(position).get("id");
                if(Util.isOnline(mContext)) {
                    new rejetFriendRequest(receiverId, position).execute();
                }else{
                    Toast.makeText(mContext, ""+ Constant.network_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }


    static class ViewHolderItem {
        TextView tvName, tvCategory, tvAcceptRequest, tvRejectRequest;
        ImageView ivUserImage;
    }



    public class acceptFriendRequest extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        String id;
        int pos;

        public acceptFriendRequest(String receiverId, int position) {
            this.id = receiverId;
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
//            http://web-medico.com/web2/intrisfeed/api/accept_request.php?frd_from=1&frd_to=3

            String response = "";
            HashMap<String, String> hashmap = new HashMap<>();
            hashmap.put("frd_from",id);
            hashmap.put("frd_to",Util.ReadSharePrefrence(mContext,Constant.SHRED_PR.KEY_USERID));
            Log.e("FROM_TO", "FRM >> " + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID) + " To:" + id);

            response = Util.getResponseofPost(Constant.BASE_URL + "accept_request.php",hashmap);
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
                    userList.remove(pos);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, "" + object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class rejetFriendRequest extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        String id;
        int pos;

        public rejetFriendRequest(String receiverId, int position) {
            this.id = receiverId;
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
//            http://web-medico.com/web2/intrisfeed/api/accept_request.php?frd_from=1&frd_to=3

            String response = "";
            HashMap<String, String> hashmap = new HashMap<>();
            hashmap.put("frd_from", id);
            hashmap.put("frd_to",Util.ReadSharePrefrence(mContext,Constant.SHRED_PR.KEY_USERID));
            Log.e("FROM_TO", "FRM >> " + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID) + " To:" + id);

            response = Util.getResponseofPost(Constant.BASE_URL + "reject_friend_request.php",hashmap);
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
                    userList.remove(pos);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, "" + object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}
