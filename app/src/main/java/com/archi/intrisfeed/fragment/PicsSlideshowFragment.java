package com.archi.intrisfeed.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.archi.intrisfeed.R;
import com.archi.intrisfeed.adapter.CustomPagerAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by archi_info on 11/21/2016.
 */

public class PicsSlideshowFragment extends Fragment implements View.OnClickListener {
    public String response, category, keyword;
    public HashMap<String, String> hashmap;
    public ArrayList<HashMap<String, String>> imageList;
    CustomPagerAdapter mCustomPagerAdapter;
    ViewPager mViewPager;
    int noofsize;
    int count = 0;
    private LinearLayout llVIewPager;

    TextView tvBack;
    Timer timer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_pics_slide_show, container, false);
        llVIewPager = (LinearLayout) view.findViewById(R.id.llViewPager);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        tvBack = (TextView) view.findViewById(R.id.tvBackBottomViewPager);
        tvBack.setOnClickListener(this);
        llVIewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                timer.cancel();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        startSlidingImages();
                    }
                }, 15000);
                return false;
            }
        });


        Bundle extras = getArguments();

        if (extras != null)
        {
            keyword = extras.getString("keyword");
            category = extras.getString("category");
            response = extras.getString("response");
            imageList = new ArrayList<HashMap<String, String>>();
        }

        try {
            JSONObject object = new JSONObject(response.toString());
            if (object.getString("status").equalsIgnoreCase("true")) {
                Log.e("DATA", "resp " + object.getJSONArray("data"));
                JSONArray arry = object.getJSONArray("data");

                // get AND  SET IMAGE LIST
                if (keyword.equalsIgnoreCase("Pics")) {
                    for (int i = 0; i < arry.length(); i++) {
                        JSONObject obj = arry.getJSONObject(i);

                        hashmap = new HashMap<String, String>();
                        if (!obj.get("image").toString().equalsIgnoreCase("")) {
                            Log.e("CATEGORY ", ">> " + obj.getString("category"));
                            hashmap.put("category", obj.getString("category"));
                            hashmap.put("image", "" + obj.getString("image"));
                            hashmap.put("content_link", "" + obj.get("content_link"));
                            imageList.add(hashmap);
                        }

                    }
                    mCustomPagerAdapter = new CustomPagerAdapter(getActivity(), imageList, getActivity().getFragmentManager());
                    mViewPager.setAdapter(mCustomPagerAdapter);
                }
            } else {
                Toast.makeText(getActivity(), "" + object.getString("msg"), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        noofsize = imageList.size();


        startSlidingImages();

        return view;
    }

    private void startSlidingImages() {
        // Timer for auto sliding
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (count <= noofsize) {
                            mViewPager.setCurrentItem(count);
                            count++;
                        } else {
                            count = 0;
                            mViewPager.setCurrentItem(count);
                        }
                    }
                });
            }
        }, 500, 3000);
    }


    @Override
    public void onClick(View v) {
        Fragment fragment = new InterisfeedFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
            final String strDate = simpleDateFormat.format(calendar.getTime());
            Log.e("TIMER 1", ">>>>>> " + strDate);
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    Log.e("TIMER ", ">>>>>> " + strDate);
                }
            });
        }

    }

}
