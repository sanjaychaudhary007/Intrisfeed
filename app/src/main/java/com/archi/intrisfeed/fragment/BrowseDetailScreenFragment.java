package com.archi.intrisfeed.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.archi.intrisfeed.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by archi_info on 11/17/2016.
 */

public class BrowseDetailScreenFragment extends Fragment implements View.OnClickListener {
    CircleImageView ivProfilePic;
    ImageView ivEditProfilePic;
    EditText etName, etEmail, etCity, etCountry , etCategories, etApprovedCategory;
    HashMap<String, String> hashMap;
    Button btBack;
    String nameStr = "",cityStr ="", countryStr= "",emailStr= "",categoryStr="", imageStr="", approvedCat = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bwose_detail_screen, container, false);
//        hashMap = new HashMap<String, String>();
        ivProfilePic = (CircleImageView) rootView.findViewById(R.id.ivProfileImage_BrowseFragment);
        ivEditProfilePic =  (ImageView) rootView.findViewById(R.id.ivSelectImage_BrowseFragment);
        etName = (EditText) rootView.findViewById(R.id.etName__BrowseFragment);
        etEmail = (EditText) rootView.findViewById(R.id.etEmail_BrowseFragment);
        etCity = (EditText) rootView.findViewById(R.id.etCity_BrowseFragment);
        etCountry = (EditText) rootView.findViewById(R.id.etCountry_BrowseFragment);
        etCategories = (EditText) rootView.findViewById(R.id.etCategory_BrowseFragment);
        etApprovedCategory = (EditText) rootView.findViewById(R.id.etApprovedCategory_BrowseFragment);
        btBack = (Button) rootView.findViewById(R.id.btBack_Browse);
        btBack.setOnClickListener(this);

        Bundle b = this.getArguments();
        if (b.getString("list", "").equals("list")) {
            hashMap = (HashMap<String, String>) b.getSerializable("hashmap");
            nameStr = hashMap.get("name");
            cityStr = hashMap.get("city");
            countryStr = hashMap.get("country");
            emailStr = hashMap.get("email");
            imageStr = hashMap.get("image");
            categoryStr = hashMap.get("categories");
            approvedCat = hashMap.get("aproved_category");


            //    bundle.putString("name", hashMap.get("name"));
//                bundle.putString("email", hashMap.get("email"));
//                bundle.putString("image", hashMap.get("image"));
//                bundle.putString("categories", hashMap.get("categories"));
//                bundle.putString("city", hashMap.get("city"));
//                bundle.putString("country", hashMap.get("country"));

        }

        setData();
        return rootView;
    }


    private void setData() {
        etName.setText(nameStr);
        etEmail.setText(emailStr);
        etCity.setText(cityStr);
        etCountry.setText(countryStr);
        etCategories.setText(categoryStr);
        etApprovedCategory.setText(approvedCat);
        Picasso.with(getActivity()).load(imageStr).error(R.drawable.ic_user).placeholder(R.drawable.default_img).into(ivProfilePic);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = new BrowseFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();
    }
}
