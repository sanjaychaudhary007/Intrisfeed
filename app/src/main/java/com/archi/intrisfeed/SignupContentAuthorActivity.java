package com.archi.intrisfeed;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;
import com.archi.intrisfeed.util.Utility;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

import static android.content.Intent.ACTION_GET_CONTENT;

/**
 * Created by archi_info on 10/22/2016.
 * API Name: add content_author   URL:
 * http://web-medico.com/web2/intrisfeed/api/add_author.php?name=samir&user_id=16&city=junagadh&country=india&email=samir@gmail.com&profile_image=abc.png
 * Parameters:user_id,name,city,country,email,profile_image
 * Description: get the user_id,name,city,country,email,profile_image and register the add content_author,,,,
 * all fields are mendatory and user_id should exist in the register_memeber.
 */

public class SignupContentAuthorActivity extends Activity implements View.OnClickListener {
    Snackbar snackbar;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA};
    private ImageView ivProfileImage;
    CoordinatorLayout snackbarCoordinatorLayout;
    private String base64Image = "";
    private EditText etName, etEmail, etPassword, etConfirmPassword, etCountry, etCity;
    private Button btSignup;
    private String nameStr, emailStr, phoneStr, passwordStr, categoriesSelectedStr, cityStr, countryStr;
    private ImageView ivSelectImage;
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    String userChoosenTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_content_author);
        init();
    }

    private void init() {
        snackbarCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.snackbarCoordinatorLayout);
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage_Signup_content_author);
        Picasso.with(SignupContentAuthorActivity.this).load(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_PROFILE_PIC)).error(R.drawable.ic_user).placeholder(R.drawable.default_img).into(ivProfileImage);

        etName = (EditText) findViewById(R.id.etName_Signup_content_author);
        etCountry = (EditText) findViewById(R.id.etCountry_Signup_content_author);
        etCity = (EditText) findViewById(R.id.etCity_Signup_content_author);
        etEmail = (EditText) findViewById(R.id.etEmail_Signup_content_author);
        ivSelectImage = (ImageView) findViewById(R.id.ivSelectImage_Signup_content_author);
        btSignup = (Button) findViewById(R.id.btSignup_content_author);
        btSignup.setOnClickListener(this);
        ivProfileImage.setOnClickListener(this);
        ivSelectImage.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivSelectImage_Signup_content_author:
                selectImage();
                break;

            case R.id.btSignup_content_author:
                if (!etName.getText().toString().equalsIgnoreCase("")) {
                    if (!etCity.getText().toString().equalsIgnoreCase("")) {
                        if (!etCountry.getText().toString().equalsIgnoreCase("")) {
                            if (!etEmail.getText().toString().equalsIgnoreCase("")) {
                                if (EMAIL_ADDRESS_PATTERN.matcher(etEmail.getText().toString()).matches()) {
                                    if (!base64Image.equalsIgnoreCase("")) {
                                        nameStr = etName.getText().toString();
//                                                    phoneStr = etEmail.getText().toString();
                                        emailStr = etEmail.getText().toString();
                                        cityStr = etCity.getText().toString();
                                        countryStr = etCountry.getText().toString();
                                        Log.e("NAME", ">>" + nameStr);
                                        Log.e("Email", ">>" + emailStr);

                                            new signupUserContentUserData().execute();

                                    } else {
                                        snackbar = Snackbar
                                                .make(snackbarCoordinatorLayout, "Please Select Profile Image.", Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                    }

                                } else {
                                    snackbar = Snackbar
                                            .make(snackbarCoordinatorLayout, "Please Enter Valid Email Address.", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                            } else {
                                snackbar = Snackbar
                                        .make(snackbarCoordinatorLayout, "Please Enter Your Email Address.", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        } else {
                            snackbar = Snackbar
                                    .make(snackbarCoordinatorLayout, "Please Enter Your Country.", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    } else {
                        snackbar = Snackbar
                                .make(snackbarCoordinatorLayout, "Please Enter Your City.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                } else {
                    snackbar = Snackbar
                            .make(snackbarCoordinatorLayout, "Please Enter Your Name.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

                break;
        }

    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupContentAuthorActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(SignupContentAuthorActivity.this);
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

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_WRITE_EXTERNAL_STORAGE_AND_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Gallery"))
                        galleryIntent();
                } else {
                    //code for deny
                    Toast.makeText(SignupContentAuthorActivity.this, "No Permission Granted, Access Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
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
            snackbar = Snackbar
                    .make(snackbarCoordinatorLayout, "You haven't picked Image", Snackbar.LENGTH_LONG);
            snackbar.show();
//            Snackbar("You haven't picked Image");
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap resizedbitmap = (Bitmap) data.getExtras().get("data");
        Log.e("CAMERA", ">>>> " + resizedbitmap);
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("profile", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        File mypath = new File(directory, System.currentTimeMillis() + ".png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            resizedbitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Picasso.with(getApplicationContext()).load(mypath).into(ivProfileImage);
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
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                Log.e("BITMAP", ">>>>> " + bitmap);
                if (bitmap != null) {
                    // convert bitmap to base64
                    base64Image = Util.bitmapToBase64(bitmap);
                    Picasso.with(getApplicationContext()).load(selectedImagePath).into(ivProfileImage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            snackbar = Snackbar
                    .make(snackbarCoordinatorLayout, "No Data Found", Snackbar.LENGTH_LONG);
            snackbar.show();

        }

    }

    public class signupUserContentUserData extends AsyncTask<String, String, String> {
        KProgressHUD hud;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            hud = KProgressHUD.create(SignupContentAuthorActivity.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .show();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
// http://web-medico.com/web2/intrisfeed/api/add_author.php?name=samir&user_id=16&city=junagadh&country=india&email=samir@gmail.com&profile_image=abc.png
            HashMap<String, String> hashmap = new HashMap<String, String>();
            hashmap.put("user_id",Util.ReadSharePrefrence(getApplicationContext(),Constant.SHRED_PR.KEY_USERID));
            hashmap.put("name", nameStr);
            hashmap.put("city", cityStr);
            hashmap.put("country", countryStr);
            hashmap.put("email", emailStr);
            hashmap.put("profile_image", base64Image);
            Log.e("NAME", ">> " + nameStr);
            response = Util.getResponseofPost(Constant.BASE_URL + "add_author.php", hashmap);
            Log.e("RESULT", ">>" + response);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
//            pd.dismiss();
            super.onPostExecute(s);

            Log.d("Response", "" + s);
            try {
                JSONObject object = new JSONObject(s.toString());
                if (object.getString("successful").equalsIgnoreCase("true")) {
                    String login_id = object.getString("id");
                    JSONObject obj = object.getJSONObject("data");
                    String email = obj.get("email").toString();
                    Log.e("EMAIL ",">>> "+email);
                    Util.WriteSharePrefrence(getApplicationContext(),Constant.SHRED_PR.KEY_CONTENT_AUTHOR_EMAIL,email);
                    Util.WriteSharePrefrence(getApplicationContext(),Constant.SHRED_PR.KEY_IS_CONTENT_AUTHOR,"1");
                    snackbar = Snackbar
                            .make(snackbarCoordinatorLayout, ""+object.getString("msg"), Snackbar.LENGTH_LONG);
                    snackbar.show();
                    finish();
                } else {
                    hud.dismiss();
                    snackbar = Snackbar
                            .make(snackbarCoordinatorLayout, ""+object.getString("msg"), Snackbar.LENGTH_LONG);
                    snackbar.show();
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
