package com.wubydax.awesomedaxsmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anna Berkovitch on 21/09/2015.
 */
public class MovieAdapter extends BaseAdapter {
    List<Results> mList;
    Context c;
    String LOG_TAG;
    int width, height;
    AbsListView.LayoutParams layoutParams;
    List<Bitmap> bitmapList;
    DataFragment dataFragment;

    public MovieAdapter(Context context, List<Results> jsonObjectList, int imageWidth, int imageHeight) {
        c = context;
        mList = jsonObjectList;
        LOG_TAG = "MovieAdapter";
        width = imageWidth;
        height = imageHeight;
        layoutParams = new AbsListView.LayoutParams(width, height);
        bitmapList = new ArrayList<>();
        dataFragment = (DataFragment) ((AppCompatActivity) c).getSupportFragmentManager().findFragmentByTag("data");


    }


    private static class ViewHolder {
        ImageView mPoster;
        TextView mTitle;

        public ViewHolder(View v) {
            mPoster = (ImageView) v.findViewById(R.id.moviePoster);
            mTitle = (TextView) v.findViewById(R.id.movieTitle);

        }

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
        ViewHolder vh;

        mTitle = mList.get(position).getTitle();
        mUrlLastSegment = mList.get(position).getPosterPath();
        mFullUrl = c.getString(R.string.db_poster_path_beginning) + mUrlLastSegment;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.grid_view_item_layout, null, false);
            convertView.setLayoutParams(layoutParams);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        }
        vh = (ViewHolder) convertView.getTag();
        vh.mTitle.setText(mTitle);
        Picasso.with(c).load(mFullUrl).fit().centerCrop().into(vh.mPoster);

        return convertView;
    }
}
