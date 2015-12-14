package com.wubydax.awesomedaxsmovies.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wubydax.awesomedaxsmovies.R;
import com.wubydax.awesomedaxsmovies.data.MovieContract;

/**
 * Created by Anna Berkovitch on 11/12/2015.
 */
public class FavMoviesAdapter extends CursorAdapter {
    Utils utils;

    public FavMoviesAdapter (Context context, Cursor c, int flags){
        super(context, c, flags);
        utils = new Utils(context);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_view_item_layout, parent, false);
        ViewHolder vh = new ViewHolder(view);
        view.setTag(vh);
        return view;
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
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder vh = (ViewHolder) view.getTag();

        Bitmap poster = utils.getImage(cursor.getBlob(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_POSTER_BITMAP_COLUMN)));
        String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_TITLE_COLUMN));
        vh.mPoster.setImageBitmap(poster);
        vh.mTitle.setText(title);

    }

}
