package com.archi.intrisfeed;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.archi.intrisfeed.quickbox_chat.ChatUtils;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;
import com.archi.intrisfeed.util.Utility;
import com.guna.libmultispinner.MultiSelectionSpinner;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.ACTION_GET_CONTENT;

/**
 * Created by archi_info on 9/21/2016.
 * GET CATEGORY LIST
 * API : http://web-medico.com/web2/intrisfeed/api/category.php
 * SIGN UP USER
 * http://web-medico.com/web2/intrisfeed/api/register.php?name=demo&phone=1234567890&email=archirayan1@gmail.com&password=12345&image=10&categories=test,teste,ffdsff,dsf,df&city=ahmedabad&country=india
 */
public class SignupActivity extends BaseActivity implements View.OnClickListener, MultiSelectionSpinner.OnMultipleItemsSelectedListener {
//    private Camera camera;
//    private int cameraId = 0;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA};
    private MultiSelectionSpinner multiSelectionSpinner;
    //data source for drop-down list
    ArrayList<String> CategoryItems;
    private CircleImageView ivProfileImage;
    private String base64Image = "";
    private EditText etName, etEmail, etPassword, etConfirmPassword, etCountry, etCity;
    private Button btSignup;
    private String nameStr, emailStr, phoneStr, passwordStr, categoriesSelectedStr="", cityStr, countryStr;
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

//    final CharSequence[] items = {"Take Photo", "Choose from Gallery",
//            "Cancel"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        init();
        CategoryItems = new ArrayList<String>();
        if (Util.isOnline(getApplicationContext())) {
            new getCategoryInterestList().execute();
        } else {
            toast(Constant.network_error);
        }

//        // do we have a camera?
//        if (!getPackageManager()
//                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
//            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
//                    .show();
//        } else {
//            cameraId = findFrontFacingCamera();
//            if (cameraId < 0) {
//                Toast.makeText(this, "No front facing camera found.",
//                        Toast.LENGTH_LONG).show();
//            } else {
//                camera = Camera.open(cameraId);
//            }
//        }

    }

    private void init() {
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.categorySpinner);
        ivProfileImage = (CircleImageView) findViewById(R.id.ivProfileImage);
        etName = (EditText) findViewById(R.id.etName_Signup);
        etCountry = (EditText) findViewById(R.id.etCountry_Signup);
        etCity = (EditText) findViewById(R.id.etCity_Signup);
//        etPhone = (EditText) findViewById(R.id.etPhone_Signup);
        etEmail = (EditText) findViewById(R.id.etEmail_Signup);
        etPassword = (EditText) findViewById(R.id.etPassword_Signup);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword_Signup);
        ivSelectImage = (ImageView) findViewById(R.id.ivSelectImage);
        btSignup = (Button) findViewById(R.id.btSignup);

        btSignup.setOnClickListener(this);
        ivProfileImage.setOnClickListener(this);
        multiSelectionSpinner.setListener(this);
        ivSelectImage.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivSelectImage:
                selectImage();
                break;
            case R.id.btSignup:
//                categoriesSelectedStr
// CSV format
//                String csv = categoriesSelectedStr.toString().replace("[", "").replace("]", "")
//                        .replace(", ", ",");
//                Log.e("CSV ","M "+csv);

                if (!etName.getText().toString().equalsIgnoreCase("")) {
                    if (!etCity.getText().toString().equalsIgnoreCase("")) {
                        if (!etCountry.getText().toString().equalsIgnoreCase("")) {
                                if (!etEmail.getText().toString().equalsIgnoreCase("")) {
                                    if (EMAIL_ADDRESS_PATTERN.matcher(etEmail.getText().toString()).matches()) {
                                        if (!etPassword.getText().toString().equalsIgnoreCase("")) {
                                            if (etConfirmPassword.getText().toString().equalsIgnoreCase(etPassword.getText().toString())) {
                                                if (!base64Image.equalsIgnoreCase("")) {
                                                    nameStr = etName.getText().toString();
//                                                    phoneStr = etEmail.getText().toString();
                                                    emailStr = etEmail.getText().toString();
                                                    passwordStr = etPassword.getText().toString();
                                                    cityStr = etCity.getText().toString();
                                                    countryStr = etCountry.getText().toString();
                                                    Log.e("NAME", ">>" + nameStr);
                                                    Log.e("Email", ">>" + emailStr);
                                                    Log.e("city", ">>" + cityStr);
                                                    Log.e("country", ">>" + countryStr);
                                                    Log.e("password", ">>" + passwordStr);
                                                    Log.e("image", ">>" + base64Image);
                                                    Log.e("categories", ">>" + categoriesSelectedStr);
//                                                    Log.e("Email", ">>" + emailStr);

                                                    if (passwordStr.length() > 7) {

                                                      //categoriesSelectedStr.split(",");
                                                        if (categoriesSelectedStr.length() > 0)
                                                        {
                                                            List<String> myList = new ArrayList<String>(Arrays.asList(categoriesSelectedStr.split(",")));



                                                            if (myList.size() <= 2)
                                                            {
                                                                Toast.makeText(SignupActivity.this,"please select atleast 3 Intrest",Toast.LENGTH_SHORT).show();

                                                            }
                                                            else
                                                            {
                                                                if (myList.size() > 5)
                                                                {
                                                                    Toast.makeText(SignupActivity.this,"please select max 5 intrest",Toast.LENGTH_SHORT).show();
                                                                }
                                                                else
                                                                {
                                                                    new signupUserData().execute();
                                                                }
                                                            }
                                                        }
                                                        else
                                                        {
                                                           Toast.makeText(SignupActivity.this,"please select intrest",Toast.LENGTH_SHORT).show();
                                                        }

                                                    } else {
                                                        toast("Password must be greter than 8 character required");
                                                    }
                                                } else {
                                                    toast("Please Select Profile Image.");
                                                }
                                            } else {
                                                toast("Password Mismatch.");
                                            }
                                        } else {
                                            toast("Please Enter Your Password.");
                                        }
                                    } else {
                                        toast("Please Enter Valid Email Address.");
                                    }
                                } else {
                                    toast("Please Enter Your Email Address.");
                                }
                        } else {
                            toast("Please Enter Your Country.");
                        }
                    } else {
                        toast("Please Enter Your City.");
                    }
                } else {
                    toast("Please Enter Your Name.");
                }
                break;
        }
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
                    Toast.makeText(SignupActivity.this, "No Permission Granted, Access Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(SignupActivity.this);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        } else {
            toast("You haven't picked Image");
        }
    }


    private void onCaptureImageResult(Intent data) {
        Bitmap resizedbitmap =  (Bitmap) data.getExtras().get("data");
        Log.e("CAMERA",">>>> "+resizedbitmap);
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
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
            Picasso.with(getApplicationContext()).load(mypath).into(ivProfileImage);
        } catch (Exception e) {
            Log.e("SAVE_IMAGE", e.getMessage(), e);
        }



//        ivImage.setImageBitmap(thumbnail);
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bitmap = null;
        Uri selectedImagePath = null;
        if (data != null) {
            try {
                selectedImagePath = data.getData();
                Log.e("selectedImagePath", "GLRY " + selectedImagePath);
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                Log.e("BITMAP",">>>>> "+bitmap);
                if(bitmap != null) {
                    // convert bitmap to base64
                    base64Image = Util.bitmapToBase64(bitmap);
                    Picasso.with(getApplicationContext()).load(selectedImagePath).into(ivProfileImage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void selectedIndices(List<Integer> indices) {


       // Toast.makeText(SignupActivity.this,indices,LENGTH_SHORT).show;

    }

    @Override
    public void selectedStrings(List<String> strings) {
//        Toast.makeText(this, strings.toString(), Toast.LENGTH_LONG).show();

            categoriesSelectedStr = strings.toString().replace("[", "").replace("]", "")
                    .replace(", ", ",");
            Log.e("CSV ", "M " + categoriesSelectedStr);

//        categoriesSelectedStr = strings.toString();
    }

    public class getCategoryInterestList extends AsyncTask<String, String, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(SignupActivity.this);
            pd.setMessage("Loading");
            pd.setCancelable(false);
            pd.show();
            CategoryItems.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            response = Util.getResponseofGet(Constant.BASE_URL + "category.php");
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
                    toast("Get Category List Successfully");
                    JSONArray dataAry = object.getJSONArray("data");
                    for (int i = 0; i < dataAry.length(); i++) {
                        String categories = dataAry.getJSONObject(i).getString("categories");
                        CategoryItems.add(categories);
                    }
                } else {
                    toast(object.getString("msg"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (CategoryItems.size() > 0) {
                multiSelectionSpinner.setItems(CategoryItems);
            }
        }
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if ((requestCode == Image_Pick) && (data != null))   {
//            Uri selectedImage = data.getData();
//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//            try {
//                // get bitmap from the image path
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
//                // convert bitmap to base64
//                base64Image = Util.bitmapToBase64(bitmap);
//                // Log.d(TAG, String.valueOf(bitmap));
//                // set image to the Imageview
//                Picasso.with(getApplicationContext()).load(selectedImage).into(ivProfileImage);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            toast("You haven't picked Image");
//        }
//    }


    public class signupUserData extends AsyncTask<String, String, String> {
        KProgressHUD hud;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            hud = KProgressHUD.create(SignupActivity.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .show();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
//            http://web-medico.com/web2/intrisfeed/api/register.php?name=demo&phone=1234567890&email=demo@gmail.com&password=12345&image=10&categories=test,teste,ffdsff,dsf,df,
            HashMap<String, String> hashmap = new HashMap<String, String>();
            hashmap.put("name", nameStr);
            hashmap.put("city", cityStr);
            hashmap.put("country", countryStr);
//            hashmap.put("phone", phoneStr);
            hashmap.put("email", emailStr);
            hashmap.put("password", passwordStr);
            hashmap.put("image", base64Image);
            hashmap.put("categories", categoriesSelectedStr);


//            Log.e("NAME", ">> " + nameStr);

            response = Util.getResponseofPost(Constant.BASE_URL + "register.php", hashmap);
//                urlString = Constant.BASE_URL + "login.php?email=" + usernamestr + "&password=" + passwordstr;
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
                    toast(object.getString("msg"));
                    Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID, object.getString("id"));
                    ChatUtils chatUtils = new ChatUtils(SignupActivity.this);
                    chatUtils.ChatRegister(SignupActivity.this, nameStr, login_id, emailStr, passwordStr);
                    hud.dismiss();
                    startActivity(LoginActivity.class);
                    finish();
                } else {
                    hud.dismiss();
                    toast(object.getString("msg"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                toast("EXCEPTION "+e);
                Log.e("EXCEPTION ","M "+e);
                hud.dismiss();
            }
        }
    }

    // get Captured Image Rotation Degree
    public static int getImageOrientation(String imagePath){
        int rotate = 0;
        try {

            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }

}
