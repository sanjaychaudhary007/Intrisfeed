package com.archi.intrisfeed.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.archi.intrisfeed.R;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by archi_info on 11/22/2016.
 */

public class InboxDetailFragment extends Fragment implements View.OnClickListener {
    TextView tvTitle, tvFrom, tvLink, tvDesc;
    String titleStr, fromStr, linkStr, descStr;
    ImageView ivCategory;
    VideoView vwCategory;
    String response = "";
    ArrayList<HashMap<String, String>> imageFeedList;
    TextView tvBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox_details, container, false);
        tvTitle = (TextView) rootView.findViewById(R.id.tvTitle_InboxDetails);
        tvFrom = (TextView) rootView.findViewById(R.id.tvFrom_InboxDetails);
        tvLink = (TextView) rootView.findViewById(R.id.tvLink_InboxDetails);
        tvDesc = (TextView) rootView.findViewById(R.id.tvDesc_InboxDetails);
        ivCategory = (ImageView) rootView.findViewById(R.id.tvImage_InboxDetails);
        vwCategory = (VideoView) rootView.findViewById(R.id.tvVideo_InboxDetails);
        vwCategory.pause();
        tvBack = (TextView) rootView.findViewById(R.id.tvBackInboxDetails);
        tvBack.setOnClickListener(this);

        Bundle extras = getArguments();
        if (extras != null) {
            Log.e("RESPONSE","DETAILS >> "+extras.getString("content_title"));

                titleStr = extras.getString("content_title");
                Log.e("TITLE",">> M "+extras.getString("content_title"));
                if(Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_IS_SENT_OR_RECEIVE).equalsIgnoreCase("1")) {
                    fromStr = extras.getString("recevie_mailid");
                }else{
                    fromStr = extras.getString("sent_mailid");
                }
                linkStr = extras.getString("content_link");
                descStr = extras.getString("content_details");
                String image = extras.getString("image");
                if(!image.equalsIgnoreCase("")){
                    Picasso.with(getActivity()).load(image).error(R.drawable.default_img).placeholder(R.drawable.default_img).into(ivCategory);
                }

                String video = extras.getString("video");
                if(!video.equalsIgnoreCase("")) {
                    vwCategory.setVideoURI(Uri.parse(video));
                    vwCategory.seekTo(200);
                }
        }

        setData();
        return rootView;
    }

    private void setData() {
        tvTitle.setText(titleStr);
        tvFrom.setText(fromStr);
        tvLink.setText(linkStr);
        tvDesc.setText(descStr);


    }

    @Override
    public void onClick(View v) {
        Fragment fragment = new InboxFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();
    }
}
