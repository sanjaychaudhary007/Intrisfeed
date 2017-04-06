package com.archi.intrisfeed.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.archi.intrisfeed.R;

/**
 * Created by archi_info on 11/12/2016.
 */

public class ViewInarnsfeedDetailsFragment extends Fragment implements View.OnClickListener {
    private ListView lvAllFeedList;
    private TextView tvBack;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_intarnsfeed_details, container, false);
        lvAllFeedList = (ListView) rootView.findViewById(R.id.lvAllFeedList);
        tvBack = (TextView) rootView.findViewById(R.id.tvBackDetails);
        tvBack.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvBackDetails:

            break;
        }
    }
}
