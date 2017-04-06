package com.archi.intrisfeed.fragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.archi.intrisfeed.R;
import com.archi.intrisfeed.adapter.InviteMsgAdapter;
import com.archi.intrisfeed.adapter.LinkRequestAdapter;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by archi_info on 9/24/2016.
 * get Friend Request list
 *API :  http://web-medico.com/web2/intrisfeed/api/request_list.php?id=3
 */
public class LinkRequestFragment extends Fragment implements View.OnClickListener {
    ProgressDialog pd;
    private static final int PERMISSION_REQUEST_CODE = 1;
    String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;     // contacts unique ID
    private LinkRequestAdapter adapterSendRequest;
    private InviteMsgAdapter inviteMsgAdapter;
    private ListView lvLinkRequest, lvInviteMsg;
    private ArrayList<HashMap<String, String>> userRequestList;
    HashMap<String, String> hashMap;

    private RadioGroup radioOptionGroup;
    private RadioButton radioOptionButton;
    private LinearLayout llContactlist, llUserRequestList;
    private TextView tvUserlist, tvContactlist, tvRequestIndicator, tvContactIndicator;

    ArrayList<HashMap<String, String>> listContacts = new ArrayList<>();
    ArrayList<HashMap<String, String>> sourceList;
    EditText etSearch;
    private String searchText = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_linkrequest, container, false);

        lvLinkRequest = (ListView) rootView.findViewById(R.id.lvRequestList_LinkrequestFragment);
        lvInviteMsg = (ListView) rootView.findViewById(R.id.lvInviteMessage_LinkrequestFragment);
        llContactlist = (LinearLayout) rootView.findViewById(R.id.llCotactList);
        llUserRequestList = (LinearLayout) rootView.findViewById(R.id.llRequestList);
        tvUserlist = (TextView) rootView.findViewById(R.id.tvUserRequestList);
        tvContactlist = (TextView) rootView.findViewById(R.id.tvContactList);
        tvRequestIndicator = (TextView) rootView.findViewById(R.id.tvRequestIndicator);
        tvContactIndicator = (TextView) rootView.findViewById(R.id.tvContactIndicator);

        tvContactlist.setOnClickListener(this);
        tvUserlist.setOnClickListener(this);

        if (Util.isOnline(getActivity())) {
            new getFriendRequestList().execute();
        } else {
            Toast.makeText(getActivity(), Constant.network_error, Toast.LENGTH_SHORT).show();
        }
        // using native contacts selection
        // Intent.ACTION_PICK = Pick an item from the data, returning what was selected.
        // startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);



        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvContactList:
                if (llContactlist.getVisibility() == View.GONE) {

                    tvRequestIndicator.setVisibility(View.GONE);
                    tvContactIndicator.setVisibility(View.VISIBLE);
                    llContactlist.setVisibility(View.VISIBLE);
                    llUserRequestList.setVisibility(View.GONE);
                    pd = new ProgressDialog(getActivity());
                    pd.setMessage("Loading");
                    pd.setCancelable(false);
                    pd.show();
                    sourceList = new ArrayList<>();
                    sourceList.clear();

                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        // Marshmallow+
                        if (!hasPermissions(getActivity(), PERMISSIONS)) {
                            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_REQUEST_CODE);
                        } else {
                            String contacts = getContacts();
                            Log.e("CONTACT LIST", ">> " + contacts);
                            if (sourceList.size() > 0) {
// add elements to al, including duplicates
                                //Converting ArrayList to HashSet to remove duplicates
                                HashSet<HashMap<String, String>> listToSet = new HashSet<HashMap<String, String>>(sourceList);
//Creating Arraylist without duplicate values
                                ArrayList<HashMap<String, String>> listWithoutDuplicates = new ArrayList<HashMap<String, String>>(listToSet);

                                inviteMsgAdapter = new InviteMsgAdapter(getActivity(), listWithoutDuplicates, getActivity().getFragmentManager());
                                lvInviteMsg.setAdapter(inviteMsgAdapter);
                                inviteMsgAdapter.notifyDataSetChanged();
                            }else{
                                Toast.makeText(getActivity(), "No Contact List Found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        //below Marshmallow
                        String contacts = getContacts();
                        Log.e("CONTACT LIST", ">> " + contacts);
                        if (sourceList.size() > 0) {
// add elements to al, including duplicates
                            //Converting ArrayList to HashSet to remove duplicates
                            HashSet<HashMap<String, String>> listToSet = new HashSet<HashMap<String, String>>(sourceList);
//Creating Arraylist without duplicate values
                            ArrayList<HashMap<String, String>> listWithoutDuplicates = new ArrayList<HashMap<String, String>>(listToSet);

                            inviteMsgAdapter = new InviteMsgAdapter(getActivity(), listWithoutDuplicates, getActivity().getFragmentManager());
                            lvInviteMsg.setAdapter(inviteMsgAdapter);
                            inviteMsgAdapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(getActivity(), "No Contact List Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                    pd.dismiss();
                }
                break;

            case R.id.tvUserRequestList:
                if (llUserRequestList.getVisibility() == View.GONE) {
                    tvContactIndicator.setVisibility(View.GONE);
                    tvRequestIndicator.setVisibility(View.VISIBLE);

                    llUserRequestList.setVisibility(View.VISIBLE);
                    llContactlist.setVisibility(View.GONE);
                    if (Util.isOnline(getActivity())) {
                        new getFriendRequestList().execute();
                    } else {
                        Toast.makeText(getActivity(), Constant.network_error, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }


    public class getFriendRequestList extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Loading");
            pd.setCancelable(false);
            pd.show();
            userRequestList = new ArrayList<HashMap<String, String>>();
            userRequestList.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
// http://web-medico.com/web2/intrisfeed/api/request_list.php?id=3

            HashMap<String, String> hashmap = new HashMap<String,String>();
            hashmap.put("id",Util.ReadSharePrefrence(getActivity(),Constant.SHRED_PR.KEY_USERID));
            response = Util.getResponseofPost(Constant.BASE_URL + "request_list.php", hashmap);
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
                        Log.e("uid",">> "+obj.getString("id"));
                        Log.e("NAME",">> "+obj.getString("name"));
                        hashMap.put("name", obj.getString("name"));
                        hashMap.put("email", obj.getString("email"));
//                        hashMap.put("phone", obj.getString("phone"));
                        hashMap.put("image", obj.getString("image"));
                        hashMap.put("categories", obj.getString("categories"));
                        hashMap.put("message",obj.getString("message"));
                        userRequestList.add(hashMap);
                    }


                    adapterSendRequest = new LinkRequestAdapter(getActivity(), userRequestList, getActivity().getFragmentManager());
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


    public String getContacts() {
        String contacts = "";

        listContacts.clear();
        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
//      Log.e("Total >>>>>> ",""+phones.getCount());

        if (phones != null) {
//            Log.e("Phone Count = ", ">>" + phones.getCount());
            phones.moveToFirst();
            do {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                Log.e("Name "," "+name+" "+phoneNumber);
//                String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
//                Uri photo = getPhotoUri(id);

                phoneNumber = phoneNumber.replaceAll("\\D+", "");
                if (phoneNumber.length() >= 10) {
                    phoneNumber = Util.lastN(phoneNumber, 10);
//                    if (contacts.length() == 0) contacts = phoneNumber;
//                    else contacts = contacts + "," + phoneNumber;

                    HashMap<String, String> hashMap1 = new HashMap<String, String>();
                    hashMap1.put("member_id", "");
                    hashMap1.put("member_name", "" + name);
                    hashMap1.put("member_image", "");
                    hashMap1.put("member_number", "" + phoneNumber);
                    hashMap1.put("selected", "2");
                    sourceList.add(hashMap1);
                }
            } while (phones.moveToNext());
            phones.close();
        }

//        Log.e("sourceList >>>>> ","Total = "+sourceList.size());

        listContacts = new ArrayList(new HashSet(
                sourceList));
        for (int i = 0; i < listContacts.size(); i++) {
            String number = listContacts.get(i).get("member_number");
            if (number.length() >= 10) {
                number = Util.lastN(number, 10);
                if (contacts.length() == 0) contacts = number;
                else contacts = contacts + "," + number;
//                Log.e("DATA",""+ i +">> " + number);
            }
        }

//        Log.e("Contact",""+contacts);
//        Log.e("sourceList >>>>> ","Total = "+listContacts.size());

        IgnoreCaseComparator icc = new IgnoreCaseComparator();
        Collections.sort(listContacts, icc);

        return contacts;

    }

    class IgnoreCaseComparator implements Comparator<HashMap<String, String>> {
        public int compare(HashMap<String, String> hashMap1, HashMap<String, String> hashMap2) {
            String strA = hashMap1.get("member_name").toLowerCase();
            String strB = hashMap2.get("member_name").toLowerCase();

            return strA.compareToIgnoreCase(strB);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}
