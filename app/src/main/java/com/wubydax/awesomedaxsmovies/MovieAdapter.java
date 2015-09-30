package com.wubydax.awesomedaxsmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anna Berkovitch on 21/09/2015.
 */
public class MovieAdapter extends BaseAdapter implements Filterable {
    List<JSONObject> filteredList, passedList;
    Context c;
    String LOG_TAG;
    int width, height;
    AbsListView.LayoutParams layoutParams;
    List<Bitmap> bitmapList;
    DataFragment dataFragment;

    public MovieAdapter(Context context, List<JSONObject> jsonObjectList, int imageWidth, int imageHeight) {
        c = context;
        passedList = jsonObjectList;
        filteredList = passedList;
        LOG_TAG = "MovieAdapter";
        width = imageWidth;
        height = imageHeight;
        layoutParams = new AbsListView.LayoutParams(width, height);
        bitmapList = new ArrayList<>();
        dataFragment = (DataFragment) ((AppCompatActivity) c).getSupportFragmentManager().findFragmentByTag("data");


    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults fr = new FilterResults();
                ArrayList<JSONObject> ai = new ArrayList<>();

                for (int i = 0; i < passedList.size(); i++) {
                    String label;
                    try {
                        label = passedList.get(i).getString(c.getString(R.string.json_title));
                        if (label.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            ai.add(passedList.get(i));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                fr.count = ai.size();
                fr.values = ai;

                return fr;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<JSONObject>) results.values;
                notifyDataSetChanged();
                dataFragment.setJsonData(filteredList);
            }
        };
        return filter;
    }

    private static class ViewHolder {
        ImageView mPoster;
        TextView mTitle;
    }

    @Override
    public void notifyDataSetChanged() {
        Log.d("MainViewFragment", "notifyDataSetChanged called");
        if (filteredList.size() == passedList.size()) {
            filteredList = passedList;
        }
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (filteredList != null) {
            return filteredList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (filteredList != null && filteredList.size() > 0) {
            return filteredList.get(position);
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

        try {
            mTitle = filteredList.get(position).getString(c.getString(R.string.json_title));
            mUrlLastSegment = filteredList.get(position).getString(c.getString(R.string.json_poster_path_segment));
            mFullUrl = c.getString(R.string.db_poster_path_beginning) + mUrlLastSegment;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "getView ", e);
        }
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.grid_view_item_layout, null, false);
            convertView.setLayoutParams(layoutParams);
            vh = new ViewHolder();
            vh.mPoster = (ImageView) convertView.findViewById(R.id.moviePoster);
            vh.mTitle = (TextView) convertView.findViewById(R.id.movieTitle);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.mTitle.setText(mTitle);
        Picasso.with(c).load(mFullUrl).fit().centerCrop().into(vh.mPoster);

        return convertView;
    }
}
