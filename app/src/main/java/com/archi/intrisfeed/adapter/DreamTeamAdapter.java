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
import android.widget.LinearLayout;
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
 * Created by archi_info on 11/18/2016.
 */

public class DreamTeamAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<HashMap<String, String>> userFriendList;
    private LayoutInflater inflater = null;
    HashMap<String, String> hashMap;
    FragmentManager fm;
    DreamTeamAdapter adapter = this;

    public DreamTeamAdapter(Context mCtx, ArrayList<HashMap<String, String>> frindListArrayList, FragmentManager fragmentManager) {
        this.mContext = mCtx;
        this.userFriendList = frindListArrayList;
        this.fm = fragmentManager;
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return userFriendList.size();
    }

    @Override
    public Object getItem(int position) {
        return userFriendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolderItem viewHolder;
        if (convertView == null) {
            // inflate the layout
            convertView = inflater.inflate(R.layout.item_dream_team, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName_DreamTeamFragment);
            viewHolder.ivUserImage = (ImageView) convertView.findViewById(R.id.ivUserImage_DreamTeamFragment);
            viewHolder.tvCategory = (TextView) convertView.findViewById(R.id.tvCategory_DreamTeamFragment);
            viewHolder.tvCity = (TextView) convertView.findViewById(R.id.tvCity_DreamTeamFragment);
            viewHolder.llRemoveFav = (LinearLayout) convertView.findViewById(R.id.llRemoveFav);
            // store the holder with the view.
            convertView.setTag(viewHolder);
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        hashMap = userFriendList.get(position);
        viewHolder.tvName.setText(hashMap.get("name"));
        viewHolder.tvCategory.setText(hashMap.get("categories"));
        viewHolder.tvCity.setText(hashMap.get("city"));
        Picasso.with(mContext).load(hashMap.get("image")).error(R.drawable.com_facebook_profile_picture_blank_portrait).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(viewHolder.ivUserImage);

        viewHolder.llRemoveFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String to_id = hashMap.get("id");
                new removeUserFromFavoriteList(to_id,position).execute();
            }
        });

        return convertView;
    }
    static class ViewHolderItem {
        TextView tvName, tvCategory, tvCity;
        ImageView ivUserImage;
        LinearLayout llRemoveFav;
    }

//    http://181.224.157.105/~hirepeop/host1/intrisfeed/api/remove_favourite.php?frd_to=25&frd_from=26

    public class removeUserFromFavoriteList extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        String to_id;
        int pos;

        public removeUserFromFavoriteList(String to_id, int position) {
            this.to_id = to_id;
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

            HashMap<String, String> hashmap = new HashMap<String, String>();
            hashmap.put("frd_to", to_id);
            hashmap.put("frd_from", Util.ReadSharePrefrence(mContext,Constant.SHRED_PR.KEY_USERID));
            response = Util.getResponseofPost(Constant.BASE_URL + "remove_favourite.php", hashmap);
//                urlString = Constant.BASE_URL + "login.php?email=" + usernamestr + "&password=" + passwordstr;
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
                if (object.getString("status").equalsIgnoreCase("true")) {
                    userFriendList.remove(pos);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(mContext, "Remove Favorite successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "" + object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
