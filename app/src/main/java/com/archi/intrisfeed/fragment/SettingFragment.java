package com.archi.intrisfeed.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.archi.intrisfeed.R;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;
import com.archi.intrisfeed.util.Utility;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.ACTION_GET_CONTENT;
import static com.archi.intrisfeed.SignupActivity.getImageOrientation;
import static com.archi.intrisfeed.util.Util.getResponseofPost;

/**
 * Created by archi_info on 12/10/2016.
 */

public class SettingFragment extends android.support.v4.app.Fragment implements View.OnClickListener{
    public TextView tvEmail;
    public EditText etName;
    public ImageView ivDrawerProfile,ivProfilePic,ivPicProfilePick;
    public Button btnUpdate,btnChagePassword;
    public Util utils;
    public String userChoosenTask,base64Image="",strUserName;
    public int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        tvEmail = (TextView) rootView.findViewById(R.id.fragment_setting_tv_email);
        etName = (EditText) rootView.findViewById(R.id.fragment_setting_tv_name);
        ivProfilePic = (ImageView)rootView.findViewById(R.id.fragment_setting_iv_profile);
        btnUpdate = (Button)rootView.findViewById(R.id.fragment_setting_btn_update);
        btnChagePassword = (Button)rootView.findViewById(R.id.fragment_setting_change_password);
        ivPicProfilePick = (ImageView)rootView.findViewById(R.id.fragment_setting_iv_pick_image);
        ivDrawerProfile = (ImageView)rootView.findViewById(R.id.ivUserImage);
       // ivDrawerProfile = (ImageView)findViewById(R.id.ivUserImage);

        utils = new Util();
        init();
        return rootView;
    }

    private void init()
    {

        etName.setText(utils.ReadSharePrefrence(getActivity(),Constant.SHRED_PR.KEY_USER_NAME));
        tvEmail.setText(utils.ReadSharePrefrence(getActivity(),Constant.SHRED_PR.KEY_USER_EMAIL));
        Picasso.with(getActivity()).load(Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_PROFILE_PIC)).error(R.drawable.ic_user).into(ivProfilePic);
        ivPicProfilePick.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fragment_setting_iv_pick_image:
                pickImage();
                break;
            case R.id.fragment_setting_btn_update:
                if (!etName.getText().toString().toString().equals(""))
                {
                    strUserName = etName.getText().toString();
                    new updateProfile().execute();
                }
                else
                {
                    if (base64Image.equals(""))
                    {
                        Toast.makeText(getActivity(),"please select image or text",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        new updateProfile().execute();
                    }

                }
                break;
        }
    }



    private void pickImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(getActivity());
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Gallery")) {
                    userChoosenTask = "Choose from Gallery";
                    if (result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        } else {
            Toast.makeText(getContext(),"You haven't picked Image",Toast.LENGTH_SHORT).show();
        }




    }

    private void onCaptureImageResult(Intent data)
    {
        Bitmap resizedbitmap =  (Bitmap) data.getExtras().get("data");
        Log.e("CAMERA",">>>> "+resizedbitmap);
        ContextWrapper cw = new ContextWrapper(getActivity());
        File directory = cw.getDir("profile", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        File mypath = new File(directory,System.currentTimeMillis() +".png");

        Matrix matrix = new Matrix();
        matrix.postRotate(getImageOrientation(mypath.getPath()));
        Bitmap rotatedBitmap = Bitmap.createBitmap(resizedbitmap, 0, 0, resizedbitmap.getWidth(),
                resizedbitmap.getHeight(), matrix, true);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Picasso.with(getActivity()).load(mypath).into(ivProfilePic);
        } catch (Exception e) {
            Log.e("SAVE_IMAGE", e.getMessage(), e);
        }

    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bitmap = null;
        Uri selectedImagePath = null;
        if (data != null) {
            try {
                selectedImagePath = data.getData();
                Log.e("selectedImagePath", "GLRY " + selectedImagePath);
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                Log.e("BITMAP",">>>>> "+bitmap);
                if(bitmap != null) {
                    // convert bitmap to base64
                    base64Image = Util.bitmapToBase64(bitmap);
                    Picasso.with(getActivity()).load(selectedImagePath).into(ivProfilePic);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getActivity(), "No Data Found", Toast.LENGTH_SHORT).show();
        }

    }



    private class updateProfile extends AsyncTask<String,String,String> {
        ProgressDialog pd = new ProgressDialog(getActivity());
        KProgressHUD hud;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            hud = KProgressHUD.create(getActivity())
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .show();

        }
        @Override
        protected String doInBackground(String... params) {
            String userId = utils.ReadSharePrefrence(getActivity(),Constant.SHRED_PR.KEY_USERID);
            //http://181.224.157.105/~hirepeop/host1/intrisfeed/api/edit_user_profile.php?user_id=17&name=abc&image=abc.jpg

            String response;
            if (base64Image.length() <= 0)
            {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",userId);
                hashMap.put("name",strUserName);
                response = Util.getResponseofPost(Constant.BASE_URL + "edit_user_profile.php", hashMap);
            }
            else
            {HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",userId);
                hashMap.put("name",strUserName);
                hashMap.put("image",base64Image);
                response = getResponseofPost(Constant.BASE_URL + "edit_user_profile.php", hashMap);
            }
            return response;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("response","upload response :"+s);
            try {
                JSONObject jsonObject = new JSONObject(s);

                if(jsonObject.getString("status").equalsIgnoreCase("true"))
                {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                    String name = jsonObject1.getString("name");
                    String image = jsonObject1.getString("image");
                    Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USER_NAME,name);
                    Util.WriteSharePrefrence(getActivity(),Constant.SHRED_PR.KEY_PROFILE_PIC,image);
                    etName.setText(name);
                    Picasso.with(getActivity()).load(image).placeholder(R.drawable.ic_user).into(ivProfilePic);
                   // Picasso.with(getActivity()).load(image).placeholder(R.drawable.ic_user).into(ivDrawerProfile);
                }
                else
                {
                    Toast.makeText(getActivity(),jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                }


            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Toast.makeText(getActivity(),"somrthing went wrong",Toast.LENGTH_SHORT).show();
            }


            hud.dismiss();
    }
}}
