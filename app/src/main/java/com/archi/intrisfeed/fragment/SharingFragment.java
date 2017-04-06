package com.archi.intrisfeed.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.archi.intrisfeed.R;
import com.archi.intrisfeed.adapter.SharingFeedListAdapter;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by archi_info on 9/24/2016.
 */
public class SharingFragment extends Fragment {
    private static int REQUEST_TAKE_GALLERY_PICTURE = 101;
    private static int REQUEST_TAKE_GALLERY_VIDEO = 102;
    ArrayList<HashMap<String, String>> allPostList;
    private SharingFeedListAdapter adapter;
    HashMap<String, String> hashMap;
//    private TextView tvPostFeed;
    private ListView lvFeedList;
//    private ImageView ivFeedImage;
//    private VideoView vvFeedVideo;

        private String userFeedMsgStr;
    private LinearLayout llImageVideoContainer, llCommentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sharing, container, false);
//        tvSelectVideo = (TextView) rootView.findViewById(R.id.tvSelectVideo);
        lvFeedList = (ListView) rootView.findViewById(R.id.lvFeedListview);

        llImageVideoContainer = (LinearLayout) rootView.findViewById(R.id.llImageVideoContainer);

//        if(!Util.ReadSharePrefrence(getActivity(),Constant.SHRED_PR.KEY_PROFILE_PIC).toString().equalsIgnoreCase("")){
//            Picasso.with(getActivity()).load(Util.ReadSharePrefrence(getActivity(),Constant.SHRED_PR.KEY_PROFILE_PIC)).error(R.drawable.ic_user).into(ivUserProfilePic);
//        }

        if (Util.isOnline(getActivity())) {
            new getAllPostFeed().execute();
        } else {
            Toast.makeText(getActivity(), "" + Constant.network_error, Toast.LENGTH_SHORT).show();
        }
//        tvPostFeed.setOnClickListener(this);
//        tvCamera.setOnClickListener(this);
//        tvVideo.setOnClickListener(this);
        return rootView;
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.tvCamera_SharingFragment:
//                Toast.makeText(getActivity(), "POST", Toast.LENGTH_SHORT).show();
//                Intent choosePhotoIntent = new Intent();
//                choosePhotoIntent.setType("image/*");
//                choosePhotoIntent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(choosePhotoIntent, "Select Image"), REQUEST_TAKE_GALLERY_PICTURE);
//                break;
//
//            case R.id.tvVideo_SharingFragment:
//                Toast.makeText(getActivity(), "POST", Toast.LENGTH_SHORT).show();
//                Intent chooseVideoIntent = new Intent();
//                chooseVideoIntent.setType("video/*");
//                chooseVideoIntent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(chooseVideoIntent, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO);
//                break;
//
//            case R.id.tvPostFeed_SharingFragment:
//                Toast.makeText(getActivity(), "POST", Toast.LENGTH_SHORT).show();
//                if (Util.isOnline(getActivity())) {
//                    if(!etTitleMsg.getText().toString().equalsIgnoreCase("")){
//                        if(imageFile != null ||  videoFile != null) {
//                            userFeedMsgStr = etTitleMsg.getText().toString();
//                            new postUserFeed().execute();
//                        }else{
//                            Toast.makeText(getActivity(),"Please Enter Select Image Or Video", Toast.LENGTH_SHORT).show();
//                        }
//                    }else{
//                        Toast.makeText(getActivity(),"Please Enter Feed Message", Toast.LENGTH_SHORT).show();
//                    }
//
//                } else {
//                    Toast.makeText(getActivity(), "" + Constant.network_error, Toast.LENGTH_SHORT).show();
//                }
//                break;
//
//
//        }
//    }


    public class getAllPostFeed extends AsyncTask<String, String, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Loading");
            pd.setCancelable(false);
            allPostList = new ArrayList<>();
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
//http://web-medico.com/web2/intrisfeed/api/upload_post.php?id=1&text=this is my first post&image=10&file=
            String response = "";
            response = Util.getResponseofGet(Constant.BASE_URL + "get_all_post.php");
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
                    JSONArray objArray = object.getJSONArray("data");
                    if(objArray.length() > 0) {
                        for (int i = 0; i < objArray.length(); i++) {
                            JSONObject obj = objArray.getJSONObject(i);
                            hashMap = new HashMap<String, String>();
                            hashMap.put("userid", obj.getString("userid"));
                            hashMap.put("text", obj.getString("text"));
                            hashMap.put("image", obj.getString("image"));
                            hashMap.put("video", obj.getString("video"));
                            hashMap.put("comment", obj.getJSONObject("comment").toString());
                            allPostList.add(hashMap);
                        }
                    }else{
                        Toast.makeText(getActivity(), "" + object.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                    adapter = new SharingFeedListAdapter(getActivity(), allPostList, getActivity().getFragmentManager());
                    lvFeedList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "" + object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
