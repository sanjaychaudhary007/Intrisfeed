package com.archi.intrisfeed.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.LoginFilter;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.archi.intrisfeed.R;
import com.archi.intrisfeed.adapter.FeedAllAdapter;
import com.archi.intrisfeed.adapter.FeedArticlesAdapter;
import com.archi.intrisfeed.adapter.FeedBlogsAdapter;
import com.archi.intrisfeed.adapter.FeedImageAdapter;
import com.archi.intrisfeed.adapter.FeedVideoAdapter;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;
import com.google.android.gms.location.internal.LocationRequestUpdateData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by archi_info on 9/24/2016.
 * PAYPAL Content author : CLIENT ID
 * CLIENT ID : AdKw0NYTKt_DLcGzi8UDBEYEHpORuy_TfPWvhb-79GT17Bp73JkkTTXfjoH0mHBG-XNfOz7sgugtuP_o
 * SECRET : EGiBJ90-UU9ZiepZJRwSGjO-vya3w0S8Xztc_4p4GVIZ7AGcksAY9mBxS7RVL9fpZnoCsn6MvzqMPYes
 */
public class InterisfeedFragment extends Fragment implements View.OnClickListener {
    FrameLayout flSpinner1, flSpinner2, flSpinner3, flSpinner4, flSpinner5, flSpinner6, flSpinner7;
    public ArrayList<String> arrayListSelectedCategorys;
    private RadioGroup radioCategoryGroup;
    private RadioButton radioCategoryButton;
    private String selectedCategory="",spinnerValue1 = "video", spinnerValue2 = "video", spinnerValue3 = "video", spinnerValue4 = "video", spinnerValue5 = "video", spinnerValue6 = "video", spinnerValue7 = "video";
    private TextView tvEnter1, tvEnter2, tvEnter3, tvEnter4, tvEnter5, tvEnter6, tvEnter7;
    private EditText etCategoryName1, etCategoryName2, etCategoryName3, etCategoryName4, etCategoryName5, etCategoryName6, etCategoryName7;
    private ArrayList<HashMap<String, String>> imageFeedList;
    private HashMap<String, String> hashmap;
    private FeedImageAdapter adapterFeedImage;
    private FeedVideoAdapter adapterFeedVideo;
    private FeedBlogsAdapter adapterFeedBlogs;
    private FeedArticlesAdapter adapterFeedArticles;
    private FeedAllAdapter adapterFeedAll;
//    private ListView lvAllFeedList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_intrisfeed, container, false);
        // Spinner Drop down elements

//        lvAllFeedList = (ListView) rootView.findViewById(lvAllFeedList);
//        1. Pics 2. Videos 3. Articles (stories) 4. Blogs 5. ALL.
        flSpinner1 = (FrameLayout) rootView.findViewById(R.id.flDropDownKeyword1);
        flSpinner2 = (FrameLayout) rootView.findViewById(R.id.flDropDownKeyword2);
        flSpinner3 = (FrameLayout) rootView.findViewById(R.id.flDropDownKeyword3);
        flSpinner4 = (FrameLayout) rootView.findViewById(R.id.flDropDownKeyword4);
        flSpinner5 = (FrameLayout) rootView.findViewById(R.id.flDropDownKeyword5);
        flSpinner6 = (FrameLayout) rootView.findViewById(R.id.flDropDownKeyword6);
        flSpinner7 = (FrameLayout) rootView.findViewById(R.id.flDropDownKeyword7);

        tvEnter1 = (TextView) rootView.findViewById(R.id.tvEnter_Feed1);
        tvEnter2 = (TextView) rootView.findViewById(R.id.tvEnter_Feed2);
        tvEnter3 = (TextView) rootView.findViewById(R.id.tvEnter_Feed3);
        tvEnter4 = (TextView) rootView.findViewById(R.id.tvEnter_Feed4);
        tvEnter5 = (TextView) rootView.findViewById(R.id.tvEnter_Feed5);
        tvEnter6 = (TextView) rootView.findViewById(R.id.tvEnter_Feed6);
        tvEnter7 = (TextView) rootView.findViewById(R.id.tvEnter_Feed7);

        etCategoryName1 = (EditText) rootView.findViewById(R.id.etCategoryName1);
        etCategoryName2 = (EditText) rootView.findViewById(R.id.etCategoryName2);
        etCategoryName3 = (EditText) rootView.findViewById(R.id.etCategoryName3);
        etCategoryName4 = (EditText) rootView.findViewById(R.id.etCategoryName4);
        etCategoryName5 = (EditText) rootView.findViewById(R.id.etCategoryName5);
        etCategoryName6 = (EditText) rootView.findViewById(R.id.etCategoryName6);
        etCategoryName7 = (EditText) rootView.findViewById(R.id.etCategoryName7);

        flSpinner1.setOnClickListener(this);
        flSpinner2.setOnClickListener(this);
        flSpinner3.setOnClickListener(this);
        flSpinner4.setOnClickListener(this);
        flSpinner5.setOnClickListener(this);
        flSpinner6.setOnClickListener(this);
        flSpinner7.setOnClickListener(this);

        tvEnter1.setOnClickListener(this);
        tvEnter2.setOnClickListener(this);
        tvEnter3.setOnClickListener(this);
        tvEnter4.setOnClickListener(this);
        tvEnter5.setOnClickListener(this);
        tvEnter6.setOnClickListener(this);
        tvEnter7.setOnClickListener(this);

//        lvAllFeedList.setOnTouchListener(new View.OnTouchListener() {
//            // Setting on Touch Listener for handling the touch inside ScrollView
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // Disallow the touch request for parent scroll on touch of child view
//                v.getParent().requestDisallowInterceptTouchEvent(true);
//                return false;
//            }
//        });
//        setListViewHeightBasedOnChildren(lvAllFeedList);
        return rootView;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }

            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public void showFeedDetails(final Context mContext, final int position) {
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.popup_category_home);
        dialog.setTitle("Intrest Feed");
        dialog.setCancelable(false);
        final ArrayAdapter<String> categoryAdapter;

        final ListView lvCateGory = (ListView)dialog.findViewById(R.id.pupup_category_home_listview);

        String[] sports = getResources().getStringArray(R.array.category_list);
        categoryAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_multiple_choice, sports);
        lvCateGory.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lvCateGory.setAdapter(categoryAdapter);

       // radioCategoryGroup = (RadioGroup) dialog.findViewById(R.id.radioCategory);

        final TextView ok = (TextView) dialog.findViewById(R.id.tvSubmit_HomeFragment);
        TextView cancel = (TextView) dialog.findViewById(R.id.tvCancel_HomeFragment);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                arrayListSelectedCategorys = new ArrayList<String>();
                SparseBooleanArray checked = lvCateGory.getCheckedItemPositions();
                ArrayList<String> selectedItems = new ArrayList<String>();
                for (int i=0;i<checked.size();i++)
                {
                    int position = checked.keyAt(i);

                    if (checked.valueAt(i))
                    {
                        selectedItems.add(categoryAdapter.getItem(position));
                    }
                }

                String[] outputStringArray = new String[selectedItems.size()];
                for (int i1=0;i1<outputStringArray.length;i1++)
                {
                    outputStringArray[i1] = selectedItems.get(i1);
                    Log.d("jai","array sizes :"+selectedItems.get(i1));
                    arrayListSelectedCategorys.add(selectedItems.get(i1));
                }
                dialog.dismiss();
            }

        });



//        ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // get selected radio button from radioGroup
//                int selectedId = radioCategoryGroup.getCheckedRadioButtonId();
//                // find the radiobutton by returned id
//                radioCategoryButton = (RadioButton) dialog.findViewById(selectedId);
//                Toast.makeText(mContext, radioCategoryButton.getText(), Toast.LENGTH_SHORT).show();
//                switch (position) {
//                    case 1:
//                        spinnerValue1 = radioCategoryButton.getText().toString();
//                        Log.e("Val1", ">> " + spinnerValue1);
//                        break;
//
//                    case 2:
//                        spinnerValue2 = radioCategoryButton.getText().toString();
//                        Log.e("Val2", ">> " + spinnerValue2);
//                        break;
//
//                    case 3:
//                        spinnerValue3 = radioCategoryButton.getText().toString();
//                        Log.e("Val3", ">> " + spinnerValue3);
//                        break;
//
//                    case 4:
//                        spinnerValue4 = radioCategoryButton.getText().toString();
//                        Log.e("Val4", ">> " + spinnerValue4);
//                        break;
//
//                    case 5:
//                        spinnerValue5 = radioCategoryButton.getText().toString();
//                        Log.e("Val5", ">> " + spinnerValue5);
//                        break;
//
//                    case 6:
//                        spinnerValue6 = radioCategoryButton.getText().toString();
//                        Log.e("Val6", ">> " + spinnerValue6);
//                        break;
//
//                    case 7:
//                        spinnerValue7 = radioCategoryButton.getText().toString();
//                        Log.e("Val7", ">> " + spinnerValue7);
//                        break;
//
//                }
//                dialog.dismiss();
//
//            }
//        });
//
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flDropDownKeyword1:
                showFeedDetails(getActivity(), 1);
                break;
            case R.id.flDropDownKeyword2:
                showFeedDetails(getActivity(), 2);
                break;
            case R.id.flDropDownKeyword3:
                showFeedDetails(getActivity(), 3);
                break;
            case R.id.flDropDownKeyword4:
                showFeedDetails(getActivity(), 4);
                break;
            case R.id.flDropDownKeyword5:
                showFeedDetails(getActivity(), 5);
                break;
            case R.id.flDropDownKeyword6:
                showFeedDetails(getActivity(), 6);
                break;
            case R.id.flDropDownKeyword7:
                showFeedDetails(getActivity(), 7);
                break;

            case R.id.tvEnter_Feed1:
                if (!spinnerValue1.equalsIgnoreCase("")) {
                    if (!etCategoryName1.getText().toString().equalsIgnoreCase("")) {
                        String cat = etCategoryName1.getText().toString();
                        new getFeedContentUsingCategoryAndKeyword(spinnerValue1, cat).execute();
                    } else {
                        Toast.makeText(getActivity(), "Please Enter Keyword to Search Feed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please Select Category from Down Arrow", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.tvEnter_Feed2:
                if (!spinnerValue2.equalsIgnoreCase("")) {
                    if (!etCategoryName2.getText().toString().equalsIgnoreCase("")) {
                        String cat = etCategoryName2.getText().toString();
                        new getFeedContentUsingCategoryAndKeyword(spinnerValue2, cat).execute();
                    } else {
                        Toast.makeText(getActivity(), "Please Enter Keyword to Search Feed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please Select Category from Down Arrow", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tvEnter_Feed3:
                if (!spinnerValue3.equalsIgnoreCase("")) {
                    if (!etCategoryName3.getText().toString().equalsIgnoreCase("")) {
                        String cat = etCategoryName3.getText().toString();
                        new getFeedContentUsingCategoryAndKeyword(spinnerValue3, cat).execute();
                    } else {
                        Toast.makeText(getActivity(), "Please Enter Keyword to Search Feed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please Select Category from Down Arrow", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tvEnter_Feed4:
                if (!spinnerValue4.equalsIgnoreCase("")) {
                    if (!etCategoryName4.getText().toString().equalsIgnoreCase("")) {
                        String cat = etCategoryName4.getText().toString();
                        new getFeedContentUsingCategoryAndKeyword(spinnerValue4, cat).execute();
                    } else {
                        Toast.makeText(getActivity(), "Please Enter Keyword to Search Feed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please Select Category from Down Arrow", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tvEnter_Feed5:
                if (!spinnerValue5.equalsIgnoreCase("")) {
                    if (!etCategoryName5.getText().toString().equalsIgnoreCase("")) {
                        String cat = etCategoryName5.getText().toString();
                        new getFeedContentUsingCategoryAndKeyword(spinnerValue5, cat).execute();
                    } else {
                        Toast.makeText(getActivity(), "Please Enter Keyword to Search Feed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please Select Category from Down Arrow", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tvEnter_Feed6:
                if (!spinnerValue6.equalsIgnoreCase("")) {
                    if (!etCategoryName6.getText().toString().equalsIgnoreCase("")) {
                        String cat = etCategoryName6.getText().toString();
                        new getFeedContentUsingCategoryAndKeyword(spinnerValue6, cat).execute();
                    } else {
                        Toast.makeText(getActivity(), "Please Enter Keyword to Search Feed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please Select Category from Down Arrow", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tvEnter_Feed7:
                if (!spinnerValue7.equalsIgnoreCase("")) {
                    if (!etCategoryName7.getText().toString().equalsIgnoreCase("")) {
                        String cat = etCategoryName7.getText().toString();
                        new getFeedContentUsingCategoryAndKeyword(spinnerValue7, cat).execute();
                    } else {
                        Toast.makeText(getActivity(), "Please Enter Keyword to Search Feed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please Select Category from Down Arrow", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    //http://181.224.157.105/~hirepeop/host1/intrisfeed/api/search_content_post.php?category=abc&keyword=All


    public class getFeedContentUsingCategoryAndKeyword extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        String category, keyword;

        public getFeedContentUsingCategoryAndKeyword(String keywrd, String cat) {
            this.keyword = keywrd;
            this.category = cat;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Loading");
            pd.setCancelable(false);
            pd.show();
            imageFeedList = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
         ;
            if (arrayListSelectedCategorys.size() == 0)
            {
                Toast.makeText(getActivity(),"please select the Items",Toast.LENGTH_SHORT).show();

            }
            else {
                for (int i = 0; i < arrayListSelectedCategorys.size(); i++) {
                    if (i <= 0) {
                        selectedCategory = arrayListSelectedCategorys.get(i);
                    } else {
                        selectedCategory = selectedCategory + "," + arrayListSelectedCategorys.get(i);
                    }
                }

               // http://181.224.157.105/~hirepeop/host1/intrisfeed/api/search_content_post_exp.php?category=study&keyword=all

                HashMap<String, String> hashmap = new HashMap<String, String>();
                hashmap.put("category", category);
                hashmap.put("keyword", selectedCategory);
                response = Util.getResponseofPost(Constant.BASE_URL + "search_content_post_exp.php", hashmap);
//                urlString = Constant.BASE_URL + "login.php?email=" + usernamestr + "&password=" + passwordstr;
                Log.e("RESULT", ">>" + response);
            }
                return response;

        }

        @Override
        protected void onPostExecute(String s) {
//            pd.dismiss();
            super.onPostExecute(s);
            pd.dismiss();

            Bundle bundle = new Bundle();
            bundle.putString("response", s);
            bundle.putString("category",category);
            bundle.putString("keyword",selectedCategory);

            Fragment fragment = new InterisfeedDetails();
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();


//            if(keyword.equalsIgnoreCase("Pics")){
//                Fragment fragment = new PicsSlideshowFragment();
//                fragment.setArguments(bundle);
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.container_body, fragment);
//                fragmentTransaction.commit();
//            }else{
//                Fragment fragment = new InterisfeedDetails();
//                fragment.setArguments(bundle);
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.container_body, fragment);
//                fragmentTransaction.commit();
//            }


//            try {
//                JSONObject object = new JSONObject(s.toString());
//                if (object.getString("status").equalsIgnoreCase("true")) {
//                    Log.e("DATA", "resp " + object.getJSONArray("data"));
//                    JSONArray arry = object.getJSONArray("data");
//
//                    // get AND  SET IMAGE LIST
//                    if (keyword.equalsIgnoreCase("Pics")) {
//                        for (int i = 0; i < arry.length(); i++) {
//                            JSONObject obj = arry.getJSONObject(i);
//
//                            hashmap = new HashMap<String, String>();
//                            if (!obj.get("image").toString().equalsIgnoreCase("")) {
//                                Log.e("CATEGORY ", ">> " + obj.getString("category"));
//                                hashmap.put("category", obj.getString("category"));
//                                hashmap.put("image", "" + obj.getString("image"));
//                                hashmap.put("content_link", "" + obj.get("content_link"));
//                                imageFeedList.add(hashmap);
//                            }
//
//                        }
//                        adapterFeedImage = new FeedImageAdapter(getActivity(), imageFeedList, getActivity().getFragmentManager());
//                        lvAllFeedList.setAdapter(adapterFeedImage);
//                        adapterFeedImage.notifyDataSetChanged();
//                    }
//
//                    // get & set Video List
//                    if (keyword.equalsIgnoreCase("Videos")) {
//                        for (int i = 0; i < arry.length(); i++) {
//                            JSONObject obj = arry.getJSONObject(i);
//
//                            hashmap = new HashMap<String, String>();
//                            if (!obj.get("video").toString().equalsIgnoreCase("")) {
//                                Log.e("CATEGORY ", ">> " + obj.getString("category"));
//                                hashmap.put("category", obj.getString("category"));
//                                hashmap.put("video", "" + obj.getString("video"));
//                                hashmap.put("content_link", "" + obj.get("content_link"));
//                                imageFeedList.add(hashmap);
//                            }
//
//                        }
//                        adapterFeedVideo = new FeedVideoAdapter(getActivity(), imageFeedList, getActivity().getFragmentManager());
//                        lvAllFeedList.setAdapter(adapterFeedVideo);
//                        adapterFeedVideo.notifyDataSetChanged();
//                    }
//
//
//                    // get & set Document List
//                    if (keyword.equalsIgnoreCase("Blogs")) {
//                        for (int i = 0; i < arry.length(); i++) {
//                            JSONObject obj = arry.getJSONObject(i);
//
//                            hashmap = new HashMap<String, String>();
//                            if (!obj.get("doc").toString().equalsIgnoreCase("")) {
//                                Log.e("CATEGORY ", ">> " + obj.getString("category"));
//                                hashmap.put("category", obj.getString("category"));
//                                hashmap.put("doc", "" + obj.getString("doc"));
//                                hashmap.put("content_link", "" + obj.get("content_link"));
//                                imageFeedList.add(hashmap);
//                            }
//                        }
//                        adapterFeedBlogs = new FeedBlogsAdapter(getActivity(), imageFeedList, getActivity().getFragmentManager());
//                        lvAllFeedList.setAdapter(adapterFeedBlogs);
//                        adapterFeedBlogs.notifyDataSetChanged();
//                    }
//
//
//                    // get & set Articles List
//                    if (keyword.equalsIgnoreCase("Articles")) {
//                        for (int i = 0; i < arry.length(); i++) {
//                            JSONObject obj = arry.getJSONObject(i);
//
//                            hashmap = new HashMap<String, String>();
//                            if (!obj.get("content_details").toString().equalsIgnoreCase("")) {
//                                Log.e("CATEGORY ", ">> " + obj.getString("category"));
//                                hashmap.put("category", obj.getString("category"));
//                                hashmap.put("content_details", "" + obj.getString("content_details"));
//                                hashmap.put("content_link", "" + obj.get("content_link"));
//                                imageFeedList.add(hashmap);
//                            }
//                        }
//                        adapterFeedArticles = new FeedArticlesAdapter(getActivity(), imageFeedList, getActivity().getFragmentManager());
//                        lvAllFeedList.setAdapter(adapterFeedArticles);
//                        adapterFeedArticles.notifyDataSetChanged();
//                    }
//
//
//                    // get & set All List
//                    if (keyword.equalsIgnoreCase("All")) {
//                        for (int i = 0; i < arry.length(); i++) {
//                            JSONObject obj = arry.getJSONObject(i);
//
//                            hashmap = new HashMap<String, String>();
//
//                            Log.e("CATEGORY ", ">> " + obj.getString("content_title"));
//                            hashmap.put("category", obj.getString("category"));
//                            hashmap.put("content_title", obj.getString("content_title"));
//                            hashmap.put("image", "" + obj.getString("image"));
//                            hashmap.put("doc", "" + obj.getString("doc"));
//                            hashmap.put("video", "" + obj.getString("video"));
//                            hashmap.put("content_details", "" + obj.getString("content_details"));
//                            hashmap.put("content_link", "" + obj.getString("content_link"));
//                            imageFeedList.add(hashmap);
//
//                        }
////                        setListViewHeightBasedOnChildren(lvAllFeedList);
////                        adapterFeedAll = new FeedAllAdapter(getActivity(), imageFeedList, getActivity().getFragmentManager());
////                        lvAllFeedList.setAdapter(adapterFeedAll);
////                        adapterFeedAll.notifyDataSetChanged();
//
//                        Bundle bundle = new Bundle();
//                        bundle.putString("response", s);
//                        bundle.putString("category", category);
//                        bundle.putString("keyword", keyword);
//                        Fragment fragment = new InterisfeedDetails();
//                        fragment.setArguments(bundle);
//                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                        fragmentTransaction.replace(R.id.container_body, fragment);
//                        fragmentTransaction.commit();
//
//                    }
//
//
//                } else {
//                    Toast.makeText(getActivity(), "" + object.getString("msg"), Toast.LENGTH_SHORT).show();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }

}
