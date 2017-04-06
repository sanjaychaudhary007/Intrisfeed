package com.archi.intrisfeed;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.archi.intrisfeed.adapter.ContentAutherDetailAdapter;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by archi_info on 12/9/2016.
 */

public class ContentAuthorDetailActivity extends AppCompatActivity implements View.OnClickListener {
   public ImageView ivBack,ivAutherImage;
    public TextView tvAutherName,tvAutherEmail;
    public ListView lvAutherCategoryTitle;
    public ContentAutherDetailAdapter contentAuthorDetailAdapter;
    public String strName,strEmail,strImage,strUserId;
    public ArrayList<HashMap<String,String>> arrayContent;
    public LinearLayout linearLayoutEmailSend;
    public ImageView ivLargeView;
    public Animator mCurrentAnimatorEffect;
    public int mShortAnimationDurationEffect;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_content_author_detail);
        ivBack = (ImageView)findViewById(R.id.activity_content_authore_back);
        ivAutherImage = (ImageView)findViewById(R.id.activity_content_auther_detail_iv_auther);
        tvAutherName = (TextView)findViewById(R.id.activity_auther_detail_tv_auther_name);
        tvAutherEmail = (TextView)findViewById(R.id.activity_content_author_detail_tv_author_email);
        //ivLargeView = (ImageView)findViewById(R.id.activiy_content_author_detail_iv_zoom);
        lvAutherCategoryTitle = (ListView)findViewById(R.id.activity_author_detail_contain_lv);
        linearLayoutEmailSend = (LinearLayout)findViewById(R.id.activity_content_auther_detail_email_send);
        if (getIntent().getExtras() != null)
        {
             strName = getIntent().getExtras().getString("name");
             strEmail = getIntent().getExtras().getString("email");
             strImage = getIntent().getExtras().getString("image");
             strUserId = getIntent().getExtras().getString("userId");
             tvAutherEmail.setText(strEmail);
             tvAutherName.setText(strName);
             Picasso.with(ContentAuthorDetailActivity.this).load(strImage).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(ivAutherImage);
        }
        init();

    }

    private void init()
    {

        mShortAnimationDurationEffect = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        ivBack.setOnClickListener(this);
        linearLayoutEmailSend.setOnClickListener(this);
        ivAutherImage.setOnClickListener(this);
        new GetContent().execute();
    }

    @Override
    public void onClick(View v) {

     switch (v.getId())
     {
         case R.id.activity_content_authore_back:
             onBackPressed();
             break;


         case R.id.activity_content_auther_detail_email_send:
             Intent intent = new Intent(Intent.ACTION_SEND);
             intent.setType("plain/text");
             intent.putExtra(Intent.EXTRA_EMAIL, new String[] {strEmail});
             intent.putExtra(Intent.EXTRA_SUBJECT, "intrisfeed");
             intent.putExtra(Intent.EXTRA_TEXT, "mail body");
             startActivity(Intent.createChooser(intent, "Choose an Email client :"));
             break;


         case R.id.activity_content_auther_detail_iv_auther:
             zoomImageToThumb();
             break;
     }

    }

    private void zoomImageToThumb()
    {
        Dialog settingsDialog = new Dialog(this);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.dialog_image_zoom,null));
        ImageView ivZoom = (ImageView) settingsDialog.findViewById(R.id.dialog_image_view_zoom);
        Picasso.with(ContentAuthorDetailActivity.this).load(strImage).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(ivZoom);
        settingsDialog.show();
    }


    private class GetContent extends AsyncTask<String,String,String>
    {
        KProgressHUD hud;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

           hud =KProgressHUD.create(ContentAuthorDetailActivity.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .show();

            arrayContent = new ArrayList<>();

        }

        @Override
        protected String doInBackground(String... params) {
           //http://181.224.157.105/~hirepeop/host1/intrisfeed/api/get_content_post.php?user_id=32
            String url = Constant.BASE_URL + "get_content_post.php?user_id=" + strUserId;
            return Util.getResponseofGet(url);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);

                 if (jsonObject.getString("status").equalsIgnoreCase("true"))
                 {
                     JSONArray jsonArray = jsonObject.getJSONArray("data");
                     for (int i=0;i<jsonArray.length();i++)
                     {
                         JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                         HashMap<String,String> hashMap = new HashMap<>();
                         hashMap.put("category",jsonObject1.getString("category"));
                         hashMap.put("content_title",jsonObject1.getString("content_title"));
                         hashMap.put("content_details",jsonObject1.getString("content_details"));
                         hashMap.put("content_link",jsonObject1.getString("content_link"));
                         hashMap.put("content_author_email",jsonObject1.getString("content_author_email"));
                         hashMap.put("image",jsonObject1.getString("image"));
                         hashMap.put("video",jsonObject1.getString("video"));
                         hashMap.put("doc",jsonObject1.getString("doc"));
                         arrayContent.add(hashMap);
                     }
                     contentAuthorDetailAdapter = new ContentAutherDetailAdapter(ContentAuthorDetailActivity.this,arrayContent);
                     lvAutherCategoryTitle.setAdapter(contentAuthorDetailAdapter);


                 }
                else
                 {

                 }

            }
            catch (JSONException e) {
                e.printStackTrace();

            }
            hud.dismiss();
        }
    }
}
