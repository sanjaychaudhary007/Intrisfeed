package com.archi.intrisfeed.adapter;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.archi.intrisfeed.R;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by archi_info on 11/14/2016.
 */

public class FeedAllAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<HashMap<String, String>> allList;
    private LayoutInflater inflater = null;
    HashMap<String, String> hashMap;
    FragmentManager fm;

    public FeedAllAdapter(Context mCtx, ArrayList<HashMap<String, String>> list, FragmentManager fm) {
        this.mContext = mCtx;
        this.allList = list;
        Log.e("SIZE ", "M>>" + list.size());
        this.fm = fm;
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return allList.size();
    }

    @Override
    public Object getItem(int position) {
        return allList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolderItem viewHolder;
        if (convertView == null) {
            // inflate the layout
            convertView = inflater.inflate(R.layout.adapter_feed_all, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.tvCategoryNameAll = (TextView) convertView.findViewById(R.id.tvCategory_AllFeedAdapter);
            viewHolder.tvVideoAll = (TextView) convertView.findViewById(R.id.tvVideo_AllFeedAdapter);
            viewHolder.tvDocAll = (TextView) convertView.findViewById(R.id.tvDoc_AllFeedAdapter);
            viewHolder.tvDescAll = (TextView) convertView.findViewById(R.id.tvDesc_AllFeedAdapter);
            viewHolder.ivCatImage = (ImageView) convertView.findViewById(R.id.ivCatImageAll);
            viewHolder.ivSendMail = (ImageView) convertView.findViewById(R.id.ivSendMail_AllAdapter);

            // store the holder with the view.
            convertView.setTag(viewHolder);
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolderItem) convertView.getTag();
        }
        ;
        hashMap = allList.get(position);
        Log.e("CAT", ">> " + hashMap.get("category").toString());
        viewHolder.tvCategoryNameAll.setText(hashMap.get("content_title").toString());

        viewHolder.tvDescAll.setText(hashMap.get("content_details").toString());

        if (!hashMap.get("video").toString().equalsIgnoreCase("")) {
            viewHolder.tvVideoAll.setText(hashMap.get("video"));
        }
        if (!hashMap.get("doc").toString().equalsIgnoreCase("")) {
            viewHolder.tvDocAll.setText(hashMap.get("doc"));
        }
//        viewHolder.tvDescAll.setText(hashMap.get("content_details"));
        if (!hashMap.get("image").toString().equalsIgnoreCase("")) {
            Picasso.with(mContext).load(hashMap.get("image")).placeholder(R.drawable.default_img).error(R.drawable.default_img).into(viewHolder.ivCatImage);
        }

        viewHolder.tvVideoAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hashMap.get("video").toString().equalsIgnoreCase("")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(hashMap.get("video")));
                    mContext.startActivity(browserIntent);
                }
            }
        });

        viewHolder.tvDocAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hashMap.get("doc").toString().equalsIgnoreCase("")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(hashMap.get("doc")));
                    mContext.startActivity(browserIntent);
                }
            }
        });



        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hashMap.get("content_link").toString().equalsIgnoreCase("")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(hashMap.get("content_link")));
                    mContext.startActivity(browserIntent);
                }
            }
        });

        viewHolder.ivSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSendMailPopup(mContext,hashMap.get("id"));
            }
        });


        return convertView;
    }

    static class ViewHolderItem {
        TextView tvCategoryNameAll, tvVideoAll, tvDocAll, tvDescAll;
        ImageView ivCatImage, ivSendMail;
    }


    public void showSendMailPopup(final Context ctx, final String postId) {


        // custom dialog
        final Dialog dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.popup_send_mail);
        dialog.setTitle("Send Mail");
        dialog.setCancelable(false);
        final EditText etFrom = (EditText) dialog.findViewById(R.id.etFromContentAuthorMailId);
        final EditText etSubject = (EditText) dialog.findViewById(R.id.etSubjectContentAuthor);
        final EditText etDesc = (EditText) dialog.findViewById(R.id.tvDescriptionSendMailPopup);

        final TextView ok = (TextView) dialog.findViewById(R.id.tvSendMailPopup);
        final TextView cancel = (TextView) dialog.findViewById(R.id.tvCancelPopup);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String from = etFrom.getText().toString();
                String sub = etSubject.getText().toString();
                String desc = etDesc.getText().toString();


                if(!from.equalsIgnoreCase("") && !sub.equalsIgnoreCase("") && !desc.equalsIgnoreCase("")) {
                    Log.e("FROM","A "+from);
                    Log.e("sub","A "+sub);
                    Log.e("desc","A "+desc);
                    Log.e("POST_ID","A "+postId);

                    new sendMailToContentAuthor(postId,from,sub,desc).execute();
                    dialog.dismiss();
                }else{
                    Toast.makeText(ctx, "Please enter All fields to send mail.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }




    public class sendMailToContentAuthor extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        String _postid, _from,_sub,_desc;

        public sendMailToContentAuthor(String postId, String from, String sub, String desc) {
            this._postid = postId;
            this._from = from;
            this._sub = sub;
            this._desc = desc;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(mContext);
            pd.setMessage("Loading");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
//            post_id=26&subject=abcd&description=all
            HashMap<String, String> hashmap = new HashMap<String, String>();
            hashmap.put("post_id", _postid);
            hashmap.put("subject", _sub);
            hashmap.put("description",_desc);
            response = Util.getResponseofPost(Constant.BASE_URL + "send_mail_post.php", hashmap);
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
                    Toast.makeText(mContext, "Mail Sent Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "" + object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
