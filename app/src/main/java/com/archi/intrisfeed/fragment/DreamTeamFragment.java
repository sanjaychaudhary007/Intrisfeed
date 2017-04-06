package com.archi.intrisfeed.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.archi.intrisfeed.R;
import com.archi.intrisfeed.adapter.DreamTeamAdapter;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by archi_info on 11/18/2016.
 */
public class DreamTeamFragment extends Fragment {
    ArrayList<HashMap<String, String>> frindListArrayList;
    HashMap<String, String> hashMap;
    DreamTeamAdapter adapter;
    ListView lvFriendList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_deam_team, container, false);
        lvFriendList = (ListView) rootView.findViewById(R.id.lvFavoriteUser_DreamTeamFragment);
        if (Util.isOnline(getActivity())) {
            new getFavoriteFriendList().execute();
        } else {
            Toast.makeText(getActivity(), "Please check internet connectivity", Toast.LENGTH_SHORT).show();
        }
        return rootView;
    }


    public class getFavoriteFriendList extends AsyncTask<String, String, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Loading");
            pd.setCancelable(false);
            frindListArrayList = new ArrayList<>();
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            Log.e("UID", " >> " + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID));
            HashMap<String, String> hashmap = new HashMap<String, String>();
            hashmap.put("id", Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID));
            response = Util.getResponseofPost(Constant.BASE_URL + "get_favourite_friend_list.php?", hashmap);
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
                    JSONArray objArray = object.getJSONArray("data");
                    for (int i = 0; i < objArray.length(); i++) {
                        JSONObject obj = objArray.getJSONObject(i);
                        hashMap = new HashMap<String, String>();
                        hashMap.put("id", obj.getString("id"));
                        hashMap.put("name", obj.getString("name"));
                        hashMap.put("email", obj.getString("email"));
                        hashMap.put("image", obj.getString("image"));
                        hashMap.put("categories", obj.getString("categories"));
                        hashMap.put("city", obj.getString("city"));
                        hashMap.put("country", obj.getString("country"));
                        frindListArrayList.add(hashMap);
                    }

                } else {
                    Toast.makeText(getActivity(), "" + object.getString("msg"), Toast.LENGTH_SHORT).show();
//                    snackbar = Snackbar
//                            .make(snackbarCoordinatorLayout, "" + object.getString("msg"), Snackbar.LENGTH_LONG);
//                    snackbar.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter = new DreamTeamAdapter(getActivity(), frindListArrayList, getActivity().getFragmentManager());
            lvFriendList.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }
    }
}
