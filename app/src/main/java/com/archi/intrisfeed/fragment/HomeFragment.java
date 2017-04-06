package com.archi.intrisfeed.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.archi.intrisfeed.R;
import com.archi.intrisfeed.SignupContentAuthorActivity;
import com.archi.intrisfeed.paypal.PayPalConfig;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.ImageFilePath;
import com.archi.intrisfeed.util.Util;
import com.archi.intrisfeed.util.Utility;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.ACTION_GET_CONTENT;
import static android.content.Intent.URI_ALLOW_UNSAFE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * search engine ID
 * 008564277742530842947:k20n63ze2dg
 * <p>
 * <p>
 * Created by archi_info on 10/13/2016.
 * <p>
 * http://web-medico.com/web2/intrisfeed/api/insert_author_category.php?userid=22&clientid=123&email=archirayan2@gmail.com&category=a,b,c,&price=25000&payment_id=1534842
 */

public class HomeFragment extends Fragment implements View.OnClickListener {
    private int SELECT_FILE = 3, SELECT_VIDEO = 2, SELECT_IMAGE = 1;
    private ArrayList<String> approvedCategoryList;
    public static final int PAYPAL_REQUEST_CODE = 121;
    private TextView tvBecomeContentAuthor;
    LinearLayout llAddCategory, llContentLayout;
    Button btnAddMoreCategory, btPayNow;
    ArrayList<String> categoryList;
    //Paypal Configuration Object
    private static PayPalConfiguration config;
    String paymentAmount = "0";
    public static double count = 0;
    String categoryStr = "", payment_id = "";
    int isFirstClick = 0;
    AutoCompleteTextView etCatApproved;
    TextView tvEnterContent, tvUploadContent;
    String matchCategoryName = "";
    // Add content Control
    TextView tvUserMailContent;
    EditText etContentTitle, etContent, etContentLink, etSelectVideo, etSelectDocument, etSelectImage;
    ImageView ivContenteThumb, ivSelectVideo, ivSelectDocument;
    Snackbar snackbar;
    CoordinatorLayout snackbarCoordinatorLayout;
    File imageUpload, videoUpload, documentUpload;
    Uri imageuploadUri = null, videouploadUri = null, documentuploadUri = null;
    String imagePath = "", videoPath = "", documentPath = "";
    String isImageOrVideo = "";
    private String[] CATEGORIEDAPPROVED;
    ArrayAdapter<String> adapterApprovedCateory;
    private ProgressDialog pdialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        snackbarCoordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.snackbarCoordinatorLayout);
        tvBecomeContentAuthor = (TextView) rootView.findViewById(R.id.tvBecomeContentAuthor);
        llAddCategory = (LinearLayout) rootView.findViewById(R.id.llAddCategoryLayout);
        btnAddMoreCategory = (Button) rootView.findViewById(R.id.btnAddMoreCategory);
        btPayNow = (Button) rootView.findViewById(R.id.btnPayNow);
        etCatApproved = (AutoCompleteTextView) rootView.findViewById(R.id.etCategoryApproved_HomeFragment);
        tvEnterContent = (TextView) rootView.findViewById(R.id.tvEnterContent);
        llContentLayout = (LinearLayout) rootView.findViewById(R.id.llContentLayout_HomeFragment);
        tvUploadContent = (TextView) rootView.findViewById(R.id.tvUploadContent_HomeFragment);

        // Add Content Control
        tvUserMailContent = (TextView) rootView.findViewById(R.id.etContentUserEmail_HomeFragment);
//        tvUserMailContent.setText(Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USER_EMAIL));
        etSelectImage = (EditText) rootView.findViewById(R.id.etSelectImage);
        etContentTitle = (EditText) rootView.findViewById(R.id.etContentTitle_HomeFragment);
        etContent = (EditText) rootView.findViewById(R.id.etContent_HomeFragment);
        etContentLink = (EditText) rootView.findViewById(R.id.etContentLink_HomeFragment);
        ivContenteThumb = (ImageView) rootView.findViewById(R.id.ivSelectImage);
        ivSelectVideo = (ImageView) rootView.findViewById(R.id.ivSelectVideo);
        ivSelectDocument = (ImageView) rootView.findViewById(R.id.ivSelectDocument);
        etSelectVideo = (EditText) rootView.findViewById(R.id.etSelectVideo);
        etSelectDocument = (EditText) rootView.findViewById(R.id.etSelectDocument);
        if (Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_IS_CONTENT_AUTHOR).equalsIgnoreCase("1")) {
            tvBecomeContentAuthor.setBackground((ContextCompat.getDrawable(getActivity(), R.drawable.btn_disable)));
            tvBecomeContentAuthor.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        } else {
            tvBecomeContentAuthor.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btnpressed));
            tvBecomeContentAuthor.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
        tvUserMailContent.setOnClickListener(this);
        tvUploadContent.setOnClickListener(this);
        tvEnterContent.setOnClickListener(this);
        tvBecomeContentAuthor.setOnClickListener(this);
        btnAddMoreCategory.setOnClickListener(this);
        tvUploadContent.setOnClickListener(this);
        ivContenteThumb.setOnClickListener(this);
        ivSelectVideo.setOnClickListener(this);
        ivSelectDocument.setOnClickListener(this);
        btPayNow.setOnClickListener(this);
        categoryList = new ArrayList<>();
        categoryList.clear();


//        etCatApproved.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                for (int i = 0; i < approvedCategoryList.size(); i++) {
//                    Log.e("CAT" + i, "" + approvedCategoryList.get(i).toLowerCase());
//                    if (etCatApproved.getText().toString().toLowerCase().contains(approvedCategoryList.get(i).toLowerCase().toString())) {
//                        matchCategoryName = approvedCategoryList.get(i);
//                        Log.e("YES", "YES");
//                    } else {
//                        Log.e("NO", "NO");
//
//                    }
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        //Paypal Configuration Object
        config = new PayPalConfiguration()
                // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
                // or live (ENVIRONMENT_PRODUCTION)
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                .clientId(PayPalConfig.PAYPAL_CLIENT_ID);
        Intent intent = new Intent(getActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        getActivity().startService(intent);
        // get Approved categorylist
        new checkAuthorApproveCategory().execute();
        return rootView;
    }


    @Override
    public void onDestroy() {
        getActivity().stopService(new Intent(getActivity(), PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvBecomeContentAuthor:
//                if (isFirstClick == 0) {
//                    isFirstClick = 1;
//                    addMoreLayout();
//                }
                Intent intent = new Intent(getActivity(), SignupContentAuthorActivity.class);
                startActivity(intent);
                break;

            case R.id.btnAddMoreCategory:
                addMoreLayout();
                break;

            case R.id.tvEnterContent:
                String categoryName = etCatApproved.getText().toString();

                boolean isThere = Arrays.asList(CATEGORIEDAPPROVED).contains(categoryName);
                if (isThere) {
                    matchCategoryName = categoryName;
                    if (llContentLayout.getVisibility() == View.GONE) {
                        llContentLayout.setVisibility(View.VISIBLE);
                        etCatApproved.setFocusable(false);
                        etCatApproved.setCursorVisible(false);
                        etCatApproved.setEnabled(false);
                    }
                } else {
                    Toast.makeText(getActivity(), "No Category Found", Toast.LENGTH_SHORT).show();
//                    snackbar = Snackbar
//                            .make(snackbarCoordinatorLayout, "No Category Found", Snackbar.LENGTH_LONG);
//                    snackbar.show();
                    if (llContentLayout.getVisibility() == View.VISIBLE) {
                        llContentLayout.setVisibility(View.GONE);
                    }
                }
                break;

            case R.id.tvUploadContent_HomeFragment:
                if (!etContentTitle.getText().toString().equalsIgnoreCase("")) {
                    if (!etContent.getText().toString().equalsIgnoreCase("")) {
                        if (!etContentLink.getText().toString().equalsIgnoreCase("")) {
                            String title = etContentTitle.getText().toString();
                            String content = etContent.getText().toString();
                            String link = etContentLink.getText().toString();
                            Log.e(">>>>>>>", ">>>>>>ADD CONTENT >>>>>>>>>");
                            Log.e("title", "title>> " + title);
                            Log.e("content", "content>> " + content);
                            Log.e("link", "link>> " + link);
                            Log.e("matchCategoryName", "CAT>> " + matchCategoryName);

//                                        new addContentAuthorPost(title, content,link,matchCategoryName).execute();
                            if (ivContenteThumb.getDrawable() != null) {
                                if (!videoPath.equalsIgnoreCase("") && documentPath.equalsIgnoreCase("")) {
                                    addContentAuthorPostVideo(matchCategoryName, title, content, link);
                                } else if (videoPath.equalsIgnoreCase("") && !documentPath.equalsIgnoreCase("")) {
                                    addContentAuthorPostDocument(matchCategoryName, title, content, link);
                                } else if(!videoPath.equalsIgnoreCase("") && !documentPath.equalsIgnoreCase("")){
                                    addContentAuthorPostAll(matchCategoryName, title, content, link);
                                }else{
                                    Toast.makeText(getActivity(), "Please select Video Or Document to upload your content.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                snackbar = Snackbar
                                        .make(snackbarCoordinatorLayout, "Please Select Image", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        } else {
                            snackbar = Snackbar
                                    .make(snackbarCoordinatorLayout, "Please Enter Content Link", Snackbar.LENGTH_LONG);
                            snackbar.show();
//                            Toast.makeText(getActivity(), "Please Enter Content Link", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        snackbar = Snackbar
                                .make(snackbarCoordinatorLayout, "Please Enter Content", Snackbar.LENGTH_LONG);
                        snackbar.show();
//                        Toast.makeText(getActivity(), "Please Enter Content", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    snackbar = Snackbar
                            .make(snackbarCoordinatorLayout, "Please Enter Content Title", Snackbar.LENGTH_LONG);
                    snackbar.show();
//                    Toast.makeText(getActivity(), "Please Enter Content Title", Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.btnPayNow:
                categoryList.clear();
                for (int i = 0; i < llAddCategory.getChildCount(); i++) {
                    View view = llAddCategory.getChildAt(i);

//                    if (view instanceof EditText) {
                    EditText edt = (EditText) view.findViewById(R.id.snack);
                    EditText edt1 = (EditText) view.findViewById(R.id.snack1);
                    EditText edt2 = (EditText) view.findViewById(R.id.snack2);

//                    String category = ((EditText) view).getText().toString();//here it will be clear all the EditText field
                    categoryList.add(edt.getText().toString() + "," + edt1.getText().toString() + "," + edt2.getText().toString() + ",");
                    if (!edt.getText().toString().equalsIgnoreCase("")) {
                        count = count + 1;
                    }
                    if (!edt1.getText().toString().equalsIgnoreCase("")) {
                        count = count + 1;
                    }
                    if (!edt2.getText().toString().equalsIgnoreCase("")) {
                        count = count + 1;
                    }

                }

                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < categoryList.size(); i++) {
                    String prefix = "";
                    for (String str : categoryList) {
                        sb.append(prefix);
                        prefix = ",";
                        sb.append(str);
                    }
                }

                categoryStr = sb + "";
                if (!categoryStr.equalsIgnoreCase("")) {
                    getPayment();
                } else {
//                    snackbar = Snackbar
//                            .make(snackbarCoordinatorLayout, "Please Enter Category", Snackbar.LENGTH_LONG);
//                    snackbar.show();
                    Toast.makeText(getActivity(), "Please Enter Category", Toast.LENGTH_SHORT).show();
                }
                Log.e("Arry", ">> " + categoryList);
                break;

            case R.id.ivSelectImage:
                boolean result1 = Utility.checkPermission(getActivity());
                if (result1) {
                    isImageOrVideo = "0";
                    galleryIntent();
                } else {
                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.ivSelectDocument:
                boolean result2 = Utility.checkPermission(getActivity());
                if (result2) {
                    isImageOrVideo = "2";
                    documentIntent();
                } else {
                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.ivSelectVideo:
                boolean result3 = Utility.checkPermission(getActivity());
                if (result3) {
                    isImageOrVideo = "1";
                    videoIntent();
                } else {
                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.etContentUserEmail_HomeFragment:
                String toMailId = tvUserMailContent.getText().toString();
                showSendMailPopup(toMailId);


                break;
        }
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

                        case "2":
                            documentIntent();
                            break;
                    }
                } else {
                    //code for deny
                    Toast.makeText(getActivity(), "No Permission Granted, Access Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void getPayment() {
        //Getting the amount from editText
        Log.e("Count", ">>>" + count);
        double payAmt = 5.99 * count;
        Log.e("PAY AMT1", ">>>" + payAmt);
        paymentAmount = payAmt + "";
        Log.e("PAY AMT F", ">>>" + paymentAmount);


        //Creating a paypalpayment
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(paymentAmount)), "USD", "Simplified Coding Fee",
                PayPalPayment.PAYMENT_INTENT_SALE);

        //Creating Paypal Payment activity intent
        Intent intent = new Intent(getActivity(), PaymentActivity.class);

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }


    private void addMoreLayout() {
//        View commentLayout = LayoutInflater.from(getActivity()).inflate(R.layout.item_category_content_author, llAddCategory, false);
        LinearLayout llHorizontal = null;
        llHorizontal = new LinearLayout(getActivity());
        for (int i = 0; i < 3; i++) {
            EditText et1 = new EditText(getActivity());
            llHorizontal.setOrientation(LinearLayout.HORIZONTAL);
            llHorizontal.setWeightSum(3);
            LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            etParams.setMargins(10, 10, 10, 10);
            et1.setPadding(15, 15, 15, 15);
            if (i == 0) {
                et1.setId(R.id.snack);
            }
            if (i == 1) {
                et1.setId(R.id.snack1);
            }
            if (i == 2) {
                et1.setId(R.id.snack2);
            }

            et1.setLayoutParams(etParams);
            et1.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.edittext_shadow));
            et1.setHint(R.string.category);
            llHorizontal.addView(et1);
        }
        llAddCategory.addView(llHorizontal);
//        EditText etCat1 = (EditText) commentLayout.findViewById(R.id.etCategory1);
//        EditText etCat2 = (EditText) commentLayout.findViewById(R.id.etCategory2);
//        EditText etCat3 = (EditText) commentLayout.findViewById(R.id.etCategory3);
//        llAddCategory.addView(commentLayout);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.e("paymentDetails", paymentDetails);
                        JSONObject jsonDetails = new JSONObject(paymentDetails);
                        JSONObject objDetails = jsonDetails.getJSONObject("response");
                        payment_id = objDetails.getString("id");
                        String status = objDetails.getString("state");



                        if (!payment_id.equalsIgnoreCase("")) {
                            if (status.equalsIgnoreCase("approved")) {
                                new savePaymentDetails().execute();
                            } else {
                                Toast.makeText(getActivity(), "Payment Fail, Try Again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == SELECT_IMAGE || requestCode == SELECT_VIDEO || requestCode == SELECT_FILE) {
            if (resultCode == RESULT_OK) {
                if (requestCode == SELECT_IMAGE)
                    onSelectFromGalleryResult(data);
                else if (requestCode == SELECT_VIDEO)
                    onSelectFromVideoResult(data);
                else if (requestCode == SELECT_FILE)
                    onSelectFromDocumentResult(data);
            } else {
                snackbar = Snackbar
                        .make(snackbarCoordinatorLayout, "You haven't picked Image", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        } else {
            snackbar = Snackbar
                    .make(snackbarCoordinatorLayout, "something went wrong, please try again", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

    }


    public class savePaymentDetails extends AsyncTask<String, String, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Loading");
            pd.setCancelable(false);
            pd.show();

            Log.e("","=================================\n\n");
            Log.e("user id :",Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID));
            Log.e("payment Id  :",payment_id);
            Log.e("email id :",Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USER_EMAIL));
            Log.e("category :",categoryStr);
            Log.e("price :",paymentAmount);
            Log.e("client id :",PayPalConfig.PAYPAL_CLIENT_ID);
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            //http://web-medico.com/web2/intrisfeed/api/insert_author_category.php?userid=22
            // &clientid=123&email=archirayan2@gmail.com&category=a,b,c,&price=25000&payment_id=1534842

            String userId =  Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);

            String email = Util.ReadSharePrefrence(getActivity(),Constant.SHRED_PR.KEY_USER_EMAIL);
            String url = Constant.BASE_URL + "insert_author_category.php?userid=" + userId + "&clientid=" + PayPalConfig.PAYPAL_CLIENT_ID + "&email=" + email + "&category=" + categoryStr + "&price=" + paymentAmount + "&payment_id=" + payment_id;


//            HashMap<String, String> hashmap = new HashMap<String, String>();
//            hashmap.put("userid", Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID));
//            hashmap.put("clientid", PayPalConfig.PAYPAL_CLIENT_ID);
//            hashmap.put("email", Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USER_EMAIL));
//            hashmap.put("category", categoryStr);
//            hashmap.put("price", paymentAmount);
//            hashmap.put("payment_id", payment_id);




//            Log.e(">>",">> "+Util.ReadSharePrefrence(getActivity(),Constant.SHRED_PR.KEY_USERID));
//            Log.e(">>",">> "+PayPalConfig.PAYPAL_CLIENT_ID);
//            Log.e(">>",">> "+Util.ReadSharePrefrence(getActivity(),Constant.SHRED_PR.KEY_USER_EMAIL));
//            Log.e(">>",">> "+categoryStr);
//            Log.e(">>",">> "+paymentAmount);
//            Log.e(">>",">> "+payment_id);
            response = Util.getResponseofGet(url);
          //  response = Util.getResponseofPost(Constant.BASE_URL + "insert_author_category.php", hashmap);
//                urlString = Constant.BASE_URL + "login.php?email=" + usernamestr + "&password=" + passwordstr;
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
                    JSONObject obj = object.getJSONObject("data");
                    String id = obj.getString("id");
                    String email = obj.getString("email");
//                    String img = obj.getString("image");
                    Log.e("id", id);
                    Log.e("email", email);
                   // Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID, id);
                   Util.WriteSharePrefrence(getActivity(),Constant.SHRED_PR.KEY_PAYMENT_SUCCSESS_ID,id);
                    Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_IS_LOGGEDIN, "1");
//                    Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_PROFILE_PIC, img);
//                    toast(object.getString("msg"));
//                    startActivity(MainActivity.class);
//                    finish();
//                    snackbar = Snackbar
//                            .make(snackbarCoordinatorLayout, "Payment Done successfully", Snackbar.LENGTH_LONG);
//                    snackbar.show();
                    Toast.makeText(getActivity(), "Payment Done successfully", Toast.LENGTH_SHORT).show();
                } else {
//                    snackbar = Snackbar
//                            .make(snackbarCoordinatorLayout, ""+ object.getString("msg"), Snackbar.LENGTH_LONG);
//                    snackbar.show();
                    Toast.makeText(getActivity(), "" + object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class checkAuthorApproveCategory extends AsyncTask<String, String, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Loading");
            pd.setCancelable(false);
            approvedCategoryList = new ArrayList<>();
            approvedCategoryList.clear();
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
//            http://web-medico.com/web2/intrisfeed/api/get_author_category.php?userid=22

            String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
            String url = Constant.BASE_URL + "get_author_category.php?userid=" + userId;
           // HashMap<String, String> hashmap = new HashMap<String, String>();
           // hashmap.put("userid", Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID));
           // response = Util.getResponseofPost(Constant.BASE_URL + "get_author_category.php", hashmap);
//                urlString = Constant.BASE_URL + "login.php?email=" + usernamestr + "&password=" + passwordstr;
           Log.e("url", url);
            response = Util.getResponseofGet(url);
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
                if (object.getString("successful").equalsIgnoreCase("true") || object.getString("status").equalsIgnoreCase("true")) {
                    Toast.makeText(getActivity(), "Get Approved Category List", Toast.LENGTH_SHORT).show();
                    JSONArray arry = object.getJSONArray("data");
                    CATEGORIEDAPPROVED = new String[arry.length()];
                    for (int i = 0; i < arry.length(); i++) {
                        JSONObject obj = arry.getJSONObject(i);
                        String category = obj.getString("category");
                        Log.e("id", category);


                        List<String> elephantList = Arrays.asList(category.split(","));
                        for (int j = 0; j < elephantList.size(); j++) {
                            Log.e(">> " + j, ">>>> " + elephantList.get(j).toString());
                            CATEGORIEDAPPROVED[i] = elephantList.get(j).toString();
//                            approvedCategoryList.add(elephantList.get(j).toString());
                        }
                    }

                    adapterApprovedCateory = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, CATEGORIEDAPPROVED);
                    etCatApproved.setAdapter(adapterApprovedCateory);


                } else {
//                    snackbar = Snackbar
//                            .make(snackbarCoordinatorLayout, ""+object.getString("msg"), Snackbar.LENGTH_LONG);
//                    snackbar.show();
                    Toast.makeText(getActivity(), "" + object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

// post Author Content TO Approve Author Content List

    public void addContentAuthorPostAll(String category, String title, String content, String link) {
        Log.e(">>>>>>>>>", ">>>>>>> ALL >>>>>>>>>>>");
        Log.e("UID", "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID));
        Log.e("category", ">>" + category);
        Log.e("title", ">>" + title);
        Log.e("content", ">>" + content);
        Log.e("link", ">>" + link);
        Log.e("EMAIL", ">>" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USER_EMAIL));
        Log.e("IMAGE", ">>" + imagePath);
        Log.e("VIDEO", ">>" + videoPath);
        Log.e("DOC", ">>" + documentPath);
        pdialog = new ProgressDialog(getActivity());
        pdialog.setMessage("Loading...");
        pdialog.show();
        Log.e("VIDE PATH",">> "+videoPath);
        Ion.with(getActivity())
                .load(Constant.BASE_URL + "insert_content_post.php")
                .progressDialog(pdialog)
                .setMultipartParameter("user_id", Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID))
                .setMultipartParameter("category", category)
                .setMultipartParameter("content_title", title)
                .setMultipartParameter("content_details", content)
                .setMultipartParameter("content_link", link)
                .setMultipartParameter("content_author_email", Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USER_EMAIL))
                .setMultipartFile("image", new File(imagePath))
                .setMultipartFile("video", new File(videoPath))
                .setMultipartFile("doc", new File(documentPath))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {

                        Log.d("msg", "res  " + result);
                        if (pdialog != null) {
                            pdialog.dismiss();
                        }

                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String status = jsonObject.getString("successful");
                            if (status.equalsIgnoreCase("true")) {
                                Toast.makeText(getActivity(), "Insert Data successfully", Toast.LENGTH_SHORT).show();

                                etContentTitle.setText("");
                                etContent.setText("");
                                etContentLink.setText("");

//                                HomeFragment homeFragment = new HomeFragment();
//                                FragmentManager fm = getActivity().getSupportFragmentManager();
//                                FragmentTransaction fragmentTransaction = fm.beginTransaction();
//
//                                fragmentTransaction.setCustomAnimations(R.anim.right_to_left, R.anim.fragment_slide_left_exit);
//                                fragmentTransaction.replace(R.id.container, homeFragment).addToBackStack("").commit();

                            } else {
                                snackbar = Snackbar
                                        .make(snackbarCoordinatorLayout, "Something Wrong", Snackbar.LENGTH_LONG);
                                snackbar.show();

//                                Toast.makeText(getActivity(), "Something Wrong", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            Log.d("msg", "error   " + e1.getMessage());
                        }

                    }
                });


    }


    public void addContentAuthorPostVideo(String category, String title, String content, String link) {
        Log.e(">>>>>>>>>", ">>>>>>> VIDEO >>>>>>>>>>>");
        Log.e("UID", "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID));
        Log.e("category", ">>" + category);
        Log.e("title", ">>" + title);
        Log.e("content", ">>" + content);
        Log.e("link", ">>" + link);
        Log.e("EMAIL", ">>" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USER_EMAIL));
        Log.e("imageupload", ">>" + imagePath);
        Log.e("videoupload", ">>" + videoPath);
//        http://web-medico.com/web2/intrisfeed/api/insert_content_post.php?user_id=17&category=abc&content_title=abc&content_details=xyz
// &content_link=xyz.com&content_author_email=abc@gmail.com&image=1.jpg&video=11.mp4&doc=1.pdf

        pdialog = new ProgressDialog(getActivity());
        pdialog.setMessage("Loading...");
        pdialog.show();
        Ion.with(getActivity())
                .load(Constant.BASE_URL + "insert_content_post.php")
                .progressDialog(pdialog)
                .setMultipartParameter("user_id", Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID))
                .setMultipartParameter("category", category)
                .setMultipartParameter("content_title", title)
                .setMultipartParameter("content_details", content)
                .setMultipartParameter("content_link", link)
                .setMultipartParameter("content_author_email", Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_CONTENT_AUTHOR_EMAIL))
                .setMultipartFile("image", new File(imagePath))
                .setMultipartFile("video", new File(videoPath))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Log.e("msg", "res  " + result);
                        if (pdialog != null) {
                            pdialog.dismiss();
                        }

                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String status = jsonObject.getString("successful");
                            if (status.equalsIgnoreCase("true")) {
                                Toast.makeText(getActivity(), "Insert Data successfully", Toast.LENGTH_SHORT).show();
                                etContentTitle.setText("");
                                etContent.setText("");
                                etContentLink.setText("");

//                                HomeFragment homeFragment = new HomeFragment();
//                                FragmentManager fm = getActivity().getSupportFragmentManager();
//                                FragmentTransaction fragmentTransaction = fm.beginTransaction();
//
//                                fragmentTransaction.setCustomAnimations(R.anim.right_to_left, R.anim.fragment_slide_left_exit);
//                                fragmentTransaction.replace(R.id.container, homeFragment).addToBackStack("").commit();

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


    public void addContentAuthorPostDocument(String category, String title, String content, String link) {
        Log.e(">>>>>>>>>", ">>>>>>> DOCUMENT >>>>>>>>>>>");
        Log.e("UID", "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID));
        Log.e("category", ">>" + category);
        Log.e("title", ">>" + title);
        Log.e("content", ">>" + content);
        Log.e("link", ">>" + link);
        Log.e("EMAIL", ">>" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USER_EMAIL));
        Log.e("IMAGE", ">>" + imagePath);
        Log.e("DOC", ">>" + documentPath);
        pdialog = new ProgressDialog(getActivity());
        pdialog.setMessage("Loading...");
        pdialog.show();
        Ion.with(getActivity())
                .load(Constant.BASE_URL + "insert_content_post.php")
                .progressDialog(pdialog)
                .setMultipartParameter("user_id", Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID))
                .setMultipartParameter("category", category)
                .setMultipartParameter("content_title", title)
                .setMultipartParameter("content_details", content)
                .setMultipartParameter("content_link", link)
                .setMultipartParameter("content_author_email", Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USER_EMAIL))
                .setMultipartFile("image", new File(imagePath))
                .setMultipartFile("doc", new File(documentPath))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {

                        Log.d("msg", "res  " + result);
                        if (pdialog != null) {
                            pdialog.dismiss();
                        }

                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String status = jsonObject.getString("successful");
                            if (status.equalsIgnoreCase("true")) {
                                Toast.makeText(getActivity(), "Insert Data successfully", Toast.LENGTH_SHORT).show();
                                etContentTitle.setText("");
                                etContent.setText("");
                                etContentLink.setText("");

//                                HomeFragment homeFragment = new HomeFragment();
//                                FragmentManager fm = getActivity().getSupportFragmentManager();
//                                FragmentTransaction fragmentTransaction = fm.beginTransaction();
//
//                                fragmentTransaction.setCustomAnimations(R.anim.right_to_left, R.anim.fragment_slide_left_exit);
//                                fragmentTransaction.replace(R.id.container, homeFragment).addToBackStack("").commit();

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


    private void galleryIntent() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_IMAGE);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
        }

//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
    }

    private void videoIntent() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("video/*");
            startActivityForResult(intent, SELECT_VIDEO);
        } else {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Video"), SELECT_VIDEO);
        }
    }

    private void documentIntent() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, SELECT_FILE);
        } else {
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(ACTION_GET_CONTENT);//
            startActivityForResult(Intent.createChooser(intent, "Select Document"), SELECT_FILE);
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
                etSelectImage.setText(imagePath);
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
//                    String imageName = path.substring(path.lastIndexOf('/') + 1);
//                        String imageName = System.currentTimeMillis() + ".png";
//                        edtProductName.setText(imageName);
//                    setImageFromIntent(filePath);
//                Picasso.with(getActivity()).load(selectedImageUri).error(R.drawable.default_img).into(ivContenteThumb);
            } else {
                imagePath = getPathFromURI(selectedImageUri);
                Log.e("File Path", ">> " + imagePath);
                String imageName = imagePath.substring(imagePath.lastIndexOf('/') + 1);
                etSelectImage.setText(imagePath);
//                Picasso.with(getActivity()).load(selectedImageUri).error(R.drawable.default_img).into(ivContenteThumb);
            }

    }


    private void onSelectFromVideoResult(Intent data) {

        Uri selectedVideoUri = data.getData();
        Log.e("Video LINK", ">> " + selectedVideoUri.getPath());
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            // Do something for 19 and above versions
            // OI FILE Manager
            // OI FILE Manager
            //MEDIA GALLERY
            String selectedImagePath;
            Uri selectedImageUri = data.getData();
            selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedImageUri);
            Log.i("Video File Path", ""+selectedImagePath);

            String wholeID = DocumentsContract.getDocumentId(selectedVideoUri);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = { MediaStore.Video.Media.DATA };

            // where id is equal to
            String sel = MediaStore.Video.Media._ID + "=?";

            Cursor cursor = getActivity().getContentResolver().
                    query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{ id }, null);
            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                videoPath = cursor.getString(columnIndex);
            }
            cursor.close();
            String videoName = videoPath.substring(videoPath.lastIndexOf('/') + 1);
            etSelectVideo.setText("" + videoName);
            Log.e("Video Name", "Name " + videoName);
            Log.e("Video Path", "Video " + videoPath);
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
//                    String imageName = path.substring(path.lastIndexOf('/') + 1);
//                        String imageName = System.currentTimeMillis() + ".png";
//                        edtProductName.setText(imageName);
//                    setImageFromIntent(filePath);

        } else {

            videoPath = getPathFromURI(selectedVideoUri);
            Log.e("Video Path", ">> " + videoPath);
            String videoName = videoPath.substring(videoPath.lastIndexOf('/') + 1);
            etSelectVideo.setText("" + videoName);
        }
    }


    private void onSelectFromDocumentResult(Intent data) {
        if (data != null) {
            documentuploadUri = data.getData();
            Log.e("documentuploadUri", "DOC " + documentuploadUri);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                // Do something for 19 and above versions
                // OI FILE Manager
                String wholeID = DocumentsContract.getDocumentId(documentuploadUri);

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
                    documentPath = cursor.getString(columnIndex);
                }
                cursor.close();
                String DocName = documentPath.substring(documentPath.lastIndexOf('/') + 1);
                etSelectDocument.setText("" + DocName);
                Log.e("DOC", "Path : " + documentPath);
                Log.e("DOC", "NAME : " + DocName);
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
//                    String imageName = path.substring(path.lastIndexOf('/') + 1);
//                        String imageName = System.currentTimeMillis() + ".png";
//                        edtProductName.setText(imageName);
//                    setImageFromIntent(filePath);

            } else {

                documentPath = getPathFromURI(documentuploadUri);
                etSelectDocument.setText("" + documentPath);
            }
        } else {
//            snackbar = Snackbar
//                    .make(snackbarCoordinatorLayout, "No Data Found", Snackbar.LENGTH_LONG);
//            snackbar.show();

            Toast.makeText(getActivity(), "No Data Found", Toast.LENGTH_SHORT).show();
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


    public void showSendMailPopup(String toMailId) {
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.popup_send_mail);
        dialog.setTitle("Send Mail");
        dialog.setCancelable(false);

        // set the custom dialog components - text, image and button
        final EditText fromMail = (EditText) dialog.findViewById(R.id.etFromContentAuthorMailId);
        final EditText subject = (EditText) dialog.findViewById(R.id.etSubjectContentAuthor);
        subject.setText("Intrisfeed : " + etCatApproved.getText().toString());
        final EditText desc = (EditText) dialog.findViewById(R.id.tvDescriptionSendMailPopup);

        TextView sendMail = (TextView) dialog.findViewById(R.id.tvSendMailPopup);
        TextView cancel = (TextView) dialog.findViewById(R.id.tvCancelPopup);
        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USER_EMAIL)});
                email.putExtra(Intent.EXTRA_SUBJECT,fromMail.getText().toString()+" : "+ subject.getText().toString());
                email.putExtra(Intent.EXTRA_TEXT, "" + desc.getText().toString());
//need this to prompts email client only
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}


