package com.archi.intrisfeed.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.archi.intrisfeed.R;
import com.archi.intrisfeed.util.Utility;
import com.google.android.gms.plus.PlusShare;

import java.io.File;
import java.util.List;

import static android.content.Intent.ACTION_GET_CONTENT;

/**
 * Created by archi_info on 9/24/2016.
 */
public class SocialNetworksFragment extends Fragment implements View.OnClickListener {
    private static final int PINTEREST = 1001;
    private static final int SELECT_IMAGE = 1000;
    TextView ibFacebook, tvTwitter, tvGooglePluse, tvPintrest;
    String imagePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_social_networks, container, false);
        ibFacebook = (TextView) rootView.findViewById(R.id.tvFacebook);
        tvTwitter = (TextView) rootView.findViewById(R.id.tvTwitter);
        tvGooglePluse = (TextView) rootView.findViewById(R.id.tvGooglePluse);
        tvPintrest = (TextView) rootView.findViewById(R.id.tvPinterest);

        ibFacebook.setOnClickListener(this);
        tvTwitter.setOnClickListener(this);
        tvGooglePluse.setOnClickListener(this);
        tvPintrest.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Intent shareIntent;
        PackageManager pm;
        List<ResolveInfo> activityList;
        switch (v.getId()) {
            case R.id.tvFacebook:
                Toast.makeText(getActivity(), "Click on Facebook", Toast.LENGTH_SHORT).show();
                String urlStr = "Check out IntrinsFeed, Download it from https://play.google.com/store/apps/details?id=com.archi.intrisfeed";
//                shareAppLinkViaFacebook(urlStr);

                shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, urlStr);
                pm = getActivity().getPackageManager();
                activityList = pm.queryIntentActivities(shareIntent, 0);
                for (final ResolveInfo app : activityList) {
                    if ((app.activityInfo.name).contains("facebook")) {
                        final ActivityInfo activity = app.activityInfo;
                        final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                        shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        shareIntent.setComponent(name);
                        startActivity(shareIntent);
                    }
                }
                break;


            case R.id.tvTwitter:
                boolean installed = appInstalledOrNot("com.twitter.android");
                if(installed) {
                    shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Check out IntrinsFeed, Download it from https://play.google.com/store/apps/details?id=com.archi.intrisfeed");
//                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile();
                    pm = getActivity().getPackageManager();
                    activityList = pm.queryIntentActivities(shareIntent, 0);
                    for (final ResolveInfo app : activityList) {
                        if ((app.activityInfo.name).contains("twitter")) {
                            final ActivityInfo activity = app.activityInfo;
                            final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                            shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                            shareIntent.setComponent(name);
                            startActivity(shareIntent);
                            break;
                        }
                    }
                }else{
                    Toast.makeText(getActivity(), "App is not currently installed on your phone",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tvGooglePluse:
                // Launch the Google+ share dialog with attribution to your app.
                shareIntent = new PlusShare.Builder(getActivity())
                        .setType("text/plain")
                        .setText("Check out IntrinsFeed, Download it")
                        .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.archi.intrisfeed"))
                        .getIntent();
                startActivityForResult(shareIntent, 0);
                break;


            case R.id.tvPinterest:
                boolean result1 = Utility.checkPermission(getActivity());
                if (result1) {
                    galleryIntent();
                }else{
                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }

                break;


//            case R.id.tvYouTube:
//                shareIntent = new Intent(android.content.Intent.ACTION_SEND);
//                shareIntent.setType("text/plain");
//                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Check out IntrinsFeed, Download it from https://play.google.com/store/apps/details?id=com.archi.intrisfeed");
////                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile();
//                pm = getActivity().getPackageManager();
//                activityList = pm.queryIntentActivities(shareIntent, 0);
//                for (final ResolveInfo app : activityList) {
//                    if ((app.activityInfo.name).contains("youtube")) {
//                        final ActivityInfo activity = app.activityInfo;
//                        final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
//                        shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//                        shareIntent.setComponent(name);
//                        startActivity(shareIntent);
//                        break;
//                    }
//                }
//                break;

        }
    }


    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PINTEREST ){
            Log.e("IMAGE SHARE","Post Successfully");
            Toast.makeText(getActivity(), "Post Successfully", Toast.LENGTH_SHORT).show();
        }else if(requestCode == SELECT_IMAGE){
            onSelectFromGalleryResult(data);
            if(!imagePath.equalsIgnoreCase("")){
                File imageFileToShare = new File(imagePath);

                Uri uri = Uri.fromFile(imageFileToShare);

                Intent sharePintrestIntent = new Intent(Intent.ACTION_SEND);
                sharePintrestIntent.setPackage("com.pinterest");
                sharePintrestIntent.putExtra("com.pinterest.EXTRA_DESCRIPTION", "Check out IntrinsFeed, Download it from https://play.google.com/store/apps/details?id=com.archi.intrisfeed");
                sharePintrestIntent.putExtra(Intent.EXTRA_STREAM, uri);
                sharePintrestIntent.setType("image/*");
                getActivity().startActivityForResult(sharePintrestIntent, PINTEREST);
            }else{
                Toast.makeText(getActivity(), "No Image Found, Please select again.", Toast.LENGTH_SHORT).show();
                galleryIntent();
            }


        }
    }


    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        Log.e("PICTURE LINK", ">> " + selectedImageUri.getPath());
        // OI FILE Manager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            // Do something for 19 and above versions
            // OI FILE Manager
            String wholeID = DocumentsContract.getDocumentId(selectedImageUri);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = { MediaStore.Images.Media.DATA };

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = getActivity().getContentResolver().
                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{ id }, null);
            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                imagePath = cursor.getString(columnIndex);
            }
            cursor.close();
            String imageName = imagePath.substring(imagePath.lastIndexOf('/') + 1);

            Log.e("PATH", "Image Path : " + imagePath);
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
//                    String imageName = path.substring(path.lastIndexOf('/') + 1);
//                        String imageName = System.currentTimeMillis() + ".png";
//                        edtProductName.setText(imageName);
//                    setImageFromIntent(filePath);
//            Picasso.with(getActivity()).load(selectedImageUri).error(R.drawable.default_img).into(ivContenteThumb);
        } else {
            imagePath = getPathFromURI(selectedImageUri);
            Log.e("File Path", ">> " + imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf('/') + 1);
//            Picasso.with(getActivity()).load(selectedImageUri).error(R.drawable.default_img).into(ivContenteThumb);
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

    public void galleryIntent(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_IMAGE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_WRITE_EXTERNAL_STORAGE_AND_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            galleryIntent();
                } else {
                    //code for deny
                    Toast.makeText(getActivity(), "No Permission Granted, Access Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }



}
