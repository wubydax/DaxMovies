package com.wubydax.awesomedaxsmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Anna Berkovitch on 21/09/2015.
 */
public class MovieAdapter extends BaseAdapter {
    List<JSONObject> mList;
    Context c;
    String LOG_TAG;

    public MovieAdapter(Context context, List<JSONObject> jsonObjectList) {
        c = context;
        mList = jsonObjectList;
        LOG_TAG = "MovieAdapter";



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
    public View getView(int position, View convertView, ViewGroup parent) {
        String mTitle = "";
        String mUrlLastSegment = "";
        String mFullUrl = "";
        int imageWidth = Math.round(c.getResources().getDimension(R.dimen.grid_poster_width));
        int imageHeight = Math.round(c.getResources().getDimension(R.dimen.grid_poster_height));
        try {
            mTitle = mList.get(position).getString(c.getString(R.string.json_title));
            mUrlLastSegment = mList.get(position).getString(c.getString(R.string.json_poster_path_segment));
            mFullUrl = c.getString(R.string.db_poster_path_beginning) + mUrlLastSegment;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "getView ", e);        }
        if(convertView==null) {
            LayoutInflater mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.grid_view_item_layout, parent, false);
            ViewHolder vh = new ViewHolder();
            vh.mPoster = (ImageView) convertView.findViewById(R.id.moviePoster);
            vh.mTitle = (TextView) convertView.findViewById(R.id.movieTitle);
            convertView.setTag(vh);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.mTitle.setText(mTitle);
        Picasso.with(c).load(mFullUrl).resize(imageWidth, imageHeight).into(viewHolder.mPoster);

        return convertView;
    }
}
