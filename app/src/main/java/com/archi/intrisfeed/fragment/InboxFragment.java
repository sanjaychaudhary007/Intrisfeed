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
import android.widget.TextView;
import android.widget.Toast;

import com.archi.intrisfeed.R;
import com.archi.intrisfeed.adapter.InboxReceivedMailAdapter;
import com.archi.intrisfeed.adapter.InboxSendMailAdapter;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by archi_info on 9/24/2016.
 */
public class InboxFragment extends Fragment implements View.OnClickListener {
    ArrayList<HashMap<String,String>> sendMailArraylist;
    ArrayList<HashMap<String,String>> receivedMailArraylist;
    InboxReceivedMailAdapter adapterReceived;
    InboxSendMailAdapter adapterSent;
    HashMap<String, String> hashmap;
    ListView lvSendMail, lvReceivedMail;
    TextView tvSent, tvReceived;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        lvSendMail = (ListView) rootView.findViewById(R.id.lvSendMailList);
        lvReceivedMail = (ListView) rootView.findViewById(R.id.lvReceivedMailList);

        tvSent = (TextView) rootView.findViewById(R.id.tvSentMail);
        tvReceived = (TextView) rootView.findViewById(R.id.tvReceivedMail);
        tvSent.setOnClickListener(this);
        tvReceived.setOnClickListener(this);

            if(Util.isOnline(getActivity())) {
                new getSentMail().execute();

                new getReceivedtMail().execute();
            }else{
                Toast.makeText(getActivity(), "No internet connectivity found, Please check your Internet connection.", Toast.LENGTH_SHORT).show();
            }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvSentMail:
                if(Util.isOnline(getActivity())) {
                    lvReceivedMail.setVisibility(View.GONE);
                    lvSendMail.setVisibility(View.VISIBLE);
                    new getSentMail().execute();
                }else{
                    Toast.makeText(getActivity(), "No internet connectivity found, Please check your Internet connection.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tvReceivedMail:
                if(Util.isOnline(getActivity())) {
                    lvReceivedMail.setVisibility(View.VISIBLE);
                    lvSendMail.setVisibility(View.GONE);
                    new getReceivedtMail().execute();
                }else{
                    Toast.makeText(getActivity(), "No internet connectivity found, Please check your Internet connection.", Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }

    public class getSentMail extends AsyncTask<String, String, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Loading");
            pd.setCancelable(false);
            pd.show();
            sendMailArraylist = new ArrayList<>();
            sendMailArraylist.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
//            http://181.224.157.105/~hirepeop/host1/intrisfeed/api/get_sendmail_data.php?user_id=25
            HashMap<String, String> hashmap = new HashMap<String, String>();
            hashmap.put("user_id",""+Util.ReadSharePrefrence(getActivity(),Constant.SHRED_PR.KEY_USERID));
          //  response = Util.getResponseofPost(Constant.BASE_URL + "get_sendmail_data.php", hashmap);
            response = Util.getResponseofGet(Constant.BASE_URL + "get_sendmail_data.php?user_id="+ hashmap.put("user_id",Util.ReadSharePrefrence(getActivity(),Constant.SHRED_PR.KEY_USERID)));
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
                    JSONArray arry = object.getJSONArray("data");
//                    JSONObject obj = object.getJSONObject("data");
                    Log.e("OBJ","> "+arry);
                    for(int j=0; j < arry.length(); j++){
                        JSONObject obj = arry.getJSONObject(j);
                        Log.e("OOO","00 "+obj);
                        hashmap = new HashMap<>();
                        hashmap.put("sent_mailid",""+obj.getString("sent_mailid"));
                        hashmap.put("id",""+obj.getString("id"));
                        String id = obj.getString("id");
                        String user_id = obj.getString("user_id");
                        String sent_mailid = obj.getString("sent_mailid");
//                        Toast.makeText(getActivity(), "" + object.getString("msg"), Toast.LENGTH_SHORT).show();
                        JSONArray array = obj.getJSONArray("post_id");
                        for(int i=0; i < array.length(); i++){
                            JSONObject obj1 = array.getJSONObject(i);
                            Log.e("OBJ!","00 "+obj1);
                            hashmap.put("content_title",""+obj1.getString("content_title"));
                            hashmap.put("content_details",""+obj1.getString("content_details"));
                            hashmap.put("content_link",""+obj1.getString("content_link"));
                            hashmap.put("content_author_email",""+obj1.getString("content_author_email"));
                            hashmap.put("image",""+obj1.getString("image"));
                            hashmap.put("video",""+obj1.getString("video"));
                            hashmap.put("doc",""+obj1.getString("doc"));
                        }
                        sendMailArraylist.add(hashmap);
                    }
                    adapterSent = new InboxSendMailAdapter(getActivity(), sendMailArraylist, getActivity().getSupportFragmentManager());
                    lvSendMail.setAdapter(adapterSent);
                    adapterSent.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "" + object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public class getReceivedtMail extends AsyncTask<String, String, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Loading");
            pd.setCancelable(false);
            pd.show();
            receivedMailArraylist = new ArrayList<>();
            receivedMailArraylist.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";

            HashMap<String, String> hashmap = new HashMap<String, String>();
            Log.e("USER_ID",""+Util.ReadSharePrefrence(getActivity(),Constant.SHRED_PR.KEY_USERID));
          //  hashmap.put("user_id","32");
            hashmap.put("user_id",Util.ReadSharePrefrence(getActivity(),Constant.SHRED_PR.KEY_USERID));
            response = Util.getResponseofPost(Constant.BASE_URL + "get_receivemail.php", hashmap);
            Log.e("RESULT", ">>" + response);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
//            pd.dismiss();
            super.onPostExecute(s);
            pd.dismiss();
            Log.e("RECEIVED MAIL", "" + s);
            try {
                JSONObject object = new JSONObject(s.toString());
                if (object.getString("status").equalsIgnoreCase("true")) {
                    JSONArray arry = object.getJSONArray("data");
//                    JSONObject obj = object.getJSONObject("data");
                    Log.e("OBJ","> "+arry);
                    Log.e("SIZE","?? "+arry.length());
                    for(int j = 0; j < arry.length(); j++){
                        JSONObject obj = arry.getJSONObject(j);
                        Log.e("OOO","00 "+obj);
                        hashmap = new HashMap<>();
                        Log.e("RECEIVED ID ",">> "+obj.getString("recevie_mailid"));
                        hashmap.put("recevie_mailid",""+obj.getString("recevie_mailid"));
                        hashmap.put("id",""+obj.getString("id"));
                        String id = obj.getString("id");
                        String user_id = obj.getString("user_id");
//                        Toast.makeText(getActivity(), "" + object.getString("msg"), Toast.LENGTH_SHORT).show();
                        JSONArray array = obj.getJSONArray("post_id");
                        for(int i=0; i < array.length(); i++){
                            JSONObject obj1 = array.getJSONObject(i);
                            Log.e("TITLE ID ",">> "+obj1.getString("content_title"));
                            hashmap.put("content_title",""+obj1.getString("content_title"));
                            hashmap.put("content_details",""+obj1.getString("content_details"));
                            hashmap.put("content_link",""+obj1.getString("content_link"));
                            hashmap.put("content_author_email",""+obj1.getString("content_author_email"));
                            hashmap.put("image",""+obj1.getString("image"));
                            hashmap.put("video",""+obj1.getString("video"));
                            hashmap.put("doc",""+obj1.getString("doc"));
                        }
                        receivedMailArraylist.add(hashmap);
                    }
                    adapterReceived = new InboxReceivedMailAdapter(getActivity(), receivedMailArraylist, getActivity().getSupportFragmentManager());
                    lvReceivedMail.setAdapter(adapterReceived);
                    adapterReceived.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "" + object.getString("msg"), Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
