package com.archi.intrisfeed;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * QUICKBOX DETAILS
 * Application id = 47880
 * Authorisation key = hCQpjWq4HcFV5uk
 * Authorisation Secret = NWKJSNhWHCZYgF9
 * <p>
 * Created by archi_info on 9/21/2016.
 * LOGIN API:
 * http://web-medico.com/web2/intrisfeed/api/login.php?email=archi@gmail.com&password=archi
 * FORGOT PASSWORD
 * http://web-medico.com/web2/intrisfeed/api/forgot_password.php?email=archirayan32@gmail.com
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    Dialog dialog;
    private EditText etEmail, etPassword;
    private Button btSignin;
    private TextView tvForgotPwd, tvNewUser;
    private String uNameStr, uPwdStr, emailStr;
    private String refreshedToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        refreshedToken = "cf1545b081ea5c3dabb8118fbffa40391f462bbcAIzaSyCAnPecYTEggJH4JLThcWwpmFlpKW40VCM";
        Log.e("DEVICE TOKEN",">> "+refreshedToken);
        init();
    }

    private void init() {
        etEmail = (EditText) findViewById(R.id.etEmail_Login);
        etPassword = (EditText) findViewById(R.id.etPassword_Login);
        btSignin = (Button) findViewById(R.id.btSignin_Login);
        tvNewUser = (TextView) findViewById(R.id.tvNewuser_Login);
        tvForgotPwd = (TextView) findViewById(R.id.tvForgetpassword_Login);
        btSignin.setOnClickListener(this);
        tvNewUser.setOnClickListener(this);
        tvForgotPwd.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btSignin_Login:
                if (Util.isOnline(getApplicationContext())) {
                    if (!etEmail.getText().toString().equalsIgnoreCase("")) {
                        if (!etPassword.getText().toString().equalsIgnoreCase("")) {
                            uNameStr = etEmail.getText().toString();
                            uPwdStr = etPassword.getText().toString();
                            new makeLogin().execute();
                        } else {
                            toast("Please Enter Password");
                        }
                    } else {
                        toast("Please Enter Email Address");
                    }
                } else {
                    toast(Constant.network_error);
                }

                break;

            case R.id.tvNewuser_Login:
                startActivity(SignupActivity.class);
                break;

            case R.id.tvForgetpassword_Login:
                // custom dialog
                dialog = new Dialog(LoginActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                dialog.setContentView(R.layout.dialog_forgot_password);
                dialog.setCancelable(false);
                // set the custom dialog components - text, image and button
                final EditText emailForgotPwd = (EditText) dialog.findViewById(R.id.etEmail_ForgotpasswordActivity);
                TextView submit = (TextView) dialog.findViewById(R.id.tvSubmit_ForgotpasswordActivity);
                TextView cancel = (TextView) dialog.findViewById(R.id.tvCancel_ForgotpasswordActivity);
                // if button is clicked, close the custom dialog
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        dialog.dismiss();
                        if (Util.isOnline(getApplicationContext())) {
                            if (!emailForgotPwd.getText().toString().equalsIgnoreCase("")) {
                                emailStr = emailForgotPwd.getText().toString();
                                new forgotpasswordData().execute();
                            } else {
                                toast("Please Enter Email Address.");
                            }
                        } else {
                            toast(Constant.network_error);
                        }
                    }
                });

                // if button is clicked, close the custom dialog
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
                break;
        }
    }


    public class makeLogin extends AsyncTask<String, String, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(LoginActivity.this);
            pd.setMessage("Loading");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";

            HashMap<String, String> hashmap = new HashMap<String, String>();
            hashmap.put("email", uNameStr);
            hashmap.put("password", uPwdStr);
            hashmap.put("token_id",refreshedToken);
            response = Util.getResponseofPost(Constant.BASE_URL + "login.php", hashmap);
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
                    String img = obj.getString("image");
                    String name = obj.getString("name");
                    String categorys = obj.getString("categories");
                    String isContentAuthor = obj.getString("is_content_author");
                    Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_IS_CONTENT_AUTHOR, isContentAuthor);
                    Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USER_NAME, name);
                    Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USER_PASSWORD, uPwdStr);
                    Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID, id);
                    Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_IS_LOGGEDIN, "1");
                    Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_PROFILE_PIC, img);
                    Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USER_EMAIL, email);
                    Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USER_INTREST,categorys);
                    toast(object.getString("msg"));
                    startActivity(MainActivity.class);
//                    loginForQuickBlox();
                    finish();
                } else
                {
                    Toast.makeText(LoginActivity.this, "" + object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    private boolean loginForQuickBlox() {
//        final QBUser user = new QBUser();
//        user.setEmail(uNameStr);
//        user.setPassword(uPwdStr);
//
//        QBUsers.signIn(user, new QBEntityCallback<QBUser>() {
//            @Override
//            public void onSuccess(QBUser users, Bundle params) {
//                toast("successfully login quick blox");
//                user.setId(users.getId());
//            }
//
//            @Override
//            public void onError(QBResponseException errors) {
//
//
//            }
//        });
//
//        return true;
//    }


    public class forgotpasswordData extends AsyncTask<String, String, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(LoginActivity.this);
            pd.setMessage("Loading");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
//            http://web-medico.com/web2/intrisfeed/api/forgot_password.php?email=archirayan32@gmail.com
            HashMap<String, String> hashmap = new HashMap<String, String>();
            hashmap.put("email", emailStr);
            response = Util.getResponseofPost(Constant.BASE_URL + "forgot_password.php", hashmap);

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
                    String password = obj.getString("password");
                    Log.e("id", password);
                    Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_PASSCODE, password);
                    toast(object.getString("msg"));
                    dialog.dismiss();
                } else {
                    Toast.makeText(LoginActivity.this, "" + object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}