package com.archi.intrisfeed.fragment;


import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.archi.intrisfeed.R;
import com.archi.intrisfeed.adapter.InviteMsgAdapter;
import com.archi.intrisfeed.adapter.BrowseListAdapter;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by archi_info on 9/24/2016.
 * http://web-medico.com/web2/intrisfeed/api/user_list.php?id=16
 */
public class BrowseFragment extends Fragment implements View.OnClickListener{
    ProgressDialog pd;

    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;     // contacts unique ID
    private BrowseListAdapter adapterSendRequest;
    private InviteMsgAdapter inviteMsgAdapter;
    private ListView lvLinkRequest, lvInviteMsg;
    private ArrayList<HashMap<String, String>> userList;
    private ArrayList<HashMap<String, String>> interestList;
    private ArrayList<HashMap<String, String>> nameList;
    private ArrayList<HashMap<String, String>> locationList;
    HashMap<String, String> hashMap;

    private RadioGroup radioOptionGroup;
    private RadioButton radioOptionButton;



    ArrayList<HashMap<String, String>> listContacts = new ArrayList<>();
    ArrayList<HashMap<String, String>> sourceList = new ArrayList<>();
    EditText etSearch;
    private String searchText = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_browse, container, false);
        lvLinkRequest = (ListView) rootView.findViewById(R.id.lvLinkRequest_LinkrequestFragment);
        radioOptionGroup = (RadioGroup) rootView.findViewById(R.id.radioGroup);
        etSearch = (EditText) rootView.findViewById(R.id.etSearch_Linkrequest);
        lvInviteMsg = (ListView) rootView.findViewById(R.id.lvInviteMessage_LinkrequestFragment);

        // using native contacts selection
        // Intent.ACTION_PICK = Pick an item from the data, returning what was selected.

//        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
        if (Util.isOnline(getActivity())) {
            new getUserList().execute();
        } else {
            Toast.makeText(getActivity(), Constant.network_error, Toast.LENGTH_SHORT).show();
        }



        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchText = etSearch.getText().toString();
                int selectedId = radioOptionGroup.getCheckedRadioButtonId();
                radioOptionButton = (RadioButton) rootView.findViewById(selectedId);
                String option = radioOptionButton.getText().toString();
                searchList(searchText,option);
            }

            @Override
            public void afterTextChanged(Editable s) {

//                if(userList.contains()){
//                    Log.e(">> ","TRUE");
//                }
            }
        });

        return rootView;
    }

    private void searchList(String searchText, String option) {
        switch (option){
            case "All":
//                Toast.makeText(getActivity(), ">>ALL ", Toast.LENGTH_SHORT).show();
                adapterSendRequest = new BrowseListAdapter(getActivity(), userList, getActivity().getSupportFragmentManager());
                break;

            case "Interest":
                interestList = new ArrayList<>();
                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).get("categories").toLowerCase().contains(searchText.toLowerCase())) {
                        interestList.add(userList.get(i));
                    }
                }
                adapterSendRequest = new BrowseListAdapter(getActivity(), interestList, getActivity().getSupportFragmentManager());
                break;

            case "Name":
                nameList = new ArrayList<>();
                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).get("name").toLowerCase().contains(searchText.toLowerCase())) {
                        nameList.add(userList.get(i));
                    }
                }
                adapterSendRequest = new BrowseListAdapter(getActivity(), nameList,getActivity().getSupportFragmentManager());
                break;

            case "Location":
                locationList = new ArrayList<>();
                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).get("city").toLowerCase().contains(searchText.toLowerCase()) || userList.get(i).get("country").toLowerCase().contains(searchText.toLowerCase())) {
                        locationList.add(userList.get(i));
                    }
                }
                adapterSendRequest = new BrowseListAdapter(getActivity(), locationList, getActivity().getSupportFragmentManager());
                break;
        }
        lvLinkRequest.setAdapter(adapterSendRequest);
        adapterSendRequest.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {
    }

    public class getUserList extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Loading");
            pd.setCancelable(false);
            pd.show();
            userList = new ArrayList<HashMap<String, String>>();
            userList.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
//            http://web-medico.com/web2/intrisfeed/api/user_list.php?id=16
            HashMap<String, String> hashmap = new HashMap<String,String>();
            hashmap.put("id",Util.ReadSharePrefrence(getActivity(),Constant.SHRED_PR.KEY_USERID));
            response = Util.getResponseofPost(Constant.BASE_URL + "user_list.php",hashmap);
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
                        Log.e("ID",""+obj.getString("id"));
                        Log.e("name",""+obj.getString("name"));
                        Log.e("-------","**********************");
                        hashMap.put("name", obj.getString("name"));
                        hashMap.put("aproved_category", obj.getString("aproved_category"));
                        hashMap.put("email", obj.getString("email"));
                        hashMap.put("phone", obj.getString("phone"));
                        hashMap.put("image", obj.getString("image"));
                        hashMap.put("categories", obj.getString("categories"));
                        hashMap.put("city", obj.getString("city"));
                        hashMap.put("country", obj.getString("country"));
                        userList.add(hashMap);
                    }


                    adapterSendRequest = new BrowseListAdapter(getActivity(), userList, getActivity().getSupportFragmentManager());
                    lvLinkRequest.setAdapter(adapterSendRequest);
                    adapterSendRequest.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "" + object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class IgnoreCaseComparator implements Comparator<HashMap<String, String>> {
        public int compare(HashMap<String, String> hashMap1, HashMap<String, String> hashMap2) {
            String strA = hashMap1.get("member_name").toLowerCase();
            String strB = hashMap2.get("member_name").toLowerCase();

            return strA.compareToIgnoreCase(strB);
        }
    }

}
