package com.archi.intrisfeed.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.archi.intrisfeed.R;
import com.archi.intrisfeed.adapter.ContentAuthorAdapter;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by archi_info on 12/9/2016.
 */

public class ContentAuthorFragment extends android.support.v4.app.Fragment
{
    public EditText etSearchAuthor;
    public ListView lvAuthorContent;
    public ArrayList<HashMap> arryGetAllAuthor;
    public ArrayList<HashMap<String,String>> arrylist,arrayListSearch;
    public ContentAuthorAdapter contentAuthorAdapter;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rooView = inflater.inflate(R.layout.fragment_content_author,container,false);
        etSearchAuthor = (EditText)rooView.findViewById(R.id.fragment_content_author_et_search);
        lvAuthorContent = (ListView)rooView.findViewById(R.id.fragment_content_author_lv);
        arrylist = new ArrayList<>();
        init();
        return rooView;

    }


    private void init()
    {
        new getAllAuthors().execute();
        etSearchAuthor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                arrayListSearch = new ArrayList<>();

                for (int i=0;i<arrylist.size();i++)
                {
                    if (arrylist.get(i).get("category").toLowerCase().contains(s.toString().toLowerCase()))
                    {
                        arrayListSearch.add(arrylist.get(i));

                    }

                }
                contentAuthorAdapter = new ContentAuthorAdapter(arrayListSearch,getActivity());
                lvAuthorContent.setAdapter(contentAuthorAdapter);

            }
        });


    }

    private class getAllAuthors extends AsyncTask<String,String,String> {
       KProgressHUD hud;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            hud = KProgressHUD.create(getActivity())
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .show();
            arryGetAllAuthor = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... params) {
         //http://181.224.157.105/~hirepeop/host1/intrisfeed/api/get_author_details.php?user_id=39
            //http://181.224.157.105/~hirepeop/host1/intrisfeed/api/get_payment_author.php
            //http://181.224.157.105/~hirepeop/host1/intrisfeed/api/get_payment_author_exp.php
            String categorys = Util.ReadSharePrefrence(getActivity(),Constant.SHRED_PR.KEY_USER_INTREST);
            String url = Constant.BASE_URL + "get_payment_author_exp.php"+categorys;
            return Util.getResponseofGet(url);
        }

        @Override
        protected void onPostExecute(String s)
        {
            try {
                 JSONObject jsonObject = new JSONObject(s.toString());
                if (jsonObject.getString("successful").equalsIgnoreCase("true"))
                  {
                      JSONArray jsonArray = jsonObject.getJSONArray("data");
                      for (int i=0;i<jsonArray.length();i++)
                      {
                          JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                          HashMap<String,String> hashMap = new HashMap<>();
                          hashMap.put("userId",jsonObject1.getString("userid"));
                          hashMap.put("name",jsonObject1.getString("username"));
                          hashMap.put("profile_image",jsonObject1.getString("image"));
                          hashMap.put("email",jsonObject1.getString("email"));
                          hashMap.put("category",jsonObject1.getString("category"));
                          JSONArray jsonContentTitle = jsonObject1.getJSONArray("content_post_titles");
                          JSONObject jsonObjectContentTitle = jsonContentTitle.getJSONObject(0);
                          hashMap.put("content_title",jsonObjectContentTitle.getString("content_title"));
                          arrylist.add(hashMap);
                      }
                  }
                contentAuthorAdapter = new ContentAuthorAdapter(arrylist,getActivity());
                lvAuthorContent.setAdapter(contentAuthorAdapter);




            }
            catch (JSONException e) {
                e.printStackTrace();
            }


            hud.dismiss();
        }
    }
}
