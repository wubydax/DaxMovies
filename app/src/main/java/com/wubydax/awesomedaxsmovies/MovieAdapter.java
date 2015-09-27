package com.wubydax.awesomedaxsmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anna Berkovitch on 21/09/2015.
 */
public class MovieAdapter extends BaseAdapter {
    List<JSONObject> mList;
    Context c;
    String LOG_TAG;
    int width, height;
    FrameLayout.LayoutParams layoutParams;
    List<Bitmap> bitmapList;

    public MovieAdapter(Context context, List<JSONObject> jsonObjectList, int imageWidth, int imageHeight) {
        c = context;
        mList = jsonObjectList;
        LOG_TAG = "MovieAdapter";
        width = imageWidth;
        height = imageHeight;
        layoutParams = new FrameLayout.LayoutParams(width, height);
        bitmapList = new ArrayList<>();



    }

    private static class ViewHolder{
        ImageView mPoster;
        TextView mTitle;
    }

    @Override
    public int getCount() {
        if (mList != null) {
            return mList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (mList != null && mList.size() > 0) {
            return mList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String mTitle = "";
        String mUrlLastSegment = "";
        String mFullUrl = "";

        try {
            mTitle = mList.get(position).getString(c.getString(R.string.json_title));
            mUrlLastSegment = mList.get(position).getString(c.getString(R.string.json_poster_path_segment));
            mFullUrl = c.getString(R.string.db_poster_path_beginning) + mUrlLastSegment;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "getView ", e);        }
        if(convertView==null) {
            LayoutInflater mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.grid_view_item_layout, parent, false);
            convertView.setLayoutParams(layoutParams);
            ViewHolder vh = new ViewHolder();
            vh.mPoster = (ImageView) convertView.findViewById(R.id.moviePoster);
            vh.mTitle = (TextView) convertView.findViewById(R.id.movieTitle);
            convertView.setTag(vh);
        }
        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.mTitle.setText(mTitle);
//        if(bitmapList.size()<=position || bitmapList.size() == 0) {
//            Target target = new Target() {
//                @Override
//                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                    if(bitmap!=null) {
//                        viewHolder.mPoster.setImageBitmap(bitmap);
//                        bitmapList.add(bitmap);
//                    }
//
//                }
//
//                @Override
//                public void onBitmapFailed(Drawable errorDrawable) {
//
//                }
//
//                @Override
//                public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                }
//            };
//            Picasso.with(c).load(mFullUrl).into(target);
//        } else {
//            viewHolder.mPoster.setImageBitmap(bitmapList.get(position));
//        }
        Picasso.with(c).load(mFullUrl).fit().centerCrop().into(viewHolder.mPoster);

        return convertView;
    }
}
