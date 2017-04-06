package com.archi.intrisfeed;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by archi on 12/7/2016.
 */

public class OpenGroupLinkActivity extends AppCompatActivity {
    public  WebView webView;
    public ImageView ivBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_link_open_activity);
        webView = (WebView)findViewById(R.id.activity_group_link_activity_webview);
        ivBack = (ImageView)findViewById(R.id.activity_image_view_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        if (getIntent().getExtras() != null)
        {
            String openUrl = getIntent().getExtras().getString("url");
            webView.setWebViewClient(new WebViewClient());
            openUrl(openUrl);

        }
        else
        {
            Toast.makeText(OpenGroupLinkActivity.this,"something went wrong ",Toast.LENGTH_SHORT).show();
        }
    }

    private void openUrl(String openUrl) {
        webView.setWebViewClient(new WebViewClient() {
                ProgressDialog progressDialog;

                //If you will not use this method url links are opeen in new brower not in webview
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                //Show loader on url load
                public void onLoadResource (WebView view, String url) {
                    if (progressDialog == null) {
                        // in standard case YourActivity.this
                        progressDialog = new ProgressDialog(OpenGroupLinkActivity.this);
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();
                    }
                }
                public void onPageFinished(WebView view, String url) {
                    try{
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                         //  progressDialog = null;
                        }
                    }catch(Exception exception){
                        exception.printStackTrace();
                    }
                }

            });

            // Javascript inabled on webview
       // webView.getSettings().setJavaScriptEnabled(true);


        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMinimumFontSize(10);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.loadDataWithBaseURL(null, openUrl, "text/html", "UTF-8", null);

       webView.loadUrl(openUrl);
        webView.requestFocus();

}}

