package com.archi.intrisfeed.adapter;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.archi.intrisfeed.R;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by archi_info on 11/21/2016.
 */

public class CustomPagerAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<HashMap<String,String>> picsList;
    FragmentManager fm;



    public CustomPagerAdapter(Context context, ArrayList<HashMap<String, String>> imageFeedList, FragmentManager fragmentManager) {
        this.mContext = context;
        this.picsList = imageFeedList;
        this.fm = fragmentManager;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return picsList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return  view == ((LinearLayout) object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageViewPager);
        String imagePath = picsList.get(position).get("image");
        Log.e("IMAGE PATH",""+imagePath);
        Picasso.with(mContext).load(imagePath).error(R.drawable.default_img).placeholder(R.drawable.default_img).into(imageView);
//        imageView.setImageResource(mResources[position]);
//        new LoadImage(imageView).execute("Your URL");

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        ImageView img=null;
        public LoadImage(ImageView img){
            this.img=img;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected Bitmap doInBackground(String... args) {
            Bitmap bitmap=null;
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap image) {
            if(image != null){
                img.setImageBitmap(image);
            }
        }
    }
}
