package com.archi.intrisfeed;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;

/**
 * Created by archi_info on 9/21/2016.
 */
public class SplashScreenActivity extends BaseActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    //    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.WRITE_CONTACTS};

    LinearLayout llName;
//    public static final String APP_ID = "47880";
//    public static final String AUTH_KEY = "hCQpjWq4HcFV5uk";
//    public static final String AUTH_SECRET = "NWKJSNhWHCZYgF9";
//    public static final String ACCOUNT_KEY = "2xzLtJGk1KXi7Uq8sys3";

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    /**
     * Called when the activity is first created.
     */
    Thread splashTread;
    SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            checkPermission();
        }
        setContentView(R.layout.activity_splash);
        llName = (LinearLayout) findViewById(R.id.llAppname);
//        QBSettings.getInstance().init(this, APP_ID, AUTH_KEY, AUTH_SECRET);
//        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);

        StartAnimations();

    }

    public void checkPermission(){
            // Marshmallow+
            if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
                ActivityCompat.requestPermissions(SplashScreenActivity.this, PERMISSIONS, PERMISSION_REQUEST_CODE);
            }else{

        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();

        llName.clearAnimation();
        llName.startAnimation(anim);

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3500) {
                        sleep(100);
                        waited += 100;
                    }
                    String status = Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_IS_LOGGEDIN);
                    if (status.equalsIgnoreCase("1")) {
                        startActivity(MainActivity.class);
                    } else {
                        startActivity(LoginActivity.class);
                    }

                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    SplashScreenActivity.this.finish();
                }

            }
        };
        splashTread.start();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_REQUEST_CODE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                } else {
//                    //code for deny
//                    Toast.makeText(SplashScreenActivity.this, "No Permission Granted, Access Denied", Toast.LENGTH_SHORT).show();
//                }
//                break;
//        }
//    }
}
