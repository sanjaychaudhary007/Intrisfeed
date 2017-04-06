package com.archi.intrisfeed.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.archi.intrisfeed.R;
import com.archi.intrisfeed.adapter.FeedAdapter;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;
import com.archi.intrisfeed.util.Utility;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by archi_info on 10/14/2016.
 */
public class FeedFragment extends Fragment implements View.OnClickListener {
    public static int REQUEST_TAKE_GALLERY_PICTURE = 101;
    public static int REQUEST_TAKE_GALLERY_VIDEO = 102;
    private int EDIT_IMAGE_FILTER = 103;
    ArrayList<HashMap<String, String>> allPostList;
    public FeedAdapter adapter;
    HashMap<String, String> hashMap;
    public TextView tvPostFeed;
    public ListView lvFeedList;

    //    private ImageView ivFeedImage;
//    private VideoView vvFeedVideo;
    public ImageView ivUserProfilePic;
    public TextView tvCamera, tvVideo;
    public File imageFile, videoFile;
    public EditText etTitleMsg;
    public String userFeedMsgStr;
    public LinearLayout llImageVideoContainer, llCommentUser;
    public Snackbar snackbar;
    public Uri imageuploadUri, videouploadUri;
    public String imgDecodableString, videoDecodeString;
    public CoordinatorLayout snackbarCoordinatorLayout;
    public String isImageOrVideo;
    //    public String  selectedImagePath;
    String filePath = "";
    String videopath = "";
    public ProgressDialog pd;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        lvFeedList = (ListView) rootView.findViewById(R.id.lvFeedListview);
        tvPostFeed = (TextView) rootView.findViewById(R.id.tvPostFeed_SharingFragment);
        tvCamera = (TextView) rootView.findViewById(R.id.tvCamera_SharingFragment);
        tvVideo = (TextView) rootView.findViewById(R.id.tvVideo_SharingFragment);
        etTitleMsg = (EditText) rootView.findViewById(R.id.etTiltePost_SharingFragment);
        llImageVideoContainer = (LinearLayout) rootView.findViewById(R.id.llImageVideoContainer);
        llCommentUser = (LinearLayout) rootView.findViewById(R.id.llCommentUser);
        snackbarCoordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.snackbarCoordinatorLayout);
//        llImageVideo = (LinearLayout) rootView.findViewById(R.id.llImageVideo_FeedFragment);

        ivUserProfilePic = (ImageView) rootView.findViewById(R.id.ivUserProfile_SharingFragment);

        Picasso.with(getActivity()).load(Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_PROFILE_PIC)).error(R.drawable.ic_user).placeholder(R.drawable.ic_user).into(ivUserProfilePic);
        if (Util.isOnline(getActivity())) {
            new getAllPostFeed().execute();
        } else {
            Toast.makeText(getActivity(), "" + Constant.network_error, Toast.LENGTH_SHORT).show();
//            snackbar = Snackbar
//                    .make(snackbarCoordinatorLayout, "" + Constant.network_error, Snackbar.LENGTH_LONG);
//            snackbar.show();
        }
        tvPostFeed.setOnClickListener(this);
        tvCamera.setOnClickListener(this);
        tvVideo.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCamera_SharingFragment:
                isImageOrVideo = "0";
//                if(!videopath.equalsIgnoreCase("")) {
                boolean result = Utility.checkPermission(getActivity());
                if (result) {
                    if(videopath.equalsIgnoreCase("")) {
                        galleryIntent();
                    }else{
                        Toast.makeText(getActivity(), "You can post Image Or Video", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Permission is denied, you cant access gallery", Toast.LENGTH_SHORT).show();
//                        snackbar = Snackbar
//                                .make(snackbarCoordinatorLayout, "Permission is denied, you cant access gallery", Snackbar.LENGTH_LONG);
//                        snackbar.show();
                }
//                }else{
//                    snackbar = Snackbar
//                            .make(snackbarCoordinatorLayout, "You can upload image / Video.", Snackbar.LENGTH_LONG);
//                    snackbar.show();
//                }
                break;

            case R.id.tvVideo_SharingFragment:
                isImageOrVideo = "1";
//                if(!filePath.equalsIgnoreCase("")) {
                boolean result1 = Utility.checkPermission(getActivity());
                if (result1) {
                    if(filePath.equalsIgnoreCase("")) {
                        videoIntent();
                    }else{
                        Toast.makeText(getActivity(), "You can post Image Or Video", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Permission is denied, you cant access gallery", Toast.LENGTH_SHORT).show();
//                        snackbar = Snackbar
//                                .make(snackbarCoordinatorLayout, "Permission is denied, you cant access gall", Snackbar.LENGTH_LONG);
//                        snackbar.show();
                }
//                }else{
//                    snackbar = Snackbar
//                            .make(snackbarCoordinatorLayout, "You can upload image / Video.", Snackbar.LENGTH_LONG);
//                    snackbar.show();
//                }
                break;

            case R.id.tvPostFeed_SharingFragment:
                if (Util.isOnline(getActivity())) {
                    if (!etTitleMsg.getText().toString().equalsIgnoreCase("")) {

                        userFeedMsgStr = etTitleMsg.getText().toString();
//                            new postUserFeed().execute();

                        if (!filePath.equalsIgnoreCase("") && videopath.equalsIgnoreCase("")) {
                            postuserImageFeed();
                        }

                        if (filePath.equalsIgnoreCase("") && !videopath.equalsIgnoreCase("")) {
                            postuserVideoFeed();
                        }

                        if (!filePath.equalsIgnoreCase("") && !videopath.equalsIgnoreCase("")) {
                            Toast.makeText(getActivity(),"You can Post Image Or Video",Toast.LENGTH_SHORT).show();
                        }

//                        } else {
////                            snackbar = Snackbar
////                                    .make(snackbarCoordinatorLayout, "Please Enter Select Image Or Video", Snackbar.LENGTH_LONG);
////                            snackbar.show();
//                            Toast.makeText(getActivity(), "Please Enter Select Image Or Video", Toast.LENGTH_SHORT).show();
//                        }
                    } else {
//                        snackbar = Snackbar
//                                .make(snackbarCoordinatorLayout, "Please Enter Feed Message", Snackbar.LENGTH_LONG);
//                        snackbar.show();
                        Toast.makeText(getActivity(), "Please Enter Feed Message", Toast.LENGTH_SHORT).show();
                    }

                } else {
//                    snackbar = Snackbar
//                            .make(snackbarCoordinatorLayout, "" + Constant.network_error, Snackbar.LENGTH_LONG);
//                    snackbar.show();
                    Toast.makeText(getActivity(), "" + Constant.network_error, Toast.LENGTH_SHORT).show();
                }
                break;


        }
    }


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
//http://web-medico.com/web2/intrisfeed/api/get_all_post.php
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
                    for (int i = 0; i < objArray.length(); i++) {
                        JSONObject obj = objArray.getJSONObject(i);
                        hashMap = new HashMap<String, String>();
                        hashMap.put("id", obj.getString("id"));
                        hashMap.put("userid", obj.getString("userid"));
                        hashMap.put("text", obj.getString("text"));
                        hashMap.put("image", obj.getString("image"));
                        hashMap.put("video", obj.getString("video"));
                        hashMap.put("comments", obj.getJSONArray("comments").toString());
                        allPostList.add(hashMap);
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
            adapter = new FeedAdapter(getActivity(), allPostList, getActivity().getFragmentManager());
            lvFeedList.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }
    }


    public void postuserImageFeed() {
//        http://web-medico.com/web2/intrisfeed/api/upload_post.php?id=18&text=first%20post&image=1.jpg&video=55.mp4
        Log.e(">>>>>>>>>", ">>>>>>>Image DATA >>>>>>>>>>>");
        Log.e("UID", "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID));
        Log.e("userFeedMsgStr", ">>" + userFeedMsgStr);
        Log.e("image", ">>" + filePath);
        Log.e("video", ">>" + videopath);
//        http://web-medico.com/web2/intrisfeed/api/insert_content_post.php?user_id=17&category=abc&content_title=abc&content_details=xyz
// &content_link=xyz.com&content_author_email=abc@gmail.com&image=1.jpg&video=11.mp4&doc=1.pdf

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.show();
        Ion.with(getActivity())
                .load(Constant.BASE_URL + "upload_post.php")
                .progressDialog(pd)
                .setMultipartParameter("id", Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID))
                .setMultipartParameter("text", userFeedMsgStr)
                .setMultipartFile("image", new File(filePath))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (pd != null) {
                            pd.dismiss();
                        }
                        Log.e("msg", "res  " + result);
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String status = jsonObject.getString("status");
                            if (status.equalsIgnoreCase("true")) {
                                filePath = "";
                                etTitleMsg.setText("");
                                ((ViewGroup) llImageVideoContainer.getParent()).removeView(llImageVideoContainer);
                                Toast.makeText(getActivity(), "Post Feed successfully", Toast.LENGTH_SHORT).show();
//                                snackbar = Snackbar
//                                        .make(snackbarCoordinatorLayout, "Post Feed successfully", Snackbar.LENGTH_LONG);
//                                snackbar.show();
                            } else {
/*                                snackbar = Snackbar
                                        .make(snackbarCoordinatorLayout, "Something Wrong", Snackbar.LENGTH_LONG);
                                snackbar.show();*/
                                Toast.makeText(getActivity(), "Something Wrong", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            Log.d("msg", "error   " + e1.getMessage());
                        }

                    }
                });


    }

    public void postuserVideoFeed() {
//        http://web-medico.com/web2/intrisfeed/api/upload_post.php?id=18&text=first%20post&image=1.jpg&video=55.mp4
        Log.e(">>>>>>>>>", ">>>>>>> DATA >>>>>>>>>>>");
        Log.e("UID", "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID));
        Log.e("userFeedMsgStr", ">>" + userFeedMsgStr);
        Log.e("image", ">>" + filePath);
        Log.e("video", ">>" + videopath);
//        http://web-medico.com/web2/intrisfeed/api/insert_content_post.php?user_id=17&category=abc&content_title=abc&content_details=xyz
// &content_link=xyz.com&content_author_email=abc@gmail.com&image=1.jpg&video=11.mp4&doc=1.pdf
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.show();
        Ion.with(getActivity())
                .load(Constant.BASE_URL + "upload_post.php")
                .progressDialog(pd)
                .setMultipartParameter("id", Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID))
                .setMultipartParameter("text", userFeedMsgStr)
                .setMultipartFile("video", new File(videopath))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Log.e("msg", "res  " + result);
                        Log.e("EXCEPTION ", "EX  " + e);
                        if (pd != null) {
                            pd.dismiss();
                        }

                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String status = jsonObject.getString("status");
                            if (status.equalsIgnoreCase("true")) {
                                videopath = "";
                                Toast.makeText(getActivity(), "Post Feed successfully", Toast.LENGTH_SHORT).show();
                                etTitleMsg.setText("");
                                ((ViewGroup) llImageVideoContainer.getParent()).removeView(llImageVideoContainer);
//                                snackbar = Snackbar
//                                        .make(snackbarCoordinatorLayout, "Post Feed successfully", Snackbar.LENGTH_LONG);
//                                snackbar.show();
                            } else {
//                                snackbar = Snackbar
//                                        .make(snackbarCoordinatorLayout, "Something Wrong", Snackbar.LENGTH_LONG);
//                                snackbar.show();
                                Toast.makeText(getActivity(), "Something Wrong", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            Log.d("msg", "error   " + e1.getMessage());
                        }

                    }
                });


    }

//    public class postUserFeed extends AsyncTask<String, String, String> {
//        ProgressDialog pd;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pd = new ProgressDialog(getActivity());
//            pd.setMessage("Loading");
//            pd.setCancelable(false);
//            pd.show();
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
////http://web-medico.com/web2/intrisfeed/api/upload_post.php?id=18&text=first%20post&image=1.jpg&video=55.mp4
//            String response = "";
//            hashMap = new HashMap<String, String>();
//            hashMap.put("id", Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID));
//            hashMap.put("text", userFeedMsgStr);
//            if (imageFile != null) {
//                hashMap.put("image", "" + imageFile);
////                File file1 = imageFile;
////                Ion ion = Ion.with(getActivity()).load(file1).
//
//            } else {
//                hashMap.put("image", "");
//            }
//            if (videoFile != null) {
//                hashMap.put("file", "" + videoFile);
//            } else {
//                hashMap.put("file", "");
//            }
//
//            response = Util.getResponseofPost(Constant.BASE_URL + "upload_post.php", hashMap);
//            Log.e("RESULT", ">>" + response);
//            return response;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
////            pd.dismiss();
//            super.onPostExecute(s);
//            pd.dismiss();
//            Log.d("Response", "M" + s);
//            try {
//                JSONObject object = new JSONObject(s.toString());
//                if (object.getString("successful").equalsIgnoreCase("true")) {
//                    JSONArray objArray = object.getJSONArray("data");
//                    for (int i = 0; i < objArray.length(); i++) {
//                        JSONObject obj = objArray.getJSONObject(i);
//
//                    }
//
//                } else {
//                    Toast.makeText(getActivity(), "" + object.getString("msg"), Toast.LENGTH_SHORT).show();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_PICTURE) {
                Uri selectedImageUri = data.getData();
                Log.e("PICTURE LINK", ">> " + selectedImageUri.getPath());
                // OI FILE Manager


                Intent intentEditImage = new Intent(Intent.ACTION_EDIT);
                intentEditImage.setDataAndType(selectedImageUri, "image/*");
                intentEditImage.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intentEditImage, EDIT_IMAGE_FILTER);

//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
//                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(100, 100);
//                    ImageView image = new ImageView(getActivity());
//                    image.setLayoutParams(params);
//                    image.setImageBitmap(bitmap);
//                    llImageVideoContainer.addView(image);
                // Log.d(TAG, String.valueOf(bitmap));
//                    ivFeedImage.setImageBitmap(bitmap);
//                } catch (IOException e) {
//                    e.printStackTrace();
            }else if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
            Uri selectedVideoUri = data.getData();
            Log.e("Video LINK", ">> " + selectedVideoUri.getPath());
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                // Do something for 19 and above versions
                // OI FILE Manager
                String wholeID = DocumentsContract.getDocumentId(selectedVideoUri);

                // Split at colon, use second item in the array
                String id = wholeID.split(":")[1];

                String[] column = {MediaStore.Images.Media.DATA};

                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";

                Cursor cursor = getActivity().getContentResolver().
                        query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                column, sel, new String[]{id}, null);
                int columnIndex = cursor.getColumnIndex(column[0]);

                if (cursor.moveToFirst()) {
                    videopath = cursor.getString(columnIndex);
                }
                cursor.close();
                String imageName = videopath.substring(videopath.lastIndexOf('/') + 1);

                Log.e("Video Path", "Video Path " + videopath);
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
//                    String imageName = path.substring(path.lastIndexOf('/') + 1);
//                        String imageName = System.currentTimeMillis() + ".png";
//                        edtProductName.setText(imageName);
//                    setImageFromIntent(filePath);

            } else {

                videopath = getPathFromURI(selectedVideoUri);
                Log.e("Video Path", ">> " + videopath);
                String videoName = videopath.substring(videopath.lastIndexOf('/') + 1);
            }

            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(100, 100);
            ImageView image = new ImageView(getActivity());
            image.setLayoutParams(params);
            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videopath,
                    MediaStore.Images.Thumbnails.MINI_KIND);
            image.setImageBitmap(thumbnail);
            llImageVideoContainer.addView(image);
        } else if (requestCode == EDIT_IMAGE_FILTER) {

                Uri uri = data.getData();
                Log.e("EDIT URI", ">> " + uri);
//                        imageAttachment.setImageURI(uri);


                try {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        // Do something for 19 and above versions
                        // OI FILE Manager
                        String wholeID = DocumentsContract.getDocumentId(uri);

                        // Split at colon, use second item in the array
                        String id = wholeID.split(":")[1];

                        String[] column = {MediaStore.Images.Media.DATA};

                        // where id is equal to
                        String sel = MediaStore.Images.Media._ID + "=?";

                        Cursor cursor = getActivity().getContentResolver().
                                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        column, sel, new String[]{id}, null);
                        int columnIndex = cursor.getColumnIndex(column[0]);

                        if (cursor.moveToFirst()) {
                            filePath = cursor.getString(columnIndex);
                        }
                        cursor.close();
                        String imageName = filePath.substring(filePath.lastIndexOf('/') + 1);

                        Log.e("PATH", "Image Path : " + filePath);

//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
//                        String imageName = path.substring(path.lastIndexOf('/') + 1);
//                        String imageName = System.currentTimeMillis() + ".png";
//                        edtProductName.setText(imageName);
//                        setImageFromIntent(filePath);

                    } else {
                        filePath = getPathFromURI(uri);
                        Log.e("File Path", ">> " + filePath);
                        String imageName = filePath.substring(filePath.lastIndexOf('/') + 1);
                    }

                    Bitmap bmp = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
                    Log.e("BITMAP", "AA " + bmp);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(100, 100);
                    ImageView image = new ImageView(getActivity());
                    image.setLayoutParams(params);
                    image.setImageBitmap(bmp);
                    llImageVideoContainer.addView(image);
                    Log.d("", String.valueOf(bmp));
                } catch (IOException e) {
                    e.printStackTrace();
                }

//
//                Bitmap bitmap = null;
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }

    /* Get the real path from the URI */
    public String getPathFromURI(Uri contentUri) {

        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(proj[0]);
            res = cursor.getString(columnIndex);
        }

        cursor.close();
        return res;

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_WRITE_EXTERNAL_STORAGE_AND_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    switch (isImageOrVideo) {
                        case "0":
                            galleryIntent();
                            break;

                        case "1":
                            videoIntent();
                            break;
                    }
                } else {
                    //code for deny
                    Toast.makeText(getActivity(), "No Permission Granted, Access Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void galleryIntent() {

        Intent choosePhotoIntent = new Intent();
        choosePhotoIntent.setType("image/*");
        choosePhotoIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(choosePhotoIntent, "Select Image"), REQUEST_TAKE_GALLERY_PICTURE);
    }

    public void videoIntent() {

        Intent chooseVideoIntent = new Intent();
        chooseVideoIntent.setType("video/*");
        chooseVideoIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(chooseVideoIntent, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO);
    }

}
